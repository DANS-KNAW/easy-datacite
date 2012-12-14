package nl.knaw.dans.easy.mock;

import static org.easymock.EasyMock.anyBoolean;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.same;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.EasyStore;
import nl.knaw.dans.easy.data.store.FileStoreAccess;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.migration.IdMap;
import nl.knaw.dans.easy.domain.model.Dataset;

import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.powermock.api.easymock.PowerMock;

public class DatasetHelper
{
    private Boolean filesExpectedAnyTimes;
    private final DmoStoreId dmoStoreId;
    private final Dataset dataset;
    private List<DmoStoreId> subOrdinates;

    /**
     * Creates a mocked instance of a {@link Dataset}. A fluent interface allows further configuration of
     * possible/expected behavior of the instance, and related methods of {@link FileStoreAccess}.
     */
    protected DatasetHelper(final String storeId) throws Exception
    {
        dmoStoreId = new DmoStoreId(storeId);
        dataset = PowerMock.createMock(Dataset.class);
        subOrdinates = new LinkedList<DmoStoreId>();
        expect(dataset.getStoreId()).andReturn(storeId).anyTimes();
        expect(dataset.getDmoStoreId()).andReturn(dmoStoreId).anyTimes();
        expect(Data.getEasyStore().retrieve(eq(dmoStoreId))).andReturn(dataset).anyTimes();
        expect(Data.getEasyStore().exists(eq(dmoStoreId))).andReturn(true).anyTimes();
        expect(Data.getEasyStore().findSubordinates(eq(dmoStoreId))).andReturn(subOrdinates).anyTimes();
        expect(Data.getMigrationRepo().exists(eq(dmoStoreId.toString()))).andReturn(true).anyTimes();
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

    public DatasetHelper with(final DatasetState state)
    {
        expect(dataset.getAdministrativeState()).andReturn(state).anyTimes();
        return this;
    }

    public DatasetHelper with(final DatasetState administrativeSate, final String state)
    {
        expect(dataset.getAdministrativeState()).andReturn(administrativeSate).anyTimes();
        expect(dataset.getState()).andReturn(state).anyTimes();
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
        expect(Data.getMigrationRepo().findById(dmoStoreId.getStoreId())).andStubReturn(idMapMock);
        return this;
    }

    /**
     * Creates expectations for {@link FileStoreAccess#getAllFiles(DmoStoreId)} and
     * {@link FileStoreAccess#getDatasetFiles(DmoStoreId)} from the mocked file objects for the mocked
     * {@link Dataset}, and adds the mocked file objects to the result of the expected
     * {@link EasyStore#findSubordinates(DmoStoreId)}.
     * 
     * @param files
     * @return this object to allow a fluent interface.
     */
    public DatasetHelper with(final FileHelper... files) throws Exception
    {
        filesExpected(true);
        final Map<String, String> fileMap = new HashMap<String, String>();
        final List<FileItemVO> fileItems = new ArrayList<FileItemVO>();
        for (final FileHelper helper : files)
        {
            fileMap.put(helper.getStoreId(), new File(helper.getPath()).getName());
            fileItems.add(helper.getFileItemVO());
            subOrdinates.add(new DmoStoreId(helper.getStoreId()));
        }
        expect(Data.getFileStoreAccess().getAllFiles(dmoStoreId)).andReturn(fileMap).anyTimes();
        expect(Data.getFileStoreAccess().getDatasetFiles(dmoStoreId)).andReturn(fileItems).anyTimes();
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
        filesExpected(false);
        final List<FileItemVO> fileItems = new ArrayList<FileItemVO>();
        for (final FileHelper helper : files)
            fileItems.add(helper.getFileItemVO());
        expect(Data.getFileStoreAccess().getDatasetFiles(dmoStoreId)).andReturn(fileItems).once();
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
        filesExpected(true);
        expect(Data.getFileStoreAccess().getAllFiles(dmoStoreId)).andReturn(new HashMap<String, String>()).anyTimes();
        expect(Data.getFileStoreAccess().getDatasetFiles(dmoStoreId)).andReturn(new ArrayList<FileItemVO>()).anyTimes();
        return this;
    }

    /**
     * Configures the expectation that
     * {@link EasyStore#purge(nl.knaw.dans.common.lang.repo.DataModelObject, boolean, String)} is called
     * exactly once for the mocked {@link Dataset} with any value for the other arguments.<br/>
     * Note that the mocked purge does not change anything to the mocked datasets or files. The mocked
     * objects are already in replay mode and therefore their behavior can't be changed any more. After
     * calling the mocked purge the dataset will keep showing up.
     * 
     * @return this object to allow a fluent interface.
     */
    public DatasetHelper expectPurgeAt(final DateTime dateTime) throws Exception
    {
        expect(Data.getEasyStore().purge(same(dataset), anyBoolean(), isA(String.class))).andReturn(dateTime).once();
        return this;
    }

    /**
     * Configures the expectation that
     * {@link EasyStore#purge(nl.knaw.dans.common.lang.repo.DataModelObject, boolean, String)} is called
     * exactly once for the mocked {@link Dataset} with any value for the other arguments.<br/>
     * Note that the mocked purge does not change anything to the mocked datasets or files. The mocked
     * objects are already in replay mode and therefore their behavior can't be changed any more. After
     * calling the mocked purge the dataset will keep showing up.
     * 
     * @return this object to allow a fluent interface.
     */
    public DatasetHelper expectMigrationPurgeAt(final DateTime dateTime) throws Exception
    {
        expect(Data.getEasyStore().purge(same(dataset), anyBoolean(), isA(String.class))).andReturn(dateTime).once();
        Data.getMigrationRepo().delete(eq(dmoStoreId.toString()));
        EasyMock.expectLastCall().once();
        return this;
    }

    private void filesExpected(boolean anyTimes)
    {
        if (anyTimes && filesExpectedAnyTimes != null)
            throw new IllegalStateException("mocked " + dmoStoreId + " allready has file expectations, can't set for anyTimes any more");
        filesExpectedAnyTimes = anyTimes;
    }

    public Dataset getDataset()
    {
        return dataset;
    }
}
