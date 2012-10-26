package nl.knaw.dans.easy.domain.exceptions;

/**
 * Indicates that an object is referenced by an id that is not unique.
 * 
 * @author ecco Feb 3, 2009
 */
public class IdNotUniqueException extends DataAccessException
{

    /**
     * 
     */
    private static final long serialVersionUID = -1439868368071291125L;

    public IdNotUniqueException()
    {
    }

    public IdNotUniqueException(String message)
    {
        super(message);
    }

    public IdNotUniqueException(Throwable cause)
    {
        super(cause);
    }

    public IdNotUniqueException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
