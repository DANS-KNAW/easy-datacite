package nl.knaw.dans.common.wicket.components.upload.postprocess.unzip;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.file.UnzipListener;
import nl.knaw.dans.common.lang.file.UnzipUtil;
import nl.knaw.dans.common.wicket.components.upload.UploadStatus;
import nl.knaw.dans.common.wicket.components.upload.postprocess.IUploadPostProcess;
import nl.knaw.dans.common.wicket.components.upload.postprocess.UploadPostProcessException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unzips an incoming file. Explicit rollback is not needed, because the directory gets cleaned on cancel by the
 * EasyUploadProcess.
 * @author lobo
 *
 */
public class UnzipPostProcess implements IUploadPostProcess, UnzipListener
{

    private static final Logger LOG = LoggerFactory.getLogger(UnzipPostProcess.class);

    private UploadStatus status = new UploadStatus("Initializing unzip process");

    private File unzipDestPath = new File("");

    private UnzipUtil unzip;

    private Boolean canceled = false;

    private File file = new File("");

    public List<File> execute(List<File> files, File destPath, Map<String, String> clientParams) throws UploadPostProcessException
    {
        canceled = false;
        if (!needsProcessing(files))
            throw new UploadPostProcessException("file does not need processing");

        try
        {
            file = files.get(0);
            unzipDestPath = destPath;
            if (!unzipDestPath.isDirectory())
                throw new IOException("unable to find directory for unzipping files");

            LOG.info("Unzipping '" + file.getName() + "' to '" + unzipDestPath.getAbsolutePath() + "'");

            unzip = new UnzipUtil(file, unzipDestPath.getPath(), this);
            List<File> unzippedFiles = unzip.run();
            return unzippedFiles;
        }
        catch (Exception e)
        {
            status.setError("Error while unzipping");
            throw new UploadPostProcessException(e);
        }
    }

    public void cancel()
    {
        LOG.info("Unzipping of file '" + file.getName() + "' canceled.");
        canceled = true;
    }

    public UploadStatus getStatus()
    {
        return status;
    }

    public boolean needsProcessing(List<File> files)
    {
        if (files.size() < 1 || files.size() > 1)
            return false;

        String filename = files.get(0).getName();
        Integer idx = filename.lastIndexOf(".");
        if (idx < 0)
            return false;
        else
            return filename.substring(idx).equalsIgnoreCase(".zip");
    }

    public void onUnzipStarted(long totalBytes)
    {
        status.setMessage("Unzipping '" + file.getName() + "': 0% ");
        status.setPercentComplete(0);
    }

    public boolean onUnzipUpdate(long bytesUnzipped, long total)
    {
        Integer percent = new Double((double) bytesUnzipped / total * 100.00).intValue();
        if (percent < 0)
            percent = 0;
        if (percent > 100)
            percent = 100;

        setPercentage(percent);

        return !canceled;
    }

    private void setPercentage(Integer percent)
    {
        status.setMessage("Unzipping '" + file.getName() + "': " + percent + "%");
        status.setPercentComplete(percent);
    }

    public void onUnzipComplete(List<File> files, boolean canceled)
    {
        // onUnzipComplete also gets called when the upload is canceled
        LOG.info("Unzipping of file '" + file.getName() + "' complete.");
        setPercentage(100);
        status.setFinished(true);
    }

    public void rollBack() throws UploadPostProcessException
    {
        // rollback of unzipping process is not necessary, because
        // the files that are unzipped are unzipped in the upload folder
        // created by the EasyUploadProcess which gets cleaned automatically
        // when rollback is called.
    }

}
