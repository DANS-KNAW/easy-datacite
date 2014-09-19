package nl.knaw.dans.common.lang.service.exceptions;

/**
 * Thrown if a fatal business exception occurs.
 * 
 * @author ecco Feb 18, 2009
 */
public class FatalBusinessException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 9161709158087408713L;

    public FatalBusinessException() {}

    public FatalBusinessException(String message) {
        super(message);
    }

    public FatalBusinessException(Throwable cause) {
        super(cause);
    }

    public FatalBusinessException(String message, Throwable cause) {
        super(message, cause);
    }

}
