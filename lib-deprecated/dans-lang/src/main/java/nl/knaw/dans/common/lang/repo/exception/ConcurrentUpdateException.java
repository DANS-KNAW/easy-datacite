package nl.knaw.dans.common.lang.repo.exception;

import nl.knaw.dans.common.lang.RepositoryException;

/**
 * Signals an update that threatens to overwrite an earlier update.
 * 
 * @author ecco Sep 24, 2009
 */
public class ConcurrentUpdateException extends RepositoryException
{

    private static final long serialVersionUID = 596910792885449651L;

    // CHECKSTYLE: OFF

    public ConcurrentUpdateException()
    {
    }

    public ConcurrentUpdateException(final String message)
    {
        super(message);
    }

    public ConcurrentUpdateException(final Throwable cause)
    {
        super(cause);
    }

    public ConcurrentUpdateException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

}
