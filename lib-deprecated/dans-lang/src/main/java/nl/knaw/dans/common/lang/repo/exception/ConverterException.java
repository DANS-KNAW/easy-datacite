package nl.knaw.dans.common.lang.repo.exception;

import nl.knaw.dans.common.lang.RepositoryException;

public class ConverterException extends RepositoryException
{

    private static final long serialVersionUID = -3806792540651006951L;

    public ConverterException()
    {
    }

    public ConverterException(String message)
    {
        super(message);
    }

    public ConverterException(Throwable cause)
    {
        super(cause);
    }

    public ConverterException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
