package nl.knaw.dans.common.ldap.repo;

import javax.naming.directory.Attributes;

import nl.knaw.dans.common.lang.user.User;
import nl.knaw.dans.common.lang.user.UserImpl;
import nl.knaw.dans.common.ldap.ds.LdapClient;

public class DansUserRepo extends AbstractLdapUserRepo<User>
{

    public DansUserRepo(LdapClient client, String context)
    {
        super(client, context, new LdapMapper<User>(UserImpl.class));
    }

    @Override
    protected User unmarshal(Attributes attrs) throws LdapMappingException
    {
        return getLdapMapper().unmarshal(new UserImpl(), attrs);
    }

}
