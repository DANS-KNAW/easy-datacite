package nl.knaw.dans.common.lang.repo.exception;

import nl.knaw.dans.common.lang.RepositoryException;

public class NoStoreAttachedException extends RepositoryException {
    private static final long serialVersionUID = 6508464975640063673L;

    public NoStoreAttachedException() {}

    public NoStoreAttachedException(String message) {
        super(message);
    }

    public NoStoreAttachedException(Throwable cause) {
        super(cause);
    }

    public NoStoreAttachedException(String message, Throwable cause) {
        super(message, cause);
    }

}
