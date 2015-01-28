package nl.knaw.dans.easy;

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.security.authz.AuthzStrategy;
import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.business.item.ItemIngesterDelegator;
import nl.knaw.dans.easy.business.md.amd.AdditionalMetadataUpdateStrategy;
import nl.knaw.dans.easy.business.services.EasyItemService;
import nl.knaw.dans.easy.data.store.StoreAccessException;
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
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FolderItem;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.worker.WorkListener;
import nl.knaw.dans.easy.security.authz.EasyFileItemVOAuthzStrategy;
import nl.knaw.dans.easy.servicelayer.services.ItemService;
import nl.knaw.dans.easy.xml.ResourceMetadataList;

import org.apache.commons.lang.NotImplementedException;
import org.dom4j.Element;

public class ItemServiceDelegate implements ItemService {

    private static final ItemService INSTANCE = new EasyItemService();

    private static final NotImplementedException NOT_IMPLEMENTED_EXCEPTION = new NotImplementedException(
            "This ItemServices delegates only methods calling the FileStoreAccess which is best mocked via " + FileStoreMocker.class);

    private static final AuthzStrategy PERMISSIVE_STRATEGY = new EasyFileItemVOAuthzStrategy() {
        private static final long serialVersionUID = 1L;

        @Override
        protected boolean canAllBeRead() {
            return true;
        }

        @Override
        public boolean canUnitBeDiscovered(String unitId) {
            return true;
        }

        @Override
        public boolean canUnitBeRead(String unitId) {
            return true;
        }
    };

    @Override
    public String getServiceTypeName() {
        throw NOT_IMPLEMENTED_EXCEPTION;
    }

    @Override
    public String getServiceDescription() {
        throw NOT_IMPLEMENTED_EXCEPTION;
    }

    @Override
    public void doBeanPostProcessing() throws ServiceException {
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
    public void updateObjects(EasyUser sessionUser, Dataset dataset, List<DmoStoreId> sidList, UpdateInfo updateInfo) throws ServiceException {
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
    public List<FileItemVO> getFiles(EasyUser sessionUser, Dataset dataset, DmoStoreId parentSid) throws ServiceException {
        List<FileItemVO> files = INSTANCE.getFiles(sessionUser, dataset, parentSid);
        for (FileItemVO item : files)
            item.setAuthzStrategy(PERMISSIVE_STRATEGY);
        return files;
    }

    @Override
    public List<FolderItemVO> getFolders(EasyUser sessionUser, Dataset dataset, DmoStoreId parentSid, Integer limit, Integer offset, ItemOrder order,
            ItemFilters filters) throws ServiceException
    {
        List<FolderItemVO> fodlers = INSTANCE.getFolders(sessionUser, dataset, parentSid, limit, offset, order, filters);
        for (ItemVO item : fodlers)
            ((FolderItemVO) item).setAuthzStrategy(PERMISSIVE_STRATEGY);
        return fodlers;
    }

    @Override
    public List<ItemVO> getFilesAndFolders(EasyUser sessionUser, Dataset dataset, DmoStoreId parentSid) throws ServiceException {
        List<ItemVO> items = INSTANCE.getFilesAndFolders(sessionUser, dataset, parentSid);
        for (ItemVO item : items)
            if (item instanceof FileItemVO)
                ((FileItemVO) item).setAuthzStrategy(PERMISSIVE_STRATEGY);
            else if (item instanceof FolderItemVO)
                ((FolderItemVO) item).setAuthzStrategy(PERMISSIVE_STRATEGY);
        return items;
    }

    @Override
    public Collection<FileItemVO> getFileItemsRecursively(EasyUser sessionUser, Dataset dataset, Collection<FileItemVO> items, ItemFilters filter,
            DmoStoreId... storeIds) throws ServiceException
    {
        Collection<FileItemVO> fileItems = INSTANCE.getFileItemsRecursively(sessionUser, dataset, items, filter, storeIds);
        for (FileItemVO item : fileItems)
            item.setAuthzStrategy(PERMISSIVE_STRATEGY);
        return fileItems;
    }

    @Override
    public List<String> getFilenames(DmoStoreId parentSid) throws ServiceException {
        return INSTANCE.getFilenames(parentSid);
    }

    @Override
    public boolean hasChildItems(DmoStoreId parentSid) throws ServiceException {
        return INSTANCE.hasChildItems(parentSid);
    }

    @Override
    public FileContentWrapper getContent(EasyUser sessionUser, Dataset dataset, DmoStoreId fileItemId) throws ServiceException {
        throw NOT_IMPLEMENTED_EXCEPTION;
    }

    @Override
    public ZipFileContentWrapper getZippedContent(EasyUser sessionUser, Dataset dataset, Collection<RequestedItem> requestedItems) throws ServiceException {
        throw NOT_IMPLEMENTED_EXCEPTION;
    }

    @Override
    public void saveDescriptiveMetadata(EasyUser sessionUser, Dataset dataset, Map<String, Element> fileMetadataMap) throws ServiceException {
        throw NOT_IMPLEMENTED_EXCEPTION;

    }

    @Override
    public void registerDownload(EasyUser sessionUser, Dataset dataset, List<? extends ItemVO> downloads) {
        throw NOT_IMPLEMENTED_EXCEPTION;

    }

    @Override
    public List<FileItemVO> getAccessibleAudioVideoFiles(EasyUser sessionUser, Dataset dataset) throws ServiceException {
        throw NOT_IMPLEMENTED_EXCEPTION;
    }

    @Override
    public URL getStreamingHost() {
        throw NOT_IMPLEMENTED_EXCEPTION;
    }

    @Override
    public void setProcessDataFileInstructions(boolean value) {
        throw NOT_IMPLEMENTED_EXCEPTION;

    }

    @Override
    public boolean mustProcessDataFileInstructions() {
        throw NOT_IMPLEMENTED_EXCEPTION;
    }

    @Override
    public FolderItemVO getRootFolder(EasyUser sessionUser, Dataset dataset, DmoStoreId dmoStoreId) throws ServiceException {
        FolderItemVO folder = INSTANCE.getRootFolder(sessionUser, dataset, dmoStoreId);
        folder.setAuthzStrategy(PERMISSIVE_STRATEGY);
        return folder;
    }

    @Override
    public Set<AccessibleTo> getItemVoAccessibilities(ItemVO item) throws StoreAccessException {
        return INSTANCE.getItemVoAccessibilities(item);
    }

    @Override
    public Set<VisibleTo> getItemVoVisibilities(ItemVO item) throws StoreAccessException {
        return INSTANCE.getItemVoVisibilities(item);
    }

    @Override
    public Set<CreatorRole> getItemVoCreatorRoles(ItemVO item) throws StoreAccessException {
        return INSTANCE.getItemVoCreatorRoles(item);
    }

    /**
     * Adds expectations with StubDelegates for all methods calling FileStoreAccess to enable the use if InMemoryDatabase. Any method that returns ItemVO's
     * overrides the default AuthzStrategy with a permissive version. Having this method here and not on EasyApplicationContextMocks increases the chance that
     * developers extend this method when new methods are added to the service. Automatically added methods will be added to the bottom of this source, please
     * move this method down.
     * 
     * @param mock
     *        an ItemService created with PowerMock or EasyMock
     * @return the argument is returned for convenience
     * @throws ServiceException
     *         any expectations of exceptions must be set before calling this method
     * @throws StoreAccessException
     *         any expectations of exceptions must be set before calling this method
     */
    @SuppressWarnings("unchecked")
    static ItemService delegate(ItemService mock) throws ServiceException, StoreAccessException {

        ItemService delegate = new ItemServiceDelegate();
        expect(mock.getFiles(anyObject(EasyUser.class), anyObject(Dataset.class), anyObject(DmoStoreId.class))).andStubDelegateTo(delegate);
        expect(mock.getFilesAndFolders(anyObject(EasyUser.class), anyObject(Dataset.class), anyObject(DmoStoreId.class))).andStubDelegateTo(delegate);
        expect(
                mock.getFolders(anyObject(EasyUser.class), anyObject(Dataset.class), anyObject(DmoStoreId.class), anyInt(), anyInt(),
                        anyObject(ItemOrder.class), anyObject(ItemFilters.class))).andStubDelegateTo(delegate);
        expect(
                mock.getFileItemsRecursively(anyObject(EasyUser.class), anyObject(Dataset.class), (Collection<FileItemVO>) anyObject(),
                        anyObject(ItemFilters.class))).andStubDelegateTo(delegate);
        expect(
                mock.getFileItemsRecursively(anyObject(EasyUser.class), anyObject(Dataset.class), (Collection<FileItemVO>) anyObject(),
                        anyObject(ItemFilters.class), anyObject(DmoStoreId.class))).andStubDelegateTo(delegate);
        expect(
                mock.getFileItemsRecursively(anyObject(EasyUser.class), anyObject(Dataset.class), (Collection<FileItemVO>) anyObject(),
                        anyObject(ItemFilters.class), (DmoStoreId[]) anyObject())).andStubDelegateTo(delegate);
        expect(mock.getFilenames(anyObject(DmoStoreId.class))).andStubDelegateTo(delegate);
        expect(mock.hasChildItems(anyObject(DmoStoreId.class))).andStubDelegateTo(delegate);
        expect(mock.getRootFolder(anyObject(EasyUser.class), anyObject(Dataset.class), anyObject(DmoStoreId.class))).andStubDelegateTo(delegate);
        expect(mock.getItemVoAccessibilities(anyObject(ItemVO.class))).andStubDelegateTo(delegate);
        expect(mock.getItemVoVisibilities(anyObject(ItemVO.class))).andStubDelegateTo(delegate);
        expect(mock.getItemVoCreatorRoles(anyObject(ItemVO.class))).andStubDelegateTo(delegate);
        return mock;
    }
}
