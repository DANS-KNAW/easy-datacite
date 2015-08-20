package nl.knaw.dans.easy.tools;

import nl.knaw.dans.common.lang.log.Reporter;

public class TaskRunnerReporter extends Reporter {

    private final TaskRunner taskRunner;

    public TaskRunnerReporter(TaskRunner taskRunner) {
        this.taskRunner = taskRunner;
    }
}
