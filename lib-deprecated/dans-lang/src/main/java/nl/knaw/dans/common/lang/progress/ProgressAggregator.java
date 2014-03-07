package nl.knaw.dans.common.lang.progress;

import java.util.ArrayList;
import java.util.List;

/**
 * Aggregates the progress of compound processes and distributes the overall progress to interested listeners.
 * Weights indicate expected execution time of processes associated with ProgressSubjects.
 * <p/>
 * <h3>Example</h3>
 * 
 * We have {@link ProgressSubject}s A, B, C and D with respective weights of 1, 2, 3 and 4, indicating that
 * process A will run in approximately 1/10 of total execution time, process B in 2/10, process C in 3/10 and
 * process D in 4/10. If we execute these registered processes in the order A, B, C, D, 
 * this ProgressAggregator will distribute the overall progress according to the weight of the 
 * process being executed. At the end of each process the progress indicated will be
 * <pre>
 *           0%  10%   30%     60%       100%
 *           | - | - - | - - - | - - - - |
 * end of:       A     B       C         D
 * </pre>
 * 
 * 
 */
public class ProgressAggregator
{

    private List<ProgressListener> myListeners = new ArrayList<ProgressListener>();
    private double totalWeight;
    private double totalPercentage;
    private String currentProcessId;

    /**
     * 
     * @param listeners
     */
    public void addListeners(ProgressListener... listeners)
    {
        synchronized (myListeners)
        {
            for (ProgressListener listener : listeners)
            {
                myListeners.add(listener);
            }
        }
    }

    public boolean removeListener(ProgressListener listener)
    {
        synchronized (myListeners)
        {
            return myListeners.remove(listener);
        }
    }

    public void registerSubject(ProgressSubject subject)
    {
        int weight = subject.getWeight();
        totalWeight += weight;
        subject.addProgressListeners(new PartialProgressListener(this, weight));
    }

    protected void onStartProgress(String processId)
    {
        if (currentProcessId != null)
        {
            throw new IllegalStateException("'onStartProgress' called with processId '" + processId + "', while current processId is still '"
                    + currentProcessId + " A call to 'onEndProcess' is missing from ProgressSubject with id '" + currentProcessId + "'");
        }
        currentProcessId = processId;
        synchronized (myListeners)
        {
            for (ProgressListener l : myListeners)
            {
                l.onStartProcess(processId);
            }
        }
    }

    protected void updateProgress(int percentage, int partialWeight)
    {
        double partialPercentage = getPartialPercentage(partialWeight, ((double) percentage));
        synchronized (myListeners)
        {
            for (ProgressListener l : myListeners)
            {
                l.updateProgress((int) (totalPercentage + partialPercentage));
            }
        }
    }

    protected void onEndProcess(String processId, int partialWeight)
    {
        if (currentProcessId == null)
        {
            throw new IllegalStateException("'onEndProgress' called with processId '" + processId + "', while current processId is null."
                    + " A call to 'onStartProcess' is missing from ProgressSubject with id '" + processId + "'");
        }
        currentProcessId = null;
        totalPercentage += getPartialPercentage(partialWeight, 100D);
        synchronized (myListeners)
        {
            for (ProgressListener l : myListeners)
            {
                l.updateProgress((int) totalPercentage);
                l.onEndProcess(processId);
            }
        }
    }

    protected double getPartialPercentage(double partialWeight, double perc)
    {
        double partialPercentage = (partialWeight / totalWeight) * perc;
        return partialPercentage;
    }

    private static class PartialProgressListener implements ProgressListener
    {
        private final ProgressAggregator aggregator;
        private final int partialWeight;

        public PartialProgressListener(ProgressAggregator aggregator, int partialWeight)
        {
            this.aggregator = aggregator;
            this.partialWeight = partialWeight;
        }

        @Override
        public void onStartProcess(String processId)
        {
            aggregator.onStartProgress(processId);
        }

        @Override
        public void updateProgress(int percentage)
        {
            aggregator.updateProgress(percentage, partialWeight);
        }

        @Override
        public void onEndProcess(String processId)
        {
            aggregator.onEndProcess(processId, partialWeight);
        }

    }

}
