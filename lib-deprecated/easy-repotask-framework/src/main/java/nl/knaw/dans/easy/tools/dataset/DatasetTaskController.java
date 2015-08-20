package nl.knaw.dans.easy.tools.dataset;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.Task;
import nl.knaw.dans.easy.tools.dmo.DmoFilter;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.NoListenerException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;
import nl.knaw.dans.easy.tools.util.RepoUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatasetTaskController extends AbstractTask {
    private static Logger log = LoggerFactory.getLogger(DatasetTaskController.class);

    private final DmoFilter<Dataset>[] datasetFilters;

    private List<Task> tasks = new ArrayList<Task>();

    public DatasetTaskController() {
        this(new AllPassDatasetFilter());
    }

    public DatasetTaskController(DmoFilter<Dataset>... datasetFilters) {
        this.datasetFilters = datasetFilters;
        printFilters();
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        try {
            RepoUtil.checkListenersActive();
        }
        catch (NoListenerException e) {
            throw new FatalTaskException("No searchEngine", e, this);
        }

        DatasetIterator diter = new DatasetIterator(datasetFilters);
        try {
            while (diter.hasNext()) {
                joint.clearCycleState();
                joint.setDataset(diter.next());
                executeSteps(joint);
            }
        }
        catch (RepositoryException e) {
            throw new FatalTaskException(e, this);
        }
        finally {
            callClose();
        }
    }

    private void callClose() throws FatalTaskException {
        for (Task task : getTasks()) {
            try {
                task.close();
            }
            catch (TaskException e) {
                log.error("Task {}: failure while closing. {}", getTaskName(), e.getMessage());
            }
            catch (TaskCycleException e) {
                log.error("Task {}: Cycle failure while closing. {}", getTaskName(), e.getMessage());
            }
        }
    }

    private void executeSteps(JointMap joint) throws FatalTaskException {
        log.info("Executing taskSteps for: {}", joint.getDataset().getStoreId());

        try {
            for (Task taskStep : getTasks()) {
                executeStep(joint, taskStep);
                if (joint.isCycleAbborted()) {
                    log.info("Cycle abborted by " + taskStep.getTaskName());
                    break;
                }
            }
        }
        catch (TaskCycleException e) {
            log.error("Could not process all steps");
        }
    }

    private void executeStep(JointMap joint, Task taskStep) throws FatalTaskException {
        try {
            taskStep.run(joint);
        }
        catch (TaskException e) {
            joint.setFitForSave(false);
            log.error("Warnings from cycle, Cycle continues");
        }
    }

    static class AllPassDatasetFilter implements DmoFilter<Dataset> {
        @Override
        public boolean accept(Dataset dataset) {
            return true;
        }

        @Override
        public String toString() {
            return this.getClass().getName();
        }
    }

    public void printFilters() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nUsing Filters[").append(datasetFilters.length).append("]:\n{");
        for (DmoFilter<Dataset> filter : datasetFilters) {
            sb.append("\n\t").append(filter.toString()).append(", ");
        }
        int start = sb.lastIndexOf(", ");
        sb.delete(start, start + 2);
        sb.append("\n}\n");

        log.info("Task: {}, {}", getTaskName(), sb.toString());
    }
}
