package nl.knaw.dans.easy.ebiu.exceptions;

import nl.knaw.dans.easy.ebiu.Task;

/**
 * Indicates an exception has occurred while executing a task. <b> A task cycle controller is expected to handle this exception; </b> the rest of the cycle can
 * continue.
 */
public class TaskException extends FatalTaskException {

    private static final long serialVersionUID = 533155907188007443L;

    public TaskException(Task thrower) {
        super(thrower);
    }

    public TaskException(String msg, Task thrower) {
        super(msg, thrower);
    }

    public TaskException(Throwable cause, Task thrower) {
        super(cause, thrower);
    }

    public TaskException(String msg, Throwable cause, Task thrower) {
        super(msg, cause, thrower);
    }

}
