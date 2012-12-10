package nl.knaw.dans.l.xml.exc;



/**
 * Signals an exception while serializing an object to xml.
 * 
 * @author ecco Sep 27, 2009
 */
public class XMLSerializationException extends XMLException
{

    private static final long serialVersionUID = 1286634981933080257L;
    
    // CHECKSTYLE: OFF 

    public XMLSerializationException()
    {
    }

    public XMLSerializationException(String message)
    {
        super(message);
    }

    public XMLSerializationException(Throwable cause)
    {
        super(cause);
    }

    public XMLSerializationException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
