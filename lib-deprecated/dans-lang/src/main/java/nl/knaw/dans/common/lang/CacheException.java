package nl.knaw.dans.common.lang;

/**
 * Wrapper class for exceptions that take place during sustaining, updating, or clearing a cache.
 * 
 * @author ecco Apr 30, 2009
 */
public class CacheException extends Exception
{

    private static final long serialVersionUID = -6007198469837665263L;

    // ecco (Sep 29, 2009): CHECKSTYLE: OFF

    public CacheException()
    {
    }

    public CacheException(final String message)
    {
        super(message);
    }

    public CacheException(final Throwable cause)
    {
        super(cause);
    }

    public CacheException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

}
