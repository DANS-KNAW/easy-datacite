package nl.knaw.dans.pf.language.xml.exc;

/**
 * General exception signaling an exception while handling xml.
 * 
 * @author ecco Sep 27, 2009
 */
public class XMLException extends Exception
{

    private static final long serialVersionUID = 8380352169698292747L;

    // CHECKSTYLE: OFF

    public XMLException()
    {
    }

    public XMLException(String message)
    {
        super(message);
    }

    public XMLException(Throwable cause)
    {
        super(cause);
    }

    public XMLException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
