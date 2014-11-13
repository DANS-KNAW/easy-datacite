package nl.knaw.dans.easy.mock;

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

/** Responsible for stubs of {@link FileStoreAccess} related to files and folders. */
class FileStoreAccessStubber {

    /** The store ID for the mocked dataset */
    private final DmoStoreId datasetStoreId;

    /** StoreId's of files and folders */
    private final List<DmoStoreId> dmoStoreIDs = new LinkedList<DmoStoreId>();;

    private final FileHandler fileHandler = new FileHandler();
    private final FolderHandler folderHandler = new FolderHandler();

    private final StoreIdGenerator storeIdGenerator;

    private final Map<String, FolderMocker> addedFolders = new HashMap<String, FolderMocker>();

    FileStoreAccessStubber(final DmoStoreId datasetStoreId, final StoreIdGenerator storeIdGenerator) {
        this.storeIdGenerator = storeIdGenerator;
        this.datasetStoreId = datasetStoreId;
    }

    void createItemExpectations(final FileMocker[] fileMockers, final FolderMocker[] folderMockers) throws Exception {
        fileHandler.process(fileMockers);
        folderHandler.process(folderMockers);

        final FileStoreAccess fsa = Data.getFileStoreAccess();
        expect(fsa.getDatasetFiles(eq(datasetStoreId)))//
                .andStubReturn(fileHandler.items);
        expect(fsa.getAllFiles(eq(datasetStoreId)))//
                .andStubReturn(fileHandler.fileNameMap);
        expect(fsa.getFilenames(eq(datasetStoreId)))//
                .andStubReturn(new ArrayList<String>(fileHandler.fileNameMap.values()));

        createChildExpectations(datasetStoreId, fileMockers);
        for (final FolderMocker folderMocker : addedFolders.values())
            createChildExpectations(new DmoStoreId(folderMocker.getStoreId()), fileMockers);
    }

    private void createChildExpectations(final DmoStoreId parentStoreId, final FileMocker[] fileMockers) throws Exception {
        final List<FolderItemVO> childFolders = folderHandler.findChildren(addedFolders.values(), parentStoreId);
        final List<FileItemVO> childFiles = fileHandler.findChildren(Arrays.asList(fileMockers), parentStoreId);
        final List<ItemVO> childItems = new ArrayList<ItemVO>();
        childItems.addAll(childFiles);
        childItems.addAll(childFolders);

        final FileStoreAccess fsa = Data.getFileStoreAccess();
        expect(fsa.getFolders(eq(parentStoreId)))//
                .andStubReturn(childFolders);
        expect(fsa.getFiles(eq(parentStoreId)))//
                .andStubReturn(childFiles);
        expect(fsa.getFilesAndFolders(eq(parentStoreId)))//
                .andStubReturn(childItems);
        expect(fsa.hasChildItems(eq(parentStoreId)))//
                .andStubReturn(childItems.size() > 0);
    }

    /** @return Store ID's of files and folders */
    List<DmoStoreId> getDmoStoreIDs() {
        return dmoStoreIDs;
    }

    private class ItemHandler<VO extends AbstractItemVO, I extends DatasetItem, M extends AbstractItemMocker<VO, I>> {
        final List<VO> items = new ArrayList<VO>();

        void addItemExpectations(final M mocker) throws Exception {
            final VO itemVO = mocker.getItemVO();
            final I item = mocker.getItem();
            final File file = new File(mocker.getPath());
            final DmoStoreId itemStoreId = new DmoStoreId(mocker.getStoreId());
            final DmoStoreId parentStoreId = addParentFolder(file.getParent());
            items.add(itemVO);
            dmoStoreIDs.add(itemStoreId);
            mocker.setParentStoreId(parentStoreId.toString());
            expect(itemVO.getDatasetSid()).andStubReturn(datasetStoreId.toString());
            expect(itemVO.getParentSid()).andStubReturn(parentStoreId.toString());
            expect(item.getDatasetId()).andStubReturn(datasetStoreId);
            expect(Data.getFileStoreAccess().getDatasetId(eq(itemStoreId)))//
                    .andStubReturn(datasetStoreId.toString());
        }

        List<VO> findChildren(final Collection<M> mockers, final DmoStoreId dmoStoreId) {
            final List<VO> itemVO = new ArrayList<VO>();
            for (final M childMocker : mockers) {
                final String parentStoreId = childMocker.getParentStoreId();
                if (parentStoreId != null && dmoStoreId.toString().equals(parentStoreId))
                    itemVO.add(childMocker.getItemVO());
            }
            return itemVO;
        }

        private DmoStoreId addParentFolder(final String parentPath) throws Exception {
            if (parentPath == null)
                return datasetStoreId;
            if (addedFolders.keySet().contains(parentPath))
                return new DmoStoreId(addedFolders.get(parentPath).getStoreId());

            // parent folder is not yet created

            final String parentStoreId = storeIdGenerator.getNext(FolderItem.NAMESPACE);
            final FolderMocker parentFolderMocker = new FolderMocker(parentPath, parentStoreId);
            addedFolders.put(parentPath, parentFolderMocker);

            // recursive call
            final DmoStoreId grandParentStoreId = addParentFolder(new File(parentPath).getParent());

            parentFolderMocker.setParentStoreId(grandParentStoreId.toString());
            folderHandler.process(parentFolderMocker);
            return new DmoStoreId(parentStoreId);
        }
    }

    private class FileHandler extends ItemHandler<FileItemVO, FileItem, FileMocker> {
        final Map<String, String> fileNameMap = new HashMap<String, String>();

        void process(final FileMocker[] fileMockers) throws Exception {
            for (final FileMocker fileMocker : fileMockers) {
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

    private class FolderHandler extends ItemHandler<FolderItemVO, FolderItem, FolderMocker> {
        void process(final FolderMocker... folderMockers) throws Exception {
            for (final FolderMocker folderMocker : folderMockers) {
                addedFolders.put(folderMocker.getPath(), folderMocker);
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
