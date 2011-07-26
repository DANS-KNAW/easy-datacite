package nl.knaw.dans.easy.business.dataset;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.easy.data.ext.ExternalServices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubmissionDispatcher
{

    private static final Logger       logger             = LoggerFactory.getLogger(SubmissionDispatcher.class);

    private List<SubmissionProcessor> processors         = new ArrayList<SubmissionProcessor>();
    private List<SubmissionProcessor> threadedProcessors = new ArrayList<SubmissionProcessor>();
    
    
    protected SubmissionDispatcher()
    {
    }

    public List<SubmissionProcessor> getProcessors()
    {
        return processors;
    }

    public void setProcessors(List<SubmissionProcessor> processors)
    {
        this.processors = processors;
    }

    public List<SubmissionProcessor> getThreadedProcessors()
    {
        return threadedProcessors;
    }

    public void setThreadedProcessors(List<SubmissionProcessor> threadedProcessors)
    {
        this.threadedProcessors = threadedProcessors;
    }

    protected void process(DatasetSubmissionImpl submission)
    {
        boolean allStepsCompleted = true;
        for (SubmissionProcessor processor : getProcessors())
        {
            if (allStepsCompleted || processor.continueAfterFailure())
            {
                allStepsCompleted &= processor.process(submission);
            }
        }
        submission.setCompleted(allStepsCompleted);

        if (allStepsCompleted && !threadedProcessors.isEmpty())
        {
            ThreadedDispatcher threadedDispatcher = new ThreadedDispatcher(new DatasetSubmissionImpl(submission));
            Thread thread = new Thread(threadedDispatcher, "dataset submission");
            thread.setPriority(Thread.MIN_PRIORITY);
            logger.debug("Handing over submission of dataset to thread " + thread.getId());
            thread.start();
        }
    }

    private class ThreadedDispatcher implements Runnable
    {

        private final DatasetSubmissionImpl submission;

        private ThreadedDispatcher(DatasetSubmissionImpl submission)
        {
            this.submission = submission;
        }

        public void run()
        {
        	try
        	{
	        	boolean allStepsCompleted = true;
	            for (SubmissionProcessor processor : threadedProcessors)
	            {
	                if (allStepsCompleted || processor.continueAfterFailure())
	                {
	                    allStepsCompleted &= processor.process(submission);
	                }
	            }
	            logger.debug(submission.getState());
	            if (!allStepsCompleted)
	            {
	                handleSubmissionFailure(new RuntimeException(
	                        "Not all steps completed in threaded handling. "
	                        + "thread=" + Thread.currentThread().getId()));
	            }
	            logger.debug("Ending dataset submission in thread " + Thread.currentThread().getId());
        	}
        	catch (Exception e)
        	{
        	    handleSubmissionFailure(e);
        	}
        	finally
        	{
        	}
        }

        private void handleSubmissionFailure(Exception e)
        {
            String state = submission.getState();
            String msg = "Failure on submission of dataset.\n" + state;
            logger.error(msg, e);
            ExternalServices.getAdminMailer().sendEmergencyMail(msg, e);
        }

    }

}
