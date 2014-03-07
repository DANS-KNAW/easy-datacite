package nl.knaw.dans.common.lang.search.exceptions;

public class PrimaryKeyMissingException extends SearchBeanException
{
    private static final long serialVersionUID = 4094555245499599266L;

    public PrimaryKeyMissingException()
    {
    }

    public PrimaryKeyMissingException(String message)
    {
        super(message);
    }

    public PrimaryKeyMissingException(Throwable cause)
    {
        super(cause);
    }

    public PrimaryKeyMissingException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
