package nl.dans.knaw.easy.mock;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.FileStoreAccess;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FolderItem;
import nl.knaw.dans.easy.mock.BusinessMocker;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

public class FileStoreAccessStubberTest
{
    private static final DateTime BASE_DATE_TIME = new DateTime("2000-01-01T00:00:00");

    private BusinessMocker mock;

    @Before
    public void setUp() throws Exception
    {
        mock = new BusinessMocker();
    }

    @After
    public void verifyAll()
    {
        PowerMock.verifyAll();
    }

    @Test
    public void purge() throws Exception
    {
        final String datasetStoreId = mock.nextDmoStoreId(Dataset.NAMESPACE);
        final String folderStoreId = mock.nextDmoStoreId(FolderItem.NAMESPACE);
        mock.dataset(datasetStoreId)//
                .with//
                (//
                        mock.file("a/b/1.gif"), //
                        mock.file("a/b/2.tif"),//
                        mock.file("a/3.png"), //
                        mock.file("a/4.png"), //
                        mock.file("a/c/5.png"), //
                        mock.file("6.gif")//
                ).with//
                (//
                        mock.folder("a/d", folderStoreId)//
                                .expectPurgeAt(BASE_DATE_TIME.plusMillis(1))//
                );

        PowerMock.replayAll();

        final DataModelObject folderItem = Data.getEasyStore().retrieve(new DmoStoreId(folderStoreId));
        Data.getEasyStore().purge(folderItem, true, " purged ");

        final FileStoreAccess fsa = Data.getFileStoreAccess();
        final DmoStoreId dmoStoreId = new DmoStoreId(datasetStoreId);
        assertThat(fsa.getFiles(dmoStoreId, 0, 0, null, null).size(), equalTo(1));
        assertThat(fsa.getFiles(dmoStoreId, 0, 0, null, null).get(0).getPath(), equalTo("6.gif"));
        assertThat(fsa.findFileByPath(dmoStoreId, "6.gif").getDatasetSid(), equalTo(datasetStoreId));
        assertThat(fsa.findFileByPath(dmoStoreId, "6.gif").getParentSid(), equalTo(datasetStoreId));
        assertThat(fsa.findFileByPath(dmoStoreId, "a/c/5.png").getDatasetSid(), equalTo(datasetStoreId));
        assertThat(fsa.findFileByPath(dmoStoreId, "a/c/5.png").getParentSid(), not(datasetStoreId));

        final String storeIdOf5 = fsa.findFileByPath(dmoStoreId, "a/c/5.png").getSid();
        assertThat(fsa.findFileById(new DmoStoreId(storeIdOf5)).getPath(), equalTo("a/c/5.png"));

        final String storeIdOfA = fsa.findFileByPath(dmoStoreId, "a/4.png").getParentSid();
        assertThat(fsa.findFolderById(new DmoStoreId(storeIdOfA)).getPath(), equalTo("a"));
        assertThat(fsa.getFiles(new DmoStoreId(storeIdOfA), 0, 0, null, null).size(), equalTo(2));
        assertThat(fsa.getFilesAndFolders(dmoStoreId, 0, 0, null, null).size(), equalTo(2));
        assertThat(fsa.getFolders(dmoStoreId, 0, 0, null, null).size(), equalTo(1));
        assertThat(fsa.getFolders(new DmoStoreId(storeIdOfA), 0, 0, null, null).size(), equalTo(2));
        assertThat(fsa.findFolderByPath(dmoStoreId, "a").getDatasetSid(), equalTo(datasetStoreId));
        assertThat(fsa.findFolderByPath(dmoStoreId, "a/b").getDatasetSid(), equalTo(datasetStoreId));
        assertThat(fsa.findFileByPath(dmoStoreId, "6.gif").getDatasetSid(), equalTo(datasetStoreId));
        assertThat(fsa.findFileByPath(dmoStoreId, "a/c/5.png").getDatasetSid(), equalTo(datasetStoreId));
        assertThat(fsa.findFolderByPath(dmoStoreId, "a/b").getParentSid()//
                , equalTo(fsa.findFolderByPath(dmoStoreId, "a/c").getParentSid()));
        assertThat(fsa.findFolderByPath(dmoStoreId, "a/b").getParentSid()//
                , equalTo(fsa.findFileByPath(dmoStoreId, "a/4.png").getParentSid()));
    }
}
