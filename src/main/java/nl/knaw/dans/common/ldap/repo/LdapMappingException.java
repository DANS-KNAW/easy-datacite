package nl.knaw.dans.common.ldap.repo;

//import nl.knaw.dans.easy.domain.exceptions.DataAccessException;
import nl.knaw.dans.common.lang.RepositoryException;

/**
 * Signals an exception while marshaling/unmarshaling an object to/from attributes.
 *
 * @author ecco Feb 16, 2009
 */
public class LdapMappingException extends RepositoryException
{

    /**
     *
     */
    private static final long serialVersionUID = -4092636710840744437L;

    public LdapMappingException()
    {
    }

    public LdapMappingException(String message)
    {
        super(message);
    }

    public LdapMappingException(Throwable cause)
    {
        super(cause);
    }

    public LdapMappingException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
