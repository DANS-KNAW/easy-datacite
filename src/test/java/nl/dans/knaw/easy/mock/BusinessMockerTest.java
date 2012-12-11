package nl.dans.knaw.easy.mock;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.migration.IdMap;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.mock.BusinessMocker;

import org.junit.Ignore;
import org.junit.Test;

public class BusinessMockerTest extends BusinessMocker
{
    @Test
    public void getMigrationAipId() throws Exception
    {
        final String storeId = "easy-dataset:1";
        final String aipId = "twips-1";
        dataset(storeId).withAipId(aipId);

        replayBusinessMocks();

        final IdMap idMap = Data.getMigrationRepo().findById(storeId);
        assertThat(idMap, notNullValue());
        assertThat(idMap.getAipId(), equalTo(aipId));
    }

    @Ignore
    //FIXME what is the essential difference with MockEasyStoreRetrieveTest?
    @Test
    public void easyStoreRetrieve() throws Exception
    {
        final String storeId = "easy-dataset:1";
        Dataset mockedDataset = dataset(storeId).getDataset();

        replayBusinessMocks();

        final Dataset retrievedDataset = (Dataset) Data.getEasyStore().retrieve(new DmoStoreId(storeId));
        assertThat(retrievedDataset, sameInstance(mockedDataset));
    }
}
