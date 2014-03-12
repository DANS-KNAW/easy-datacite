package nl.knaw.dans.pf.language.emd.types;

/**
 * Signifies an attempt to modify a date with an invalid string.
 *
 * @author ecco
 */
public class InvalidDateStringException extends EasyMetadataException
{

    private static final long serialVersionUID = 5670941485794338819L;

    // ecco: CHECKSTYLE: OFF

    public InvalidDateStringException()
    {
        super();
    }

    public InvalidDateStringException(final String message)
    {
        super(message);
    }

    public InvalidDateStringException(final Throwable cause)
    {
        super(cause);
    }

    public InvalidDateStringException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

}
