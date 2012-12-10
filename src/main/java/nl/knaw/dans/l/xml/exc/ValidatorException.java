package nl.knaw.dans.l.xml.exc;


/**
 * Signals an exception during validation.
 *
 * @author ecco
 *
 */
public class ValidatorException extends XMLException
{
    private static final long serialVersionUID = -122877223054951114L;
    
    // ecco (Sep 29, 2009): CHECKSTYLE: OFF 

    public ValidatorException()
    {
        super();
    }

    public ValidatorException(String message)
    {
        super(message);
    }

    public ValidatorException(Throwable cause)
    {
        super(cause);
    }

    public ValidatorException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
