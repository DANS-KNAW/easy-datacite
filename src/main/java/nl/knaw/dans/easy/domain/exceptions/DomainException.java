package nl.knaw.dans.easy.domain.exceptions;

public class DomainException extends Exception
{
    private static final long serialVersionUID = 9117175401407461288L;

    public DomainException()
    {
    }

    public DomainException(String message)
    {
        super(message);
    }

    public DomainException(Throwable cause)
    {
        super(cause);
    }

    public DomainException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
