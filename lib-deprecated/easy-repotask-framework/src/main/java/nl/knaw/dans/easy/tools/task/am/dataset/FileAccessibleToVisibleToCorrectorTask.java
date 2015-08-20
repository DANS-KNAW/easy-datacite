package nl.knaw.dans.easy.tools.task.am.dataset;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import nl.knaw.dans.common.lang.RepositoryException;
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
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;
import nl.knaw.dans.easy.tools.util.Reporter;

public class FileAccessibleToVisibleToCorrectorTask extends AbstractTask {
    private EasyUser migration;
    private CorrectionsMap<VisibleTo> visibleToCorrections;
    private CorrectionsMap<AccessibleTo> accessibleToCorrections;

    private boolean affected = false;

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        Dataset dataset = joint.getDataset();
        affected = false;

        String aipId = getAipId(dataset);
        if (aipId == null && !visibleToCorrections.contains(aipId) && !accessibleToCorrections.contains(aipId)) {
            return;
        }

        UnitOfWork uow = new EasyUnitOfWork(getUser());
        try {
            uow.attach(dataset);
        }
        catch (RepositoryException e) {
            throw new FatalTaskException(e, this);
        }

        correctFiles(uow, dataset, aipId);

        if (affected) {
            try {
                uow.commit();
            }
            catch (RepositoryException e) {
                throw new TaskException(e, this);
            }
            catch (UnitOfWorkInterruptException e) {
                throw new TaskException(e, this);
            }
            try {
                Reporter.appendReport("completed.txt", aipId);
            }
            catch (IOException e) {
                // oops
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() throws TaskException, TaskCycleException, FatalTaskException {
        Reporter.closeAllFiles();
    }

    private String getAipId(Dataset dataset) {
        return dataset.getEasyMetadata().getEmdIdentifier().getAipId();
    }

    private void correctFiles(UnitOfWork uow, Dataset dataset, String aipId) throws FatalTaskException {
        try {
            Map<String, String> fileMap = Data.getFileStoreAccess().getAllFiles(dataset.getDmoStoreId());
            for (String fileItemId : fileMap.keySet()) {
                FileItem fileItem = (FileItem) Data.getEasyStore().retrieve(new DmoStoreId(fileItemId));
                String path = fileItem.getFileItemMetadata().getPath();

                if (accessibleToCorrections.containsPath(aipId, path)) {
                    uow.attach(fileItem);
                    fileItem.setAccessibleTo((AccessibleTo) accessibleToCorrections.getCorrection(aipId, path));
                    affected = true;
                }

                if (visibleToCorrections.containsPath(aipId, path)) {
                    uow.attach(fileItem);
                    fileItem.setVisibleTo((VisibleTo) visibleToCorrections.getCorrection(aipId, path));
                    affected = true;
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

    protected void abbortIfNotMigration(JointMap joint) throws TaskCycleException {
        Dataset dataset = joint.getDataset();
        String aipId = dataset.getEasyMetadata().getEmdIdentifier().getAipId();
        String storeId = dataset.getStoreId();
        if (aipId == null) {
            RL.warn(new Event(getTaskName(), "Not a migration dataset", storeId, "datasetState=" + dataset.getAdministrativeState(), "depositor="
                    + dataset.getAdministrativeMetadata().getDepositorId()));
            throw new TaskCycleException("Not a migration dataset " + storeId, this);
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

    public void setVisibilityCorrectionsFile(File corrections) throws FatalTaskException {
        visibleToCorrections = new CorrectionsMap<VisibleTo>(corrections, VisibleTo.NONE);
    }

    public void setAccessibilityCorrectionsFile(File corrections) throws FatalTaskException {
        accessibleToCorrections = new CorrectionsMap<AccessibleTo>(corrections, AccessibleTo.NONE);
    }

}
