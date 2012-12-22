package nl.knaw.dans.easy.mock;

import static org.easymock.EasyMock.anyBoolean;
import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.same;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.EasyStore;
import nl.knaw.dans.easy.data.store.FileStoreAccess;
import nl.knaw.dans.easy.domain.dataset.item.AbstractItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.dataset.item.ItemOrder;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.dataset.item.filter.ItemFilters;
import nl.knaw.dans.easy.domain.migration.IdMap;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.DatasetItem;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FolderItem;

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
    private final List<DmoStoreId> subOrdinates;

    private final Map<String, FolderMocker> addedFolders = new HashMap<String, FolderMocker>();

    private final StoreIdGenerator storeIdGenerator;

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
     * Creates expectations that link the mocked files and folders with each other and with the mocked
     * {@link Dataset}. Mocked folders are created for parent folders that were not yet created.
     * 
     * @param fileMockers
     * @return this object to allow a fluent interface.
     */
    public DatasetMocker with(final FileMocker... fileMockers) throws Exception
    {
        final FileHandler fileHandler = new FileHandler();
        final FolderHandler folderHandler = new FolderHandler();
        final FileStoreAccess fsa = Data.getFileStoreAccess();

        fileHandler.process(fileMockers);
        folderHandler.process(addedFolders.values());

        expect(fsa.getDatasetFiles(dmoStoreId))//
                .andStubReturn(fileHandler.items);
        expect(fsa.getAllFiles(dmoStoreId))//
                .andStubReturn(fileHandler.fileNameMap);
        expect(fsa.getFilenames(dmoStoreId, true))//
                .andStubReturn(new ArrayList<String>(fileHandler.fileNameMap.values()));

        createChildExpectations(dmoStoreId, fileHandler, folderHandler, fileMockers);
        for (FolderMocker folderMocker : getFolderMockers())
            createChildExpectations(new DmoStoreId(folderMocker.getStoreId()), fileHandler, folderHandler, fileMockers);

        return this;
    }

    private void createChildExpectations(final DmoStoreId parentStoreId, final FileHandler fileHandler, final FolderHandler folderHandler,
            final FileMocker... fileMockers) throws Exception
    {
        final FileStoreAccess fsa = Data.getFileStoreAccess();

        final List<FolderItemVO> childFolders = folderHandler.filter(getFolderMockers(), parentStoreId.toString());
        final List<FileItemVO> childFiles = fileHandler.filter(fileMockers, parentStoreId.toString());
        final List<ItemVO> childItems = new ArrayList<ItemVO>();
        childItems.addAll(childFiles);
        childItems.addAll(childFolders);

        expect(fsa.getFolders(eq(parentStoreId), anyInt(), anyInt(), anyObject(ItemOrder.class), anyObject(ItemFilters.class)))//
                .andStubReturn(childFolders);
        expect(fsa.getFiles(eq(parentStoreId), anyInt(), anyInt(), anyObject(ItemOrder.class), anyObject(ItemFilters.class)))//
                .andStubReturn(childFiles);
        expect(fsa.getFilesAndFolders(eq(parentStoreId), anyInt(), anyInt(), anyObject(ItemOrder.class), anyObject(ItemFilters.class)))//
                .andStubReturn(childItems);
        expect(fsa.hasChildItems(eq(parentStoreId)))//
                .andStubReturn(childItems.size() > 0);
    }

    private FolderMocker[] getFolderMockers()
    {
        final Collection<FolderMocker> values = addedFolders.values();
        return values.toArray(new FolderMocker[values.size()]);
    }

    private class ItemHandler<VO extends AbstractItemVO, I extends DatasetItem, M extends AbstractItemMocker<VO, I>>
    {
        final List<VO> items = new ArrayList<VO>();

        void addItemExpectations(final M mocker) throws Exception
        {
            final VO itemVO = mocker.getItemVO();
            final File file = new File(mocker.getPath());
            final String parentStoreId = addFolder(file.getParent()).toString();
            mocker.setParentStoreId(parentStoreId);
            items.add(itemVO);
            expect(itemVO.getDatasetSid()).andStubReturn(dmoStoreId.toString());
            expect(itemVO.getParentSid()).andStubReturn(parentStoreId);
            expect(mocker.getItem().getDatasetId()).andStubReturn(dmoStoreId);
            expect(Data.getFileStoreAccess().getDatasetId(new DmoStoreId(mocker.getStoreId()))).andStubReturn(parentStoreId);
            subOrdinates.add(new DmoStoreId(mocker.getStoreId()));
        }

        List<VO> filter(final M[] mockers, final String dmoStoreId)
        {
            final List<VO> itemVO = new ArrayList<VO>();
            for (final M mocker : mockers)
            {
                if (mocker.getParentStoreId().equals(dmoStoreId))
                    itemVO.add(mocker.getItemVO());
            }
            return itemVO;
        }
    }

    private class FileHandler extends ItemHandler<FileItemVO, FileItem, FileMocker>
    {
        final Map<String, String> fileNameMap = new HashMap<String, String>();

        void process(final FileMocker[] fileMockers) throws Exception
        {
            for (final FileMocker fileMocker : fileMockers)
            {
                final String path = fileMocker.getPath();
                addItemExpectations(fileMocker);
                fileNameMap.put(fileMocker.getStoreId(), new File(path).getName());
                expect(Data.getFileStoreAccess().findFileById(new DmoStoreId(fileMocker.getStoreId())))//
                        .andStubReturn(fileMocker.getItemVO());
            }
        }
    }

    private class FolderHandler extends ItemHandler<FolderItemVO, FolderItem, FolderMocker>
    {
        void process(final Collection<FolderMocker> collection) throws Exception
        {
            for (final FolderMocker folderMocker : collection)
            {
                addItemExpectations(folderMocker);
                expect(Data.getFileStoreAccess().findFolderById(new DmoStoreId(folderMocker.getStoreId())))//
                        .andStubReturn(folderMocker.getItemVO());
            }
        }
    }

    /**
     * Creates expectations that link the mocked folders with the mocked {@link Dataset}. Mocked folders
     * are created for parent folders that were not yet created. Note that links between folders and
     * files are established by {@link #with(FileMocker...)}.
     * 
     * @param folders
     * @return this object to allow a fluent interface.
     */
    public DatasetMocker with(final FolderMocker... folders) throws Exception
    {
        for (final FolderMocker mocker : folders)
        {
            final String path = mocker.getPath();
            if (addedFolders.keySet().contains(path))
                throw new IllegalStateException(path + " is already configured for " + dmoStoreId);
            addedFolders.put(path, new FolderMocker(path, mocker.getStoreId()));
            mocker.setParentStoreId(addFolder(new File(path).getParent()).toString());
        }
        return this;
    }

    private DmoStoreId addFolder(final String path) throws Exception
    {
        if (path == null)
            return dmoStoreId;
        if (addedFolders.keySet().contains(path))
            return new DmoStoreId(addedFolders.get(path).getStoreId());
        final String storeId = storeIdGenerator.getNext(FolderItem.NAMESPACE);
        final FolderMocker folderMocker = new FolderMocker(path, storeId);
        addedFolders.put(path, folderMocker);
        folderMocker.setParentStoreId(addFolder(new File(path).getParent()).toString());
        return new DmoStoreId(storeId);
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

    /** @return the mocked dataset */
    Dataset getDataset()
    {
        return dataset;
    }
}
