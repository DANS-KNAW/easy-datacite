package nl.knaw.dans.common.lang.repo.exception;

import nl.knaw.dans.common.lang.RepositoryException;

public class ObjectCreationException extends RepositoryException {

    /**
     * 
     */
    private static final long serialVersionUID = -6200641615592448714L;

    public ObjectCreationException() {}

    public ObjectCreationException(String message) {
        super(message);
    }

    public ObjectCreationException(Throwable cause) {
        super(cause);
    }

    public ObjectCreationException(String message, Throwable cause) {
        super(message, cause);
    }

}
