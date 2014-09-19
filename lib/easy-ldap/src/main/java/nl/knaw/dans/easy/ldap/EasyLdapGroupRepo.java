package nl.knaw.dans.easy.ldap;

import java.util.List;

import javax.naming.directory.Attributes;

import nl.knaw.dans.common.ldap.ds.LdapClient;
import nl.knaw.dans.common.ldap.repo.AbstractGenericRepo;
import nl.knaw.dans.common.ldap.repo.LdapMapper;
import nl.knaw.dans.common.ldap.repo.LdapMappingException;
import nl.knaw.dans.easy.data.userrepo.GroupRepo;
import nl.knaw.dans.easy.domain.exceptions.DataAccessException;
import nl.knaw.dans.easy.domain.model.user.Group;
import nl.knaw.dans.easy.domain.user.GroupImpl;

/**
 * Implements a {@link GroupRepo} with an {@link LdapClient}.
 * 
 * @author ecco Nov 20, 2009
 */
public class EasyLdapGroupRepo extends AbstractGenericRepo<Group> implements GroupRepo {

    public static final String RDN = "ou";

    /**
     * Construct a new LdapGroupRepo.
     * 
     * @param client
     *        the LdapClient this UserRepo talks to
     * @param context
     *        the context where groups are kept on the client, i.e. "ou=groups,ou=easy,dc=dans,dc=knaw,dc=nl"
     */
    public EasyLdapGroupRepo(LdapClient client, String context) {
        super(client, context, RDN, new LdapMapper<Group>(GroupImpl.class));
    }

    @Override
    protected Group unmarshal(Attributes attrs) throws LdapMappingException {
        return getLdapMapper().unmarshal(new GroupImpl(), attrs);
    }

}
