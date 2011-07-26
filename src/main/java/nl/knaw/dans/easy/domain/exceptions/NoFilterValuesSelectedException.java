package nl.knaw.dans.easy.domain.exceptions;

public class NoFilterValuesSelectedException extends DomainException
{
	private static final long serialVersionUID = -7823376924464392254L;

	public NoFilterValuesSelectedException()
    {
    }

    public NoFilterValuesSelectedException(String message)
    {
        super(message);
    }

    public NoFilterValuesSelectedException(Throwable cause)
    {
        super(cause);
    }

    public NoFilterValuesSelectedException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
