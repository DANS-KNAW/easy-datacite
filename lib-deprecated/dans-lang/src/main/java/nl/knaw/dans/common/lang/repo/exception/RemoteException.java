package nl.knaw.dans.common.lang.repo.exception;

import nl.knaw.dans.common.lang.RepositoryException;

/**
 * Signals that the service, store, database or repository encountered an exception while fulfilling a
 * request.
 * 
 * @author ecco Sep 24, 2009
 */
public class RemoteException extends RepositoryException
{

    private static final long serialVersionUID = -7795802327773517335L;

    // CHECKSTYLE: OFF
    public RemoteException()
    {
    }

    public RemoteException(final String message)
    {
        super(message);
    }

    public RemoteException(final Throwable cause)
    {
        super(cause);
    }

    public RemoteException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

}
