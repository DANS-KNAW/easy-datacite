package nl.knaw.dans.easy.ldap;

import java.util.List;

import javax.naming.directory.Attributes;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.ldap.ds.LdapClient;
import nl.knaw.dans.common.ldap.repo.AbstractGenericRepo;
import nl.knaw.dans.common.ldap.repo.LdapMapper;
import nl.knaw.dans.common.ldap.repo.LdapMappingException;
import nl.knaw.dans.easy.data.migration.MigrationRepo;
import nl.knaw.dans.easy.domain.migration.IdMap;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EasyLdapMigrationRepo extends AbstractGenericRepo<IdMap> implements MigrationRepo {

    public static final String RDN = "dansStoreId";

    private static final Logger logger = LoggerFactory.getLogger(EasyLdapMigrationRepo.class);

    public EasyLdapMigrationRepo(LdapClient client, String context) {
        super(client, context, RDN, new LdapMapper<IdMap>(IdMap.class));
    }

    @Override
    protected IdMap unmarshal(Attributes attrs) throws LdapMappingException {
        return getLdapMapper().unmarshal(new IdMap(), attrs);
    }

    @Override
    public List<IdMap> findByAipId(String aipId) throws RepositoryException {
        String filter = "(&(objectClass=" + getObjectClassName() + ")(dansPreviousId=" + aipId + "))";
        List<IdMap> idMaps = search(filter);
        if (logger.isDebugEnabled()) {
            logger.debug("Find by aipId " + aipId + ", found " + idMaps.size() + " idMaps.");
        }
        return idMaps;
    }

    @Override
    public List<IdMap> findByPersistentIdentifier(String persistentIdentifier) throws RepositoryException {
        String filter = "(&(objectClass=" + getObjectClassName() + ")(dansPid=" + persistentIdentifier + "))";
        List<IdMap> idMaps = search(filter);
        if (logger.isDebugEnabled()) {
            logger.debug("Find by persistentIdentifier " + persistentIdentifier + ", found " + idMaps.size() + " idMaps.");
        }
        return idMaps;
    }

    @Override
    public IdMap getMostRecentByAipId(String aipId) throws RepositoryException {
        return getMostRecent(findByAipId(aipId));
    }

    @Override
    public IdMap getMostRecentByPersistentIdentifier(String persistentIdentifier) throws RepositoryException {
        return getMostRecent(findByPersistentIdentifier(persistentIdentifier));
    }

    private IdMap getMostRecent(List<IdMap> idMaps) {
        IdMap idMap = null;
        DateTime latestMigrationDate = null;
        for (IdMap idm : idMaps) {
            if (latestMigrationDate == null || idm.getMigrationDate().isAfter(latestMigrationDate)) {
                latestMigrationDate = idm.getMigrationDate();
                idMap = idm;
            }
        }
        return idMap;
    }

}
