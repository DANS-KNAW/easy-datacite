package nl.knaw.dans.easy.data.migration;

import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.ldap.GenericRepo;
import nl.knaw.dans.easy.domain.migration.IdMap;

public interface MigrationRepo extends GenericRepo<IdMap> {

    List<IdMap> findByAipId(String aipId) throws RepositoryException;

    List<IdMap> findByPersistentIdentifier(String persistentIdentifier) throws RepositoryException;

    /**
     * @param aipId
     *        aipId to look for
     * @return IdMap or <code>null</code> if not found
     * @throws RepositoryException
     */
    IdMap getMostRecentByAipId(String aipId) throws RepositoryException;

    IdMap getMostRecentByPersistentIdentifier(String persistentIdentifier) throws RepositoryException;

}
