package nl.knaw.dans.common.lang.progress;

import java.util.ArrayList;
import java.util.List;

public class ProgressSubject
{
    public static final String PROCESS_ID_PREFIX = "process.id.";

    public static final int DEFAULT_WEIGHT = 10;

    public static final int DEFAULT_PERCENTAGE_STEP = 10;

    private int stepWeight;

    private int percentageStep;

    private int currentReportLevel;

    private String processId;

    private final List<ProgressListener> myListeners = new ArrayList<ProgressListener>();

    public ProgressSubject()
    {
        stepWeight = DEFAULT_WEIGHT;
    }

    public ProgressSubject(int stepWeight)
    {
        this.stepWeight = stepWeight;
    }

    public ProgressSubject(String processId, int stepWeight)
    {
        this.processId = processId;
        this.stepWeight = stepWeight;
    }

    public String getProcessId()
    {
        if (processId == null)
        {
            processId = this.getClass().getName();
        }
        return processId;
    }

    public void setProcessId(String processId)
    {
        this.processId = processId;
    }

    public void setWeight(int stepWeight)
    {
        this.stepWeight = stepWeight;
    }

    public int getWeight()
    {
        return stepWeight;
    }

    public void addProgressListeners(ProgressListener... listeners)
    {
        synchronized (myListeners)
        {
            for (ProgressListener listener : listeners)
            {
                myListeners.add(listener);
            }
        }
    }

    public boolean removeProgressListener(ProgressListener listener)
    {
        synchronized (myListeners)
        {
            return myListeners.remove(listener);
        }
    }

    protected int getPercentageStep()
    {
        if (percentageStep <= 0)
        {
            percentageStep = DEFAULT_PERCENTAGE_STEP;
        }
        return percentageStep;
    }

    protected void setPercentageStep(int percentageStep)
    {
        this.percentageStep = percentageStep;
    }

    protected void onStartProcess()
    {
        onStartProcess(getProcessId());
    }

    protected void onStartProcess(String processId)
    {
        currentReportLevel = 0;
        synchronized (myListeners)
        {
            for (ProgressListener l : myListeners)
            {
                l.onStartProcess(PROCESS_ID_PREFIX + processId);
            }
        }
    }

    protected void onProgress(int totalItems, int currentItem)
    {
        int percentage = (int) (((double) currentItem) / ((double) totalItems) * 100D);
        if (percentage >= currentReportLevel)
        {
            currentReportLevel += getPercentageStep();
            synchronized (myListeners)
            {
                for (ProgressListener l : myListeners)
                {
                    l.updateProgress(percentage);
                }
            }
        }
    }

    protected void onEndProcess()
    {
        onEndProcess(getProcessId());
    }

    protected void onEndProcess(String processId)
    {
        synchronized (myListeners)
        {
            for (ProgressListener l : myListeners)
            {
                l.updateProgress(100);
                l.onEndProcess(PROCESS_ID_PREFIX + processId);
            }
        }
    }

}
