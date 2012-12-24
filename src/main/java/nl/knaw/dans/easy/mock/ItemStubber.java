package nl.knaw.dans.easy.mock;

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.FileStoreAccess;
import nl.knaw.dans.easy.domain.dataset.item.AbstractItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.dataset.item.ItemOrder;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.dataset.item.filter.ItemFilters;
import nl.knaw.dans.easy.domain.model.DatasetItem;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FolderItem;

public class ItemStubber
{

    /** The store ID for the mocked dataset */
    private final DmoStoreId datasetStoreId;

    /** StoreId's of files and folders */
    private final List<DmoStoreId> dmoStoreIDs = new LinkedList<DmoStoreId>();;

    private final FileHandler fileHandler = new FileHandler();
    private final FolderHandler folderHandler = new FolderHandler();

    private final StoreIdGenerator storeIdGenerator;

    private final List<FolderMocker> folderMockers = new ArrayList<FolderMocker>();
    private final List<FileMocker> fileMockers = new ArrayList<FileMocker>();

    ItemStubber(final DmoStoreId datasetStoreId, final StoreIdGenerator storeIdGenerator)
    {
        this.storeIdGenerator = storeIdGenerator;
        this.datasetStoreId = datasetStoreId;
    }

    void createItemExpectations(final FileMocker[] fileMockers, final FolderMocker[] folderMockers) throws Exception
    {
        this.folderMockers.addAll(Arrays.asList(folderMockers));
        this.fileMockers.addAll(Arrays.asList(fileMockers));
        fileHandler.process();
        folderHandler.process();

        final FileStoreAccess fsa = Data.getFileStoreAccess();
        expect(fsa.getDatasetFiles(eq(datasetStoreId)))//
                .andStubReturn(fileHandler.items);
        expect(fsa.getAllFiles(eq(datasetStoreId)))//
                .andStubReturn(fileHandler.fileNameMap);
        expect(fsa.getFilenames(eq(datasetStoreId), eq(true)))//
                .andStubReturn(new ArrayList<String>(fileHandler.fileNameMap.values()));

        createChildExpectations(datasetStoreId);
        for (final FolderMocker folderMocker : folderMockers)
            createChildExpectations(new DmoStoreId(folderMocker.getStoreId()));
    }

    private void createChildExpectations(final DmoStoreId parentStoreId) throws Exception
    {
        final List<FolderItemVO> childFolders = folderHandler.filter(folderMockers, parentStoreId.toString());
        final List<FileItemVO> childFiles = fileHandler.filter(fileMockers, parentStoreId.toString());
        final List<ItemVO> childItems = new ArrayList<ItemVO>();
        childItems.addAll(childFiles);
        childItems.addAll(childFolders);

        final FileStoreAccess fsa = Data.getFileStoreAccess();
        expect(fsa.getFolders(eq(parentStoreId), anyInt(), anyInt(), anyObject(ItemOrder.class), anyObject(ItemFilters.class)))//
                .andStubReturn(childFolders);
        expect(fsa.getFiles(eq(parentStoreId), anyInt(), anyInt(), anyObject(ItemOrder.class), anyObject(ItemFilters.class)))//
                .andStubReturn(childFiles);
        expect(fsa.getFilesAndFolders(eq(parentStoreId), anyInt(), anyInt(), anyObject(ItemOrder.class), anyObject(ItemFilters.class)))//
                .andStubReturn(childItems);
        expect(fsa.hasChildItems(eq(parentStoreId)))//
                .andStubReturn(childItems.size() > 0);
    }

    List<DmoStoreId> getDmoStoreIDs()
    {
        return dmoStoreIDs;
    }

    private class ItemHandler<VO extends AbstractItemVO, I extends DatasetItem, M extends AbstractItemMocker<VO, I>>
    {
        final List<VO> items = new ArrayList<VO>();
        private final Map<String, FolderMocker> addedFolders = new HashMap<String, FolderMocker>();

        void addItemExpectations(final M mocker) throws Exception
        {
            final VO itemVO = mocker.getItemVO();
            final I item = mocker.getItem();
            final File file = new File(mocker.getPath());
            final DmoStoreId itemStoreId = new DmoStoreId(mocker.getStoreId());
            final String parentStoreId = addFolder(file.getParent()).toString();
            items.add(itemVO);
            dmoStoreIDs.add(itemStoreId);
            mocker.setParentStoreId(parentStoreId);
            expect(itemVO.getDatasetSid()).andStubReturn(datasetStoreId.toString());
            expect(itemVO.getParentSid()).andStubReturn(parentStoreId);
            expect(item.getDatasetId()).andStubReturn(datasetStoreId);
            expect(Data.getFileStoreAccess().getDatasetId(eq(itemStoreId)))//
                    .andStubReturn(datasetStoreId.toString());
        }

        List<VO> filter(final List<M> mockers, final String dmoStoreId)
        {
            final List<VO> itemVO = new ArrayList<VO>();
            for (final M mocker : mockers)
            {
                if (mocker.getParentStoreId().equals(dmoStoreId))
                    itemVO.add(mocker.getItemVO());
            }
            return itemVO;
        }

        private DmoStoreId addFolder(final String path) throws Exception
        {
            if (path == null)
                return datasetStoreId;
            if (addedFolders.keySet().contains(path))
                return new DmoStoreId(addedFolders.get(path).getStoreId());
            final String storeId = storeIdGenerator.getNext(FolderItem.NAMESPACE);
            final FolderMocker folderMocker = new FolderMocker(path, storeId);
            addedFolders.put(path, folderMocker);
            folderMocker.setParentStoreId(addFolder(new File(path).getParent()).toString());
            return new DmoStoreId(storeId);
        }
    }

    private class FileHandler extends ItemHandler<FileItemVO, FileItem, FileMocker>
    {
        final Map<String, String> fileNameMap = new HashMap<String, String>();

        void process() throws Exception
        {
            for (final FileMocker fileMocker : fileMockers)
            {
                addItemExpectations(fileMocker);
                final DmoStoreId fileStoreId = new DmoStoreId(fileMocker.getStoreId());
                final String path = fileMocker.getPath();
                fileNameMap.put(fileMocker.getStoreId(), new File(path).getName());
                expect(Data.getFileStoreAccess().findFileById(eq(fileStoreId)))//
                        .andStubReturn(fileMocker.getItemVO());
                expect(Data.getFileStoreAccess().findFileByPath(eq(datasetStoreId), eq(path)))//
                        .andStubReturn(fileMocker.getItemVO());
            }
        }
    }

    private class FolderHandler extends ItemHandler<FolderItemVO, FolderItem, FolderMocker>
    {
        void process() throws Exception
        {
            for (final FolderMocker folderMocker : folderMockers)
            {
                addItemExpectations(folderMocker);
                final DmoStoreId folderStoreId = new DmoStoreId(folderMocker.getStoreId());
                final String path = folderMocker.getPath();
                expect(Data.getFileStoreAccess().findFolderById(eq(folderStoreId)))//
                        .andStubReturn(folderMocker.getItemVO());
                expect(Data.getFileStoreAccess().findFolderByPath(eq(datasetStoreId), eq(path)))//
                        .andStubReturn(folderMocker.getItemVO());
            }
        }
    }
}
