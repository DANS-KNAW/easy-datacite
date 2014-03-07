package nl.knaw.dans.common.lang.repo.exception;

/**
 * Signals the failing retrieval of an object that is not in store.
 * 
 * @author ecco Sep 24, 2009
 */
public class ObjectNotInStoreException extends RemoteException
{

    private static final long serialVersionUID = -1248056836633940446L;

    // CHECKSTYLE: OFF
    public ObjectNotInStoreException()
    {
    }

    public ObjectNotInStoreException(final String message)
    {
        super(message);
    }

    public ObjectNotInStoreException(final Throwable cause)
    {
        super(cause);
    }

    public ObjectNotInStoreException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

}
