package nl.knaw.dans.easy.tools.exceptions;

/**
 * Indicates a fatal runtime exception. The application should stop.
 * 
 * @author ecco
 */
public class FatalRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -9012038558486724543L;

    public FatalRuntimeException() {}

    public FatalRuntimeException(String msg) {
        super(msg);
    }

    public FatalRuntimeException(Throwable e) {
        super(e);
    }

    public FatalRuntimeException(String msg, Throwable e) {
        super(msg, e);
    }

}
