package nl.knaw.dans.common.ldap.repo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.ldap.UserRepo;
import nl.knaw.dans.common.lang.user.User;
import nl.knaw.dans.common.ldap.ds.LdapClient;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a {@link UserRepo} with an {@link LdapClient}.
 * 
 * @author ecco Feb 10, 2009
 */
public abstract class AbstractLdapUserRepo<T extends User> extends AbstractGenericRepo<T> implements UserRepo<T>
{

    public static final String RDN = "uid";

    private static Logger logger = LoggerFactory.getLogger(AbstractLdapUserRepo.class);

    /**
     * Construct a new LdapUserRepo.
     * 
     * @param client
     *        the LdapClient this UserRepo talks to
     * @param context
     *        the context where users are kept on the client, i.e.
     *        "ou=users,ou=easy,dc=dans,dc=knaw,dc=nl"
     * @param ldapMapper
     *        the mapper to use;
     */
    public AbstractLdapUserRepo(LdapClient client, String context, LdapMapper<T> ldapMapper)
    {
        super(client, context, RDN, ldapMapper);
    }

    /**
     * {@inheritDoc}
     */
    public boolean authenticate(String uid, String userpass) throws RepositoryException
    {
        if (StringUtils.isBlank(uid) || StringUtils.isBlank(userpass))
        {
            logger.debug("Insufficient data for authentication.");
            return false;
        }
        String filter = "(&(objectClass=" + getObjectClassName() + ")(uid=" + uid + "))";

        boolean authenticated;
        try
        {
            authenticated = getClient().authenticate(userpass, getContext(), filter, getObjectClassesArray());
            logger.debug("User '" + uid + "' is authenticated=" + authenticated);

        }
        catch (NamingException e)
        {
            throw new RepositoryException(e);
        }
        return authenticated;
    }

    /**
     * Note that {@link User.getPassword()} will not give the password from the repository after
     * 'unmarshalling'. The user repository must be queried for this because the password is never
     * retrieved from the repository and the User object does not contain it.
     */
    public boolean isPasswordStored(String userId) throws RepositoryException
    {
        if (StringUtils.isBlank(userId))
        {
            logger.debug("Insufficient data for getting user info.");
            throw new IllegalArgumentException();
        }
        String filter = "(&(objectClass=" + getObjectClassName() + ")(uid=" + userId + "))";

        final String PASSWD_ATTR_NAME = "userPassword";
        boolean passwordStored = false;
        SearchControls ctls = new SearchControls();
        ctls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        ctls.setCountLimit(1);
        ctls.setReturningAttributes(new String[] {"uid", PASSWD_ATTR_NAME});

        try
        {
            NamingEnumeration<SearchResult> resultEnum = getClient().search(getContext(), filter, ctls);
            while (resultEnum.hasMoreElements())
            {
                SearchResult result = resultEnum.next();
                Attributes attrs = result.getAttributes();
                if (attrs.get(PASSWD_ATTR_NAME) != null)
                    passwordStored = true;
            }
        }
        catch (NamingException e)
        {
            throw new RepositoryException(e);
        }

        return passwordStored;
    }

    public List<T> find(T example)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public List<T> findByEmail(String emailAddress) throws RepositoryException
    {
        String filter = "(&(objectClass=" + getObjectClassName() + ")(mail=" + emailAddress + "))";
        List<T> users = search(filter);
        if (logger.isDebugEnabled())
        {
            logger.debug("Find by email " + emailAddress + ", found " + users.size() + " users.");
        }
        return users;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> findByCommonNameStub(String stub, long maxCount) throws RepositoryException
    {
        Map<String, String> idNameMap = new LinkedHashMap<String, String>();
        String text = censorHumanoidSearchPhrase(stub);
        String filter = "(&(objectClass=" + getObjectClassName() + ")(cn=" + text + "*))";
        SearchControls ctls = new SearchControls();
        ctls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        ctls.setCountLimit(maxCount);
        ctls.setReturningAttributes(new String[] {"cn", "uid"});

        try
        {
            NamingEnumeration<SearchResult> resultEnum = getClient().search(getContext(), filter, ctls);
            while (resultEnum.hasMoreElements())
            {
                SearchResult result = resultEnum.next();
                Attributes attrs = result.getAttributes();
                idNameMap.put((String) attrs.get("uid").get(), (String) attrs.get("cn").get());
            }
        }
        catch (NamingException e)
        {
            throw new RepositoryException(e);
        }
        return idNameMap;
    }

}
