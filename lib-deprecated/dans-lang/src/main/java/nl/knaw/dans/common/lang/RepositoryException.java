package nl.knaw.dans.common.lang;

/**
 * Signals an exception while opening, using or closing a repository.
 * 
 * @author ecco Sep 4, 2009
 */
public class RepositoryException extends Exception {

    private static final long serialVersionUID = 7465810880323799020L;

    // CHECKSTYLE: OFF

    public RepositoryException() {}

    public RepositoryException(final String message) {
        super(message);
    }

    public RepositoryException(final Throwable cause) {
        super(cause);
    }

    public RepositoryException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
