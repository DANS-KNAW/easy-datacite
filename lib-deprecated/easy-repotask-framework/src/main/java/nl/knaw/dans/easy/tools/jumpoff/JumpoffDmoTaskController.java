package nl.knaw.dans.easy.tools.jumpoff;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.Task;
import nl.knaw.dans.easy.tools.dmo.DmoFilter;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;

public class JumpoffDmoTaskController extends AbstractTask {

    private final DmoFilter<JumpoffDmo> jumpoffDmoFilter;

    private List<Task> tasks = new ArrayList<Task>();

    private String currentStoreId;
    private String reportLocation;
    private boolean reportDetails;

    private boolean creatingDetailReports;

    public JumpoffDmoTaskController() {
        this(new AllPassJumpoffFilter());
    }

    public JumpoffDmoTaskController(DmoFilter<JumpoffDmo> jumpoffDmoFilter) {
        this.jumpoffDmoFilter = jumpoffDmoFilter;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public void onEvent(Event event) {
        if (creatingDetailReports && reportDetails && currentStoreId != null) {
            event.setDetails("details", currentStoreId + ".txt", getReportLocation());
        }
    }

    private String getReportLocation() {
        if (reportLocation == null) {
            reportLocation = RL.getReportLocation().getAbsolutePath();
        }
        return reportLocation;
    }

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {

        JumpoffDmoIterator jiter = new JumpoffDmoIterator(jumpoffDmoFilter);
        try {
            while (jiter.hasNext()) {
                joint.clearCycleState();
                joint.setJumpoffDmo(jiter.next());
                executeSteps(joint);
            }
        }
        catch (RepositoryException e) {
            throw new FatalTaskException(e, this);
        }

    }

    private void executeSteps(JointMap joint) throws FatalTaskException {
        currentStoreId = joint.getJumpoffDmo().getStoreId();
        RL.info(new Event(getTaskName(), "Executing taskSteps", currentStoreId));

        reportDetails = true;
        try {
            for (Task taskStep : getTasks()) {
                executeStep(joint, taskStep);
            }
        }
        catch (TaskCycleException e) {
            RL.error(new Event(getTaskName(), e, "Could not process all steps", currentStoreId));
        }
        finally {
            reportDetails = false;
        }
    }

    private void executeStep(JointMap joint, Task taskStep) throws FatalTaskException {
        try {
            taskStep.run(joint);
        }
        catch (TaskException e) {
            joint.setFitForSave(false);
            RL.error(new Event(RL.GLOBAL, e, "Warnings from cycle", currentStoreId, "Cycle continues"));
        }
    }

    static class AllPassJumpoffFilter implements DmoFilter<JumpoffDmo> {

        @Override
        public boolean accept(JumpoffDmo jumpoffDmo) {
            return true;
        }

    }
}
