package nl.knaw.dans.common.wicket.components.upload.postprocess;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.wicket.components.upload.UploadStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadPostProcessRunner {
    private static final Logger LOG = LoggerFactory.getLogger(UploadPostProcessRunner.class);

    private List<IUploadPostProcess> postProcessors;
    private boolean canceled = false;
    private List<File> files;
    private File basePath;
    private IUploadPostProcess currentPostProcess = null;
    private Integer currentStep = 0;
    private String errorMsg = "";
    private Map<String, String> clientParams;
    private boolean finished = false;
    private Object currentStepLock = new Object();

    public UploadPostProcessRunner(List<IUploadPostProcess> postProcessesors, List<File> files, File basePath, Map<String, String> clientParams) {
        super();
        this.postProcessors = postProcessesors;
        this.basePath = basePath;
        this.files = files;
        this.clientParams = clientParams;
    }

    private void error(String errorMsg, Throwable e) {
        this.errorMsg = errorMsg;
        LOG.error(e.getMessage(), e);
    }

    public void run() {
        Iterator<IUploadPostProcess> i = postProcessors.iterator();
        currentStep = 0;
        currentPostProcess = null;
        try {
            while ((i.hasNext())) {
                synchronized (currentStepLock) {
                    if (canceled)
                        break;
                    currentPostProcess = i.next();
                    if (i.hasNext())
                        currentStep++;
                }
                files = currentPostProcess.execute(files, basePath, clientParams);
            }
            if (!canceled) {
                finished = true;
                onSuccess(basePath, files);
            }
        }
        catch (UploadPostProcessException e) {
            String processErrorMsg = "Error during postprocessing.";
            if (currentPostProcess.getStatus().isError())
                processErrorMsg = currentPostProcess.getStatus().getMessage();
            error(processErrorMsg, e);
        }
        finished = true;
    }

    /**
     * The cancel functions cancels the post processing. It must of course be called from another thread because this thread will not be able to interrupt into
     * the execution process of the post processing.
     */
    public void cancel() {
        if (finished)
            // too late
            return;

        synchronized (currentStepLock) {
            canceled = true;

            if (currentStep < 0 || currentPostProcess == null)
                // postprocessing has not yet started
                return;

            try {
                currentPostProcess.cancel();
            }
            catch (UploadPostProcessException e) {
                error("Error canceling upload process", e);
            }
        }
    }

    public UploadStatus getStatus() {
        synchronized (currentStepLock) {
            if (errorMsg.length() > 0) {
                UploadStatus errorStatus = new UploadStatus(errorMsg);
                errorStatus.setError(true);
                return errorStatus;
            }
            if (currentPostProcess == null) {
                return new UploadStatus("Initializing ...");
            }
            return currentPostProcess.getStatus();
        }
    }

    public Integer getCurrentStep() {
        return currentStep;
    }

    public boolean isFinished() {
        return finished;
    }

    public void onSuccess(File basePath, List<File> files) {}
}
