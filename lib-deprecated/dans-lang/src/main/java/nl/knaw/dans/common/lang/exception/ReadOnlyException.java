package nl.knaw.dans.common.lang.exception;

/**
 * Indicates an attempt has been made to modify a value that is read only.
 */
public class ReadOnlyException extends RuntimeException
{

    private static final long serialVersionUID = -1400434570706805802L;

    public ReadOnlyException()
    {

    }

    public ReadOnlyException(String msg)
    {
        super(msg);
    }

    public ReadOnlyException(Throwable cause)
    {
        super(cause);
    }

    public ReadOnlyException(String msg, Throwable cause)
    {
        super(msg, cause);
    }

}
