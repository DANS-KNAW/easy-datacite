package nl.knaw.dans.easy.domain.exceptions;

/**
 * Signals exceptions in the deserialization of objects.
 * 
 * @author ecco Aug 25, 2009
 */
public class DeserializationException extends RuntimeException {

    private static final long serialVersionUID = 973280980711823632L;

    public DeserializationException() {}

    public DeserializationException(String message) {
        super(message);
    }

    public DeserializationException(Throwable cause) {
        super(cause);
    }

    public DeserializationException(String message, Throwable cause) {
        super(message, cause);
    }

}
