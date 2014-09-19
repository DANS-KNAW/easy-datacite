package nl.knaw.dans.common.lang.repo.exception;

import nl.knaw.dans.common.lang.RepositoryException;

public class DmoStoreEventListenerException extends RepositoryException {
    private static final long serialVersionUID = -7209405146120011776L;

    public DmoStoreEventListenerException() {}

    public DmoStoreEventListenerException(String message) {
        super(message);
    }

    public DmoStoreEventListenerException(Throwable cause) {
        super(cause);
    }

    public DmoStoreEventListenerException(String message, Throwable cause) {
        super(message, cause);
    }

}
