package nl.knaw.dans.easy.ebiu.exceptions;

import nl.knaw.dans.easy.ebiu.Task;

/**
 * Indicates an error in the currently running task cycle. Other task cycles can continue.
 */
public class TaskCycleException extends FatalTaskException {

    private static final long serialVersionUID = 1923440126647070403L;

    public TaskCycleException(Task thrower) {
        super(thrower);
    }

    public TaskCycleException(String msg, Task thrower) {
        super(msg, thrower);
    }

    public TaskCycleException(Throwable e, Task thrower) {
        super(e, thrower);
    }

    public TaskCycleException(String msg, Throwable e, Task thrower) {
        super(msg, e, thrower);
    }

}
