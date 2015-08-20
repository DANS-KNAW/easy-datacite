package nl.knaw.dans.easy.tools.task.am.dataset;

import java.util.Map;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.UnitOfWork;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.repo.exception.UnitOfWorkInterruptException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.EasyUnitOfWork;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;

// issue https://drivenbydata.onjira.com/browse/EASY-246
public class FileRightsNoAccessCorrectorTask extends AbstractDatasetTask {

    private EasyUser migration;
    private boolean affected;

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        abbortIfNotMigration(joint);

        if (hasTaskStamp(joint)) {
            return; // already did this one
        }

        affected = false;
        Dataset dataset = joint.getDataset();
        UnitOfWork uow = new EasyUnitOfWork(getUser());
        try {
            uow.attach(dataset);
        }
        catch (RepositoryException e) {
            throw new FatalTaskException(e, this);
        }

        if (AccessCategory.NO_ACCESS.equals(dataset.getAccessCategory())) {
            processNoAccess(uow, dataset);
        }

        if (affected) {
            try {
                setTaskStamp(joint);
                uow.commit();
            }
            catch (RepositoryException e) {
                throw new TaskException(e, this);
            }
            catch (UnitOfWorkInterruptException e) {
                throw new TaskException(e, this);
            }
        }

    }

    private void processNoAccess(UnitOfWork uow, Dataset dataset) throws FatalTaskException {
        try {
            Map<String, String> fileMap = Data.getFileStoreAccess().getAllFiles(dataset.getDmoStoreId());
            for (String fileItemId : fileMap.keySet()) {
                FileItem fileItem = (FileItem) Data.getEasyStore().retrieve(new DmoStoreId(fileItemId));
                if (VisibleTo.ANONYMOUS.equals(fileItem.getVisibleTo()) && AccessibleTo.NONE.equals(fileItem.getAccessibleTo())) {
                    affected = true;
                    uow.attach(fileItem);
                    fileItem.setAccessibleTo(AccessibleTo.KNOWN);
                    RL.info(new Event(getTaskName(), "NONE to KNOWN", dataset.getStoreId(), fileItemId));
                }
            }
        }
        catch (StoreAccessException e) {
            throw new TaskException(e, this);
        }
        catch (RepositoryException e) {
            throw new TaskException(e, this);
        }

    }

    private EasyUser getUser() throws FatalTaskException {
        if (migration == null) {
            try {
                migration = Data.getUserRepo().findById("migration");
            }
            catch (ObjectNotInStoreException e) {
                throw new FatalTaskException(e, this);
            }
            catch (RepositoryException e) {
                throw new FatalTaskException(e, this);
            }
        }
        return migration;
    }

}
