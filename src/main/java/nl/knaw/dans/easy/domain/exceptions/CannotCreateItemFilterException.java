package nl.knaw.dans.easy.domain.exceptions;

public class CannotCreateItemFilterException extends DomainException
{
	private static final long serialVersionUID = 6957679120861150718L;

	public CannotCreateItemFilterException()
	{
	}

	public CannotCreateItemFilterException(String message)
	{
		super(message);
	}

	public CannotCreateItemFilterException(Throwable cause)
	{
		super(cause);
	}

	public CannotCreateItemFilterException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
