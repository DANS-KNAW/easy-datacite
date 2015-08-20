package nl.knaw.dans.easy.tools.exceptions;

/**
 * Indicates an exception during the execution of a task. <b> A Task is expected to handle the exception by itself; </b> scheduled Task execution can continue.
 * 
 * @author dev
 */
public class TaskExecutionException extends Exception {

    private static final long serialVersionUID = -1028503515624189790L;

    public TaskExecutionException() {

    }

    public TaskExecutionException(String message) {
        super(message);
    }

    public TaskExecutionException(Throwable cause) {
        super(cause);
    }

    public TaskExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

}
