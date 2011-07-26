package nl.knaw.dans.easy.business.item;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.UnitOfWork;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.security.authz.AuthzStrategy;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.dataset.FileItemDescription;
import nl.knaw.dans.easy.domain.dataset.item.UpdateInfo;
import nl.knaw.dans.easy.domain.dataset.item.filter.ItemFilters;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.DatasetItemContainer;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.worker.WorkListener;
import nl.knaw.dans.easy.security.authz.AuthzStrategyProvider;

import org.dom4j.Element;

public class ItemWorkDispatcher
{

    public ItemWorkDispatcher()
    {

    }

    public void addDirectoryContents(EasyUser sessionUser, Dataset dataset, DatasetItemContainer parentContainer, File rootFile, FileFilter fileFilter,
            UnitOfWork uow, ItemIngesterDelegator delegator, WorkListener...workListeners) throws ServiceException
    {
        ItemIngester ingester = new ItemIngester(uow, dataset, sessionUser, delegator);
        ingester.addWorkListeners(workListeners);
        ingester.workAddDirectoryContents(parentContainer, rootFile, fileFilter);
    }
    
    public void updateObjects(EasyUser sessionUser, Dataset dataset, List<String> sids, UpdateInfo updateInfo, ItemFilters itemFilters, UnitOfWork uow, WorkListener...workListeners)
        throws ServiceException
    {
        ItemWorker worker = new ItemWorker(uow);
        worker.addWorkListeners(workListeners);
        worker.workUpdateObjects(dataset, sids, updateInfo, itemFilters);
    }   
    

    public void saveDescriptiveMetadata(EasyUser sessionUser, final UnitOfWork uow, final Dataset dataset, final Map<String, Element> descriptiveMetadataMap, WorkListener...workListeners) throws ServiceException
    {
        DescriptiveMetadataWorker worker = new DescriptiveMetadataWorker(uow);
        worker.addWorkListeners(workListeners);
        worker.saveDescriptiveMetadata(dataset, descriptiveMetadataMap);
    }
    
    public FileItem getFileItem(EasyUser sessionUser, Dataset dataset, String fileItemId) throws ServiceException
    {
        FileItem fileItem = getFileItem(dataset, fileItemId);
        String name = fileItem.getAutzStrategyName();
        AuthzStrategy strategy = AuthzStrategyProvider.newAuthzStrategy(name, sessionUser, fileItem, dataset);
        fileItem.setAuthzStrategy(strategy);
        return fileItem;
    }
    
    public FileItemDescription getFileItemDescription(EasyUser sessionUser, Dataset dataset, FileItem fileItem) throws ServiceException
    {
        return new FileItemDescription(fileItem.getFileItemMetadata(), fileItem.getDescriptiveMetadata());
    }
    
    public URL getFileContentURL(EasyUser sessionUser, Dataset dataset, FileItem fileItem) throws ServiceException
    {
        URL url = Data.getEasyStore().getFileURL(fileItem.getStoreId());
        return url;
    }

    public URL getDescriptiveMetadataURL(EasyUser sessionUser, Dataset dataset, String fileItemId) throws ServiceException
    {
        URL url = Data.getEasyStore().getDescriptiveMetadataURL(fileItemId);
        return url;
    }
    
    private FileItem getFileItem(Dataset dataset, String fileItemId) throws ObjectNotAvailableException, ServiceException
    {
        FileItem fileItem;
        try
        {
            fileItem = (FileItem) Data.getEasyStore().retrieve(fileItemId);
            if (!fileItem.getFileItemMetadata().getDatasetId().equals(dataset.getStoreId()))
            {
                throw new ObjectNotAvailableException("FileItem '" + fileItemId + "' does not belong to dataset '" + dataset.getStoreId() + "'");
            }                    
        }
        catch (ObjectNotInStoreException e)
        {
            throw new ObjectNotAvailableException(e);
        }
        catch (RepositoryException e)
        {
            throw new ServiceException(e);
        }
        return fileItem;
    }

}
