package nl.knaw.dans.easy.data.store;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.dataset.item.ItemOrder;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.dataset.item.filter.ItemFilters;

public interface FileStoreAccess
{

    FileItemVO findFileById(DmoStoreId dmoStoreId) throws StoreAccessException;

    FolderItemVO findFolderById(DmoStoreId dmoStoreId) throws StoreAccessException;

    // used by DownloadWorker
    List<FileItemVO> findFilesById(Collection<DmoStoreId> sids) throws StoreAccessException;

    // not directly used
    List<FolderItemVO> findFoldersById(Collection<DmoStoreId> sids) throws StoreAccessException;

    // used by EasyItemService.getFilesAndFolders(user, dataset, collection) <-- not used
    List<ItemVO> findFilesAndFoldersById(Collection<DmoStoreId> sids) throws StoreAccessException;

    /**
     * Gets a list of files from a folder or dataset (based on parentSid).
     * Folders are listed first, the files after, unless sorting is applied to a field
     * that both items (folder and file) have.
     * Paging, filtering and ordering is applied optionally.
     * @param parentSid the system id of the parent object
     * @param limit the maximum return array count or -1 for unlimited
     * @param offset at what point in the list to start getting objects or -1  if the
     * offset is not important
     * @param order the field on which to order or null if ordering is unimportant 
     * @param filters one or more filters that may be applied or null when no filters need 
     * to be applied
     * @return
     * @throws StoreAccessException
     */
    public List<FileItemVO> getFiles(DmoStoreId parentSid, Integer limit, Integer offset, ItemOrder order, ItemFilters filters) throws StoreAccessException;

    /**
     * Gets a list of folders from a folder or dataset (based on parentSid).
     * Folders are listed first, the files after, unless sorting is applied to a field
     * that both items (folder and file) have.
     * Paging, filtering and ordering is applied optionally.
     * @param parentSid the system id of the parent object
     * @param limit the maximum return array count or -1 for unlimited
     * @param offset at what point in the list to start getting objects or -1  if the
     * offset is not important
     * @param order the field on which to order or null if ordering is unimportant 
     * @param filters one or more filters that may be applied or null when no filters need 
     * to be applied
     * @return
     * @throws StoreAccessException
     */
    public List<FolderItemVO> getFolders(DmoStoreId parentSid, Integer limit, Integer offset, ItemOrder order, ItemFilters filters) throws StoreAccessException;

    /**
     * Gets a list of files and folders from a folder or dataset (based on parentSid).
     * Folders are listed first, the files after, unless sorting is applied to a field
     * that both items (folder and file) have.
     * Paging, filtering and ordering is applied optionally.
     * @param parentSid the system id of the parent object
     * @param limit the maximum return array count or -1 for unlimited
     * @param offset at what point in the list to start getting objects or -1  if the
     * offset is not important
     * @param order the field on which to order or null if ordering is unimportant 
     * @param filters one or more filters that may be applied or null when no filters need 
     * to be applied
     * @return
     * @throws StoreAccessException
     */
    public List<ItemVO> getFilesAndFolders(DmoStoreId parentSid, Integer limit, Integer offset, ItemOrder order, ItemFilters filters)
            throws StoreAccessException;

    /**
     * Returns a list of filenames with their full path. First the files
     * are listed then the folders. If recursive is on the folders and
     * their files are separated by a backslash (/) sign. Folders are
     * not listed separately unless they are empty. Empty folders also
     * have a trailing backslash, thus making it possible for the user
     * to always distinguish between a folder and a file.     
     * @param parentSid the system id of the parent object
     * @param recursive set to true to get the filenames of the folders
     * @return returns a list of filenames with their full path
     * @throws StoreException
     */
    List<String> getFilenames(DmoStoreId parentSid, boolean recursive) throws StoreAccessException;

    /**
     * Returns true when the item container contains one or more child items 
     * (folders or files).  
     * @param parentSid the system id of the parent object
     * @param folderName the name of the folder to check for
     * @return true when the parentSid has a folder with name folderName 
     * @throws StoreException
     */
    boolean hasChildItems(DmoStoreId parentSid) throws StoreAccessException;

    /**
     * @return map with key=storeId and value=name pairs.
     */
    Map<String, String> getAllFiles(DmoStoreId datasetStoreId) throws StoreAccessException;

    List<FileItemVO> getDatasetFiles(DmoStoreId dmoStoreId) throws StoreAccessException;

    FileItemVO findFileByPath(DmoStoreId datasetSid, String relativePath) throws StoreAccessException;

    FolderItemVO findFolderByPath(DmoStoreId datasetSid, String relativePath) throws StoreAccessException;

    /**
     * Get the datasetId of the dataset the item with <code>storeId</code> belongs to.
     * @param storeId storeId with namespace "easy-file" or "easy-folder"
     * @return the datasetId or <code>null</code> if an object with <code>storeId</code> was not found
     * @throws StoreException wrapper for exceptions
     */
    String getDatasetId(DmoStoreId storeId) throws StoreException;
}
