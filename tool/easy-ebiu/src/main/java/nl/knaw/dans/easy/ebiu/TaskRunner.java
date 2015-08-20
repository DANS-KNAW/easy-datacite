package nl.knaw.dans.easy.ebiu;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.easy.ebiu.exceptions.FatalTaskException;
import nl.knaw.dans.easy.ebiu.util.Printer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskRunner {

    private static final Logger logger = LoggerFactory.getLogger(TaskRunner.class);

    private List<Task> tasks = new ArrayList<Task>();

    private int executedTaskCount;

    private int totalInfoCount;
    private int totalWarningCount;
    private int totalErrorCount;

    private int currentTaskInfoCount;
    private int currentTaskWarningCount;
    private int currentTaskErrorCount;

    public TaskRunner() {

    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public int getTotalInfoCount() {
        return totalInfoCount;
    }

    public int getTotalWarningCount() {
        return totalWarningCount;
    }

    public int getTotalErrorCount() {
        return totalErrorCount;
    }

    private void resetTotalCounts() {
        totalInfoCount = 0;
        totalWarningCount = 0;
        totalErrorCount = 0;
        executedTaskCount = 0;
    }

    private void resetCurrentTaskCounts() {
        currentTaskInfoCount = 0;
        currentTaskWarningCount = 0;
        currentTaskErrorCount = 0;
    }

    public void execute(JointMap joint) {
        String msg;
        if (tasks == null || tasks.isEmpty()) {
            logger.info("No tasks defined in application context.");
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
                resetCurrentTaskCounts();

                msg = "Executing task " + task.getClass().getName();
                logger.info(Printer.format(msg));
                RL.info(new Event(RL.GLOBAL, msg));

                task.run(joint);
                executedTaskCount++;

                msg = "Finished task " + task.getClass().getName() + " with " + currentTaskErrorCount + " exception(s)" + " and " + currentTaskWarningCount
                        + " warning(s)";
                RL.info(new Event(RL.GLOBAL, msg));
                logger.info(msg);

            }
            msg = "Finished all tasks with " + totalErrorCount + " exception(s)" + " and " + totalWarningCount + " warning(s)";
            RL.info(new Event(RL.GLOBAL, msg));
            logger.info(Printer.format(msg));

        }
        catch (FatalTaskException e) {
            msg = "Fatal exception while executing " + e.getThrower().getTaskName();
            logger.error(msg + ": ", e);
            RL.error(new Event(RL.GLOBAL, e, msg));

            msg = "Aborting further tasks. Finished " + executedTaskCount + " task(s) with " + totalErrorCount + " exception(s)";
            logger.error(msg);
            RL.error(new Event(RL.GLOBAL, e, msg));
        }
        catch (Throwable t) {
            msg = "Exception while running tasks. Aborting further tasks. Finished " + executedTaskCount + " task(s) with " + totalErrorCount + " exception(s)";
            logger.error(msg, t);
            RL.error(new Event(RL.GLOBAL, t, msg));

        }
        finally {

        }
    }

    void onInfo(Event event) {
        if (!RL.GLOBAL.equals(event.getEventName())) {
            currentTaskInfoCount++;
            totalInfoCount++;
        }
        onEvent(event);
    }

    void onWarn(Event event) {
        if (!RL.GLOBAL.equals(event.getEventName())) {
            currentTaskWarningCount++;
            totalWarningCount++;
        }
        onEvent(event);
    }

    void onError(Event event) {
        if (!RL.GLOBAL.equals(event.getEventName())) {
            currentTaskErrorCount++;
            totalErrorCount++;
        }
        onEvent(event);
    }

    private void onEvent(Event event) {
        for (Task task : tasks) {
            task.onEvent(event);
        }
    }

}
