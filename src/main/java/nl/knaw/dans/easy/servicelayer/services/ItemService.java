package nl.knaw.dans.easy.servicelayer.services;

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.business.item.ItemIngesterDelegator;
import nl.knaw.dans.easy.business.md.amd.AdditionalMetadataUpdateStrategy;
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
import nl.knaw.dans.easy.domain.model.DatasetItemContainer;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FolderItem;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.worker.WorkListener;
import nl.knaw.dans.easy.xml.ResourceMetadata;
import nl.knaw.dans.easy.xml.ResourceMetadataList;

import org.dom4j.Element;

/**
 * Service for items.
 * <p/>
 * WARNING <br/>
 * As some of the processes that can be initiated through services offered by this service affect
 * multiple objects, beware of stale objects in the presentation layer.
 */
public interface ItemService extends EasyService
{

    FileItem getFileItem(EasyUser sessionUser, Dataset dataset, DmoStoreId dmoStoreId) throws ObjectNotAvailableException, CommonSecurityException,
            ServiceException;

    FileItem getFileItemByPath(EasyUser sessionUser, Dataset dataset, String path) throws ObjectNotAvailableException, CommonSecurityException,
            ServiceException;

    FolderItem getFolderItemByPath(EasyUser sessionUser, Dataset dataset, String path) throws ObjectNotAvailableException, CommonSecurityException,
            ServiceException;

    FileItemDescription getFileItemDescription(EasyUser sessionUser, Dataset dataset, DmoStoreId fileItemId) throws ObjectNotAvailableException,
            CommonSecurityException, ServiceException;

    URL getFileContentURL(EasyUser sessionUser, Dataset dataset, FileItem fileItem) throws ObjectNotAvailableException, CommonSecurityException,
            ServiceException;

    URL getDescriptiveMetadataURL(EasyUser sessionUser, Dataset dataset, DmoStoreId fileItemId) throws ObjectNotAvailableException, CommonSecurityException,
            ServiceException;

    /**
     * Add the contents of the given directory <code>rootFile</code> as {@link FolderItem}s and
     * {@link FileItem}s to the {@link DatasetItemContainer} with the given <code>parentId</code>. This
     * process can be used to ingest or to update a folder/file-structure.
     * <p/>
     * If updating, the name of files and folders that are to be added, serve as identifier. If an Item
     * with an identical name already exist under the same ItemContainer, the item is updated. If not, it
     * is ingested. Note: If an existing FolderItem with name 'X' during an update is replaced by a file
     * (not a directory) with name 'X' (or vice versa) a ServiceException will be thrown.
     * <p/>
     * The given {@link WorkListener} can stop the ongoing process by returning <code>true</code> upon
     * one of it's method calls. If so, a roll back is performed. Roll back only affects newly ingested
     * files and folders.
     * <p/>
     * Note that {@link DataModelObject}s that live elsewhere, and who's state is affected by this
     * transaction will become stale.
     * <p/>
     * If <code>parentId == null</code> the given dataset will be the parent.
     * 
     * @param sessionUser
     *        the user that initiates this action
     * @param dataset
     *        the dataset that is augmented
     * @param parentId
     *        storeId of the parent, can be <code>null</code>
     * @param rootFile
     *        directory with contents (0,* files and/or 0,* folders)
     * @param filesToIngest
     *        a list with files (directories and files) to ingest
     * @param workListeners
     *        listener(s) for events in the process, can be null
     * @throws ServiceException
     *         wrapper for exceptions
     */
    void addDirectoryContents(EasyUser sessionUser, Dataset dataset, DmoStoreId parentId, File rootFile, List<File> filesToIngest, WorkListener... workListeners)
            throws ServiceException;

    // used by easyTools batch ingest
    void addDirectoryContents(EasyUser sessionUser, Dataset dataset, DmoStoreId parentId, File rootFile, ItemIngesterDelegator delegator,
            WorkListener... workListeners) throws ServiceException;

    /**
     * Update the objects listed in sidList to the state specified in updateInfo, using the given
     * itemFilters to filter affected items.
     * 
     * @param sessionUser
     *        the user that initiates this action
     * @param dataset
     *        the dataset that is affected
     * @param sidList
     *        a list of storeId's
     * @param updateInfo
     *        what should be updated
     * @param itemFilters
     *        which items are affected
     * @param workListeners
     *        listener(s) for events in the process, can be null
     * @throws ServiceException
     *         wrapper for exceptions
     */
    void updateObjects(EasyUser sessionUser, Dataset dataset, List<DmoStoreId> sidList, UpdateInfo updateInfo, ItemFilters itemFilters,
            WorkListener... workListeners) throws ServiceException;

    /**
     * Update FileItem metadata according to a {@link ResourceMetadataList}. The ResourceMetadataList
     * contains sections of {@link ResourceMetadata} identified with the fileItemId or the relative path
     * within the dataset.
     * 
     * @param sessionUser
     *        the user that initiates this action
     * @param dataset
     *        the dataset that is affected
     * @param file
     *        an xml-file that can be deserialized as a {@link ResourceMetadataList}
     * @param strategy
     *        the {@link AdditionalMetadataUpdateStrategy} to use
     * @param workListeners
     *        listener(s) for events in the process, can be null
     * @throws ServiceException
     *         wrapper for exceptions
     */
    void updateFileItemMetadata(EasyUser sessionUser, Dataset dataset, File file, AdditionalMetadataUpdateStrategy strategy, WorkListener... workListeners)
            throws ServiceException;

    /**
     * Update FileItem metadata according to a {@link ResourceMetadataList}. The ResourceMetadataList
     * contains sections of {@link ResourceMetadata} identified with the fileItemId or the relative path
     * within the dataset.
     * 
     * @param sessionUser
     *        the user that initiates this action
     * @param dataset
     *        the dataset that is affected
     * @param resourceMetadataList
     *        the object containing ResourceMetadata
     * @param strategy
     *        the {@link AdditionalMetadataUpdateStrategy} to use
     * @param workListeners
     *        listener(s) for events in the process, can be null
     * @throws ServiceException
     *         wrapper for exceptions
     */
    void updateFileItemMetadata(EasyUser sessionUser, Dataset dataset, ResourceMetadataList resourceMetadataList, AdditionalMetadataUpdateStrategy strategy,
            WorkListener... workListeners) throws ServiceException;

    /**
     * Gets a list of files from a folder or dataset (based on parentSid). Folders are listed first, the
     * files after, unless sorting is applied to a field that both items (folder and file) have. Paging,
     * filtering and ordering is applied optionally.
     * 
     * @param sessionUser
     * @param dataset
     * @param parentSid
     *        the system id of the parent object
     * @param limit
     *        the maximum return array count or -1 for unlimited
     * @param offset
     *        at what point in the list to start getting objects or -1 if the offset is not important
     * @param order
     *        the field on which to order or null if ordering is unimportant
     * @param filters
     *        one or more filters that may be applied or null when no filters need to be applied
     * @return
     * @throws ServiceException
     */
    public List<FileItemVO> getFiles(EasyUser sessionUser, Dataset dataset, DmoStoreId parentSid, Integer limit, Integer offset, ItemOrder order,
            ItemFilters filters) throws ServiceException;

    /**
     * Gets a list of folders from a folder or dataset (based on parentSid). Folders are listed first,
     * the files after, unless sorting is applied to a field that both items (folder and file) have.
     * Paging, filtering and ordering is applied optionally.
     * 
     * @param sessionUser
     * @param dataset
     * @param parentSid
     *        the system id of the parent object
     * @param limit
     *        the maximum return array count or -1 for unlimited
     * @param offset
     *        at what point in the list to start getting objects or -1 if the offset is not important
     * @param order
     *        the field on which to order or null if ordering is unimportant
     * @param filters
     *        one or more filters that may be applied or null when no filters need to be applied
     * @return
     * @throws ServiceException
     */
    public List<FolderItemVO> getFolders(EasyUser sessionUser, Dataset dataset, DmoStoreId parentSid, Integer limit, Integer offset, ItemOrder order,
            ItemFilters filters) throws ServiceException;

    /**
     * Gets a list of files and folders from a folder or dataset (based on parentSid). Folders are listed
     * first, the files after, unless sorting is applied to a field that both items (folder and file)
     * have. Paging, filtering and ordering is applied optionally.
     * 
     * @param parentSid
     *        the system id of the parent object
     * @param limit
     *        the maximum return array count or -1 for unlimited
     * @param offset
     *        at what point in the list to start getting objects or -1 if the offset is not important
     * @param order
     *        the field on which to order or null if ordering is unimportant
     * @param filters
     *        one or more filters that may be applied or null when no filters need to be applied
     * @return
     * @throws ServiceAccessException
     */
    List<ItemVO> getFilesAndFolders(EasyUser sessionUser, Dataset dataset, DmoStoreId parentSid, Integer limit, Integer offset, ItemOrder order, ItemFilters filters)
            throws ServiceException;

    List<ItemVO> getFilesAndFolders(EasyUser sessionUser, Dataset dataset, Collection<DmoStoreId> itemIds) throws ServiceException;

    Collection<FileItemVO> getFileItemsRecursively(EasyUser sessionUser, Dataset dataset, final Collection<FileItemVO> items, final ItemFilters filter,
            final DmoStoreId... storeIds) throws ServiceException;

    /**
     * Returns a list of filenames with their full path
     * 
     * @param parentSid
     *        the system id of the parent object
     * @param recursive
     *        set to true to get the filenames of the folders
     * @return returns a list of filenames with their full path
     * @throws ServiceException
     */
    List<String> getFilenames(DmoStoreId parentSid, boolean recursive) throws ServiceException;

    /**
     * Returns true when the item container contains one or more child items
     * 
     * @param parentSid
     *        the system id of the parent object
     * @param folderName
     *        the name of the folder to check for
     * @return true when the parentSid has a folder with name folderName
     * @throws ServiceException
     */
    boolean hasChildItems(DmoStoreId parentSid) throws ServiceException;

    /**
     * Get a download connection for a certain file.
     * 
     * @param sessionUser
     *        the user that initiates this action
     * @param dataset
     *        the dataset that is the parent of the file
     * @param fileItemId
     *        the storeId of the FileItem
     * @return URL wrapped in a FileContentWrapper
     * @throws ServiceException
     *         wrapper for exceptions
     */
    FileContentWrapper getContent(EasyUser sessionUser, Dataset dataset, DmoStoreId fileItemId) throws ServiceException;

    ZipFileContentWrapper getZippedContent(EasyUser sessionUser, final Dataset dataset, final Collection<RequestedItem> requestedItems) throws ServiceException;

    @Deprecated
    void saveDescriptiveMetadata(EasyUser sessionUser, final Dataset dataset, final Map<String, Element> fileMetadataMap) throws ServiceException;

    void registerDownload(EasyUser sessionUser, Dataset dataset, List<? extends ItemVO> downloads);
}
