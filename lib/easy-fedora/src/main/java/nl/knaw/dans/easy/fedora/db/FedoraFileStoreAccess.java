package nl.knaw.dans.easy.fedora.db;

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
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.dataset.item.ItemOrder;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.dataset.item.filter.AccessibleToFieldFilter;
import nl.knaw.dans.easy.domain.dataset.item.filter.CreatorRoleFieldFilter;
import nl.knaw.dans.easy.domain.dataset.item.filter.ItemFieldFilter;
import nl.knaw.dans.easy.domain.dataset.item.filter.ItemFilter;
import nl.knaw.dans.easy.domain.dataset.item.filter.ItemFilters;
import nl.knaw.dans.easy.domain.dataset.item.filter.VisibleToFieldFilter;
import nl.knaw.dans.easy.domain.exceptions.NoFilterValuesSelectedException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FileItemVOAttribute;
import nl.knaw.dans.easy.domain.model.FolderItem;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.fedora.db.exceptions.UnknownItemFilterException;

import org.apache.commons.lang.ClassUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Disjunction;
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

    private static final String FILENAME_QUERY = "SELECT name FROM " + FileItemVO.class.getName() + " WHERE parentSid=:parentSid";

    private static final String FOLDERNAME_QUERY = "SELECT name, sid FROM " + FolderItemVO.class.getName() + " WHERE parentSid=:parentSid";

    private static final String ALL_FILES_QUERY = "SELECT name, sid FROM " + FileItemVO.class.getName() + " WHERE datasetSid=:datasetSid";

    private static final String HASCHILDFILE_QUERY = "SELECT sid FROM " + FileItemVO.class.getName() + " WHERE parentSid=:parentSid"; // LIMIT
    // 1

    private static final String HASCHILDFOLDER_QUERY = "SELECT sid FROM " + FolderItemVO.class.getName() + " WHERE parentSid=:parentSid"; // LIMIT
    // 1

    private static final String SELECT_DATASET_FILEITEMS = "SELECT fivo FROM " + FileItemVO.class.getName() //
            + " AS fivo"//
            + " WHERE datasetSid=:datasetSid"; //

    private static final String SELECT_DATASET_FOLDERITEMS = "SELECT fovo FROM " + FolderItemVO.class.getName() //
            + " AS fovo"//
            + " WHERE datasetSid=:datasetSid"; //

    private static final String FILE_PATH_QUERY = "SELECT fivo FROM " + FileItemVO.class.getName() //
            + " AS fivo" //
            + " WHERE datasetSid=:datasetSid AND path=:path";

    private static final String FOLDER_PATH_QUERY = "SELECT fovo FROM " + FolderItemVO.class.getName() //
            + " AS fovo" //
            + " WHERE datasetSid=:datasetSid AND (path=:path OR path=:path2)";

    private static final String DATASET_ID_OF_FOLDER_QUERY = "SELECT datasetSid AS datasetId FROM " + FolderItemVO.class.getName() //
            + " WHERE pid=:itemId";

    private static final String DATASET_ID_OF_FILE_QUERY = "SELECT datasetSid AS datasetId FROM " + FileItemVO.class.getName() //
            + " WHERE pid=:itemId";

    @SuppressWarnings("rawtypes")
    static private Class[] implementedFilters = {CreatorRoleFieldFilter.class, VisibleToFieldFilter.class, AccessibleToFieldFilter.class};

    public FedoraFileStoreAccess() throws StoreAccessException {
        if (!DbUtil.hasLocalConfig()) {
            throw new StoreAccessException("No local configuration set on " + DbUtil.class.getName());
        }
    }

    private String getInfoMsg(final DmoStoreId parentSid, final Integer limit, final Integer offset, final ItemOrder order, final ItemFilters filters) {
        String result = parentSid.getStoreId();
        result += " ";
        result += limit >= 0 ? "limit = " + limit : "limiting off";
        result += " ";
        result += limit >= 0 ? "offset = " + limit : "no offset";
        result += " ";
        result += order != null ? "order = " + order.getField().propertyName + " " + (order.isAscending() ? "ascending" : "descending") : "no ordering";
        result += " ";
        if (filters != null) {
            result += "Filters: ";
            for (final ItemFilter filter : filters.getFilters()) {
                if (filter != null)
                    result += filter.toString();
            }
        } else
            result += "no filtering";
        return result;
    }

    @SuppressWarnings("unchecked")
    public FileItemVO findFileById(final DmoStoreId dmoStoreId) throws StoreAccessException {
        FileItemVO fivo = null;
        final Session session = sessionFactory.openSession();
        final Transaction tx = session.beginTransaction();
        try {

            final List<FileItemVO> items = session.createQuery("select fivo from " + FileItemVO.class.getName() + " as fivo where fivo.sid = :sid")
                    .setParameter("sid", dmoStoreId.getStoreId()).setFetchSize(1).list();
            if (items.size() > 0) {
                fivo = items.get(0);
            }
            tx.commit();
        }
        catch (final HibernateException e) {
            throw new StoreAccessException(e);
        }
        finally {
            if (tx.isActive())
                tx.rollback();
            sessionFactory.closeSession();
        }
        return fivo;
    }

    @SuppressWarnings("unchecked")
    public FileItemVO findFileByPath(final DmoStoreId datasetSid, final String relativePath) throws StoreAccessException {
        if (!FileUtil.isValidRelativePath(relativePath)) {
            throw new IllegalArgumentException("Not a valid relative path: " + relativePath);
        }

        FileItemVO fivo = null;
        final Session session = sessionFactory.openSession();

        final Transaction tx = session.beginTransaction();
        try {
            final List<FileItemVO> items = session.createQuery(FILE_PATH_QUERY) //
                    .setParameter("datasetSid", datasetSid.getStoreId()) //
                    .setParameter("path", relativePath) //
                    .setFetchSize(1) //
                    .list();
            if (items.size() > 0) {
                fivo = items.get(0);
            }
            tx.commit();
        }
        catch (final HibernateException e) {
            throw new StoreAccessException(e);
        }
        finally {
            if (tx.isActive())
                tx.rollback();
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
        final Transaction tx = session.beginTransaction();
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
            tx.commit();
        }
        catch (final HibernateException e) {
            throw new StoreAccessException(e);
        }
        finally {
            if (tx.isActive())
                tx.rollback();
            sessionFactory.closeSession();
        }
        return fovo;
    }

    @SuppressWarnings("unchecked")
    public FolderItemVO findFolderById(final DmoStoreId dmoStoreId) throws StoreAccessException {
        FolderItemVO fovo = null;
        final Session session = sessionFactory.openSession();
        final Transaction tx = session.beginTransaction();
        try {

            final List<FolderItemVO> items = session.createQuery("select fovo from " + FolderItemVO.class.getName() + " as fovo where fovo.sid = :sid")
                    .setParameter("sid", dmoStoreId.getStoreId()).setFetchSize(1).list();
            if (items.size() > 0) {
                fovo = items.get(0);
            }
            tx.commit();
        }
        catch (final HibernateException e) {
            throw new StoreAccessException(e);
        }
        finally {
            if (tx.isActive())
                tx.rollback();
            sessionFactory.closeSession();
        }
        return fovo;
    }

    @SuppressWarnings("unchecked")
    public List<FileItemVO> findFilesById(final Collection<DmoStoreId> dmoStoreIds) throws StoreAccessException {
        if (dmoStoreIds.isEmpty()) {
            throw new IllegalArgumentException("Nothing to find. Empty collection.");
        }
        List<FileItemVO> fivoList;
        final Session session = sessionFactory.openSession();
        final Transaction tx = session.beginTransaction();
        try {

            fivoList = session.createCriteria(FileItemVO.class).add(Restrictions.in("sid", DmoStoreId.asStrings(dmoStoreIds))).list();
            tx.commit();
        }
        catch (final HibernateException e) {
            throw new StoreAccessException(e);
        }
        finally {
            if (tx.isActive())
                tx.rollback();
            sessionFactory.closeSession();
        }
        return fivoList;
    }

    @SuppressWarnings("unchecked")
    public List<FolderItemVO> findFoldersById(final Collection<DmoStoreId> dmoStoreIds) throws StoreAccessException {
        if (dmoStoreIds.isEmpty()) {
            throw new IllegalArgumentException("Nothing to find. Empty collection.");
        }
        List<FolderItemVO> fovoList;
        final Session session = sessionFactory.openSession();
        final Transaction tx = session.beginTransaction();
        try {

            fovoList = session.createCriteria(FolderItemVO.class).add(Restrictions.in("sid", DmoStoreId.asStrings(dmoStoreIds))).list();
            tx.commit();
        }
        catch (final HibernateException e) {
            throw new StoreAccessException(e);
        }
        finally {
            if (tx.isActive())
                tx.rollback();
            sessionFactory.closeSession();
        }
        return fovoList;
    }

    public List<ItemVO> findFilesAndFoldersById(final Collection<DmoStoreId> dmoStoreIds) throws StoreAccessException {
        final List<ItemVO> items = new ArrayList<ItemVO>();
        items.addAll(findFilesById(dmoStoreIds));
        items.addAll(findFoldersById(dmoStoreIds));
        return items;
    }

    public List<ItemVO> getFilesAndFolders(final DmoStoreId parentSid, final Integer limit, final Integer offset, final ItemOrder order,
            final ItemFilters filters) throws StoreAccessException
    {
        if (limit > 0 || offset > 0 || order != null)
            throw new StoreAccessException("filter and order not implemented yet.");

        LOGGER.debug("Getting files and folders from the Fedora database for " + getInfoMsg(parentSid, limit, offset, order, filters));

        try {
            // open session from ThreadLocalSessionFactory ensures
            // the same session gets shared between getFiles and getFolders
            sessionFactory.openSession();

            final List<FolderItemVO> folders = getFolders(parentSid, limit, offset, order, filters);
            if (limit > 0 && limit - folders.size() <= 0) {
                final List<ItemVO> result = new ArrayList<ItemVO>(folders.size());
                result.addAll(folders);
                return result;
            }

            final List<FileItemVO> files = getFiles(parentSid, limit - folders.size(), offset, order, filters);

            final List<ItemVO> result = new ArrayList<ItemVO>(files.size() + folders.size());
            result.addAll(folders);
            result.addAll(files);

            LOGGER.debug("Returned " + result.size() + " files and folders.");

            return result;
        }
        catch (final HibernateException e) {
            throw new StoreAccessException(e);
        }
        finally {
            sessionFactory.closeSession();
        }
    }

    @SuppressWarnings("unchecked")
    public List<FileItemVO> getFiles(final DmoStoreId parentSid, final Integer limit, final Integer offset, final ItemOrder order, final ItemFilters filters)
            throws StoreAccessException
    {
        if (limit > 0 || offset > 0 || order != null)
            throw new StoreAccessException("filter and order not implemented yet.");

        LOGGER.debug("Getting files from the Fedora database for " + getInfoMsg(parentSid, limit, offset, order, filters));

        try {
            final Session session = sessionFactory.openSession();
            final Criteria select = createGetCriteria(session, FileItemVO.class, parentSid.getStoreId(), limit, offset, order, filters);
            final List<FileItemVO> files = select.list();
            LOGGER.debug("Returned " + files.size() + " files.");
            return files;

        }
        catch (final HibernateException e) {
            throw new StoreAccessException(e);
        }
        finally {
            sessionFactory.closeSession();
        }
    }

    @SuppressWarnings("unchecked")
    public List<FolderItemVO> getFolders(final DmoStoreId parentSid, final Integer limit, final Integer offset, final ItemOrder order, final ItemFilters filters)
            throws StoreAccessException
    {
        if (limit > 0 || offset > 0 || order != null)
            throw new StoreAccessException("paging and order not implemented yet.");

        LOGGER.debug("Getting folders from the Fedora database for " + getInfoMsg(parentSid, limit, offset, order, filters));

        try {
            final Session session = sessionFactory.openSession();

            final Criteria select = createGetCriteria(session, FolderItemVO.class, parentSid.getStoreId(), limit, offset, order, filters);
            final List<FolderItemVO> folders = select.list();

            LOGGER.debug("Returned " + folders.size() + " folders.");

            return folders;
        }
        catch (final HibernateException e) {
            throw new StoreAccessException(e);
        }
        finally {
            sessionFactory.closeSession();
        }
    }

    @Override
    public String getDatasetId(final DmoStoreId storeId) throws StoreException {
        String query;
        if (storeId.isInNamespace(FolderItem.NAMESPACE)) {
            query = DATASET_ID_OF_FOLDER_QUERY;
        } else if (storeId.isInNamespace(FileItem.NAMESPACE)) {
            query = DATASET_ID_OF_FILE_QUERY;
        } else {
            throw new StoreException("storeId with unqueriable namespace: " + storeId);
        }

        final Session session = sessionFactory.openSession();
        try {
            return (String) session.createQuery(query).setParameter("itemId", storeId.getStoreId()).uniqueResult();
        }
        finally {
            sessionFactory.closeSession();
        }
    }

    public List<String> getFilenames(final DmoStoreId parentSid, final boolean recursive) throws StoreAccessException {
        return getFilenames(parentSid, true, "");
    }

    @SuppressWarnings("unchecked")
    private List<String> getFilenames(final DmoStoreId parentSid, final boolean recursive, final String prefix) throws StoreAccessException {
        LOGGER.debug("Getting filenames for folder " + parentSid + ".");

        try {
            final Session session = sessionFactory.openSession();

            final List<String> result = new ArrayList<String>();

            Query query = session.createQuery(FILENAME_QUERY);
            query.setParameter("parentSid", parentSid.getStoreId());
            if (prefix.equals("")) {
                result.addAll(query.list());
            } else {
                final List<String> filenames = query.list();
                for (final String filename : filenames) {
                    result.add(prefix + filename);
                }
            }

            query = session.createQuery(FOLDERNAME_QUERY);
            query.setParameter("parentSid", parentSid.getStoreId());
            final List<Object[]> folders = query.list();
            for (final Object[] folder : folders) {
                boolean folderIsEmpty = false;
                if (recursive) {
                    final List<String> folderFilenames = getFilenames(new DmoStoreId(folder[1].toString()), true, prefix + folder[0] + "\\");
                    folderIsEmpty = folderFilenames.size() == 0;
                    if (!folderIsEmpty)
                        result.addAll(folderFilenames);
                }

                if (folderIsEmpty || !recursive)
                    result.add(prefix + folder[0].toString() + "\\");
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

    @SuppressWarnings("unchecked")
    public Map<String, String> getAllFiles(final DmoStoreId datasetStoreId) throws StoreAccessException {
        LOGGER.debug("Getting files for dataset " + datasetStoreId + ".");

        final Session session = sessionFactory.openSession();
        try {
            final Query query = session.createQuery(ALL_FILES_QUERY);
            query.setParameter("datasetSid", datasetStoreId.getStoreId());
            final List<Object[]> files = query.list();

            final Map<String, String> result = new HashMap<String, String>();
            for (final Object[] file : files) {
                result.put(file[1].toString(), file[0].toString());
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

    public List<ItemVO> getItemAndAllChildren(final DmoStoreId dmoStoreId) throws StoreAccessException {
        final List<ItemVO> itemVOs = new ArrayList<ItemVO>();
        final Session session = sessionFactory.openSession();
        try {
            if (dmoStoreId.isInNamespace(Dataset.NAMESPACE)) {
                collectDatasetChildren(itemVOs, session, dmoStoreId);
            }
        }
        catch (final HibernateException e) {
            throw new StoreAccessException(e);
        }
        finally {
            sessionFactory.closeSession();
        }
        return itemVOs;
    }

    public List<FileItemVO> getDatasetFiles(final DmoStoreId dmoStoreId) throws StoreAccessException {
        if (!dmoStoreId.isInNamespace(Dataset.NAMESPACE))
            throw new IllegalArgumentException(dmoStoreId + " is not in the namespace " + Dataset.NAMESPACE);
        try {
            final Session session = sessionFactory.openSession();
            return collectDatasetFiles(session, dmoStoreId);
        }
        catch (final HibernateException e) {
            throw new StoreAccessException(e);
        }
        finally {
            sessionFactory.closeSession();
        }
    }

    @SuppressWarnings("unchecked")
    private void collectDatasetChildren(final List<ItemVO> itemVOs, final Session session, final DmoStoreId datasetId) {
        final List<FileItemVO> files = collectDatasetFiles(session, datasetId);
        itemVOs.addAll(files);

        final List<FolderItemVO> folders = session.createQuery(SELECT_DATASET_FOLDERITEMS).setParameter("datasetSid", datasetId.getStoreId()).list();
        itemVOs.addAll(folders);
    }

    @SuppressWarnings("unchecked")
    private List<FileItemVO> collectDatasetFiles(final Session session, final DmoStoreId datasetId) {
        return (List<FileItemVO>) session.createQuery(SELECT_DATASET_FILEITEMS).setParameter("datasetSid", datasetId.getStoreId()).list();
    }

    public boolean hasChildItems(final DmoStoreId parentSid) throws StoreAccessException {
        LOGGER.debug("Determining if folder " + parentSid + " has child items.");

        final Session session = sessionFactory.openSession();
        try {
            Query query = session.createQuery(HASCHILDFILE_QUERY);
            query.setParameter("parentSid", parentSid.getStoreId());
            query.setFetchSize(1);
            @SuppressWarnings("rawtypes")
            List l = query.list();
            if (l.size() > 0)
                return true;

            query = session.createQuery(HASCHILDFOLDER_QUERY);
            query.setParameter("parentSid", parentSid.getStoreId());
            query.setFetchSize(1);
            l = query.list();
            if (l.size() > 0)
                return true;

            return false;
        }
        catch (final HibernateException e) {
            throw new StoreAccessException(e);
        }
        finally {
            sessionFactory.closeSession();
        }
    }

    private Criteria createGetCriteria(final Session session, final Class<? extends ItemVO> clazz, final String parentSid, final Integer limit,
            final Integer offset, final ItemOrder order, final ItemFilters filters) throws StoreAccessException
    {
        final Criteria select = session.createCriteria(clazz);
        select.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        // select.setProjection(Projections.distinct(Projections.id()));
        addFiltersOrderingPaging(select, clazz, parentSid, limit, offset, order, filters);
        return select;
    }

    private boolean isFilterClassImplemented(final Class<?> filter) {
        for (final Class<?> implementedFilter : implementedFilters) {
            if (filter.equals(implementedFilter))
                return true;
        }
        return false;
    }

    private void addFiltersOrderingPaging(final Criteria select, final Class<? extends ItemVO> clazz, final String parentSid, final Integer limit,
            final Integer offset, final ItemOrder order, final ItemFilters filters) throws StoreAccessException
    {
        select.add(Restrictions.eq("parentSid", parentSid));

        if (limit >= 0)
            select.setFetchSize(limit);
        if (offset > 0)
            select.setFirstResult(offset);

        if (filters != null) {
            for (final ItemFilter filter : filters.getFilters()) {
                if (filter != null) {
                    final Class<?> filterClass = filter.getClass();
                    if (!isFilterClassImplemented(filterClass)) {
                        throw new UnknownItemFilterException("unknown filter class " + filterClass.getName());
                    }
                }
            }

            addFieldFilter(select, clazz, filters.getCreatorRoleFilter());
            addFieldFilter(select, clazz, filters.getVisibleToFilter());
            addFieldFilter(select, clazz, filters.getAccessibleToFilter());
        }
    }

    private void addFieldFilter(final Criteria select, final Class<? extends ItemVO> clazz, final ItemFieldFilter<?> filter) throws StoreAccessException {
        if (filter == null)
            return;
        final Disjunction disjunction = buildDisjunction(filter.getFilterField().filePropertyName, filter.getDesiredValues());
        if (clazz.equals(FolderItemVO.class))
            select.createCriteria(filter.getFilterField().folderSetPropertyName).add(disjunction);
        else
            select.add(disjunction);
    }

    private Disjunction buildDisjunction(final String propertyName, final Set<?> enumValues) throws StoreAccessException {
        if (enumValues.size() == 0)
            throw new StoreAccessException(new NoFilterValuesSelectedException());

        final Disjunction result = Restrictions.disjunction();
        final Iterator<?> values = enumValues.iterator();
        while (values.hasNext())
            result.add(Restrictions.eq(propertyName, values.next()));
        return result;
    }

    @Override
    public int getTotalMemberCount(final DmoStoreId storeId, final Class<? extends AbstractItemVO> memberClass, final FileItemVOAttribute... fieldValue)
            throws StoreAccessException
    {
        return fetchCount(countMembers(false, storeId, memberClass, fieldValue));
    }

    @Override
    public int getDirectMemberCount(final DmoStoreId storeId, final Class<? extends AbstractItemVO> memberClass, final FileItemVOAttribute... fieldValue)
            throws StoreAccessException
    {
        return fetchCount(countMembers(true, storeId, memberClass, fieldValue));
    }

    @Override
    public boolean hasMember(final DmoStoreId storeId, final Class<? extends AbstractItemVO> memberClass, final FileItemVOAttribute... fieldValue)
            throws StoreAccessException
    {
        // performance hint (also for hasDirectMember):
        // perhaps avoid count(*), you only want to know if any exists, for example consider
        // "SELECT DISTINCT m.datasetSid" or drop distinct and check of the list size is non-zero
        return !countedZero(countMembers(false, storeId, memberClass, fieldValue));
    }

    @Override
    public boolean hasDirectMember(final DmoStoreId storeId, final Class<? extends AbstractItemVO> memberClass, final FileItemVOAttribute... fieldValue)
            throws StoreAccessException
    {
        return !countedZero(countMembers(true, storeId, memberClass, fieldValue));
    }

    @Override
    public boolean hasVisibleFiles(final DmoStoreId storeId, final boolean userIsKnown, final boolean userHasGroupAccess, final boolean userHasPermission)
            throws StoreAccessException
    {
        if (!Dataset.NAMESPACE.equals(storeId.getNamespace()))
            throw new IllegalArgumentException("storeId should be a dataset");

        // performance hint: implement "m.fileName in (...)" for the optional condition
        final Class<FileItemVO> memberClass = FileItemVO.class;

        if (!countedZero(countMembers(false, storeId, memberClass, VisibleTo.ANONYMOUS)))
            return true;

        if (userIsKnown && !countedZero(countMembers(false, storeId, memberClass, VisibleTo.KNOWN)))
            return true;

        if (userHasGroupAccess && !countedZero(countMembers(false, storeId, memberClass, VisibleTo.RESTRICTED_GROUP)))
            return true;

        if (userHasPermission && !countedZero(countMembers(false, storeId, memberClass, VisibleTo.RESTRICTED_REQUEST)))
            return true;

        return false;
    }

    private int fetchCount(final List<? extends Object> query) {
        return new BigDecimal((Long) query.get(0)).intValue();
    }

    private boolean countedZero(final List<? extends Object> query) {
        return ((Long) query.get(0)) == 0;
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
    public <T extends FileItemVOAttribute> Set<T> getValuesFor(final DmoStoreId itemContainer, final Class<T> attribute) throws IllegalArgumentException,
            StoreAccessException
    {
        final String fromWhereClauses;
        if (FolderItem.NAMESPACE.equals(itemContainer.getNamespace()))
            fromWhereClauses = recursiveFromClause(FileItemVO.class) + RECURSIVE_WHERE_CLAUSE;
        else if (Dataset.NAMESPACE.equals(itemContainer.getNamespace()))
            fromWhereClauses = childFromClause(FileItemVO.class) + String.format("WHERE m.datasetSid=:%s", CONTAINER_ID);
        else
            throw new IllegalArgumentException("storeId should be a dataset or folder");

        final String fieldName = Introspector.decapitalize(ClassUtils.getShortCanonicalName(attribute));
        final String queryString = "SELECT DISTINCT m." + fieldName + " " + fromWhereClauses;

        if (LOGGER.isDebugEnabled()) {
            final StackTraceElement callerOfPublicMethod = new Exception().getStackTrace()[1];
            LOGGER.debug("{}:{} {}", callerOfPublicMethod.getMethodName(), callerOfPublicMethod.getLineNumber(), queryString);
        }
        final Session session = sessionFactory.openSession();
        try {
            final Query query = session.createQuery(queryString);
            query.setParameter(CONTAINER_ID, itemContainer.getStoreId());

            /*
             * Fragile code. Query.list() returns a list of Object arrays or a single values depending on query. In this case we expect a list of single values.
             * If for some reason a list of Object[] were to be returned the code will fail further down the line, not here. It seems the Hibernate API does not
             * allow us to find a more robust solution.
             */
            @SuppressWarnings("rawtypes")
            final List rows = query.list();

            final Set<T> result = new HashSet<T>();
            for (final Object row : rows) {

                @SuppressWarnings("unchecked")
                final T value = (T) row;

                if (value != null)
                    result.add(value);
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
}
