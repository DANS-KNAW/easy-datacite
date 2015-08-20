package nl.knaw.dans.easy.tools.dataset;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.migration.MigrationRepo;
import nl.knaw.dans.easy.domain.migration.IdMap;
import nl.knaw.dans.easy.tools.IdConverter;
import nl.knaw.dans.easy.tools.exceptions.FatalException;

public class AipIdConverter implements IdConverter {

    @Override
    public List<String> convert(String aipId) throws FatalException {
        List<String> convertedIds = new ArrayList<String>();
        MigrationRepo migrationRepo = Data.getMigrationRepo();
        try {
            List<IdMap> idMapList = migrationRepo.findByAipId(aipId);
            for (IdMap idMap : idMapList) {
                convertedIds.add(idMap.getStoreId());
            }
        }
        catch (RepositoryException e) {
            throw new FatalException("Cannot convert id: " + aipId, e);
        }
        return convertedIds;
    }

}
