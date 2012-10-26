package nl.knaw.dans.common.wicket.components.upload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.util.FileUtil;
import nl.knaw.dans.common.wicket.components.upload.postprocess.IUploadPostProcess;
import nl.knaw.dans.common.wicket.components.upload.postprocess.UploadPostProcessThread;

import org.apache.wicket.Session;
import org.apache.wicket.util.upload.DiskFileItem;
import org.apache.wicket.util.upload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lobo
 * This object is the upload workflow controller. It has an UploadStatus object
 * which is the model. It's role is not only to keep the UploadStatus object up to date
 * but also to handle the different steps of the uploading process. It delegates this
 * action to the an UploadPostProcessThread, staying in close contact.
 *
 * This class does its own error handling; it does not throw exceptions.
 * It is assumed to be used from within an ajax or multi-part upload environment.
 */
public class EasyUploadProcess
{
    private static final Logger LOG = LoggerFactory.getLogger(EasyUploadProcess.class);

    private final Integer uploadId;

    private final String filename;

    private Map<String, String> clientParams;

    protected final EasyUploadStatus status;

    private EasyUpload easyUpload;

    private Integer steps;

    private Boolean canceled = false;

    private List<IUploadPostProcess> postProcessesors;

    private File uploadedFile = null;

    private File basePath = null;

    private UploadPostProcessThread postProcessorThread = null;

    /**
     * This boolean determines if the postprocessing will block the wicket operation. The only
     * use for this is debugging and stress testing.
     */
    private final boolean POSTPROCESSORS_BLOCKING = false;

    public EasyUploadProcess(Integer uploadId, String filename, Map<String, String> clientParams, EasyUpload easyUpload)
    {
        this.uploadId = uploadId;
        this.filename = FileUtil.getBasicFilename(filename);
        this.easyUpload = easyUpload;
        this.clientParams = clientParams;
        if (easyUpload == null)
            throw new IllegalArgumentException("easyUpload");

        status = new EasyUploadStatus(uploadId, "Initializing upload process");

        // get post processes and induce number of steps from that
        postProcessesors = easyUpload.getPostProcesses(filename);
        steps = postProcessesors.size() + 1;
    }

    public EasyUploadStatus getStatus()
    {
        // the upload process is set to finished after the postProcessorThread finished
        if (status.isFinished())
            return status;

        // update status according to postProcessing if process not finished and thread
        // still exists
        if (postProcessorThread != null)
        {
            UploadStatus poststat = postProcessorThread.getStatus();
            status.setPercentComplete(poststat.getPercentComplete());
            if (poststat.isError())
                status.setError(poststat.getMessage());
            else
                status.setMessage(poststat.getMessage() + " " + getStepMessage());
            // wait for thread to return
            status.setFinished(false);
        }

        return status;
    }

    protected String getStepMessage()
    {
        if (steps <= 1)
            return "";

        int currentStep = 1;
        if (postProcessorThread != null)
            currentStep = postProcessorThread.getCurrentStep() + 1;

        return "(step " + currentStep + "/" + steps + ")";
    }

    protected void error(String userMessage, Exception e)
    {
        if (e != null)
            LOG.error(e.getMessage(), e);
        status.setError(userMessage);
        rollBack();
    }

    protected void error(String userMessage, String logMessage)
    {
        LOG.error(logMessage);
        error(userMessage, (Exception) null);
    }

    public void onUploadStarted(int totalBytes)
    {
        setPercentage(0);
    }

    public void onUploadUpdate(int bytesUploaded, int total)
    {
        int percent = new Double((double) bytesUploaded / total * 100.00).intValue();
        setPercentage(percent);
    }

    public void onUploadCompleted(FileItem file)
    {
        if (canceled)
        {
            LOG.info("Rolling back upload for file: '" + filename + "'");
            file.delete();
            return;
        }

        setPercentage(100);

        // do check on the filename
        String uploadedFilename = FileUtil.getBasicFilename(file.getName());
        if (!uploadedFilename.equals(filename))
        {
            LOG.warn("UploadProcess.filename != uploadedFilename");
            LOG.warn("UploadProcess.filename = \"" + filename + "\"");
            LOG.warn("uploadedFilename       = \"" + uploadedFilename + "\"");
            // try to continue anyway
        }

        // make sure file is on disk and in the right location
        try
        {
            basePath = FileUtil.createTempDirectory(new File(easyUpload.getBasePath()), "upload");
        }
        catch (IOException e1)
        {
            error("Could not write to disk.", "createUniquePath failed");
            return;
        }
        uploadedFile = new File(basePath.getAbsolutePath() + File.separatorChar + uploadedFilename);

        // try to move the uploaded file
        boolean fileMoved = false;
        if (!file.isInMemory() && file instanceof DiskFileItem)
        {
            try
            {
                fileMoved = ((DiskFileItem) file).getStoreLocation().renameTo(uploadedFile);
                if (fileMoved == false)
                {
                    LOG.error("Error occured while moving uploaded file from temporary location '" + ((DiskFileItem) file).getStoreLocation().getAbsolutePath()
                            + "' to '" + uploadedFile + "'");
                }
            }
            catch (Exception ex)
            {
                // write error to log, but continue execution. fileMoved == false means fallback mechanism.
                LOG.error("Exception occured while moving uploaded file from temp. location '" + ((DiskFileItem) file).getStoreLocation().getAbsolutePath()
                        + "' to '" + uploadedFile + "'. Exception: " + ex.getMessage(), ex);
            }
        }

        // if file moving failed, copy it to disk (from memory or disk)
        if (fileMoved == false)
        {
            try
            {
                OutputStream outputStream = new FileOutputStream(uploadedFile);
                InputStream inputStream = file.getInputStream();
                byte[] buffer = new byte[8192];
                int length;
                while (((length = inputStream.read(buffer)) != -1) && (!canceled))
                {
                    outputStream.write(buffer, 0, length);
                }
                inputStream.close();
                outputStream.close();
            }
            catch (FileNotFoundException e)
            {
                error("File could not be created on disk", e);
                return;
            }
            catch (IOException e)
            {
                error("Error writing file to disk", e);
                return;
            }
        }

        if (canceled)
            return;

        LOG.info("Received file " + uploadedFile.getAbsolutePath());

        List<File> files = new ArrayList<File>(1);
        files.add(uploadedFile);
        if (postProcessesors.size() > 0)
        {
            // start post processor thread
            postProcessorThread = new UploadPostProcessThread(postProcessesors, files, basePath, clientParams, Session.get())
            {
                @Override
                public void onSuccess(File basePath, List<File> files)
                {
                    setUploadCompleted(files);
                }
            };

            if (POSTPROCESSORS_BLOCKING)
                postProcessorThread.run();
            else
                postProcessorThread.start();
        }
        else
        {
            setUploadCompleted(files);
        }
    }

    private void setPercentage(int percent)
    {
        if (percent < 0)
            percent = 0;
        if (percent > 100)
            percent = 100;
        status.setMessage("Uploading '" + filename + "': " + percent + "% " + getStepMessage());
        status.setPercentComplete(percent);
    }

    public void rollBack()
    {
        if (basePath == null)
            // means nothings to rol lback
            return;

        LOG.info("Rollingback upload process with uploadId " + uploadId + ".");
        cleanFiles();
    }

    public void cleanFiles()
    {
        if (basePath == null)
            // means nothings to clean
            return;

        LOG.info("Deleting directory '" + basePath + "'");
        try
        {
            FileUtil.deleteDirectory(basePath);
            basePath = null;
        }
        catch (IOException e)
        {
            LOG.error("Deleting of directory " + basePath + " failed", e);
        }
    }

    protected void setUploadCompleted(List<File> files)
    {
        String msg = "Upload of '" + filename + "' complete";
        if (files.size() > 1)
            msg += " (" + files.size() + " files and folders)";
        status.setMessage(msg);
        status.setFinished(true);

        easyUpload.onReceivedFiles(clientParams, basePath.getAbsolutePath(), files);

        if (easyUpload.getConfig().autoRemoveFiles())
            cleanFiles();
    }

    public boolean killPostProcessorThread(int timeoutSeconds)
    {
        postProcessorThread.cancel();
        if (timeoutSeconds == -1)
            return true;

        boolean threadIsAlive = postProcessorThread.isAlive();
        try
        {
            for (int i = 0; i < timeoutSeconds * 10 && threadIsAlive; i++)
            {
                threadIsAlive = postProcessorThread.isAlive();
                Thread.sleep(100);
            }
        }
        catch (InterruptedException e)
        {
            threadIsAlive = postProcessorThread.isAlive();
        }

        // return threadIsDead;
        return !threadIsAlive;
    }

    @SuppressWarnings("deprecation")
    public void cancel()
    {
        if (status.isFinished())
            // too late
            return;

        if (postProcessorThread != null && postProcessorThread.isFinished() == false)
        {
            if (!killPostProcessorThread(-1))
            {
                LOG.error("postProcessorThread does not want to die. Trying deprecated bullets.");
                postProcessorThread.stop();
                // last chance (another 3 seconds)
                killPostProcessorThread(3);

                if (postProcessorThread.isAlive())
                    LOG.error("postProcessorThread not responding to bullets. This is serious. Why don't you die you son of a thr##@d!");
            }
        }

        canceled = true;
        rollBack();
    }

    public Map<String, String> getClientParams()
    {
        return clientParams;
    }

    public EasyUpload getEasyUpload()
    {
        return easyUpload;
    }

    public Integer getUploadId()
    {
        return uploadId;
    }
}
