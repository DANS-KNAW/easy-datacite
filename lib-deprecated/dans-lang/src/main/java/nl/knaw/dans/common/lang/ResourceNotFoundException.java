package nl.knaw.dans.common.lang;

/**
 * Signals that a resource was not found.
 * 
 * @author ecco Sep 27, 2009
 */
public class ResourceNotFoundException extends Exception {

    private static final long serialVersionUID = 6846502885709367426L;

    // ecco (Sep 29, 2009): CHECKSTYLE: OFF

    public ResourceNotFoundException() {}

    public ResourceNotFoundException(final String message) {
        super(message);
    }

    public ResourceNotFoundException(final Throwable cause) {
        super(cause);
    }

    public ResourceNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
