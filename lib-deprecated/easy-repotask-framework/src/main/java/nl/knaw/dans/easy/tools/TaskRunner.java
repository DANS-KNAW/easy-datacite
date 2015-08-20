package nl.knaw.dans.easy.tools;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.util.Printer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskRunner {

    private static final Logger log = LoggerFactory.getLogger(TaskRunner.class);

    private List<Task> tasks = new ArrayList<Task>();

    private int executedTaskCount;

    public TaskRunner() {

    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    private void resetTotalCounts() {
        executedTaskCount = 0;
    }

    public void execute(JointMap joint) {
        if (tasks == null || tasks.isEmpty()) {
            log.info("No tasks defined in application context.");
            return;
        }

        resetTotalCounts();

        for (Task task : tasks) {
            if (task.needsAuthentication()) {
                Application.authenticate();
                break;
            }
        }

        try {
            for (Task task : tasks) {
                log.info(Printer.format("Executing task " + task.getClass().getName()));

                task.run(joint);
                executedTaskCount++;

                log.info("Finished task " + task.getClass().getName());
            }
            log.info(Printer.format("Finished all tasks with"));

        }
        catch (FatalTaskException e) {
            log.error("Fatal exception while executing " + e.getThrower().getTaskName(), e);
            log.error("Aborting further tasks. Finished " + executedTaskCount + " task(s)");

        }
        catch (Throwable t) {
            log.error("Exception while running tasks. Aborting further tasks. Finished " + executedTaskCount + " task(s)", t);
        }
    }
}
