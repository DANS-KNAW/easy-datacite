package nl.knaw.dans.easy.domain.exceptions;

/**
 * Thrown when an operation cannot be performed, because the user is an anonymous user.
 * 
 * @author lobo
 */
public class AnonymousUserException extends DomainRuntimeException
{
    /**
     * 
     */
    private static final long serialVersionUID = -1806127483115916003L;

    public AnonymousUserException()
    {
    }

    public AnonymousUserException(String message)
    {
        super(message);
    }

    public AnonymousUserException(Throwable cause)
    {
        super(cause);
    }

    public AnonymousUserException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
