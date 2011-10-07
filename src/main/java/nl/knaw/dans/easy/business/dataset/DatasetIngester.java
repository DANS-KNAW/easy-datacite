package nl.knaw.dans.easy.business.dataset;

import java.util.Arrays;
import java.util.List;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.UnitOfWork;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.business.item.ItemWorker;
import nl.knaw.dans.easy.data.store.EasyUnitOfWork;
import nl.knaw.dans.easy.domain.dataset.item.UpdateInfo;
import nl.knaw.dans.easy.domain.dataset.item.filter.ItemFilters;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.easy.domain.model.user.Group;
import nl.knaw.dans.easy.domain.user.GroupImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatasetIngester implements SubmissionProcessor
{   
    private static final Logger logger = LoggerFactory.getLogger(DatasetIngester.class);
    
    private boolean updateFileRights;
    
    public DatasetIngester(boolean updateFileRights)
    {
        this.updateFileRights = updateFileRights;
    }
    
    public boolean continueAfterFailure()
    {
        return false;
    }

    public boolean process(DatasetSubmissionImpl submission)
    {
        UnitOfWork uow = new EasyUnitOfWork(submission.getSessionUser());
        Dataset dataset = submission.getDataset();
        DatasetState previousState = dataset.getAdministrativeMetadata().getAdministrativeState();
        try
        {
            uow.attach(dataset);
            
            // submission date is already set while generating the license.
            dataset.getAdministrativeMetadata().setAdministrativeState(DatasetState.SUBMITTED);
            AccessCategory accessCategory = dataset.getAccessCategory();
            //  Provisional implementation of assigning groups
            boolean hasMDFarchaeology = MetadataFormat.ARCHAEOLOGY.equals(dataset.getMetadataFormat());
            if (AccessCategory.GROUP_ACCESS.equals(accessCategory) && hasMDFarchaeology)
            {
                dataset.addGroup(new GroupImpl(Group.ID_ARCHEOLOGY));
                logger.info(">>>>>>>>>>> Provisional implementation of assigning groups to datasets. <<<<<<<<<<<<<<<");
            }
            else
            {
                dataset.removeGroup(new GroupImpl(Group.ID_ARCHEOLOGY));
            }
            //  End provisional implementation of assigning groups
            
            if (updateFileRights)
            {
                VisibleTo vt = VisibleTo.ANONYMOUS; // all files are visible, unless an archivist decides differently.
                AccessibleTo at = AccessibleTo.translate(accessCategory);
                UpdateInfo updateInfo = new UpdateInfo();
                updateInfo.updateAccessibleTo(at);
                updateInfo.updateVisibleTo(vt);
                List<String> sids = Arrays.asList(dataset.getStoreId());
                ItemWorkerProxy proxy = new ItemWorkerProxy(uow);
                proxy.updateObjects(dataset, sids, updateInfo, null);
            }
            
            uow.commit();
            submission.setSubmitted(true); 
        }
        catch (Exception e)
        {
            logger.error("Exception while submitting dataset " + dataset.getStoreId(), e);
            dataset.getAdministrativeMetadata().setAdministrativeState(previousState);
        }
        finally
        {
            uow.close();
        }
        return submission.isSubmitted();
    }
    
    private static class ItemWorkerProxy extends ItemWorker
    {
        
        protected ItemWorkerProxy(UnitOfWork uow)
        {
            super(uow);
        }
        
        protected void updateObjects(Dataset dataset, List<String> sids, UpdateInfo updateInfo,
                ItemFilters itemFilters) throws ServiceException
        {
            super.workUpdateObjects(dataset, sids, updateInfo, itemFilters);
        }
    }

}
