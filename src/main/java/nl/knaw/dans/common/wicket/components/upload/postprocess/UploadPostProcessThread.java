package nl.knaw.dans.common.wicket.components.upload.postprocess;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.wicket.components.upload.UploadStatus;

import org.apache.wicket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadPostProcessThread extends Thread
{

    private List<IUploadPostProcess> postProcessors;

    private boolean canceled = false;

    private List<File> files;

    private File basePath;

    private IUploadPostProcess currentPostProcess = null;

    private Integer currentStep = 0;

    private String errorMsg = "";

    private Map<String, String> clientParams;

    private static final Logger LOG = LoggerFactory.getLogger(UploadPostProcessThread.class);

    private boolean finished = false;

    private Object currentStepLock = new Object();

    private Session session;

    public UploadPostProcessThread(List<IUploadPostProcess> postProcessesors, List<File> files, File basePath, Map<String, String> clientParams, Session session)
    {
        super();
        this.postProcessors = postProcessesors;
        this.basePath = basePath;
        this.files = files;
        this.clientParams = clientParams;
        this.session = session;
    }

    public void error(String errorMsg, Throwable e)
    {
        this.errorMsg = errorMsg;
        LOG.error(e.getMessage(), e);
    }

    @Override
    public void run()
    {
        // attach the session to the current thread
        Session.set(session);

        // start the upload postprocessors
        Iterator<IUploadPostProcess> i = postProcessors.iterator();
        currentStep = 0;
        currentPostProcess = null;
        try
        {
            while ((i.hasNext()))
            {
                synchronized (currentStepLock)
                {
                    if (canceled)
                        break;
                    currentPostProcess = i.next();
                    currentStep++;
                }
                files = currentPostProcess.execute(files, basePath, clientParams);
            }

            if (!canceled)
            {
                // set this before onSuccess
                finished = true;
                // successfully completed all steps!
                onSuccess(basePath, files);
            }
            else
            {
                // don't set finished until after rollback
                synchronized (currentStepLock)
                {
                    LOG.info("Rolling back post processors.");

                    // call rollback on all post processors in reverse order
                    if (currentStep >= 1)
                    {
                        for (int j = currentStep - 1; j >= 0; j--)
                        {
                            postProcessors.get(j).rollBack();
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            String processErrorMsg = "Error during postprocessing.";
            if (currentPostProcess.getStatus().isError())
                processErrorMsg = currentPostProcess.getStatus().getMessage();
            error(processErrorMsg, e);
        }

        finished = true;
        onFinished();
    }

    /**
     * The cancel functions cancels the post processing. It must of course be called from another thread
     * because this thread will not be able to interrupt into the execution process of the post processing.
     */
    public void cancel()
    {
        if (finished)
            // too late
            return;

        synchronized (currentStepLock)
        {
            canceled = true;

            if (currentStep < 0 || currentPostProcess == null)
                // postprocessing has not yet started
                return;

            try
            {
                currentPostProcess.cancel();
            }
            catch (UploadPostProcessException e)
            {
                error("Error canceling upload process", e);
            }
        }
    }

    public UploadStatus getStatus()
    {
        synchronized (currentStepLock)
        {
            if (currentStep <= 0)
                return new UploadStatus("Postprocessing initializing");

            if (errorMsg.length() > 0)
            {
                UploadStatus errorStatus = new UploadStatus(errorMsg);
                errorStatus.setError(true);
                return errorStatus;
            }

            return currentPostProcess.getStatus();
        }
    }

    public Integer getCurrentStep()
    {
        return currentStep;
    }

    public Integer getStepCount()
    {
        return postProcessors.size();
    }

    public boolean isFinished()
    {
        return finished;
    }

    /**
     * This method is called when the thread has finished execution (no matter for what reason). Override it to gain
     * event access.
     */
    public void onFinished()
    {
    }

    /**
     * This method is called when the thread has finished execution successfully (no error/ no cancel). Override it to gain
     * event access.
     */
    public void onSuccess(File basePath, List<File> files)
    {
    }
}
