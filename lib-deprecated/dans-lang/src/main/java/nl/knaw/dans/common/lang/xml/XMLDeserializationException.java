package nl.knaw.dans.common.lang.xml;

/**
 * Signals an exception while deserializing an objet from xml.
 * 
 * @author ecco Sep 27, 2009
 */
public class XMLDeserializationException extends XMLException
{

    private static final long serialVersionUID = 8679533276783819362L;

    // CHECKSTYLE: OFF

    public XMLDeserializationException()
    {
    }

    public XMLDeserializationException(String message)
    {
        super(message);
    }

    public XMLDeserializationException(Throwable cause)
    {
        super(cause);
    }

    public XMLDeserializationException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
