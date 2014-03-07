package nl.knaw.dans.easy.fedora.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DefaultDmoStoreEventListener;
import nl.knaw.dans.common.lang.repo.DmoStore;
import nl.knaw.dans.common.lang.repo.exception.DmoStoreEventListenerException;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FolderItem;

public class FileStoreSyncListener extends DefaultDmoStoreEventListener
{

    private static final Logger logger = LoggerFactory.getLogger(FileStoreSyncListener.class);

    private final FedoraFileStoreManager fsManager;

    public FileStoreSyncListener() throws StoreAccessException
    {
        fsManager = new FedoraFileStoreManager();
    }

    @Override
    public void afterIngest(DmoStore store, DataModelObject dmo) throws DmoStoreEventListenerException
    {
        if (FileItem.NAMESPACE.sameAs(dmo.getDmoNamespace()))
        {
            try
            {
                fsManager.onIngestFileItem((FileItem) dmo);
            }
            catch (StoreAccessException e)
            {
                throw new DmoStoreEventListenerException(e);
            }
        }
        else if (FolderItem.NAMESPACE.sameAs(dmo.getDmoNamespace()))
        {
            try
            {
                fsManager.onIngestFolderItem((FolderItem) dmo);
            }
            catch (StoreAccessException e)
            {
                throw new DmoStoreEventListenerException(e);
            }
        }
    }

    @Override
    public void afterUpdate(DmoStore store, DataModelObject dmo) throws DmoStoreEventListenerException
    {
        if (FileItem.NAMESPACE.sameAs(dmo.getDmoNamespace()))
        {
            try
            {
                fsManager.onUpdateFileItem((FileItem) dmo);
            }
            catch (StoreAccessException e)
            {
                throw new DmoStoreEventListenerException(e);
            }
        }
        else if (FolderItem.NAMESPACE.sameAs(dmo.getDmoNamespace()))
        {
            try
            {
                fsManager.onUpdateFolderItem((FolderItem) dmo);
            }
            catch (StoreAccessException e)
            {
                throw new DmoStoreEventListenerException(e);
            }
        }
    }

    @Override
    public void afterPartialUpdate(DmoStore store, DataModelObject dmo) throws DmoStoreEventListenerException
    {
        if (FileItem.NAMESPACE.sameAs(dmo.getDmoNamespace()) || FolderItem.NAMESPACE.sameAs(dmo.getDmoNamespace()))
        {
            logger.error("SEVERE. \n\tPartial update of FileItem or FolderItem. FileStore may not be in sync with Fedora.\n");
        }
    }

    @Override
    public void afterPurge(DmoStore store, DataModelObject dmo) throws DmoStoreEventListenerException
    {
        if (FileItem.NAMESPACE.sameAs(dmo.getDmoNamespace()))
        {
            try
            {
                fsManager.onPurgeFileItem((FileItem) dmo);
            }
            catch (StoreAccessException e)
            {
                throw new DmoStoreEventListenerException(e);
            }
        }
        else if (FolderItem.NAMESPACE.sameAs(dmo.getDmoNamespace()))
        {
            try
            {
                fsManager.onPurgeFolderItem((FolderItem) dmo);
            }
            catch (StoreAccessException e)
            {
                throw new DmoStoreEventListenerException(e);
            }
        }
    }

}
