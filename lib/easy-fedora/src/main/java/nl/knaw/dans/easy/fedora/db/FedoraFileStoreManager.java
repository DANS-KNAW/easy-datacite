package nl.knaw.dans.easy.fedora.db;

import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.db.DbUtil;
import nl.knaw.dans.easy.db.ThreadLocalSessionFactory;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FolderItem;

import org.hibernate.HibernateException;
import org.hibernate.Session;

public class FedoraFileStoreManager {

    private ThreadLocalSessionFactory sessionFactory = ThreadLocalSessionFactory.instance();

    public FedoraFileStoreManager() throws StoreAccessException {
        if (!DbUtil.hasLocalConfig()) {
            throw new StoreAccessException("No local configuration set on " + DbUtil.class.getName());
        }
    }

    public void onIngestFileItem(FileItem fileItem) throws StoreAccessException {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            FileItemVO fivo = new FileItemVO(fileItem);
            session.save(fivo);
            evictAncestorsCollectionPropertiesFromSecondLevelCache(session, fivo.getParentSid());
            session.getTransaction().commit();
        }
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw new StoreAccessException("While inserting metadata on FileItem " + fileItem, e);
        }
        finally {
            sessionFactory.closeSession();
        }
    }

    public FileItemVO onUpdateFileItem(FileItem fileItem) throws StoreAccessException {
        FileItemVO fivo;
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            fivo = (FileItemVO) session.get(FileItemVO.class, fileItem.getStoreId());
            fivo.updateTo(fileItem);
            session.update(fivo);
            evictAncestorsCollectionPropertiesFromSecondLevelCache(session, fivo.getParentSid());
            session.getTransaction().commit();
        }
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw new StoreAccessException("While updating metadata on FileItem " + fileItem, e);
        }
        finally {
            sessionFactory.closeSession();
        }
        return fivo;
    }

    private void evictAncestorsCollectionPropertiesFromSecondLevelCache(Session s, String parentSid) {
        if (parentSid != null & !parentSid.equals("")) {
            FolderItemVO parent = (FolderItemVO) s.get(FolderItemVO.class, parentSid);
            if (parent != null) {
                s.getSessionFactory().getCache().evictCollection("nl.knaw.dans.easy.domain.dataset.item.FolderItemVO.visibilities", parentSid);
                s.getSessionFactory().getCache().evictCollection("nl.knaw.dans.easy.domain.dataset.item.FolderItemVO.accessibilities", parentSid);
                s.getSessionFactory().getCache().evictCollection("nl.knaw.dans.easy.domain.dataset.item.FolderItemVO.creatorRoles", parentSid);
                s.getSessionFactory().getCache().evictCollection("nl.knaw.dans.easy.domain.dataset.item.FolderItemVO.folders", parentSid);
                s.getSessionFactory().getCache().evictCollection("nl.knaw.dans.easy.domain.dataset.item.FolderItemVO.files", parentSid);
                evictAncestorsCollectionPropertiesFromSecondLevelCache(s, parent.getParentSid());
            }
        }
    }

    public void onPurgeFileItem(FileItem fileItem) throws StoreAccessException {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            FileItemVO fivo = (FileItemVO) session.get(FileItemVO.class, fileItem.getStoreId());
            session.delete(fivo);
            evictAncestorsCollectionPropertiesFromSecondLevelCache(session, fivo.getParentSid());
            session.getTransaction().commit();
        }
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw new StoreAccessException("While purging metadata on FileItem " + fileItem, e);
        }
        finally {
            sessionFactory.closeSession();
        }
    }

    public void onIngestFolderItem(FolderItem folderItem) throws StoreAccessException {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            FolderItemVO fovo = new FolderItemVO(folderItem);
            session.save(fovo);
            session.getTransaction().commit();
        }
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw new StoreAccessException("While inserting metadata on FolderItem " + folderItem, e);
        }
        finally {
            sessionFactory.closeSession();
        }
    }

    public FolderItemVO onUpdateFolderItem(FolderItem folderItem) throws StoreAccessException {
        Session session = sessionFactory.openSession();
        FolderItemVO fovo;
        try {
            session.beginTransaction();
            fovo = (FolderItemVO) session.get(FolderItemVO.class, folderItem.getStoreId());
            fovo.updateTo(folderItem);
            session.update(fovo);
            session.getTransaction().commit();
        }
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw new StoreAccessException("While updating metadata on FolderItem " + folderItem, e);
        }
        finally {
            sessionFactory.closeSession();
        }
        return fovo;
    }

    public void onPurgeFolderItem(FolderItem folderItem) throws StoreAccessException {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            FolderItemVO fovo = (FolderItemVO) session.get(FolderItemVO.class, folderItem.getStoreId());
            session.delete(fovo);
            session.getTransaction().commit();
        }
        catch (HibernateException e) {
            session.getTransaction().rollback();
            throw new StoreAccessException("While purging metadata on FolderItem " + folderItem, e);
        }
        finally {
            sessionFactory.closeSession();
        }
    }

}
