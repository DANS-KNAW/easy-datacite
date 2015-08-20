package nl.knaw.dans.easy.tools.task;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.Task;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;

public abstract class AbstractTaskController extends AbstractTask {

    protected static Logger log = LoggerFactory.getLogger(InputTaskController.class);
    private List<Task> tasks = new ArrayList<Task>();

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    protected void closeTasks() throws FatalTaskException {
        for (Task task : getTasks()) {
            try {
                task.close();
            }
            catch (TaskException e) {
                log.error("Task {}: failure while closing", getTaskName());
            }
            catch (TaskCycleException e) {
                log.error("Task {}: Cycle failure while closing", getTaskName());
            }
        }
    }

    protected void executeSteps(JointMap joint) throws FatalTaskException {
        try {
            log.info("--- Starting steps for {}", joint);
            for (Task taskStep : getTasks()) {
                executeStep(joint, taskStep);
                if (joint.isCycleAbborted()) {
                    log.info("Cycle for {} abborted by {}", joint, taskStep.getTaskName());
                    break;
                }
            }
            log.info("Completed steps for {}", joint);
        }
        catch (TaskCycleException e) {
            log.error("Could not process all steps for {}", joint);
        }
    }

    private void executeStep(JointMap joint, Task taskStep) throws FatalTaskException {
        try {
            log.info("executing step " + taskStep.getTaskName());
            taskStep.run(joint);
        }
        catch (TaskException e) {
            joint.setFitForSave(false);
            log.error("Warnings from cycle, Cycle continues");
        }
    }
}
