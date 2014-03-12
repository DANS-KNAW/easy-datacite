package nl.knaw.dans.pf.language.emd.exceptions;

/**
 * Indicates that a Term does not exist.
 *
 * @author ecco
 */
public class NoSuchTermException extends RuntimeException
{

    private static final long serialVersionUID = -432326920395537600L;

    // ecco: CHECKSTYLE: OFF

    public NoSuchTermException()
    {
        super();
    }

    public NoSuchTermException(final String message)
    {
        super(message);
    }

    public NoSuchTermException(final Throwable cause)
    {
        super(cause);
    }

    public NoSuchTermException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

}
