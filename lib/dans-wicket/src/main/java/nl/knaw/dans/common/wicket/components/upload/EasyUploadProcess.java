package nl.knaw.dans.common.wicket.components.upload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.util.FileUtil;
import nl.knaw.dans.common.wicket.components.upload.postprocess.IUploadPostProcess;
import nl.knaw.dans.common.wicket.components.upload.postprocess.UploadPostProcessRunner;

import org.apache.commons.io.FileUtils;
import org.apache.wicket.util.upload.DiskFileItem;
import org.apache.wicket.util.upload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lobo This object is the upload workflow controller. It has an UploadStatus object which is the model. It's role is not only to keep the UploadStatus
 *         object up to date but also to handle the different steps of the uploading process. It delegates this action to the an UploadPostProcessThread,
 *         staying in close contact. This class does its own error handling; it does not throw exceptions. It is assumed to be used from within an ajax or
 *         multi-part upload environment.
 */
public class EasyUploadProcess {
    private static final Logger LOG = LoggerFactory.getLogger(EasyUploadProcess.class);

    private final Integer uploadId;

    private final String filename;

    private Map<String, String> clientParams;

    private EasyUploadStatus status;

    private EasyUpload easyUpload;

    private Integer steps;

    private Boolean canceled = false;

    private List<IUploadPostProcess> postProcessesors;

    private File uploadedFile = null;

    private File basePath = null;

    private UploadPostProcessRunner postProcessorThread = null;

    public EasyUploadProcess(Integer uploadId, String filename, Map<String, String> clientParams, EasyUpload easyUpload) {
        this.uploadId = uploadId;
        this.filename = FileUtil.getBasicFilename(filename);
        this.easyUpload = easyUpload;
        this.clientParams = clientParams;
        if (easyUpload == null)
            throw new IllegalArgumentException("easyUpload");

        status = new EasyUploadStatus(uploadId, "Initializing upload process");

        // get post processes and induce number of steps from that
        postProcessesors = easyUpload.getPostProcesses(filename);
        steps = postProcessesors.size();
    }

    public EasyUploadStatus getStatus() {
        // the upload process is set to finished after the postProcessorThread finished
        if (status.isFinished())
            return status;

        // update status according to postProcessing if process not finished and thread
        // still exists
        if (postProcessorThread != null) {
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

    private String getStepMessage() {
        int currentStep = 1;
        if (postProcessorThread != null)
            currentStep = postProcessorThread.getCurrentStep();

        if (currentStep == 0)
            currentStep = 1;

        return "(step " + currentStep + "/" + steps + ")";
    }

    protected void error(String userMessage, Exception e) {
        if (e != null)
            LOG.error(e.getMessage(), e);
        status.setError(userMessage);
        rollBack();
    }

    protected void error(String userMessage, String logMessage) {
        LOG.error(logMessage);
        error(userMessage, (Exception) null);
    }

    public void onUploadStarted(int totalBytes) {
        setPercentage(0);
    }

    public void onUploadUpdate(int bytesUploaded, int total) {
        int percent = new Double((double) bytesUploaded / total * 100.00).intValue();
        setPercentage(percent);
    }

    private String dump(String s) {
        return s + Arrays.toString(s.toCharArray()) + Arrays.toString(s.getBytes());
    }

    public void onUploadCompleted(FileItem file) {
        if (canceled) {
            LOG.info("Rolling back upload for file: '" + filename + "'");
            file.delete();
            return;
        }

        setPercentage(100);

        // do check on the filename
        // from wicket's normalization (accents as a separate character)
        // to java.io.File normalization (accents integrated into a single character)
        String uploadedFilename = FileUtil.getBasicFilename(file.getName());
        // String uploadedFilename = Normalizer.normalize(FileUtil.getBasicFilename(file.getName()),
        // Normalizer.Form.NFC);
        if (!uploadedFilename.equals(filename)) {
            LOG.warn("UploadProcess.filename != uploadedFilename");
            LOG.warn("UploadProcess.filename = " + dump(filename));
            LOG.warn("uploadedFilename       = " + dump(uploadedFilename));
            // String s = Normalizer.normalize(FileUtil.getBasicFilename(file.getName()),
            // Normalizer.Form.NFD);
            // if (!s.equals(filename))
            // LOG.warn("normalisation mismatch (treatment of accents), it varies by JVM and/or OS");
            // try to continue anyway
        }

        // make sure file is on disk and in the right location
        try {
            basePath = FileUtil.createTempDirectory(new File(easyUpload.getBasePath()), "upload");
        }
        catch (IOException e1) {
            error("Could not write to disk.", "createUniquePath failed");
            return;
        }
        String uploadFilename = basePath.getAbsolutePath() + File.separatorChar + uploadedFilename;
        uploadedFile = new File(uploadFilename);

        // try to move the uploaded file
        boolean fileMoved = false;
        if (!file.isInMemory() && file instanceof DiskFileItem) {
            try {
                fileMoved = ((DiskFileItem) file).getStoreLocation().renameTo(uploadedFile);
                if (fileMoved == false) {
                    LOG.error("Error occured while moving uploaded file from temporary location '" + ((DiskFileItem) file).getStoreLocation().getAbsolutePath()
                            + "' to '" + uploadedFile + "'");
                }
            }
            catch (Exception ex) {
                // write error to log, but continue execution. fileMoved == false means fallback
                // mechanism.
                LOG.error("Exception occured while moving uploaded file from temp. location '" + ((DiskFileItem) file).getStoreLocation().getAbsolutePath()
                        + "' to '" + uploadedFile + "'. Exception: " + ex.getMessage(), ex);
            }
        }

        // if file moving failed, copy it to disk (from memory or disk)
        if (fileMoved == false) {
            try {
                OutputStream outputStream = new FileOutputStream(uploadedFile);
                InputStream inputStream = file.getInputStream();
                byte[] buffer = new byte[8192];
                int length;
                while (((length = inputStream.read(buffer)) != -1) && (!canceled)) {
                    outputStream.write(buffer, 0, length);
                }
                inputStream.close();
                outputStream.close();
            }
            catch (FileNotFoundException e) {
                error("File could not be created on disk", e);
                return;
            }
            catch (IOException e) {
                error("Error writing file to disk", e);
                return;
            }
        }

        if (canceled)
            return;

        LOG.info("Received file " + uploadedFile.getAbsolutePath());

        List<File> files = new ArrayList<File>(2);
        if (!hasDiacritics())
            files.add(uploadedFile);
        else
            addDiacriticVariants(files);
        if (postProcessesors.size() > 0) {
            // start post processor thread
            postProcessorThread = new UploadPostProcessRunner(postProcessesors, files, basePath, clientParams) {
                @Override
                public void onSuccess(File basePath, List<File> files) {
                    setUploadCompleted(files);
                }
            };
            postProcessorThread.run();
        } else {
            setUploadCompleted(files);
        }
    }

    private void setPercentage(int percent) {
        if (percent < 0)
            percent = 0;
        if (percent > 100)
            percent = 100;
        status.setMessage("Uploading '" + filename + "': " + percent + "% " + getStepMessage());
        status.setPercentComplete(percent);
    }

    public void rollBack() {
        if (basePath == null)
            // means nothings to rol lback
            return;

        LOG.info("Rollingback upload process with uploadId " + uploadId + ".");
        cleanFiles();
    }

    public void cleanFiles() {
        if (basePath == null)
            // means nothings to clean
            return;

        LOG.info("Deleting directory '" + basePath + "'");
        try {
            FileUtils.deleteDirectory(basePath);
            basePath = null;
        }
        catch (IOException e) {
            LOG.error("Deleting of directory " + basePath + " failed", e);
        }
    }

    protected void setUploadCompleted(List<File> files) {
        String msg = "Upload of '" + filename + "' complete.";
        if (files.size() > 1 && !files.get(0).getName().equals(filename)) {
            if (!hasDiacritics() || !hasInitialDiacriticFileNames(files))
                msg += " (" + files.size() + " files and folders)";
        }
        status.setMessage(msg);
        status.setFinished(true);

        easyUpload.onReceivedFiles(clientParams, basePath.getAbsolutePath(), files);

        if (easyUpload.getConfig().autoRemoveFiles())
            cleanFiles();
    }

    private boolean hasInitialDiacriticFileNames(List<File> files) {
        return files.size() == 2 && files.get(0).getName().equals(toNFC(filename)) && files.get(1).getName().equals(toNFD(filename));
    }

    private void addDiacriticVariants(List<File> files) {
        // depending on JVM/OS Files.listFiles() may use different normalization
        // we add both variants
        // thus the filter of ItemIngester.workAddDirectoryContents() finds the uploaded file
        files.add(new File(toNFC(uploadedFile.getPath())));
        files.add(new File(toNFD(uploadedFile.getPath())));
    }

    private String toNFD(String path) {
        return Normalizer.normalize(path, Normalizer.Form.NFD);
    }

    private String toNFC(String path) {
        return Normalizer.normalize(path, Normalizer.Form.NFC);
    }

    private boolean hasDiacritics() {
        String s2 = Normalizer.normalize(uploadedFile.getPath(), Normalizer.Form.NFD);
        boolean matches = s2.matches("(?s).*\\p{InCombiningDiacriticalMarks}.*");
        return matches;
    }

    public void cancel() {
        if (status.isFinished())
            // too late
            return;

        canceled = true;
        rollBack();
    }

    public Map<String, String> getClientParams() {
        return clientParams;
    }

    public EasyUpload getEasyUpload() {
        return easyUpload;
    }

    public Integer getUploadId() {
        return uploadId;
    }
}
