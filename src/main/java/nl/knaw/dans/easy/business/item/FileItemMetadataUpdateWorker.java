package nl.knaw.dans.easy.business.item;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.common.lang.ApplicationException;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.repo.UnitOfWork;
import nl.knaw.dans.common.lang.repo.exception.UnitOfWorkInterruptException;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.business.md.amd.AdditionalMetadataOwner;
import nl.knaw.dans.easy.business.md.amd.AdditionalMetadataUpdateException;
import nl.knaw.dans.easy.business.md.amd.AdditionalMetadataUpdateStrategy;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.xml.AdditionalMetadata;
import nl.knaw.dans.easy.xml.ResourceMetadata;
import nl.knaw.dans.easy.xml.ResourceMetadataList;

public class FileItemMetadataUpdateWorker extends ItemWorker
{
    
    private static final Logger logger = LoggerFactory.getLogger(FileItemMetadataUpdateWorker.class);
    
    private final AdditionalMetadataUpdateStrategy updateStrategy;

    public FileItemMetadataUpdateWorker(EasyUser sessionUser, AdditionalMetadataUpdateStrategy updateStrategy)
    {
        super(sessionUser);
        this.updateStrategy = updateStrategy;
    }
    
    public FileItemMetadataUpdateWorker(UnitOfWork uow, AdditionalMetadataUpdateStrategy updateStrategy)
    {
        super(uow);
        this.updateStrategy = updateStrategy;
    }
    
    protected void workUpdateMetadata(Dataset dataset, ResourceMetadataList rmdl) throws ServiceException
    {
        UnitOfWork uow = getUnitOfWork();
        try
        {
            uow.attach(dataset);
            List<ResourceMetadata> rmdList = rmdl.getResourceMetadataAsList();
            for (ResourceMetadata rmd : rmdList)
            {
                String fileItemId = getFileItemId(dataset, rmd);
                if (fileItemId == null)
                {
                    String msg = "Object not found. datasetId=" + dataset.getStoreId() 
                        + " fileItemId=" + rmd.getSid() + " path=" + rmd.getPath();
                    logger.error(msg);
                    informListeners(new ObjectNotAvailableException(msg));
                }
                else
                {
                    FileItem fileItem = (FileItem) uow.retrieveObject(fileItemId);
                    updateMetadata(fileItem, dataset, rmd);
                }
            }
            uow.commit();
        }
        catch (RepositoryException e)
        {
            logger.error("While updating metadata: ", e);
            throw new ServiceException(e);
        }
        catch (UnitOfWorkInterruptException e)
        {
            throw new ServiceException(e);
        }
        finally
        {
            uow.close();
        }
    }

    private void updateMetadata(FileItem fileItem, Dataset dataset, ResourceMetadata rmd)
    {
        try
        {
            checkIntegrity(dataset, fileItem);
        }
        catch (ApplicationException e)
        {
            logger.error("Integrety violation: ", e);
            informListeners(e);
            return;
        }
        
        setDiscoverRights(rmd, fileItem);
        setReadRights(rmd, fileItem);
        
        updateAdditionalMetadata(rmd, fileItem);
    }
    
    private void updateAdditionalMetadata(ResourceMetadata rmd, final FileItem fileItem)
    {
        AdditionalMetadataOwner owner = new AdditionalMetadataOwner()
        {
            
            @Override
            public void setAdditionalMetadata(AdditionalMetadata addmd)
            {
                fileItem.setAdditionalMetadata(addmd);
            }
            
            @Override
            public AdditionalMetadata getAdditionalMetadata()
            {
                return fileItem.getAdditionalMetadata();
            }
        };
        
        try
        {
            updateStrategy.update(owner, rmd.getAdditionalMetadata());
        }
        catch (AdditionalMetadataUpdateException e)
        {
            logger.error("Could not update. fileitemId=" + fileItem.getStoreId(), e);
            informListeners(e);
        }
    }

    private void setDiscoverRights(ResourceMetadata rmd, FileItem fileItem)
    {
        AccessCategory discoverCat = rmd.getCategoryDiscover();
        if (discoverCat != null)
        {
            fileItem.setVisibleTo(VisibleTo.translate(discoverCat));
        }
    }
    
    private void setReadRights(ResourceMetadata rmd, FileItem fileItem)
    {
        AccessCategory readCat = rmd.getCategoryRead();
        if (readCat != null)
        {
            fileItem.setAccessibleTo(AccessibleTo.translate(readCat));
        }
    }

    private String getFileItemId(Dataset dataset, ResourceMetadata rmd) throws ServiceException
    {
        String fileItemId = rmd.getSid();
        if (fileItemId == null)
        {
            fileItemId = getFileItemIdByPath(dataset, rmd);
        }
        return fileItemId;
    }

    private String getFileItemIdByPath(Dataset dataset, ResourceMetadata rmd) throws ServiceException
    {
        String fileItemId;
        String path = rmd.getPath();
        try
        {
            FileItemVO fileItemVO = Data.getFileStoreAccess().findFileByPath(dataset.getStoreId(), path);
            fileItemId = fileItemVO != null ? fileItemVO.getSid() : null;
        }
        catch (StoreAccessException e)
        {
            throw new ServiceException(e);
        }
        return fileItemId;
    }

}
