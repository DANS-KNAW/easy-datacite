package nl.knaw.dans.easy.domain.exceptions;

/**
 * Indicates that an object does not exist or was not found in the repository.
 * @author ecco Feb 2, 2009
 *
 */
public class ObjectNotFoundException extends DomainException
{

    /**
     * 
     */
    private static final long serialVersionUID = 6536593750267448502L;

    public ObjectNotFoundException()
    {
    }

    public ObjectNotFoundException(String message)
    {
        super(message);
    }

    public ObjectNotFoundException(Throwable cause)
    {
        super(cause);
    }

    public ObjectNotFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
