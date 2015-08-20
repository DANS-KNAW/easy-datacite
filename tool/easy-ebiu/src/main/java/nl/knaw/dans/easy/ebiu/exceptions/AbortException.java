package nl.knaw.dans.easy.ebiu.exceptions;

/**
 * Indicates user aborts execution.
 */
public class AbortException extends RuntimeException {

    private static final long serialVersionUID = -1795980479717783998L;

    public AbortException() {}

    public AbortException(String msg) {
        super(msg);
    }

    public AbortException(Throwable cause) {
        super(cause);
    }

    public AbortException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
