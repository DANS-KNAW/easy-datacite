package nl.knaw.dans.easy;

import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.common.DisciplineUtils;
import nl.knaw.dans.easy.web.fileexplorer.Util;

import org.powermock.api.easymock.PowerMock;

public class TestUtil {
    public static void cleanup() {
        PowerMock.resetAll();
        new Util().setDatasetService(null);
        new DisciplineUtils().setDepositService(null);
        new Data().setCollectionAccess(null);
        new Data().setDatasetSearch(null);
        new Data().setEasyStore(null);
        new Data().setFederativeUserRepo(null);
        new Data().setFileStoreAccess(null);
        new Data().setGroupRepo(null);
        new Data().setMigrationRepo(null);
        new Data().setSearchEngine(null);
        new Data().setUserRepo(null);
        new Services().setCollectionService(null);
        new Services().setDatasetService(null);
        new Services().setDepositService(null);
        new Services().setDisciplineService(null);
        new Services().setFederativeUserService(null);
        new Services().setItemService(null);
        new Services().setJumpoffService(null);
        new Services().setMigrationService(null);
        new Services().setSearchService(null);
        new Services().setUserService(null);
    }
}
