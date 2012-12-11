package nl.knaw.dans.easy.mock;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.repo.DmoStoreEventListener;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.migration.MigrationRepo;
import nl.knaw.dans.easy.data.store.EasyStore;
import nl.knaw.dans.easy.data.store.FileStoreAccess;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.migration.IdMap;
import nl.knaw.dans.easy.domain.model.Dataset;

import org.aspectj.lang.annotation.Before;
import org.easymock.internal.matchers.Any;
import org.powermock.api.easymock.PowerMock;

public class DatasetHelper
{
    private final DmoStoreId dmoStoreId;
    private final Dataset dataset;
    private static FileStoreAccess fileStoreAccessMock;
    private static MigrationRepo migrationRepoMock;
    private static EasyStore easyStoreMock;

    /**
     * Creates a mocked instance of a {@link Dataset}. A fluent interface allows further configuration of
     * possible/expected behavior of the instance, and related methods of {@link FileStoreAccess}.
     */
    protected DatasetHelper(final String storeId) throws Exception
    {
        dmoStoreId = new DmoStoreId(storeId);
        dataset = PowerMock.createMock(Dataset.class);
        expect(dataset.getStoreId()).andReturn(storeId).anyTimes();
        expect(dataset.getDmoStoreId()).andReturn(dmoStoreId).anyTimes();
        expect(easyStoreMock.retrieve(eq(dmoStoreId))).andReturn(dataset).anyTimes();
        expect(easyStoreMock.exists(eq(dmoStoreId))).andReturn(true).anyTimes();
    }

    /**
     * Prepares the mocks for a new configuration. To be called by a {@link Before}.
     */
    static void reset(EasyStore easyStore)
    {
        migrationRepoMock = PowerMock.createMock(MigrationRepo.class);
        fileStoreAccessMock = PowerMock.createMock(FileStoreAccess.class);
        easyStoreMock = easyStore;
        new Data().setEasyStore(easyStoreMock);
        new Data().setFileStoreAccess(fileStoreAccessMock);
        new Data().setMigrationRepo(migrationRepoMock);
        expect(easyStoreMock.getListeners()).andReturn(new ArrayList<DmoStoreEventListener>()).anyTimes();
    }

    public static void verifyAll()
    {
        PowerMock.verifyAll();
    }

    public static void replayAll()
    {
        PowerMock.replayAll();
    }

    public DatasetHelper withPid(final String persitentIdentifier)
    {
        expect(dataset.getPersistentIdentifier()).andReturn(persitentIdentifier).anyTimes();
        return this;
    }

    public DatasetHelper withAipId(final String aipId) throws Exception
    {
        final IdMap idMapMock = new IdMap()
        {
            private static final long serialVersionUID = 1L;

            public String getAipId()
            {
                return aipId;
            }
        };
        expect(migrationRepoMock.findById(dmoStoreId.getStoreId())).andStubReturn(idMapMock);
        return this;
    }

    /**
     * Creates expectations for {@link FileStoreAccess#getAllFiles(DmoStoreId)} and
     * {@link FileStoreAccess#getDatasetFiles(DmoStoreId)} from the mocked file objects for the mocked
     * {@link Dataset}.
     * 
     * @param files
     * @return this object to allow a fluent interface.
     */
    public DatasetHelper with(final FileHelper... files) throws Exception
    {
        final Map<String, String> fileMap = new HashMap<String, String>();
        final List<FileItemVO> fileItems = new ArrayList<FileItemVO>();
        for (final FileHelper helper : files)
        {
            if (helper != null)
            {
                fileMap.put(helper.getStoreId(), new File(helper.getPath()).getName());
                fileItems.add(helper.getFileItemVO());
            }
        }
        expect(fileStoreAccessMock.getAllFiles(dmoStoreId)).andReturn(fileMap).anyTimes();
        expect(fileStoreAccessMock.getDatasetFiles(dmoStoreId)).andReturn(fileItems).anyTimes();
        return this;
    }

    /**
     * Creates the expectation that {@link FileStoreAccess#getDatasetFiles(DmoStoreId)} once returns the
     * mocked file objects for the mocked {@link Dataset}.
     * 
     * @param files
     * @return this object to allow a fluent interface.
     */
    public DatasetHelper expectGetDatasetFilesOnce(final FileHelper... files) throws Exception
    {
        final List<FileItemVO> fileItems = new ArrayList<FileItemVO>();
        for (final FileHelper helper : files)
            fileItems.add(helper.getFileItemVO());
        expect(fileStoreAccessMock.getDatasetFiles(dmoStoreId)).andReturn(fileItems).once();
        return this;
    }

    /**
     * Creates expectations for {@link FileStoreAccess#getAllFiles(DmoStoreId)} and
     * {@link FileStoreAccess#getDatasetFiles(DmoStoreId)} for the mocked {@link Dataset}.
     * 
     * @return
     * @throws Exception
     */
    public DatasetHelper withoutFiles() throws Exception
    {
        expect(fileStoreAccessMock.getAllFiles(dmoStoreId)).andReturn(new HashMap<String, String>()).anyTimes();
        expect(fileStoreAccessMock.getDatasetFiles(dmoStoreId)).andReturn(new ArrayList<FileItemVO>()).anyTimes();
        return this;
    }

    public Dataset getDataset()
    {
        return dataset;
    }
}
