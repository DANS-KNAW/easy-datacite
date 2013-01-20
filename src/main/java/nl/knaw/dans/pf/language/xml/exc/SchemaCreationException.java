package nl.knaw.dans.pf.language.xml.exc;

/**
 * Signals an exception in the creation of a {@link javax.xml.validation.Schema}.
 * @author ecco
 *
 */
public class SchemaCreationException extends XMLException
{

    private static final long serialVersionUID = 1927611118541344925L;

    // ecco (Sep 29, 2009): CHECKSTYLE: OFF 

    public SchemaCreationException()
    {
        super();
    }

    public SchemaCreationException(String message)
    {
        super(message);
    }

    public SchemaCreationException(Throwable cause)
    {
        super(cause);
    }

    public SchemaCreationException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
