package nl.knaw.dans.easy.data.store;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.domain.dataset.item.AbstractItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.dataset.item.ItemOrder;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.dataset.item.filter.ItemFilters;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItemVOAttribute;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;

public interface FileStoreAccess {

    FileItemVO findFileById(DmoStoreId dmoStoreId) throws StoreAccessException;

    FolderItemVO findFolderById(DmoStoreId dmoStoreId) throws StoreAccessException;

    // used by DownloadWorker
    List<FileItemVO> findFilesById(Collection<DmoStoreId> sids) throws StoreAccessException;

    // not directly used
    List<FolderItemVO> findFoldersById(Collection<DmoStoreId> sids) throws StoreAccessException;

    /**
     * Gets a list of files from a folder or dataset (based on parentSid). Folders are listed first, the files after, unless sorting is applied to a field that
     * both items (folder and file) have. Paging, filtering and ordering is applied optionally.
     * 
     * @param parentSid
     *        the system id of the parent object
     * @return
     * @throws StoreAccessException
     */
    public List<FileItemVO> getFiles(DmoStoreId parentSid) throws StoreAccessException;

    /**
     * Gets a list of folders from a folder or dataset (based on parentSid). Folders are listed first, the files after, unless sorting is applied to a field
     * that both items (folder and file) have. Paging, filtering and ordering is applied optionally.
     * 
     * @param parentSid
     *        the system id of the parent object
     * @return
     * @throws StoreAccessException
     */
    public List<FolderItemVO> getFolders(DmoStoreId parentSid) throws StoreAccessException;

    /**
     * Gets a list of files and folders from a folder or dataset (based on parentSid). Folders are listed first, the files after, unless sorting is applied to a
     * field that both items (folder and file) have. Paging, filtering and ordering is applied optionally.
     * 
     * @param parentSid
     *        the system id of the parent object
     * @return
     * @throws StoreAccessException
     */
    public List<ItemVO> getFilesAndFolders(DmoStoreId parentSid) throws StoreAccessException;

    /**
     * Returns a list of filenames with their full path. First the files are listed then the folders. If recursive is on the folders and their files are
     * separated by a backslash (/) sign. Folders are not listed separately unless they are empty. Empty folders also have a trailing backslash, thus making it
     * possible for the user to always distinguish between a folder and a file.
     * 
     * @param parentSid
     *        the system id of the parent object
     * @return returns a list of filenames with their full path
     * @throws StoreException
     */
    List<String> getFilenames(DmoStoreId parentSid) throws StoreAccessException;

    /**
     * Returns true when the item container contains one or more child items (folders or files).
     * 
     * @param parentSid
     *        the system id of the parent object
     * @param folderName
     *        the name of the folder to check for
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
     * 
     * @param storeId
     *        storeId with namespace "easy-file" or "easy-folder"
     * @return the datasetId or <code>null</code> if an object with <code>storeId</code> was not found
     * @throws StoreException
     *         wrapper for exceptions
     * @throws StoreAccessException
     */
    String getDatasetId(DmoStoreId storeId) throws StoreException, StoreAccessException;

    /**
     * Get the total number of files or folders in a dataset or folder.
     * 
     * @param storeId
     *        id of a container (dataset or folder)
     * @param memberClass
     *        type of members (files or folders) to count recursively
     * @param fieldValue
     *        if present only files are counted with the specified value.
     * @return
     * @throws StoreAccessException
     */
    public int getTotalMemberCount(final DmoStoreId storeId, Class<? extends AbstractItemVO> memberClass, FileItemVOAttribute... attribute)
            throws StoreAccessException;

    boolean hasMember(DmoStoreId storeId, Class<? extends AbstractItemVO> memberClass) throws StoreAccessException;

    boolean hasMember(DmoStoreId storeId, Class<? extends AbstractItemVO> memberClass, FileItemVOAttribute fieldValue) throws StoreAccessException;

    /**
     * Check if any files of a data set are visible to a user with specified permissions.
     * 
     * @param storeId
     * @param userIsKnown
     * @param userHasGroupAccess
     * @param userHasPermissionAccess
     * @return true if the dataset has any file visible for the specified permissions.
     * @throws StoreAccessException
     */
    boolean hasVisibleFiles(DmoStoreId storeId, boolean userIsKnown, boolean userHasGroupAccess, boolean userHasPermissionAccess) throws StoreAccessException;

    public FolderItemVO getFolderItemVO(DmoStoreId itemContainer) throws StoreAccessException;

    FolderItemVO getRootFolder(DmoStoreId dmoStoreId) throws StoreAccessException;

    Set<AccessibleTo> getItemVoAccessibilities(ItemVO item) throws StoreAccessException;

    Set<VisibleTo> getItemVoVisibilities(ItemVO item) throws StoreAccessException;

    Set<CreatorRole> getItemVoCreatorRoles(ItemVO item) throws StoreAccessException;

    boolean belongsItemTo(ItemVO item, Dataset dataset);
}