package nl.knaw.dans.easy.tools.exceptions;

import nl.knaw.dans.easy.tools.Task;

/**
 * Indicates a fatal exception has occurred while executing one of the tasks. The application should stop after throwing such an exception.
 * 
 * @author ecco
 */
public class FatalTaskException extends Exception {

    private final Task thrower;

    private static final long serialVersionUID = -1137682956431751415L;

    public FatalTaskException(Task thrower) {
        super();
        this.thrower = thrower;
    }

    public FatalTaskException(String msg, Task thrower) {
        super(msg);
        this.thrower = thrower;
    }

    public FatalTaskException(Throwable e, Task thrower) {
        super(e);
        this.thrower = thrower;
    }

    public FatalTaskException(String msg, Throwable e, Task thrower) {
        super(msg, e);
        this.thrower = thrower;
    }

    public Task getThrower() {
        return thrower;
    }

}
