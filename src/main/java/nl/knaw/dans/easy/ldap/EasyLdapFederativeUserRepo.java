package nl.knaw.dans.easy.ldap;

import java.util.List;

import javax.naming.directory.Attributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.ldap.ds.LdapClient;
import nl.knaw.dans.common.ldap.repo.AbstractGenericRepo;
import nl.knaw.dans.common.ldap.repo.LdapMapper;
import nl.knaw.dans.common.ldap.repo.LdapMappingException;
import nl.knaw.dans.easy.data.federation.FederativeUserRepo;
import nl.knaw.dans.easy.domain.federation.FederativeUserIdMap;
import nl.knaw.dans.easy.domain.migration.IdMap;


public class EasyLdapFederativeUserRepo extends AbstractGenericRepo<FederativeUserIdMap> implements FederativeUserRepo
{
    public static final String RDN = "fedUserId";
    
    private static final Logger logger = LoggerFactory.getLogger(EasyLdapFederativeUserRepo.class);
    

    public EasyLdapFederativeUserRepo(LdapClient client, String context)
    {
        super(client, context, RDN, new LdapMapper<FederativeUserIdMap>(FederativeUserIdMap.class));
    }

    @Override
    protected FederativeUserIdMap unmarshal(Attributes attrs) throws LdapMappingException
    {
        return getLdapMapper().unmarshal(new FederativeUserIdMap(), attrs);
    }

    @Override
    public List<FederativeUserIdMap> findByDansUserId(String dansUserId) throws RepositoryException
    {
        String filter = "(&(objectClass=" + getObjectClassName() + ")(dansUserId=" + dansUserId + "))";
        List<FederativeUserIdMap> idMaps = search(filter);
        if (logger.isDebugEnabled())
        {
            logger.debug("Find by dansUserId " + dansUserId + ", found " + idMaps.size() + " idMaps.");
        }
        return idMaps;
    }

}
