package nl.knaw.dans.easy.ldap;

import java.util.List;

import javax.naming.directory.Attributes;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.ldap.ds.LdapClient;
import nl.knaw.dans.common.ldap.repo.AbstractLdapUserRepo;
import nl.knaw.dans.common.ldap.repo.LdapMapper;
import nl.knaw.dans.common.ldap.repo.LdapMappingException;
import nl.knaw.dans.easy.data.userrepo.EasyUserRepo;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a {@link EasyUserRepo} with an {@link LdapClient}.
 * 
 * @author ecco Feb 10, 2009
 */
public class EasyLdapUserRepo extends AbstractLdapUserRepo<EasyUser> implements EasyUserRepo
{

    private static Logger logger = LoggerFactory.getLogger(EasyLdapUserRepo.class);

    /**
     * Construct a new EasyLdapUserRepo.
     * 
     * @param client
     *        the LdapClient this UserRepo talks to
     * @param context
     *        the context where users are kept on the client, i.e.
     *        "ou=users,ou=easy,dc=dans,dc=knaw,dc=nl"
     */
    public EasyLdapUserRepo(LdapClient client, String context)
    {
        super(client, context, new LdapMapper<EasyUser>(EasyUserImpl.class));
    }

    @Override
    protected EasyUser unmarshal(Attributes attrs) throws LdapMappingException
    {
        return getLdapMapper().unmarshal(new EasyUserImpl(), attrs);
    }

    /**
     * {@inheritDoc}
     */
    public List<EasyUser> findByRole(Role role) throws RepositoryException
    {
        String filter = "(&(objectClass=" + getObjectClassName() + ")(easyRoles=" + role.toString() + "))";
        List<EasyUser> users = search(filter);
        if (logger.isDebugEnabled())
        {
            logger.debug("Find by role " + role + ", found " + users.size() + " users.");
        }
        return users;
    }
}
