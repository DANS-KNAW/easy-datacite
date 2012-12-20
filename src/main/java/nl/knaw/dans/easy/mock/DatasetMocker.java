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

public class DatasetMocker
{
    /** the mocked dataset */
    private final Dataset dataset;

    /** The store ID for the mocked dataset */
    private final DmoStoreId dmoStoreId;

    /** Subordinates for the dataset, such as files */
    private List<DmoStoreId> subOrdinates;

    /**
     * Creates a mocked instance of a {@link Dataset}. A fluent interface allows further configuration of
     * possible/expected behavior of the instance, and related methods of {@link FileStoreAccess}.
     */
    protected DatasetMocker(final String storeId) throws Exception
    {
        dmoStoreId = new DmoStoreId(storeId);
        dataset = PowerMock.createMock(Dataset.class);
        subOrdinates = new LinkedList<DmoStoreId>();
        expect(dataset.getStoreId()).andStubReturn(storeId);
        expect(dataset.getDmoStoreId()).andStubReturn(dmoStoreId);
        expect(Data.getEasyStore().retrieve(eq(dmoStoreId))).andStubReturn(dataset);
        expect(Data.getEasyStore().exists(eq(dmoStoreId))).andStubReturn(true);
        expect(Data.getEasyStore().findSubordinates(eq(dmoStoreId))).andStubReturn(subOrdinates);
        expect(Data.getMigrationRepo().exists(eq(dmoStoreId.toString()))).andStubReturn(true);
    }

    public DatasetMocker withPid(final String persitentIdentifier)
    {
        expect(dataset.getPersistentIdentifier()).andStubReturn(persitentIdentifier);
        return this;
    }

    public DatasetMocker with(final DatasetState state)
    {
        expect(dataset.getAdministrativeState()).andStubReturn(state);
        return this;
    }

    public DatasetMocker with(final DatasetState administrativeSate, final String state)
    {
        expect(dataset.getAdministrativeState()).andStubReturn(administrativeSate);
        expect(dataset.getState()).andStubReturn(state);
        return this;
    }

    public DatasetMocker withAipId(final String aipId) throws Exception
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
    public DatasetMocker with(final FileMocker... files) throws Exception
    {
        final Map<String, String> fileMap = new HashMap<String, String>();
        final List<FileItemVO> fileItems = new ArrayList<FileItemVO>();
        for (final FileMocker mocker : files)
        {
            fileMap.put(mocker.getStoreId(), new File(mocker.getPath()).getName());
            fileItems.add(mocker.getFileItemVO());
            expect(mocker.getFileItemVO().getDatasetSid()).andStubReturn(dmoStoreId.toString());
            expect(mocker.getFileItem().getDatasetId()).andStubReturn(dmoStoreId);
            subOrdinates.add(new DmoStoreId(mocker.getStoreId()));
        }
        expect(Data.getFileStoreAccess().getAllFiles(dmoStoreId)).andStubReturn(fileMap);
        expect(Data.getFileStoreAccess().getDatasetFiles(dmoStoreId)).andStubReturn(fileItems);
        return this;
    }

    /**
     * Creates the expectation that {@link FileStoreAccess#getDatasetFiles(DmoStoreId)} once returns the
     * mocked file objects for the mocked {@link Dataset}.
     * 
     * @param files
     * @return this object to allow a fluent interface.
     */
    public DatasetMocker expectGetDatasetFilesOnce(final FileMocker... files) throws Exception
    {
        final List<FileItemVO> fileItems = new ArrayList<FileItemVO>();
        for (final FileMocker mocker : files)
            fileItems.add(mocker.getFileItemVO());
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
    public DatasetMocker withoutFiles() throws Exception
    {
        expect(Data.getFileStoreAccess().getAllFiles(dmoStoreId)).andStubReturn(new HashMap<String, String>());
        expect(Data.getFileStoreAccess().getDatasetFiles(dmoStoreId)).andStubReturn(new ArrayList<FileItemVO>());
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
    public DatasetMocker expectPurgeAt(final DateTime dateTime) throws Exception
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
    public DatasetMocker expectMigrationPurgeAt(final DateTime dateTime) throws Exception
    {
        expect(Data.getEasyStore().purge(same(dataset), anyBoolean(), isA(String.class))).andReturn(dateTime).once();
        Data.getMigrationRepo().delete(eq(dmoStoreId.toString()));
        EasyMock.expectLastCall().once();
        return this;
    }

    /**
     * Returns the mocked dataset. The behavior of the returned object may be changed by this.
     * 
     * @return the mocked dataset
     */
    public Dataset getDataset()
    {
        return dataset;
    }
}
