package nl.knaw.dans.common.ldap.repo;

/**
 * Thrown when a required attribute is null or blank when marshaling an object.
 * 
 * @author ecco Feb 16, 2009
 */
public class MissingAttributeException extends LdapMappingException
{

    /**
     * 
     */
    private static final long serialVersionUID = 9158334870250287440L;

    public MissingAttributeException()
    {
    }

    public MissingAttributeException(String message)
    {
        super(message);
    }

    public MissingAttributeException(Throwable cause)
    {
        super(cause);
    }

    public MissingAttributeException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
