package nl.knaw.dans.pf.language.emd.types;

/**
 * Signifies an attempt to modify a language token with an invalid argument.
 *
 * @author ecco
 */
public class InvalidLanguageTokenException extends EasyMetadataException
{

    private static final long serialVersionUID = 6193499442701332049L;

    // ecco: CHECKSTYLE: OFF

    InvalidLanguageTokenException()
    {
        super();
    }

    InvalidLanguageTokenException(final String message)
    {
        super(message);
    }

    InvalidLanguageTokenException(final Throwable cause)
    {
        super(cause);
    }

    InvalidLanguageTokenException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

}
