package nl.knaw.dans.easy.fedora.db;

import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.db.DbUtil;
import nl.knaw.dans.easy.db.ThreadLocalSessionFactory;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FolderItem;

import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;

public class FedoraFileStoreManager
{

    private ThreadLocalSessionFactory sessionFactory = ThreadLocalSessionFactory.instance();

    public FedoraFileStoreManager() throws StoreAccessException
    {
        if (!DbUtil.hasLocalConfig())
        {
            throw new StoreAccessException("No local configuration set on " + DbUtil.class.getName());
        }
    }

    public void onIngestFileItem(FileItem fileItem) throws StoreAccessException
    {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try
        {
            tx.begin();
            FileItemVO fivo = new FileItemVO(fileItem);
            session.save(fivo);
            tx.commit();
        }
        catch (HibernateException e)
        {
            throw new StoreAccessException("While inserting metadata on FileItem " + fileItem, e);
        }
        finally
        {
            if (tx.isActive())
                tx.rollback();
            sessionFactory.closeSession();
        }
    }

    public FileItemVO onUpdateFileItem(FileItem fileItem) throws StoreAccessException
    {
        FileItemVO fivo;
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try
        {
            tx.begin();
            fivo = (FileItemVO) session.get(FileItemVO.class, fileItem.getStoreId());
            fivo.updateTo(fileItem);
            session.update(fivo);
            tx.commit();
        }
        catch (HibernateException e)
        {
            throw new StoreAccessException("While updating metadata on FileItem " + fileItem, e);
        }
        finally
        {
            if (tx.isActive())
                tx.rollback();
            sessionFactory.closeSession();
        }
        return fivo;
    }

    public void onPurgeFileItem(FileItem fileItem) throws StoreAccessException
    {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try
        {
            tx.begin();
            FileItemVO fivo = (FileItemVO) session.get(FileItemVO.class, fileItem.getStoreId());
            session.delete(fivo);
            tx.commit();
        }
        catch (HibernateException e)
        {
            throw new StoreAccessException("While purging metadata on FileItem " + fileItem, e);
        }
        finally
        {
            if (tx.isActive())
                tx.rollback();
            sessionFactory.closeSession();
        }
    }

    public void onIngestFolderItem(FolderItem folderItem) throws StoreAccessException
    {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try
        {
            tx.begin();
            FolderItemVO fovo = new FolderItemVO(folderItem);
            session.save(fovo);
            tx.commit();
        }
        catch (HibernateException e)
        {
            throw new StoreAccessException("While inserting metadata on FolderItem " + folderItem, e);
        }
        finally
        {
            if (tx.isActive())
                tx.rollback();
            sessionFactory.closeSession();
        }
    }

    public FolderItemVO onUpdateFolderItem(FolderItem folderItem) throws StoreAccessException
    {
        Session session = sessionFactory.openSession();
        FolderItemVO fovo;
        Transaction tx = session.beginTransaction();
        try
        {
            tx.begin();
            fovo = (FolderItemVO) session.get(FolderItemVO.class, folderItem.getStoreId());
            fovo.updateTo(folderItem);
            session.update(fovo);
            tx.commit();
        }
        catch (HibernateException e)
        {
            throw new StoreAccessException("While updating metadata on FolderItem " + folderItem, e);
        }
        finally
        {
            if (tx.isActive())
                tx.rollback();
            sessionFactory.closeSession();
        }
        return fovo;
    }

    public void onPurgeFolderItem(FolderItem folderItem) throws StoreAccessException
    {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try
        {
            tx.begin();
            FolderItemVO fovo = (FolderItemVO) session.get(FolderItemVO.class, folderItem.getStoreId());
            session.delete(fovo);
            tx.commit();
        }
        catch (HibernateException e)
        {
            throw new StoreAccessException("While purging metadata on FolderItem " + folderItem, e);
        }
        finally
        {
            if (tx.isActive())
                tx.rollback();
            sessionFactory.closeSession();
        }
    }

}
