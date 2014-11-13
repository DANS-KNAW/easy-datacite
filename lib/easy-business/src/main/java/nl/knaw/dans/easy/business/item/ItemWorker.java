package nl.knaw.dans.easy.business.item;

import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.UnitOfWork;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.repo.exception.UnitOfWorkInterruptException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.dataset.item.UpdateInfo;
import nl.knaw.dans.easy.domain.dataset.item.filter.ItemFilters;
import nl.knaw.dans.easy.domain.exceptions.ApplicationException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.DatasetItem;
import nl.knaw.dans.easy.domain.model.DatasetItemContainer;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.worker.AbstractWorker;
import nl.knaw.dans.easy.util.StringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemWorker extends AbstractWorker {

    private static final Logger logger = LoggerFactory.getLogger(ItemWorker.class);

    protected ItemWorker(EasyUser sessionUser) {
        super(sessionUser);
    }

    protected ItemWorker(UnitOfWork uow) {
        super(uow);
    }

    protected void workUpdateObjects(Dataset dataset, List<DmoStoreId> dmoStoreIds, UpdateInfo updateInfo, ItemFilters itemFilters) throws ServiceException {

        UnitOfWork uow = getUnitOfWork();

        try {
            for (DmoStoreId sdmoStoreId : dmoStoreIds) {
                DataModelObject dmo = uow.retrieveObject(sdmoStoreId);
                checkIntegrity(dataset, dmo);
                update(dmo, updateInfo, itemFilters);
            }

            // show debug info on updated objects
            // LB: show lots of debug info, because
            // otherwise it is quite hard to find problems in
            // these updates without it
            if (logger.isDebugEnabled()) {
                logger.debug("\n" + "---------------------------------------------------------------------\n"
                        + "------------------------- UPDATING OBJECTS --------------------------\n"
                        + "---------------------------------------------------------------------\n" + updateInfo.getAction() + "\n"
                        + "Objects selected for update=(" + StringUtil.commaSeparatedList(dmoStoreIds) + ");\n"
                        + "---------------------------------------------------------------------\n");

                for (DataModelObject dmo : uow.getAttachedObjects()) {
                    String updateLine = "Updating " + dmo.getLabel() + " (" + dmo.getStoreId() + "): ";

                    if (updateInfo.getActions().contains(UpdateInfo.Action.RENAME) && dmoStoreIds.contains(dmo.getDmoStoreId())) {
                        updateLine += "renaming " + dmo.getStoreId() + " to " + updateInfo.getName();
                    }

                    if (updateInfo.getActions().contains(UpdateInfo.Action.DELETE)) {
                        if (dmo.isRegisteredDeleted())
                            logger.debug("Deleting " + dmo.getStoreId());
                        else
                            logger.debug(dmo.getStoreId() + " not deleting?");
                    } else {
                        logger.debug(updateLine);
                    }
                }
                logger.debug("\n" + "---------------------------------------------------------------------\n");
                logger.debug("Now committing update...");
            }

            uow.commit();

            // finish the debug block
            logger.debug("\n" + "---------------------------------------------------------------------\n"
                    + "------------------------- END UPDATING OBJECTS ----------------------\n"
                    + "---------------------------------------------------------------------\n");
        }
        catch (UnitOfWorkInterruptException e) {
            // rollBack(e.getMessage());
            throw new UnsupportedOperationException("Rollback not implemented");
        }
        catch (RepositoryException e) {
            throw new ServiceException(e);
        }
        finally {
            uow.close();
        }
    }

    protected void checkIntegrity(Dataset dataset, DataModelObject dmo) {
        DmoStoreId datasetId;
        if (dmo instanceof DatasetItem) {
            datasetId = ((DatasetItem) dmo).getDatasetId();
        } else if (dmo instanceof Dataset) {
            datasetId = ((Dataset) dmo).getDmoStoreId();
        } else {
            throw new ApplicationException("Unknown DataModelObject: " + dmo);
        }
        if (!dataset.getDmoStoreId().equals(datasetId)) {
            throw new ApplicationException("The DataModelObject with id " + dmo.getStoreId() + " does not belong to the dataset " + dataset.getStoreId());
        }
    }

    // recursively update dmo's
    private void update(DataModelObject dmo, UpdateInfo updateInfo, ItemFilters itemFilters) throws StoreAccessException, ObjectNotInStoreException,
            RepositoryException, UnitOfWorkInterruptException
    {
        if (dmo instanceof DatasetItemContainer) {
            updateItemContainer((DatasetItemContainer) dmo, updateInfo, itemFilters);
        } else if (dmo instanceof FileItem) {
            updateFileItem((FileItem) dmo, updateInfo);
        } else {
            throw new ApplicationException("Unknown type: " + dmo);
        }
    }

    private void updateItemContainer(DatasetItemContainer itemContainer, UpdateInfo updateInfo, ItemFilters itemFilters) throws StoreAccessException,
            ObjectNotInStoreException, RepositoryException, UnitOfWorkInterruptException
    {
        if (updateInfo.hasNameUpdate()) {
            itemContainer.setLabel(updateInfo.getName());
        }
        if (updateInfo.hasPropagatingUpdates()) {
            List<ItemVO> itemVOs = Data.getFileStoreAccess().getFilesAndFolders(itemContainer.getDmoStoreId());
            for (ItemVO itemVO : itemVOs) {
                DatasetItem kidItem = (DatasetItem) getUnitOfWork().retrieveObject(new DmoStoreId(itemVO.getSid()));
                update(kidItem, updateInfo, itemFilters);
            }
        }
        if (updateInfo.isRegisteredDeleted()) {
            itemContainer.registerDeleted();
        }
    }

    private void updateFileItem(FileItem fileItem, UpdateInfo updateInfo) throws RepositoryException, UnitOfWorkInterruptException {
        if (updateInfo.hasNameUpdate()) {
            fileItem.setLabel(updateInfo.getName());
        }
        if (updateInfo.isRegisteredDeleted()) {
            fileItem.registerDeleted();
        }
        if (updateInfo.hasAccessibleToUpdate()) {
            fileItem.setAccessibleTo(updateInfo.getAccessibleTo());
        }
        if (updateInfo.hasVisibleToUpdate()) {
            fileItem.setVisibleTo(updateInfo.getVisibleTo());
        }
    }

}
