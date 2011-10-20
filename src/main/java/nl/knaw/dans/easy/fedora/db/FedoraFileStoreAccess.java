package nl.knaw.dans.easy.fedora.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.knaw.dans.common.fedora.Fedora;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.data.store.StoreException;
import nl.knaw.dans.easy.db.DbLocalConfig;
import nl.knaw.dans.easy.db.DbUtil;
import nl.knaw.dans.easy.db.ThreadLocalSessionFactory;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.dataset.item.ItemOrder;
import nl.knaw.dans.easy.domain.dataset.item.ItemOrderField;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.dataset.item.filter.AccessibleToFieldFilter;
import nl.knaw.dans.easy.domain.dataset.item.filter.CreatorRoleFieldFilter;
import nl.knaw.dans.easy.domain.dataset.item.filter.ItemFieldFilter;
import nl.knaw.dans.easy.domain.dataset.item.filter.ItemFilter;
import nl.knaw.dans.easy.domain.dataset.item.filter.ItemFilters;
import nl.knaw.dans.easy.domain.dataset.item.filter.VisibleToFieldFilter;
import nl.knaw.dans.easy.domain.exceptions.NoFilterValuesSelectedException;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FolderItem;
import nl.knaw.dans.easy.fedora.db.exceptions.UnknownItemFilterException;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lobo
 */
public class FedoraFileStoreAccess implements nl.knaw.dans.easy.data.store.FileStoreAccess
{
    private static final Logger       LOGGER               = LoggerFactory.getLogger(FedoraFileStoreAccess.class);

    private ThreadLocalSessionFactory sessionFactory       = new ThreadLocalSessionFactory();

    private static final String       FILENAME_QUERY       = "SELECT name FROM " + FileItemVO.class.getName()
                                                                   + " WHERE parentSid=:parentSid";

    private static final String       ALL_FILES_QUERY      = "SELECT name, sid FROM " + FileItemVO.class.getName()
        + " WHERE datasetSid=:datasetSid";

    private static final String       FOLDERNAME_QUERY     = "SELECT name, sid FROM " + FolderItemVO.class.getName()
                                                                   + " WHERE parentSid=:parentSid";

    private static final String       HASCHILDFILE_QUERY   = "SELECT sid FROM " + FileItemVO.class.getName()
                                                                   + " WHERE parentSid=:parentSid";               // LIMIT
    // 1

    private static final String       HASCHILDFOLDER_QUERY = "SELECT sid FROM " + FolderItemVO.class.getName()
                                                                   + " WHERE parentSid=:parentSid";               // LIMIT
    // 1
    
    private static final String FILE_PATH_QUERY = "SELECT fivo FROM " + FileItemVO.class.getName() //
        + " AS fivo" //
        + " WHERE datasetSid=:datasetSid AND path=:path";
    
    private static final String FOLDER_PATH_QUERY = "SELECT fovo FROM " + FolderItemVO.class.getName() //
        + " AS fovo" //
        + " WHERE datasetSid=:datasetSid AND (path=:path OR path=:path2)";
    
    private static final String DATASET_ID_OF_FOLDER_QUERY = "SELECT datasetSid AS datasetId FROM "
        + FolderItemVO.class.getName() //
        + " WHERE pid=:itemId";
    
    private static final String DATASET_ID_OF_FILE_QUERY = "SELECT datasetSid AS datasetId FROM "
        + FileItemVO.class.getName() //
        + " WHERE pid=:itemId";

    private static final String       NAME_FILE_ITEM       = FileItemVO.class.getName();
    private static final String       NAME_FOLDER_ITEM     = FolderItemVO.class.getName();

    @SuppressWarnings("rawtypes")
    static private Class[]            implementedFilters   = {CreatorRoleFieldFilter.class, VisibleToFieldFilter.class,
            AccessibleToFieldFilter.class                  };

    public FedoraFileStoreAccess()
    {
        // null constructor (do not call)
    }

    public FedoraFileStoreAccess(Fedora fedora, DbLocalConfig localConfig)
    {
        DbUtil.setLocalConfig(localConfig);
        DbUtil.checkConnection();
    }

    private String getInfoMsg(String parentSid, Integer limit, Integer offset, ItemOrder order, ItemFilters filters)
    {
        String result = parentSid;
        result += " ";
        result += limit >= 0 ? "limit = " + limit : "limiting off";
        result += " ";
        result += limit >= 0 ? "offset = " + limit : "no offset";
        result += " ";
        result += order != null ? "order = " + order.getField().propertyName + " "
                + (order.isAscending() ? "ascending" : "descending") : "no ordering";
        result += " ";
        if (filters != null)
        {
            result += "Filters: ";
            for (ItemFilter filter : filters.getFilters())
            {
                if (filter != null)
                    result += filter.toString();
            }
        }
        else
            result += "no filtering";
        return result;
    }

    @SuppressWarnings("unchecked")
    public FileItemVO findFileById(String sid) throws StoreAccessException
    {
        FileItemVO fivo = null;
        Session session = sessionFactory.openSession();
        try
        {
            session.beginTransaction();

            List<FileItemVO> items = session.createQuery(
                    "select fivo from " + NAME_FILE_ITEM + " as fivo where fivo.sid = :sid").setParameter("sid", sid)
                    .setFetchSize(1).list();
            if (items.size() > 0)
            {
                fivo = items.get(0);
            }
        }
        catch (HibernateException e)
        {
            throw new StoreAccessException(e);
        }
        finally
        {
            session.getTransaction().commit();
            sessionFactory.closeSession();
        }
        return fivo;
    }
    
    @SuppressWarnings("unchecked")
    public FileItemVO findFileByPath(String datasetSid, String relativePath) throws StoreAccessException
    {
        FileItemVO fivo = null;
        Session session = sessionFactory.openSession();
        
        try
        {
            session.beginTransaction();
            List<FileItemVO> items = session.createQuery(FILE_PATH_QUERY) //
                .setParameter("datasetSid", datasetSid) //
                .setParameter("path", relativePath) //
                .setFetchSize(1) //
                .list();
            if (items.size() > 0)
            {
                fivo = items.get(0);
            }
        }
        catch (HibernateException e)
        {
            throw new StoreAccessException(e);
        }
        finally
        {
            session.getTransaction().commit();
            sessionFactory.closeSession();
        }
        return fivo;
    }
    
    @SuppressWarnings("unchecked")
    public FolderItemVO findFolderByPath(String datasetSid, String relativePath) throws StoreAccessException
    {
        FolderItemVO fovo = null;
        Session session = sessionFactory.openSession();
        String path2 = relativePath.endsWith("/") ? relativePath.substring(0, relativePath.length() -1) : relativePath + "/";
        try
        {
            session.beginTransaction();
            List<FolderItemVO> items = session.createQuery(FOLDER_PATH_QUERY) //
                .setParameter("datasetSid", datasetSid) //
                .setParameter("path", relativePath) //
                .setParameter("path2", path2) //
                .setFetchSize(1) //
                .list();
            if (items.size() > 0)
            {
                fovo = items.get(0);
            }
        }
        catch (HibernateException e)
        {
            throw new StoreAccessException(e);
        }
        finally
        {
            session.getTransaction().commit();
            sessionFactory.closeSession();
        }
        return fovo;
    }

    @SuppressWarnings("unchecked")
    public FolderItemVO findFolderById(String sid) throws StoreAccessException
    {
        FolderItemVO fovo = null;
        Session session = sessionFactory.openSession();
        try
        {
            session.beginTransaction();

            List<FolderItemVO> items = session.createQuery(
                    "select fovo from " + NAME_FOLDER_ITEM + " as fovo where fovo.sid = :sid").setParameter("sid", sid)
                    .setFetchSize(1).list();
            if (items.size() > 0)
            {
                fovo = items.get(0);
            }
        }
        catch (HibernateException e)
        {
            throw new StoreAccessException(e);
        }
        finally
        {
            session.getTransaction().commit();
            sessionFactory.closeSession();
        }
        return fovo;
    }

    @SuppressWarnings("unchecked")
    public List<FileItemVO> findFilesById(Collection<String> sids) throws StoreAccessException
    {
        if (sids.isEmpty())
        {
            throw new IllegalArgumentException("Nothing to find. Empty collection.");
        }
        List<FileItemVO> fivoList;
        Session session = sessionFactory.openSession();
        try
        {
            session.beginTransaction();

            fivoList = session.createCriteria(FileItemVO.class).add(Restrictions.in("sid", sids)).list();
        }
        catch (HibernateException e)
        {
            throw new StoreAccessException(e);
        }
        finally
        {
            session.getTransaction().commit();
            sessionFactory.closeSession();
        }
        return fivoList;
    }

    @SuppressWarnings("unchecked")
    public List<FolderItemVO> findFoldersById(Collection<String> sids) throws StoreAccessException
    {
        if (sids.isEmpty())
        {
            throw new IllegalArgumentException("Nothing to find. Empty collection.");
        }
        List<FolderItemVO> fovoList;
        Session session = sessionFactory.openSession();
        try
        {
            session.beginTransaction();

            fovoList = session.createCriteria(FolderItemVO.class).add(Restrictions.in("sid", sids)).list();
        }
        catch (HibernateException e)
        {
            throw new StoreAccessException(e);
        }
        finally
        {
            session.getTransaction().commit();
            sessionFactory.closeSession();
        }
        return fovoList;
    }

    public List<ItemVO> findFilesAndFoldersById(Collection<String> sids) throws StoreAccessException
    {
        List<ItemVO> items = new ArrayList<ItemVO>();
        items.addAll(findFilesById(sids));
        items.addAll(findFoldersById(sids));
        return items;
    }

    public List<ItemVO> getFilesAndFolders(String parentSid, Integer limit, Integer offset, ItemOrder order,
            ItemFilters filters) throws StoreAccessException
    {
        if (limit > 0 || offset > 0 || order != null)
            throw new StoreAccessException("filter and order not implemented yet.");

        LOGGER.debug("Getting files and folders from the Fedora database for "
                + getInfoMsg(parentSid, limit, offset, order, filters));

        try
        {
            // open session from ThreadLocalSessionFactory ensures
            // the same session gets shared between getFiles and getFolders
            sessionFactory.openSession();

            List<FolderItemVO> folders = getFolders(parentSid, limit, offset, order, filters);
            if (limit > 0 && limit - folders.size() <= 0)
            {
                List<ItemVO> result = new ArrayList<ItemVO>(folders.size());
                result.addAll(folders);
                return result;
            }

            List<FileItemVO> files = getFiles(parentSid, limit - folders.size(), offset, order, filters);

            List<ItemVO> result = new ArrayList<ItemVO>(files.size() + folders.size());
            result.addAll(folders);
            result.addAll(files);

            LOGGER.debug("Returned " + result.size() + " files and folders.");

            return result;
        }
        catch (HibernateException e)
        {
            throw new StoreAccessException(e);
        }
        finally
        {
            sessionFactory.closeSession();
        }
    }

    @SuppressWarnings("unchecked")
    public List<FileItemVO> getFiles(String parentSid, Integer limit, Integer offset, ItemOrder order,
            ItemFilters filters) throws StoreAccessException
    {
        if (limit > 0 || offset > 0 || order != null)
            throw new StoreAccessException("filter and order not implemented yet.");

        LOGGER.debug("Getting files from the Fedora database for "
                + getInfoMsg(parentSid, limit, offset, order, filters));

        try
        {
            Session session = sessionFactory.openSession();

            Criteria select = createGetCriteria(session, FileItemVO.class, parentSid, limit, offset, order, filters);
            if (order != null)
            {
                if (!order.getField().equals(ItemOrderField.NAME))
                {
                    String orderField = order.getField().toString();
                    select.addOrder(order.isAscending() ? Order.asc(orderField) : Order.desc(orderField));
                }
                else
                {
                    // do something special for name ordering
                }
            }

            List<FileItemVO> files = select.list();
            LOGGER.debug("Returned " + files.size() + " files.");
            return files;

        }
        catch (HibernateException e)
        {
            throw new StoreAccessException(e);
        }
        finally
        {
            sessionFactory.closeSession();
        }
    }

    @SuppressWarnings("unchecked")
    public List<FolderItemVO> getFolders(String parentSid, Integer limit, Integer offset, ItemOrder order,
            ItemFilters filters) throws StoreAccessException
    {
        if (limit > 0 || offset > 0 || order != null)
            throw new StoreAccessException("paging and order not implemented yet.");

        LOGGER.debug("Getting folders from the Fedora database for "
                + getInfoMsg(parentSid, limit, offset, order, filters));

        try
        {
            Session session = sessionFactory.openSession();

            Criteria select = createGetCriteria(session, FolderItemVO.class, parentSid, limit, offset, order, filters);
            List<FolderItemVO> folders = select.list();

            // add child counts for each folder
            for (FolderItemVO folder : folders)
            {
                folder.setChildItemCount(getChildCount(folder.getSid(), limit, offset, order, filters));
            }

            LOGGER.debug("Returned " + folders.size() + " folders.");

            return folders;
        }
        catch (HibernateException e)
        {
            throw new StoreAccessException(e);
        }
        finally
        {
            sessionFactory.closeSession();
        }
    }
    
    @Override
    public String getDatasetId(String storeId) throws StoreException
    {
        String query;
        if (storeId.startsWith(FolderItem.NAMESPACE))
        {
            query = DATASET_ID_OF_FOLDER_QUERY;
        }
        else if (storeId.startsWith(FileItem.NAMESPACE))
        {
            query = DATASET_ID_OF_FILE_QUERY;
        }
        else
        {
            throw new StoreException("storeId with unqueriable namespace: " + storeId);
        }
        
        Session session = sessionFactory.openSession();
        String result = (String) session.createQuery(query)
            .setParameter("itemId", storeId)
            .uniqueResult();

        
        return result;
    }

    private int getChildCount(String parentSid, Integer limit, Integer offset, ItemOrder order, ItemFilters filters)
            throws StoreAccessException
    {
        LOGGER.debug("Getting childcount for folder " + parentSid + ".");

        return getChildCount(parentSid, FileItemVO.class, limit, offset, order, filters)
                + getChildCount(parentSid, FolderItemVO.class, limit, offset, order, filters);
    }

    private int getChildCount(String parentSid, Class<? extends ItemVO> clazz, Integer limit, Integer offset,
            ItemOrder order, ItemFilters filters) throws StoreAccessException
    {
        try
        {
            Session session = sessionFactory.openSession();

            Criteria select = session.createCriteria(clazz).setProjection(
                    Projections.projectionList().add(Projections.count("sid")));
            addFiltersOrderingPaging(select, clazz, parentSid, -1, -1, null, filters);
            Object result = select.uniqueResult();
            if (result instanceof Integer)
                return (Integer) result;
            else
                throw new StoreAccessException("Could not retrieve child count for parent sid " + parentSid + ". "
                        + "Result type of child count query is not an int.");
        }
        catch (HibernateException e)
        {
            throw new StoreAccessException(e);
        }
        finally
        {
            sessionFactory.closeSession();
        }
    }

    public List<String> getFilenames(String parentSid, boolean recursive) throws StoreAccessException
    {
        return getFilenames(parentSid, true, "");
    }

    @SuppressWarnings("unchecked")
    private List<String> getFilenames(String parentSid, boolean recursive, String prefix) throws StoreAccessException
    {
        LOGGER.debug("Getting filenames for folder " + parentSid + ".");

        try
        {
            Session session = sessionFactory.openSession();

            List<String> result = new ArrayList<String>();

            Query query = session.createQuery(FILENAME_QUERY);
            query.setParameter("parentSid", parentSid);
            if (prefix.equals(""))
            {
                result.addAll(query.list());
            }
            else
            {
                List<String> filenames = query.list();
                for (String filename : filenames)
                {
                    result.add(prefix + filename);
                }
            }

            query = session.createQuery(FOLDERNAME_QUERY);
            query.setParameter("parentSid", parentSid);
            List<Object[]> folders = query.list();
            for (Object[] folder : folders)
            {
                boolean folderIsEmpty = false;
                if (recursive)
                {
                    List<String> folderFilenames = getFilenames(folder[1].toString(), true, prefix + folder[0] + "\\");
                    folderIsEmpty = folderFilenames.size() == 0;
                    if (!folderIsEmpty)
                        result.addAll(folderFilenames);
                }

                if (folderIsEmpty || !recursive)
                    result.add(prefix + folder[0].toString() + "\\");
            }

            return result;
        }
        catch (HibernateException e)
        {
            throw new StoreAccessException(e);
        }
        finally
        {
            sessionFactory.closeSession();
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String,String> getAllFiles(final String datasetStoreId) throws StoreAccessException
    {
        LOGGER.debug("Getting files for dataset " + datasetStoreId + ".");

        try
        {
            Session session = sessionFactory.openSession();

            Query query = session.createQuery(ALL_FILES_QUERY);
            query.setParameter("datasetSid", datasetStoreId);
            List<Object[]> files = query.list();

            Map<String,String> result = new HashMap<String,String>();
            for (Object[] file: files){
                result.put(file[1].toString(), file[0].toString());
            }
            return result;
        }
        catch (HibernateException e)
        {
            throw new StoreAccessException(e);
        }
        finally
        {
            sessionFactory.closeSession();
        }
    }

    @SuppressWarnings("unchecked")
    public boolean hasChildItems(String parentSid) throws StoreAccessException
    {
        LOGGER.debug("Determining if folder " + parentSid + " has child items.");

        try
        {
            Session session = sessionFactory.openSession();

            Query query = session.createQuery(HASCHILDFILE_QUERY);
            query.setParameter("parentSid", parentSid);
            query.setFetchSize(1);
            List l = query.list();
            if (l.size() > 0)
                return true;

            query = session.createQuery(HASCHILDFOLDER_QUERY);
            query.setParameter("parentSid", parentSid);
            query.setFetchSize(1);
            l = query.list();
            if (l.size() > 0)
                return true;

            return false;
        }
        catch (HibernateException e)
        {
            throw new StoreAccessException(e);
        }
        finally
        {
            sessionFactory.closeSession();
        }
    }

    private Criteria createGetCriteria(Session session, Class<? extends ItemVO> clazz, String parentSid, Integer limit,
            Integer offset, ItemOrder order, ItemFilters filters) throws StoreAccessException
    {
        Criteria select = session.createCriteria(clazz);
        select.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        // select.setProjection(Projections.distinct(Projections.id()));
        addFiltersOrderingPaging(select, clazz, parentSid, limit, offset, order, filters);
        return select;
    }

    private boolean isFilterClassImplemented(Class filter)
    {
        for (Class implementedFilter : implementedFilters)
        {
            if (filter.equals(implementedFilter))
                return true;
        }
        return false;
    }

    private void addFiltersOrderingPaging(Criteria select, Class<? extends ItemVO> clazz, String parentSid,
            Integer limit, Integer offset, ItemOrder order, ItemFilters filters) throws StoreAccessException
    {
        select.add(Restrictions.eq("parentSid", parentSid));

        if (limit >= 0)
            select.setFetchSize(limit);
        if (offset > 0)
            select.setFirstResult(offset);

        if (filters != null)
        {
            for (ItemFilter filter : filters.getFilters())
            {
                if (filter != null)
                {
                    Class<?> filterClass = filter.getClass();
                    if (!isFilterClassImplemented(filterClass))
                    {
                        throw new UnknownItemFilterException("unknown filter class " + filterClass.getName());
                    }
                }
            }

            addFieldFilter(select, clazz, filters.getCreatorRoleFilter());
            addFieldFilter(select, clazz, filters.getVisibleToFilter());
            addFieldFilter(select, clazz, filters.getAccessibleToFilter());
        }
    }

    private void addFieldFilter(Criteria select, Class<? extends ItemVO> clazz, ItemFieldFilter<?> filter)
            throws StoreAccessException
    {
        if (filter == null)
            return;
        Disjunction disjunction = buildDisjunction(filter.getFilterField().filePropertyName, filter.getDesiredValues());
        if (clazz.equals(FolderItemVO.class))
            select.createCriteria(filter.getFilterField().folderSetPropertyName).add(disjunction);
        else
            select.add(disjunction);
    }

    private Disjunction buildDisjunction(String propertyName, Set enumValues) throws StoreAccessException
    {
        if (enumValues.size() == 0)
            throw new StoreAccessException(new NoFilterValuesSelectedException());

        Disjunction result = Restrictions.disjunction();
        Iterator values = enumValues.iterator();
        while (values.hasNext())
            result.add(Restrictions.eq(propertyName, values.next()));
        return result;
    }
}
