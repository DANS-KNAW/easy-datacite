package nl.knaw.dans.easy.tools.task.am.dataset;

import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;

/**
 * List all persistent identifiers for the datasets filtered by the DatasetController.
 */
public class ListPidsWithFilter extends AbstractTask {

    private int datasetCounter = 0;

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        datasetCounter++;
        RL.info(new Event(getTaskName(), joint.getDataset().getPersistentIdentifier()));
    }

    /**
     * When we are finished looping through all datasets, log the total number of datasets before exiting.
     */
    @Override
    public void close() {
        RL.info(new Event(getTaskName(), "Found: [" + datasetCounter + "] datasets."));
    }
}
