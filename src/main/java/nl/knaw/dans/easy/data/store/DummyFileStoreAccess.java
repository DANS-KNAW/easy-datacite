package nl.knaw.dans.easy.data.store;

import static nl.knaw.dans.easy.domain.model.user.CreatorRole.ARCHIVIST;
import static nl.knaw.dans.easy.domain.model.user.CreatorRole.DEPOSITOR;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemAccessibleTo;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemCreatorRole;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVisibleTo;
import nl.knaw.dans.easy.domain.dataset.item.ItemOrder;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.dataset.item.filter.ItemFilters;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FolderItem;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyFileStoreAccess implements FileStoreAccess
{

    private static final Logger LOGGER = LoggerFactory.getLogger(DummyFileStoreAccess.class);

    /** sid prefix */
    private static final String FILE = FileItem.NAMESPACE + "-dummy:";

    /** sid prefix */
    private static final String FOLDER = FolderItem.NAMESPACE + "-dummy:";

    /** sid */
    public static final String DUMMY_DATASET_SID = Dataset.NAMESPACE + "-dummy:1";

    public static final String EMPTY_DATASET_SID = Dataset.NAMESPACE + "-dummy:0";

    /** key-value pairs where the key is a sid and the value is a file or folder */
    private final Map<String, ItemVO> items;

    /** key-value pairs where the key is a sid and the value is content of the folder */
    private final Map<String, List<ItemVO>> compositions;

    public DummyFileStoreAccess()
    {
        LOGGER.warn("using debug class");
        items = new HashMap<String, ItemVO>();
        compositions = new HashMap<String, List<ItemVO>>();

        // create the content before the folder
        addFile(FILE + 1, DUMMY_DATASET_SID, ARCHIVIST, VisibleTo.NONE, AccessibleTo.KNOWN, "somefile.txt", "txt", 1024, "/Folder2"); // FOLDER 2
        addFile(FILE + 2, DUMMY_DATASET_SID, DEPOSITOR, VisibleTo.KNOWN, AccessibleTo.RESTRICTED_REQUEST, "hello.world", "doc", 1024, "/Folder2"); // FOLDER 2
        addFile(FILE + 3, DUMMY_DATASET_SID, ARCHIVIST, VisibleTo.ANONYMOUS, AccessibleTo.ANONYMOUS, "foo", "pdf", 1024, "/"); // ROOT
        addFile(FILE + 4, DUMMY_DATASET_SID, DEPOSITOR, VisibleTo.KNOWN, AccessibleTo.RESTRICTED_REQUEST, "bar", "png", 1024, "/"); // ROOT
        addFile(FILE + 5, DUMMY_DATASET_SID, DEPOSITOR, VisibleTo.KNOWN, AccessibleTo.RESTRICTED_GROUP, "testfile", "txt", 1024, "/Folder4"); // FOLDER 4
        addFile(FILE + 6, DUMMY_DATASET_SID, DEPOSITOR, VisibleTo.KNOWN, AccessibleTo.NONE, "jokefile", "grp", 2048, "/Folder4"); // FOLDER 4

        addFolder(FOLDER + 1, FOLDER + 2, "/Folder2", DUMMY_DATASET_SID);
        addFolder(FOLDER + 2, DUMMY_DATASET_SID, "/", DUMMY_DATASET_SID, FOLDER + 1, FILE + 1, FILE + 2);
        addFolder(FOLDER + 3, DUMMY_DATASET_SID, "/", DUMMY_DATASET_SID);
        addFolder(FOLDER + 4, DUMMY_DATASET_SID, "/", DUMMY_DATASET_SID, FILE + 5, FILE + 6);

        addFolder(DUMMY_DATASET_SID, DUMMY_DATASET_SID, "/", DUMMY_DATASET_SID, FOLDER + 2, FOLDER + 3, FOLDER + 4, FILE + 3, FILE + 4);

        addFolder("0", "0", "0", "0");
    }

    public void addFile(final String sid, final String datasetSid, final CreatorRole creatorRole, final VisibleTo visibleTo, final AccessibleTo accessibleTo,
            final String name, final String mimetype, final int size, String path)
    {
        final FileItemVO file = new FileItemVO();
        file.setSid(sid);
        file.setDatasetSid(datasetSid);
        file.setCreatorRole(creatorRole);
        file.setName(name);
        file.setMimetype(mimetype);
        file.setSize(size);
        file.setVisibleTo(visibleTo);
        file.setAccessibleTo(accessibleTo);
        file.setPath(path);
        items.put(sid, file);
    }

    public void addFolder(final String folderSid, final String parentSid, final String datasetSid, String path, final String... contentSids)
    {
        final List<ItemVO> composition = new ArrayList<ItemVO>();
        final String name = folderSid.replace(":", "");

        final Set<FolderItemVisibleTo> visibleTos = new HashSet<FolderItemVisibleTo>();
        final Set<FolderItemAccessibleTo> accessibleTos = new HashSet<FolderItemAccessibleTo>();
        final Set<FolderItemCreatorRole> folderCreators = new HashSet<FolderItemCreatorRole>();
        int childCount = 0;

        if (contentSids.length == 0)
        {
            folderCreators.add(new FolderItemCreatorRole(folderSid, ARCHIVIST));
            folderCreators.add(new FolderItemCreatorRole(folderSid, DEPOSITOR));
            visibleTos.add(new FolderItemVisibleTo(folderSid, VisibleTo.NONE));
            visibleTos.add(new FolderItemVisibleTo(folderSid, VisibleTo.KNOWN));
            visibleTos.add(new FolderItemVisibleTo(folderSid, VisibleTo.ANONYMOUS));
            visibleTos.add(new FolderItemVisibleTo(folderSid, VisibleTo.RESTRICTED_GROUP));
            visibleTos.add(new FolderItemVisibleTo(folderSid, VisibleTo.RESTRICTED_REQUEST));
            accessibleTos.add(new FolderItemAccessibleTo(folderSid, AccessibleTo.NONE));
            accessibleTos.add(new FolderItemAccessibleTo(folderSid, AccessibleTo.KNOWN));
            accessibleTos.add(new FolderItemAccessibleTo(folderSid, AccessibleTo.ANONYMOUS));
            accessibleTos.add(new FolderItemAccessibleTo(folderSid, AccessibleTo.RESTRICTED_GROUP));
            accessibleTos.add(new FolderItemAccessibleTo(folderSid, AccessibleTo.RESTRICTED_REQUEST));
        }
        else
        {
            for (final String contentSid : contentSids)
            {
                final ItemVO item = items.get(contentSid);
                composition.add(item);
                if (item instanceof FolderItemVO)
                {
                    final FolderItemVO subFolder = (FolderItemVO) item;
                    for (final FolderItemCreatorRole creatorRole : subFolder.getCreatorRoles())
                    {
                        folderCreators.add(new FolderItemCreatorRole(folderSid, creatorRole.getCreatorRole()));
                    }
                    for (final FolderItemVisibleTo visibleTo : subFolder.getVisibleToList())
                    {
                        visibleTos.add(new FolderItemVisibleTo(folderSid, visibleTo.getVisibleTo()));
                    }
                    for (final FolderItemAccessibleTo accessibleTo : subFolder.getAccessibleToList())
                    {
                        accessibleTos.add(new FolderItemAccessibleTo(folderSid, accessibleTo.getAccessibleTo()));
                    }
                }
                else if (item instanceof FileItemVO)
                {
                    final FileItemVO file = (FileItemVO) item;
                    final CreatorRole createrRole = file.getCreatorRole();
                    folderCreators.add(new FolderItemCreatorRole(folderSid, createrRole));
                    visibleTos.add(new FolderItemVisibleTo(folderSid, file.getVisibleTo()));
                    accessibleTos.add(new FolderItemAccessibleTo(folderSid, file.getAccessibleTo()));
                }
                item.setParentSid(folderSid);
                childCount++;
            }
        }
        final FolderItemVO folder = new FolderItemVO();
        folder.setSid(folderSid);
        folder.setName(name);
        folder.setParentSid(parentSid);
        folder.setDatasetSid(datasetSid);
        folder.setCreatorRoles(folderCreators);
        folder.setVisibleToList(visibleTos);
        folder.setAccessibleToList(accessibleTos);
        folder.setChildItemCount(childCount);
        folder.setPath(path);
        compositions.put(folderSid, composition);
        items.put(folderSid, folder);
    }

    public List<ItemVO> getFilesAndFolders(final DmoStoreId parentSid, final Integer limit,//
            final Integer offset, final ItemOrder order, final ItemFilters filters) throws StoreAccessException
    {
        if (limit > 0 || offset > 0 || order != null)
            throw new StoreAccessException("order and pages not implemented yet.");
        if (!compositions.containsKey(parentSid.getStoreId()))
            throw new StoreAccessException(parentSid + " not found.");

        List<ItemVO> result = new ArrayList<ItemVO>();
        final List<ItemVO> composition = compositions.get(parentSid);
        if (composition == null)
            return result;
        ;
        for (final ItemVO item : composition)
        {
            /*
             * Returns cloned objects, so that the internal composition cannot get changed by
             * external parties.
             */
            try
            {
                result.add((ItemVO) item.clone());
            }
            catch (final CloneNotSupportedException e)
            {
                throw new StoreAccessException(e);
            }
        }
        if (filters == null)
            return result;

        if (filters != null)
        {
            try
            {
                result = (List<ItemVO>) filters.apply(result);
            }
            catch (final DomainException e)
            {
                throw new StoreAccessException(e);
            }
        }
        return result;
    }

    public List<String> getFilenames(final DmoStoreId parentSid, final boolean recursive) throws StoreAccessException
    {
        return getFilenames(parentSid, recursive, "");
    }

    private List<String> getFilenames(final DmoStoreId parentSid, final boolean recursive, final String prefix) throws StoreAccessException
    {
        final List<String> result = new ArrayList<String>();

        final List<FileItemVO> files = getFiles(parentSid, -1, -1, null, null);
        for (final FileItemVO file : files)
        {
            result.add(prefix + file.getName());
        }

        final List<FolderItemVO> folders = getFolders(parentSid, -1, -1, null, null);
        for (final FolderItemVO folder : folders)
        {
            if (compositions.get(folder.getSid()).size() > 0)
            {

                result.addAll(getFilenames(new DmoStoreId(folder.getSid()), recursive, prefix + folder.getName() + "\\"));
            }
            else
                result.add(prefix + folder.getName() + "\\");
        }

        return result;
    }

    public boolean hasChildItems(final DmoStoreId parentSid)
    {
        final FolderItemVO folder = (FolderItemVO) items.get(parentSid);
        return ((folder).getChildItemCount() > 0);
    }

    public List<FileItemVO> getFiles(final DmoStoreId parentSid, final Integer limit, final Integer offset, final ItemOrder order, final ItemFilters filters)
            throws StoreAccessException
    {
        final List<FileItemVO> result = new ArrayList<FileItemVO>();

        for (final ItemVO item : getFilesAndFolders(parentSid, limit, offset, order, filters))
        {
            if (item instanceof FileItemVO)
            {
                result.add((FileItemVO) item);
            }
        }
        return result;
    }

    /**
     * Returns cloned objects, so that the internal composition cannot get changed by external
     * parties.
     */
    public List<FolderItemVO> getFolders(final DmoStoreId parentSid, final Integer limit, final Integer offset, final ItemOrder order, final ItemFilters filters)
            throws StoreAccessException
    {
        if (limit > 0 || offset > 0 || order != null)
            throw new StoreAccessException("order and pages not implemented yet.");

        final List<FolderItemVO> result = new ArrayList<FolderItemVO>();
        final List<ItemVO> filesAndFolders = getFilesAndFolders(parentSid, limit, offset, order, filters);
        if (filesAndFolders == null)
            return result;
        for (final ItemVO item : filesAndFolders)
        {
            if (item instanceof FolderItemVO)
            {
                result.add((FolderItemVO) item);
            }
        }
        return result;
    }

    public List<FileItemVO> getFiles(final DmoStoreId parentSid, final Integer limit, final Integer offset, final ItemOrder order, final ItemFilters filter,
            final EasyUser sessionUser) throws ServiceException
    {
        try
        {
            return getFiles(parentSid, limit, offset, order, filter);
        }
        catch (final StoreAccessException e)
        {
            throw new ServiceException(e);
        }
    }

    public URLConnection getFileURLConnection(final DmoStoreId sid) throws StoreAccessException
    {
        throw new RuntimeException("method not implemented");
    }

    public File getZipFile(final File zipFilePath, final String zipFilename, final List<Map<String, String>> content) throws StoreAccessException
    {
        throw new RuntimeException("method not implemented");
    }

    public FileItemVO findFileById(DmoStoreId sid)
    {
        throw new UnsupportedOperationException("This is a dummy, dummy.");
    }

    public List<FileItemVO> findFilesById(Collection<DmoStoreId> sids) throws StoreAccessException
    {
        throw new UnsupportedOperationException("This is a dummy, dummy.");
    }

    public List<ItemVO> findFilesAndFoldersById(Collection<DmoStoreId> sids) throws StoreAccessException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public FolderItemVO findFolderById(DmoStoreId sid) throws StoreAccessException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<FolderItemVO> findFoldersById(Collection<DmoStoreId> sids) throws StoreAccessException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public URL getFileURL(DmoStoreId sid)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, String> getAllFiles(DmoStoreId datasetSid) throws StoreAccessException
    {
        throw new UnsupportedOperationException("This is a dummy, dummy.");
    }

    @Override
    public FileItemVO findFileByPath(DmoStoreId datasetSid, String relativePath) throws StoreAccessException
    {
        throw new UnsupportedOperationException("This is a dummy, dummy.");
    }

    @Override
    public FolderItemVO findFolderByPath(DmoStoreId datasetSid, String relativePath) throws StoreAccessException
    {
        throw new UnsupportedOperationException("This is a dummy, dummy.");
    }

    @Override
    public String getDatasetId(DmoStoreId storeId) throws StoreException
    {
        throw new UnsupportedOperationException("This is a dummy, dummy.");
    }

    @Override
    public List<FileItemVO> getDatasetFiles(DmoStoreId dmoStoreId) throws StoreAccessException
    {
        throw new UnsupportedOperationException("This is a dummy, dummy.");
    }

}
