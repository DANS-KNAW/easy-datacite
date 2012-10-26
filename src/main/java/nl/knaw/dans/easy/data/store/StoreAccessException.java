package nl.knaw.dans.easy.data.store;

import nl.knaw.dans.common.lang.RepositoryException;

/**
 * Signals an exception while manipulating objects in a store.
 * 
 * deprecated use nl.knaw.dans.common.lang.repo.exception.RepositoryException
 *
 * @author ecco
 * 
 */
public class StoreAccessException extends RepositoryException
{
    private static final long serialVersionUID = 2014301566495934433L;

    public StoreAccessException()
    {
        super();
    }

    public StoreAccessException(String message)
    {
        super(message);
    }

    public StoreAccessException(Throwable cause)
    {
        super(cause);
    }

    public StoreAccessException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
