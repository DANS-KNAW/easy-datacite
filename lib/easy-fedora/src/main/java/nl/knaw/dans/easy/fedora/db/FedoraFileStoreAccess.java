package nl.knaw.dans.easy.fedora.db;

import static org.hibernate.criterion.Restrictions.eq;

import java.beans.Introspector;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.util.FileUtil;
import nl.knaw.dans.easy.data.store.FileStoreAccess;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.data.store.StoreException;
import nl.knaw.dans.easy.db.DbUtil;
import nl.knaw.dans.easy.db.ThreadLocalSessionFactory;
import nl.knaw.dans.easy.domain.dataset.item.AbstractItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemAccessibleTo;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemCreatorRole;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVisibleTo;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.DatasetItemContainer;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FileItemVOAttribute;
import nl.knaw.dans.easy.domain.model.FolderItem;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;

import org.apache.commons.lang.ClassUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FedoraFileStoreAccess implements FileStoreAccess {
    private static final Logger LOGGER = LoggerFactory.getLogger(FedoraFileStoreAccess.class);

    private final ThreadLocalSessionFactory sessionFactory = ThreadLocalSessionFactory.instance();

    /** query parameter name representing the value of a dataset or folder */
    private static final String CONTAINER_ID = "containerId";

    private static final String RECURSIVE_WHERE_CLAUSE = "WHERE c.sid=:" + CONTAINER_ID + " AND m.datasetSid=c.datasetSid AND m.path LIKE c.path || '_%'";

    /** query parameter name representing the value of filtered field */
    private static final String FILTER = "filter";

    private static final String FILE_PATH_QUERY = "SELECT fivo FROM " + FileItemVO.class.getName() //
            + " AS fivo" //
            + " WHERE datasetSid=:datasetSid AND path=:path";

    private static final String FOLDER_PATH_QUERY = "SELECT fovo FROM " + FolderItemVO.class.getName() //
            + " AS fovo" //
            + " WHERE datasetSid=:datasetSid AND (path=:path OR path=:path2)";

    public FedoraFileStoreAccess() throws StoreAccessException {
        if (!DbUtil.hasLocalConfig()) {
            throw new StoreAccessException("No local configuration set on " + DbUtil.class.getName());
        }
    }

    public FileItemVO findFileById(final DmoStoreId dmoStoreId) throws StoreAccessException {
        final Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            return (FileItemVO) session.get(FileItemVO.class, dmoStoreId.toString());
        }
        catch (final HibernateException e) {
            throw new StoreAccessException(e);
        }
        finally {
            sessionFactory.closeSession();
        }
    }

    @SuppressWarnings("unchecked")
    public FileItemVO findFileByPath(final DmoStoreId datasetSid, final String relativePath) throws StoreAccessException {
        if (!FileUtil.isValidRelativePath(relativePath)) {
            throw new IllegalArgumentException("Not a valid relative path: " + relativePath);
        }

        FileItemVO fivo = null;
        final Session session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            final List<FileItemVO> items = session.createQuery(FILE_PATH_QUERY) //
                    .setParameter("datasetSid", datasetSid.getStoreId()) //
                    .setParameter("path", relativePath) //
                    .setFetchSize(1) //
                    .list();
            if (items.size() > 0) {
                fivo = items.get(0);
            }
        }
        catch (final HibernateException e) {
            throw new StoreAccessException(e);
        }
        finally {
            sessionFactory.closeSession();
        }
        return fivo;
    }

    @SuppressWarnings("unchecked")
    public FolderItemVO findFolderByPath(final DmoStoreId datasetSid, final String relativePath) throws StoreAccessException {
        if (!FileUtil.isValidRelativePath(relativePath)) {
            throw new IllegalArgumentException("Not a valid relative path: " + relativePath);
        }

        FolderItemVO fovo = null;
        final String path = relativePath.endsWith("/") ? relativePath.substring(0, relativePath.length() - 1) : relativePath + "/";
        final Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            final List<FolderItemVO> items = session.createQuery(FOLDER_PATH_QUERY) //
                    .setParameter("datasetSid", datasetSid.getStoreId()) //
                    .setParameter("path", relativePath) //
                    .setParameter("path2", path) //
                    .setFetchSize(1) //
                    .list();
            if (items.size() > 0) {
                fovo = items.get(0);
            }
        }
        catch (final HibernateException e) {
            throw new StoreAccessException(e);
        }
        finally {
            sessionFactory.closeSession();
        }
        return fovo;
    }

    public FolderItemVO findFolderById(final DmoStoreId dmoStoreId) throws StoreAccessException {
        final Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            return (FolderItemVO) session.get(FolderItemVO.class, dmoStoreId.toString());
        }
        catch (final HibernateException e) {
            throw new StoreAccessException(e);
        }
        finally {
            sessionFactory.closeSession();
        }
    }

    public List<FileItemVO> findFilesById(final Collection<DmoStoreId> dmoStoreIds) throws StoreAccessException {
        if (dmoStoreIds.isEmpty()) {
            throw new IllegalArgumentException("Nothing to find. Empty collection.");
        }
        final Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            List<FileItemVO> results = new ArrayList<FileItemVO>(dmoStoreIds.size());
            for (DmoStoreId id : dmoStoreIds) {
                results.add((FileItemVO) session.load(FileItemVO.class, id.toString()));
            }
            return results;
        }
        catch (final HibernateException e) {
            throw new StoreAccessException(e);
        }
        finally {
            sessionFactory.closeSession();
        }
    }

    public List<FolderItemVO> findFoldersById(final Collection<DmoStoreId> dmoStoreIds) throws StoreAccessException {
        if (dmoStoreIds.isEmpty()) {
            throw new IllegalArgumentException("Nothing to find. Empty collection.");
        }
        final Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            List<FolderItemVO> results = new ArrayList<FolderItemVO>(dmoStoreIds.size());
            for (DmoStoreId id : dmoStoreIds) {
                results.add((FolderItemVO) session.load(FolderItemVO.class, id.toString()));
            }
            return results;
        }
        catch (final HibernateException e) {
            throw new StoreAccessException(e);
        }
        finally {
            sessionFactory.closeSession();
        }
    }

    // Let op: niet recursief
    public List<ItemVO> getFilesAndFolders(final DmoStoreId parentSid) throws StoreAccessException {
        LOGGER.debug("Getting files and folders from the Fedora database for {}", parentSid);
        FolderItemVO parent = getFolderItemVO(parentSid);
        final Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.update(parent);
            final Set<FolderItemVO> folders = parent.getFolders() == null ? new HashSet<FolderItemVO>() : parent.getFolders();
            final Set<FileItemVO> files = parent.getFiles() == null ? new HashSet<FileItemVO>() : parent.getFiles();
            final List<ItemVO> result = new ArrayList<ItemVO>(files.size() + folders.size());
            result.addAll(folders);
            result.addAll(files);
            LOGGER.debug("Returned " + result.size() + " files and folders.");
            return result;
        }
        finally {
            sessionFactory.closeSession();
        }
    }

    public List<FileItemVO> getFiles(final DmoStoreId parentSid) throws StoreAccessException {
        LOGGER.debug("Getting files from the Fedora database for " + parentSid);
        final Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            FolderItemVO folder = (FolderItemVO) session.get(FolderItemVO.class, parentSid.toString());
            if (folder == null) {
                return new ArrayList<FileItemVO>(0);
            } else {
                return new ArrayList<FileItemVO>(folder.getFiles());
            }
        }
        catch (final HibernateException e) {
            throw new StoreAccessException(e);
        }
        finally {
            sessionFactory.closeSession();
        }
    }

    public List<FolderItemVO> getFolders(final DmoStoreId parentSid) throws StoreAccessException {
        LOGGER.debug("Getting folders from the Fedora database for " + parentSid);

        final Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            FolderItemVO folder = (FolderItemVO) session.get(FolderItemVO.class, parentSid.toString());
            if (folder == null) {
                return new ArrayList<FolderItemVO>(0);
            } else {
                return new ArrayList<FolderItemVO>(folder.getFolders());
            }
        }
        catch (final HibernateException e) {
            throw new StoreAccessException(e);
        }
        finally {
            sessionFactory.closeSession();
        }
    }

    @Override
    public String getDatasetId(final DmoStoreId storeId) throws StoreException, StoreAccessException {
        ItemVO item;
        if (storeId.isInNamespace(FolderItem.NAMESPACE)) {
            item = getFolderItemVO(storeId);
        } else if (storeId.isInNamespace(FileItem.NAMESPACE)) {
            item = findFileById(storeId);
        } else {
            throw new StoreException("storeId with unqueriable namespace: " + storeId);
        }
        return item.getDatasetSid();
    }

    public List<String> getFilenames(final DmoStoreId parentSid) throws StoreAccessException {
        List<FileItemVO> files = getDatasetFiles(parentSid);
        List<String> fileNames = new ArrayList<String>();
        for (FileItemVO f : files) {
            fileNames.add(f.getPath());
        }
        return fileNames;
    }

    @Override
    public List<FileItemVO> getDatasetFiles(final DmoStoreId datasetSid) throws StoreAccessException {
        LOGGER.debug("Getting FileItemVO's for dataset {}", datasetSid);
        final Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            Criteria c = session.createCriteria(FileItemVO.class);
            c.add(eq("datasetSid", datasetSid.toString()));
            @SuppressWarnings("unchecked")
            List<FileItemVO> files = c.list();
            return files;
        }
        catch (HibernateException e) {
            throw new StoreAccessException(e);
        }
        finally {
            sessionFactory.closeSession();
        }
    }

    public Map<String, String> getAllFiles(final DmoStoreId datasetStoreId) throws StoreAccessException {
        List<FileItemVO> files = getDatasetFiles(datasetStoreId);
        final Map<String, String> result = new HashMap<String, String>();
        for (final FileItemVO file : files) {
            result.put(file.getSid(), file.getName());
        }
        return result;
    }

    public boolean hasChildItems(final DmoStoreId parentSid) throws StoreAccessException {
        LOGGER.debug("Determining if folder " + parentSid + " has child items.");
        return !getFiles(parentSid).isEmpty() || !getFolders(parentSid).isEmpty();
    }

    @Override
    public int getTotalMemberCount(final DmoStoreId storeId, final Class<? extends AbstractItemVO> memberClass, final FileItemVOAttribute... fieldValue)
            throws StoreAccessException
    {
        return fetchCount(countMembers(false, storeId, memberClass, fieldValue));
    }

    @Override
    public boolean hasMember(final DmoStoreId storeId, final Class<? extends AbstractItemVO> memberClass, final FileItemVOAttribute fieldValue)
            throws StoreAccessException
    {
        FolderItemVO folder = getFolderItemVO(storeId);
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            /**
             * We will load the object again, because it was retrieved during an earlier session. TODO: refactor this to the Hibernate-way, using attach en
             * detach.
             */
            folder = (FolderItemVO) session.get(FolderItemVO.class, folder.getSid());
            if (fieldValue instanceof AccessibleTo) {
                return translateAccessibilities(folder.getAccessibilities()).contains(fieldValue);
            } else if (fieldValue instanceof VisibleTo) {
                return translateVisibilities(folder.getVisibilities()).contains(fieldValue);
            } else if (fieldValue instanceof CreatorRole) {
                return translateCreatorRoles(folder.getCreatorRoles()).contains(fieldValue);
            } else {
                assert false : "Invalid fieldValue";
                return false;
            }
        }
        finally {
            sessionFactory.closeSession();
        }
    }

    @Override
    public boolean hasMember(final DmoStoreId storeId, final Class<? extends AbstractItemVO> memberClass) throws StoreAccessException {
        return getFiles(storeId).size() + getFolders(storeId).size() > 0;
    }

    @Override
    public boolean hasVisibleFiles(final DmoStoreId storeId, final boolean userIsKnown, final boolean userHasGroupAccess, final boolean userHasPermission)
            throws StoreAccessException
    {
        if (!Dataset.NAMESPACE.equals(storeId.getNamespace()))
            throw new IllegalArgumentException("storeId should be a dataset");
        FolderItemVO root = getRootFolder(storeId); // Make sure there is a root folder; create one if not
        final Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            root = (FolderItemVO) session.load(FolderItemVO.class, storeId.toString());
            Set<VisibleTo> visibilities = translateVisibilities(root.getVisibilities());

            if (visibilities.contains(VisibleTo.ANONYMOUS))
                return true;

            if (userIsKnown && visibilities.contains(VisibleTo.KNOWN))
                return true;

            if (userHasGroupAccess && visibilities.contains(VisibleTo.RESTRICTED_GROUP))
                return true;

            if (userHasPermission && visibilities.contains(VisibleTo.RESTRICTED_REQUEST))
                return true;

            return false;
        }
        catch (HibernateException e) {
            throw new StoreAccessException(e);
        }
        finally {
            sessionFactory.closeSession();
        }
    }

    public static Set<VisibleTo> translateVisibilities(Set<FolderItemVisibleTo> visibilities) {
        Set<VisibleTo> translated = new HashSet<VisibleTo>();
        for (FolderItemVisibleTo i : visibilities) {
            translated.add(i.getVisibleTo());
        }
        return translated;
    }

    public static Set<AccessibleTo> translateAccessibilities(Set<FolderItemAccessibleTo> accessibilities) {
        Set<AccessibleTo> translated = new HashSet<AccessibleTo>();
        for (FolderItemAccessibleTo i : accessibilities) {
            translated.add(i.getAccessibleTo());
        }
        return translated;
    }

    public static Set<CreatorRole> translateCreatorRoles(Set<FolderItemCreatorRole> roles) {
        Set<CreatorRole> translated = new HashSet<CreatorRole>();
        for (FolderItemCreatorRole i : roles) {
            translated.add(i.getCreatorRole());
        }
        return translated;
    }

    private int fetchCount(final List<? extends Object> query) {
        return new BigDecimal((Long) query.get(0)).intValue();
    }

    private List<? extends Object> countMembers(final boolean direct, final DmoStoreId storeId, final Class<? extends AbstractItemVO> memberClass,
            final FileItemVOAttribute... attribute) throws StoreAccessException
    {
        checkCountArguments(storeId, memberClass, attribute);
        String optionalCondition = "";
        String fieldName = "";
        FileItemVOAttribute fieldValue = null;
        if (attribute != null && attribute.length == 1) {
            fieldValue = attribute[0];
            fieldName = Introspector.decapitalize(ClassUtils.getShortCanonicalName(fieldValue.getClass()));
            optionalCondition = " AND m." + fieldName + "=:" + FILTER;
        }
        final String queryString = createCountMemberQueryString(direct, storeId, memberClass, optionalCondition);

        if (LOGGER.isDebugEnabled()) {
            final StackTraceElement callerOfPublicMethod = new Exception().getStackTrace()[2];
            LOGGER.debug("Getting number of {}'s for '{}' filtered by: {} {}", ClassUtils.getShortCanonicalName(memberClass), storeId, fieldName, fieldValue);
            LOGGER.debug("{}:{} {}", callerOfPublicMethod.getMethodName(), callerOfPublicMethod.getLineNumber(), queryString);
        }
        return createCountMemberQuery(queryString, storeId, fieldValue);
    }

    private void checkCountArguments(final DmoStoreId storeId, final Class<? extends AbstractItemVO> memberClass, final FileItemVOAttribute... attribute) {
        if (!FolderItem.NAMESPACE.equals(storeId.getNamespace()) && !Dataset.NAMESPACE.equals(storeId.getNamespace()))
            throw new IllegalArgumentException("storeId should be a dataset or folder");
        if (attribute != null && attribute.length > 1)
            throw new UnsupportedOperationException("so far at most one attribute value implemented");
        if (attribute != null && attribute.length == 1 && memberClass.equals(FolderItemVO.class))
            throw new UnsupportedOperationException("so far no attributes implemented when counting folders");
    }

    private String createCountMemberQueryString(final boolean direct, final DmoStoreId storeId, final Class<? extends AbstractItemVO> memberClass,
            final String optionalCondition)
    {
        String fromClause;
        String whereClause;
        if (direct) {
            fromClause = childFromClause(memberClass);
            whereClause = String.format("WHERE m.parentSid=:%s", CONTAINER_ID);
        } else if (Dataset.NAMESPACE.equals(storeId.getNamespace())) {
            fromClause = childFromClause(memberClass);
            whereClause = String.format("WHERE m.datasetSid=:%s", CONTAINER_ID);
        } else
        // because of checkCountArguments we now have a Folder name space
        {
            fromClause = recursiveFromClause(memberClass);
            whereClause = RECURSIVE_WHERE_CLAUSE;
        }
        return "SELECT count(*) " + fromClause + whereClause + optionalCondition;
    }

    private String childFromClause(final Class<? extends AbstractItemVO> memberClass) {
        return String.format("FROM %s m ", memberClass.getName());
    }

    private String recursiveFromClause(final Class<? extends AbstractItemVO> memberClass) {
        return String.format("FROM %s m, %s c ", memberClass.getName(), FolderItemVO.class.getName());
    }

    private List<? extends Object> createCountMemberQuery(final String queryString, final DmoStoreId storeId, final FileItemVOAttribute fieldValue)
            throws StoreAccessException
    {
        final Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            final Query query = session.createQuery(queryString);
            query.setParameter(CONTAINER_ID, storeId.getStoreId());
            if (fieldValue != null)
                query.setParameter(FILTER, fieldValue);

            @SuppressWarnings("unchecked")
            final List<? extends Object> result = query.list();
            return result;
        }
        catch (final HibernateException e) {
            throw new StoreAccessException(e);
        }
        finally {
            sessionFactory.closeSession();
        }
    }

    @Override
    public FolderItemVO getFolderItemVO(final DmoStoreId itemContainer) throws StoreAccessException {
        if (itemContainer.isInNamespace(FileItem.NAMESPACE))
            throw new IllegalArgumentException("Item must be a container (dataset or folder), not a file item");

        if (itemContainer.isInNamespace(Dataset.NAMESPACE))
            return getRootFolder(itemContainer);

        final Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            FolderItemVO folder = (FolderItemVO) session.get(FolderItemVO.class, itemContainer.toString());
            if (folder == null) {
                throw new StoreAccessException("No such folder: " + itemContainer.getId());
            }
            return folder;
        }
        catch (final HibernateException e) {
            throw new StoreAccessException(e);
        }
        finally {
            sessionFactory.closeSession();
        }
    }

    @Override
    public FolderItemVO getRootFolder(DmoStoreId dmoStoreId) throws StoreAccessException {
        final Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            FolderItemVO root = (FolderItemVO) session.get(FolderItemVO.class, dmoStoreId.toString());
            if (root == null) {
                root = new FolderItemVO();
                root.setSid(dmoStoreId.toString());
                root.setParentSid("");
                root.setDatasetSid(dmoStoreId.toString());
                root.setName("Dataset Contents");
                root.setPath("");
                session.save(root);
                session.flush();
                session.getTransaction().commit();
            }
            return root;
        }
        catch (final HibernateException e) {
            throw new StoreAccessException(e);
        }
        finally {
            sessionFactory.closeSession();
        }
    }

    @Override
    public Set<AccessibleTo> getItemVoAccessibilities(ItemVO item) throws StoreAccessException {
        final Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            Set<AccessibleTo> result = new HashSet<AccessibleTo>();
            if (item instanceof FolderItemVO) {
                FolderItemVO folder = (FolderItemVO) session.load(FolderItemVO.class, item.getSid());
                result = translateAccessibilities(folder.getAccessibilities());
            } else {
                FileItemVO file = (FileItemVO) session.load(FileItemVO.class, item.getSid());
                result.add(file.getAccessibleTo());
            }
            return result;
        }
        catch (final HibernateException e) {
            throw new StoreAccessException(e);
        }
        finally {
            sessionFactory.closeSession();
        }
    }

    @Override
    public Set<VisibleTo> getItemVoVisibilities(ItemVO item) throws StoreAccessException {
        final Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            Set<VisibleTo> result = new HashSet<VisibleTo>();
            if (item instanceof FolderItemVO) {
                FolderItemVO folder = (FolderItemVO) session.load(FolderItemVO.class, item.getSid());
                result = translateVisibilities(folder.getVisibilities());
            } else {
                FileItemVO file = (FileItemVO) session.load(FileItemVO.class, item.getSid());
                result.add(file.getVisibleTo());
            }
            return result;
        }
        catch (final HibernateException e) {
            throw new StoreAccessException(e);
        }
        finally {
            sessionFactory.closeSession();
        }
    }

    @Override
    public Set<CreatorRole> getItemVoCreatorRoles(ItemVO item) throws StoreAccessException {
        final Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            Set<CreatorRole> result = new HashSet<CreatorRole>();
            if (item instanceof FolderItemVO) {
                FolderItemVO folder = (FolderItemVO) session.load(FolderItemVO.class, item.getSid());
                result = translateCreatorRoles(folder.getCreatorRoles());
            } else {
                FileItemVO file = (FileItemVO) session.load(FileItemVO.class, item.getSid());
                result.add(file.getCreatorRole());
            }
            return result;
        }
        catch (final HibernateException e) {
            throw new StoreAccessException(e);
        }
        finally {
            sessionFactory.closeSession();
        }
    }

    @Override
    public boolean belongsItemTo(ItemVO item, Dataset dataset) {
        final Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.update(item);
            return item.belongsTo(dataset);
        }
        finally {
            sessionFactory.closeSession();
        }
    }
}
