package nl.knaw.dans.common.lang.repo.exception;

/**
 * Signals that an object was about to be newly stored with an identity similar to the identity of an object already stored.
 * 
 * @author ecco Sep 24, 2009
 */
public class ObjectExistsException extends RemoteException {

    private static final long serialVersionUID = 7930889382613126618L;

    // CHECKSTYLE: OFF
    public ObjectExistsException() {}

    public ObjectExistsException(final String message) {
        super(message);
    }

    public ObjectExistsException(final Throwable cause) {
        super(cause);
    }

    public ObjectExistsException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
