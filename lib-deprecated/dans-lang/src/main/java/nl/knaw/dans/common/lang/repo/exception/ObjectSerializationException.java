package nl.knaw.dans.common.lang.repo.exception;

/**
 * Signals an exception while serializing an object.
 * 
 * @author ecco Sep 24, 2009
 */
public class ObjectSerializationException extends ConverterException
{

    private static final long serialVersionUID = -2486810834396468739L;

    // CHECKSTYLE: OFF 
    public ObjectSerializationException()
    {
    }

    public ObjectSerializationException(final String message)
    {
        super(message);
    }

    public ObjectSerializationException(final Throwable cause)
    {
        super(cause);
    }

    public ObjectSerializationException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

}
