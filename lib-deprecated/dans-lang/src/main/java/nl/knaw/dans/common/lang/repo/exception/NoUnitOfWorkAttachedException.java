package nl.knaw.dans.common.lang.repo.exception;

import nl.knaw.dans.common.lang.RepositoryException;

public class NoUnitOfWorkAttachedException extends RepositoryException {
    private static final long serialVersionUID = -4705390811278984089L;

    public NoUnitOfWorkAttachedException() {
        super();
    }

    public NoUnitOfWorkAttachedException(final String message) {
        super(message);
    }

    public NoUnitOfWorkAttachedException(final Throwable cause) {
        super(cause);
    }

    public NoUnitOfWorkAttachedException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
