package nl.knaw.dans.easy.mock;

import static org.easymock.EasyMock.anyBoolean;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.same;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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
    private final DmoStoreId datasetStoreId;

    /** Subordinates for the dataset, such as files */
    private final List<DmoStoreId> subOrdinates;

    private final StoreIdGenerator storeIdGenerator;

    private FolderMocker[] folderMockers = {/* default in case only files were specified */};

    /**
     * Creates a mocked instance of a {@link Dataset}. A fluent interface allows further configuration of
     * possible/expected behavior of the instance, and related methods of {@link FileStoreAccess}.
     * 
     * @param storeId
     *        of the dataset
     * @param storeIdGenerator
     *        to generate ID's for files and folders
     */
    DatasetMocker(final String storeId, final StoreIdGenerator storeIdGenerator) throws Exception
    {
        this.storeIdGenerator = storeIdGenerator;
        datasetStoreId = new DmoStoreId(storeId);
        dataset = PowerMock.createMock(Dataset.class);
        subOrdinates = new LinkedList<DmoStoreId>();
        expect(dataset.getStoreId()).andStubReturn(storeId);
        expect(dataset.getDmoStoreId()).andStubReturn(datasetStoreId);
        expect(Data.getEasyStore().retrieve(eq(datasetStoreId))).andStubReturn(dataset);
        expect(Data.getEasyStore().exists(eq(datasetStoreId))).andStubReturn(true);
        expect(Data.getEasyStore().findSubordinates(eq(datasetStoreId))).andStubReturn(subOrdinates);
        expect(Data.getMigrationRepo().exists(eq(datasetStoreId.toString()))).andStubReturn(true);
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
        expect(Data.getMigrationRepo().findById(datasetStoreId.getStoreId())).andStubReturn(idMapMock);
        return this;
    }

    /**
     * Creates stubs for {@link FileStoreAccess} to relate mocked files and folders with one another and
     * with the mocked {@link Dataset}. Mocked folders are created for parent folders that were not yet
     * created.
     * 
     * @param fileMockers
     * @return this object to allow a fluent interface.
     */
    public DatasetMocker with(final FileMocker... fileMockers) throws Exception
    {
        final ItemStubber stubber = new ItemStubber(datasetStoreId, storeIdGenerator);
        stubber.createItemExpectations(fileMockers, folderMockers);
        subOrdinates.addAll(stubber.getDmoStoreIDs());
        return this;
    }

    /**
     * Links the mocked folders with the mocked {@link Dataset}. Note that expectations are created
     * together with {@link #with(FileMocker...)} because relations can only be established using both.
     * 
     * @param folderMockers
     * @return this object to allow a fluent interface.
     */
    public DatasetMocker with(final FolderMocker... folderMockers) throws Exception
    {
        this.folderMockers = folderMockers;
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
            fileItems.add(mocker.getItemVO());
        expect(Data.getFileStoreAccess().getDatasetFiles(datasetStoreId)).andReturn(fileItems).once();
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
        expect(Data.getFileStoreAccess().getAllFiles(datasetStoreId)).andStubReturn(new HashMap<String, String>());
        expect(Data.getFileStoreAccess().getDatasetFiles(datasetStoreId)).andStubReturn(new ArrayList<FileItemVO>());
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
        Data.getMigrationRepo().delete(eq(datasetStoreId.toString()));
        EasyMock.expectLastCall().once();
        return this;
    }

    /** @return the mocked dataset */
    Dataset getDataset()
    {
        return dataset;
    }
}
