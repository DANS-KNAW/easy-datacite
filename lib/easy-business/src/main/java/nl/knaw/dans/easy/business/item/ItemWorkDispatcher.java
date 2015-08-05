package nl.knaw.dans.easy.business.item;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.UnitOfWork;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.security.authz.AuthzStrategy;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.business.md.amd.AdditionalMetadataUpdateStrategy;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.domain.dataset.FileItemDescription;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.dataset.item.UpdateInfo;
import nl.knaw.dans.easy.domain.dataset.item.filter.ItemFilters;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.DatasetItemContainer;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FolderItem;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.worker.WorkListener;
import nl.knaw.dans.easy.security.authz.AuthzStrategyProvider;
import nl.knaw.dans.easy.xml.ResourceMetadataList;

import org.dom4j.Element;

public class ItemWorkDispatcher {

    public ItemWorkDispatcher() {

    }

    public void addDirectoryContents(EasyUser sessionUser, Dataset dataset, DatasetItemContainer parentContainer, File rootFile, FileFilter fileFilter,
            UnitOfWork uow, ItemIngesterDelegator delegator, WorkListener... workListeners) throws ServiceException
    {
        ItemIngester ingester = new ItemIngester(uow, dataset, sessionUser, delegator);
        ingester.addWorkListeners(workListeners);
        ingester.workAddDirectoryContents(parentContainer, rootFile, fileFilter);
    }

    public void updateObjects(EasyUser sessionUser, Dataset dataset, List<DmoStoreId> dmoStoreIds, UpdateInfo updateInfo, UnitOfWork uow)
            throws ServiceException
    {
        ItemWorker worker = new ItemWorker(uow);
        worker.workUpdateObjects(dataset, dmoStoreIds, updateInfo, null);
    }

    public void updateFileItemMetadata(EasyUser sessionUser, Dataset dataset, ResourceMetadataList resourceMetadataList,
            AdditionalMetadataUpdateStrategy strategy, WorkListener... workListeners) throws ServiceException
    {
        FileItemMetadataUpdateWorker worker = new FileItemMetadataUpdateWorker(sessionUser, strategy);
        worker.addWorkListeners(workListeners);
        worker.workUpdateMetadata(dataset, resourceMetadataList);
    }

    public void saveDescriptiveMetadata(EasyUser sessionUser, final UnitOfWork uow, final Dataset dataset, final Map<String, Element> descriptiveMetadataMap,
            WorkListener... workListeners) throws ServiceException
    {
        DescriptiveMetadataWorker worker = new DescriptiveMetadataWorker(uow);
        worker.addWorkListeners(workListeners);
        worker.saveDescriptiveMetadata(dataset, descriptiveMetadataMap);
    }

    public FileItem getFileItem(EasyUser sessionUser, Dataset dataset, DmoStoreId fileItemId) throws ServiceException {
        FileItem fileItem = getFileItem(dataset, fileItemId);
        String name = fileItem.getAutzStrategyName();
        AuthzStrategy strategy = AuthzStrategyProvider.newAuthzStrategy(name, sessionUser, fileItem, dataset);
        fileItem.setAuthzStrategy(strategy);
        return fileItem;
    }

    public FolderItem getFolderItem(EasyUser sessionUser, Dataset dataset, DmoStoreId folderItemId) throws ServiceException {
        FolderItem folderItem = getFolderItem(dataset, folderItemId);

        return folderItem;
    }

    public FileItem getFileItemByPath(EasyUser sessionUser, Dataset dataset, String path) throws ObjectNotAvailableException, ServiceException {
        FileItemVO fileItemVO;
        try {
            fileItemVO = Data.getFileStoreAccess().findFileByPath(dataset.getDmoStoreId(), path);
        }
        catch (StoreAccessException e) {
            throw new ServiceException(e);
        }
        if (fileItemVO == null) {
            throw new ObjectNotAvailableException("FileItem not found: datasetId=" + dataset.getStoreId() + " path=" + path);
        }
        return getFileItem(sessionUser, dataset, new DmoStoreId(fileItemVO.getSid()));
    }

    public FolderItem getFolderItemByPath(EasyUser sessionUser, Dataset dataset, String path) throws ObjectNotAvailableException, ServiceException {
        FolderItemVO folderItemVO;
        try {
            folderItemVO = Data.getFileStoreAccess().findFolderByPath(dataset.getDmoStoreId(), path);
        }
        catch (StoreAccessException e) {
            throw new ServiceException(e);
        }
        if (folderItemVO == null) {
            throw new ObjectNotAvailableException("FileItem not found: datasetId=" + dataset.getStoreId() + " path=" + path);
        }
        return getFolderItem(sessionUser, dataset, new DmoStoreId(folderItemVO.getSid()));
    }

    public FileItemDescription getFileItemDescription(EasyUser sessionUser, Dataset dataset, FileItem fileItem) throws ServiceException {
        return new FileItemDescription(fileItem);
    }

    public URL getFileContentURL(EasyUser sessionUser, Dataset dataset, FileItem fileItem) throws ServiceException {
        URL url = Data.getEasyStore().getFileURL(fileItem.getDmoStoreId());
        return url;
    }

    public URL getDescriptiveMetadataURL(EasyUser sessionUser, Dataset dataset, DmoStoreId fileItemId) throws ServiceException, RepositoryException {
        URL url = Data.getEasyStore().getDescriptiveMetadataURL(fileItemId);
        return url;
    }

    private FileItem getFileItem(Dataset dataset, DmoStoreId fileItemId) throws ObjectNotAvailableException, ServiceException {
        FileItem fileItem;
        try {
            fileItem = (FileItem) Data.getEasyStore().retrieve(fileItemId);
            if (!fileItem.getDatasetId().equals(dataset.getDmoStoreId())) {
                throw new ObjectNotAvailableException("FileItem '" + fileItemId + "' does not belong to dataset '" + dataset.getStoreId() + "'");
            }
        }
        catch (ObjectNotInStoreException e) {
            throw new ObjectNotAvailableException(e);
        }
        catch (RepositoryException e) {
            throw new ServiceException(e);
        }
        return fileItem;
    }

    private FolderItem getFolderItem(Dataset dataset, DmoStoreId folderItemId) throws ObjectNotAvailableException, ServiceException {
        FolderItem folderItem;
        try {
            folderItem = (FolderItem) Data.getEasyStore().retrieve(folderItemId);
            if (!folderItem.getDatasetId().equals(dataset.getDmoStoreId())) {
                throw new ObjectNotAvailableException("FolderItem '" + folderItemId + "' does not belong to dataset '" + dataset.getStoreId() + "'");
            }
        }
        catch (ObjectNotInStoreException e) {
            throw new ObjectNotAvailableException(e);
        }
        catch (RepositoryException e) {
            throw new ServiceException(e);
        }
        return folderItem;
    }

}
