package nl.knaw.dans.easy;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.business.item.ItemIngesterDelegator;
import nl.knaw.dans.easy.business.md.amd.AdditionalMetadataUpdateStrategy;
import nl.knaw.dans.easy.business.services.EasyItemService;
import nl.knaw.dans.easy.db.testutil.InMemoryDatabase;
import nl.knaw.dans.easy.domain.dataset.FileItemDescription;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.dataset.item.ItemOrder;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.dataset.item.RequestedItem;
import nl.knaw.dans.easy.domain.dataset.item.UpdateInfo;
import nl.knaw.dans.easy.domain.dataset.item.filter.ItemFilters;
import nl.knaw.dans.easy.domain.download.FileContentWrapper;
import nl.knaw.dans.easy.domain.download.ZipFileContentWrapper;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FolderItem;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.worker.WorkListener;
import nl.knaw.dans.easy.servicelayer.services.ItemService;
import nl.knaw.dans.easy.xml.ResourceMetadataList;

import org.apache.commons.lang.NotImplementedException;
import org.dom4j.Element;

public class ItemServiceDelegate implements ItemService
{
    private static final ItemService INSTANCE = new EasyItemService();

    /** An authorization that does not render files invisible even if visible and accessible to anonymous */
    private static final AuthzStrategyTestImpl AUTHZ_STRATEGY = new AuthzStrategyTestImpl();

    private static final NotImplementedException NOT_IMPLEMENTED_EXCEPTION = new NotImplementedException(
            "This ItemServices delegate only implements methods calling the FileStoreAccess which is best mocked via " + InMemoryDatabase.class);

    @Override
    public String getServiceTypeName()
    {
        throw NOT_IMPLEMENTED_EXCEPTION;
    }

    @Override
    public String getServiceDescription()
    {
        throw NOT_IMPLEMENTED_EXCEPTION;
    }

    @Override
    public void doBeanPostProcessing() throws ServiceException
    {
        throw NOT_IMPLEMENTED_EXCEPTION;

    }

    @Override
    public FileItem getFileItem(EasyUser sessionUser, Dataset dataset, DmoStoreId dmoStoreId) throws ObjectNotAvailableException, CommonSecurityException,
            ServiceException
    {
        throw NOT_IMPLEMENTED_EXCEPTION;
    }

    @Override
    public FileItem getFileItemByPath(EasyUser sessionUser, Dataset dataset, String path) throws ObjectNotAvailableException, CommonSecurityException,
            ServiceException
    {
        throw NOT_IMPLEMENTED_EXCEPTION;
    }

    @Override
    public FolderItem getFolderItemByPath(EasyUser sessionUser, Dataset dataset, String path) throws ObjectNotAvailableException, CommonSecurityException,
            ServiceException
    {
        throw NOT_IMPLEMENTED_EXCEPTION;
    }

    @Override
    public FileItemDescription getFileItemDescription(EasyUser sessionUser, Dataset dataset, DmoStoreId fileItemId) throws ObjectNotAvailableException,
            CommonSecurityException, ServiceException
    {
        throw NOT_IMPLEMENTED_EXCEPTION;
    }

    @Override
    public URL getFileContentURL(EasyUser sessionUser, Dataset dataset, FileItem fileItem) throws ObjectNotAvailableException, CommonSecurityException,
            ServiceException
    {
        throw NOT_IMPLEMENTED_EXCEPTION;
    }

    @Override
    public URL getDescriptiveMetadataURL(EasyUser sessionUser, Dataset dataset, DmoStoreId fileItemId) throws ObjectNotAvailableException,
            CommonSecurityException, ServiceException
    {
        throw NOT_IMPLEMENTED_EXCEPTION;
    }

    @Override
    public void addDirectoryContents(EasyUser sessionUser, Dataset dataset, DmoStoreId parentId, File rootFile, List<File> filesToIngest,
            WorkListener... workListeners) throws ServiceException
    {
        throw NOT_IMPLEMENTED_EXCEPTION;

    }

    @Override
    public void addDirectoryContents(EasyUser sessionUser, Dataset dataset, DmoStoreId parentId, File rootFile, ItemIngesterDelegator delegator,
            WorkListener... workListeners) throws ServiceException
    {
        throw NOT_IMPLEMENTED_EXCEPTION;

    }

    @Override
    public void updateObjects(EasyUser sessionUser, Dataset dataset, List<DmoStoreId> sidList, UpdateInfo updateInfo, ItemFilters itemFilters,
            WorkListener... workListeners) throws ServiceException
    {
        throw NOT_IMPLEMENTED_EXCEPTION;

    }

    @Override
    public void updateFileItemMetadata(EasyUser sessionUser, Dataset dataset, File file, AdditionalMetadataUpdateStrategy strategy,
            WorkListener... workListeners) throws ServiceException
    {
        throw NOT_IMPLEMENTED_EXCEPTION;

    }

    @Override
    public void updateFileItemMetadata(EasyUser sessionUser, Dataset dataset, ResourceMetadataList resourceMetadataList,
            AdditionalMetadataUpdateStrategy strategy, WorkListener... workListeners) throws ServiceException
    {
        throw NOT_IMPLEMENTED_EXCEPTION;

    }

    @Override
    public List<FileItemVO> getFiles(EasyUser sessionUser, Dataset dataset, DmoStoreId parentSid, Integer limit, Integer offset, ItemOrder order,
            ItemFilters filters) throws ServiceException
    {
        List<FileItemVO> files = INSTANCE.getFiles(sessionUser, dataset, parentSid, limit, offset, order, filters);
        for (FileItemVO item : files)
            item.setAuthzStrategy(AUTHZ_STRATEGY);
        return files;
    }

    @Override
    public List<FolderItemVO> getFolders(EasyUser sessionUser, Dataset dataset, DmoStoreId parentSid, Integer limit, Integer offset, ItemOrder order,
            ItemFilters filters) throws ServiceException
    {
        return INSTANCE.getFolders(sessionUser, dataset, parentSid, limit, offset, order, filters);
    }

    @Override
    public List<ItemVO> getFilesAndFolders(EasyUser sessionUser, Dataset dataset, DmoStoreId parentSid, Integer limit, Integer offset, ItemOrder order,
            ItemFilters filters) throws ServiceException
    {
        List<ItemVO> items = INSTANCE.getFilesAndFolders(sessionUser, dataset, parentSid, limit, offset, order, filters);
        for (ItemVO item : items)
            if (item instanceof FileItemVO)
                ((FileItemVO) item).setAuthzStrategy(AUTHZ_STRATEGY);
        return items;
    }

    @Override
    public List<ItemVO> getFilesAndFolders(EasyUser sessionUser, Dataset dataset, Collection<DmoStoreId> itemIds) throws ServiceException
    {
        throw NOT_IMPLEMENTED_EXCEPTION;
    }

    @Override
    public Collection<FileItemVO> getFileItemsRecursively(EasyUser sessionUser, Dataset dataset, Collection<FileItemVO> items, ItemFilters filter,
            DmoStoreId... storeIds) throws ServiceException
    {
        Collection<FileItemVO> fileItems = INSTANCE.getFileItemsRecursively(sessionUser, dataset, items, filter, storeIds);
        for (FileItemVO item : fileItems)
            item.setAuthzStrategy(AUTHZ_STRATEGY);
        return fileItems;
    }

    @Override
    public List<String> getFilenames(DmoStoreId parentSid, boolean recursive) throws ServiceException
    {
        return INSTANCE.getFilenames(parentSid, recursive);
    }

    @Override
    public boolean hasChildItems(DmoStoreId parentSid) throws ServiceException
    {
        return INSTANCE.hasChildItems(parentSid);
    }

    @Override
    public FileContentWrapper getContent(EasyUser sessionUser, Dataset dataset, DmoStoreId fileItemId) throws ServiceException
    {
        throw NOT_IMPLEMENTED_EXCEPTION;
    }

    @Override
    public ZipFileContentWrapper getZippedContent(EasyUser sessionUser, Dataset dataset, Collection<RequestedItem> requestedItems) throws ServiceException
    {
        throw NOT_IMPLEMENTED_EXCEPTION;
    }

    @Override
    public void saveDescriptiveMetadata(EasyUser sessionUser, Dataset dataset, Map<String, Element> fileMetadataMap) throws ServiceException
    {
        throw NOT_IMPLEMENTED_EXCEPTION;

    }

    @Override
    public void registerDownload(EasyUser sessionUser, Dataset dataset, List<? extends ItemVO> downloads)
    {
        throw NOT_IMPLEMENTED_EXCEPTION;

    }

    @Override
    public List<FileItemVO> getAccessibleAudioVideoFiles(EasyUser sessionUser, Dataset dataset) throws ServiceException
    {
        throw NOT_IMPLEMENTED_EXCEPTION;
    }

    @Override
    public URL getStreamingHost()
    {
        throw NOT_IMPLEMENTED_EXCEPTION;
    }

    @Override
    public void setMustProcessAudioVideoInstructions(boolean value)
    {
        throw NOT_IMPLEMENTED_EXCEPTION;

    }

    @Override
    public boolean mustProcessAudioVideoInstructions()
    {
        throw NOT_IMPLEMENTED_EXCEPTION;
    }

}
