package nl.dans.knaw.easy.mock;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.migration.IdMap;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.mock.BusinessMocker;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class BusinessMockerTest {
    private static final DateTime BASE_DATE_TIME = new DateTime("2000-01-01T00:00:00");

    private BusinessMocker mock;

    @Before
    public void setUp() throws Exception {
        mock = new BusinessMocker();
    }

    @After
    public void verifyAll() {
        PowerMock.verifyAll();
    }

    @Test
    public void easyStoreRetrieve() throws Exception {
        final String storeId = mock.nextDmoStoreId(Dataset.NAMESPACE);
        mock.dataset(storeId);

        PowerMock.replayAll();

        final DmoStoreId dmoStoreId = new DmoStoreId(storeId);
        assertThat((Dataset) Data.getEasyStore().retrieve(dmoStoreId), notNullValue());
        assertThat(Data.getEasyStore().exists(dmoStoreId), equalTo(true));
    }

    @Test
    public void getMigrationAipId() throws Exception {
        final String storeId = mock.nextDmoStoreId(Dataset.NAMESPACE);
        final String aipId = "twips-1";
        mock.dataset(storeId).withAipId(aipId);

        PowerMock.replayAll();

        final IdMap idMap = Data.getMigrationRepo().findById(storeId);
        assertThat(idMap.getAipId(), equalTo(aipId));
    }

    @Test
    public void noPurge() throws Exception {
        final String path = "tif/2.tif";
        final String storeId = mock.nextDmoStoreId(FileItem.NAMESPACE);
        mock.file(path, storeId);

        PowerMock.replayAll();

        final FileItem fileItem = (FileItem) Data.getEasyStore().retrieve(new DmoStoreId(storeId));
        assertThat(fileItem.getPath(), equalTo(path));
    }

    @Test
    public void missingEmptyFolder() throws Exception {
        mock.dataset(mock.nextDmoStoreId(Dataset.NAMESPACE));
        mock.dataset(mock.nextDmoStoreId(Dataset.NAMESPACE));
        mock.dataset(mock.nextDmoStoreId(Dataset.NAMESPACE));

        assertThat(mock.getDatasets().size(), equalTo(3));

        PowerMock.replayAll();
    }

    @Test
    public void migrationPurge() throws Exception {
        final String datasetStoreId = mock.nextDmoStoreId(Dataset.NAMESPACE);

        mock.dataset(datasetStoreId).expectMigrationPurgeAt(BASE_DATE_TIME.plusMillis(1));

        PowerMock.replayAll();

        final Dataset dataset = (Dataset) Data.getEasyStore().retrieve(new DmoStoreId(datasetStoreId));
        Data.getEasyStore().purge(dataset, true, "oops");
        Data.getMigrationRepo().delete(datasetStoreId);
    }

    @Test
    public void purge() throws Exception {
        final String datasetStoreId = mock.nextDmoStoreId(Dataset.NAMESPACE);
        mock.dataset(datasetStoreId).expectPurgeAt(BASE_DATE_TIME.plusMillis(2));

        PowerMock.replayAll();

        final Dataset dataset = (Dataset) Data.getEasyStore().retrieve(new DmoStoreId(datasetStoreId));
        Data.getEasyStore().purge(dataset, true, "oops");
    }
}
