package nl.knaw.dans.easy.data.federation;

import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.ldap.GenericRepo;
import nl.knaw.dans.easy.domain.federation.FederativeUserIdMap;
import nl.knaw.dans.easy.domain.migration.IdMap;

public interface FederativeUserRepo extends GenericRepo<FederativeUserIdMap> {
    List<FederativeUserIdMap> findByDansUserId(String dansUserId) throws RepositoryException;
}
