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
public class StoreException extends RepositoryException
{
    private static final long serialVersionUID = 2014301566495934433L;


    public StoreException()
    {
        super();
    }

    public StoreException(String message)
    {
        super(message);
    }

    public StoreException(Throwable cause)
    {
        super(cause);
    }

    public StoreException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
