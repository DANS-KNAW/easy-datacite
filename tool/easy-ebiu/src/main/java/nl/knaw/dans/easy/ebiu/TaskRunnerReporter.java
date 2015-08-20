package nl.knaw.dans.easy.ebiu;

import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.Reporter;

public class TaskRunnerReporter extends Reporter {

    private final TaskRunner taskRunner;

    public TaskRunnerReporter(TaskRunner taskRunner) {
        this.taskRunner = taskRunner;
    }

    @Override
    public void info(Event event) {
        taskRunner.onInfo(event);
        super.info(event);
    }

    @Override
    public void warn(Event event) {
        taskRunner.onWarn(event);
        super.warn(event);
    }

    @Override
    public void error(Event event) {
        taskRunner.onError(event);
        super.error(event);
    }

}
