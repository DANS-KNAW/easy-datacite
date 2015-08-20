package nl.knaw.dans.easy.tools.task.am.dataset;

import java.util.Arrays;
import java.util.Map;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.UnitOfWork;
import nl.knaw.dans.common.lang.repo.exception.UnitOfWorkInterruptException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.EasyUnitOfWork;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;

public class ChangeAccessRightsTask extends AbstractTask {
    private VisibleTo[] targetFileVisibility;
    private AccessibleTo[] targetFileAccessibility;

    private boolean listOnlyMode;

    private AccessCategory newDatasetAccessCategory;

    private VisibleTo newFileVisibility;
    private AccessibleTo newFileAccessibility;

    private int datasetCounter = 0;
    private int fileCounter = 0;

    private boolean datasetAffected;
    private boolean filesAffected;

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        if (isListOnlyMode()) {
            printDatasets(joint);
        } else {
            process(joint);
        }
    }

    private void process(JointMap joint) throws FatalTaskException, TaskException {
        datasetCounter++;
        datasetAffected = false;
        filesAffected = false;
        Dataset dataset = joint.getDataset();
        UnitOfWork uow = new EasyUnitOfWork(dataset.getDepositor());
        try {
            uow.attach(dataset);
        }
        catch (RepositoryException e) {
            throw new FatalTaskException(e, this);
        }

        processDataset(uow, dataset);
        processFiles(uow, dataset);

        if (datasetAffected || filesAffected) {
            try {
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

    private void processDataset(UnitOfWork uow, Dataset dataset) throws TaskException {
        RL.info(new Event(getTaskName(), String.format("Processing dataset %s", dataset.getStoreId())));
        String oldAccess = dataset.getAccessCategory().toString();
        if (!newDatasetAccessCategory.equals(dataset.getAccessCategory())) {
            datasetAffected = true;
            dataset.getEasyMetadata().getEmdRights().setAccessCategory(newDatasetAccessCategory, "common.dcterms.accessrights");

            RL.info(new Event(getTaskName(), "Changed Access from: " + oldAccess + " to " + newDatasetAccessCategory.toString(), dataset.getStoreId()));
        }
    }

    private void processFiles(UnitOfWork uow, Dataset dataset) throws TaskException {
        try {
            Map<String, String> fileMap = Data.getFileStoreAccess().getAllFiles(dataset.getDmoStoreId());
            for (String fileItemId : fileMap.keySet()) {
                FileItem fileItem = (FileItem) Data.getEasyStore().retrieve(new DmoStoreId(fileItemId));
                VisibleTo fileVisibility = fileItem.getVisibleTo();
                AccessibleTo fileAccessibility = fileItem.getAccessibleTo();

                if (Arrays.asList(getTargetFileVisibility()).contains(fileVisibility)
                        || Arrays.asList(getTargetFileAccessibility()).contains(fileAccessibility))
                {
                    filesAffected = true;
                    fileCounter++;
                    uow.attach(fileItem);
                    if (Arrays.asList(getTargetFileVisibility()).contains(fileVisibility)) {
                        fileItem.setVisibleTo(getNewFileVisibility());
                        RL.info(new Event(getTaskName(), fileVisibility + " to " + getNewFileVisibility(), dataset.getStoreId(), fileItemId));
                    }
                    if (Arrays.asList(getTargetFileAccessibility()).contains(fileAccessibility)) {
                        fileItem.setAccessibleTo(getNewFileAccessibility());
                        RL.info(new Event(getTaskName(), fileAccessibility + " to " + getNewFileAccessibility(), dataset.getStoreId(), fileItemId));
                    }
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

    private void printDatasets(JointMap joint) throws TaskException {
        datasetCounter++;
        Dataset dataset = joint.getDataset();
        String pid = dataset.getPersistentIdentifier();
        String datasetId = dataset.getStoreId();
        String title = dataset.getPreferredTitle();
        String depositor = dataset.getDepositor().getDisplayName();
        String state = dataset.getAdministrativeState().toString();
        String copy = "-";
        if (dataset.getEasyMetadata().getEmdRights().getTermsRightsHolder().size() > 0) {
            copy = dataset.getEasyMetadata().getEmdRights().getTermsRightsHolder().get(0).toString();
        }
        String accessRights = dataset.getAccessCategory().toString();
        String files;
        try {
            files = "" + Data.getFileStoreAccess().getTotalMemberCount(dataset.getDmoStoreId(), FileItemVO.class);
            String folders = "" + Data.getFileStoreAccess().getTotalMemberCount(dataset.getDmoStoreId(), FolderItemVO.class);
            RL.info(new Event(getTaskName(), pid, datasetId, depositor, title, state, copy, accessRights, files, folders));
        }
        catch (StoreAccessException e) {
            throw new TaskException(e.getMessage(), e, this);
        }
    }

    public void setListOnlyMode(boolean listOnlyMode) {
        this.listOnlyMode = listOnlyMode;
    }

    public boolean isListOnlyMode() {
        return listOnlyMode;
    }

    public void setNewDatasetAccessCategory(AccessCategory accessCategory) {
        this.newDatasetAccessCategory = accessCategory;
    }

    public AccessCategory getNewDatasetAccessCategory() {
        return newDatasetAccessCategory;
    }

    public void setNewFileVisibility(VisibleTo visibility) {
        this.newFileVisibility = visibility;
    }

    public VisibleTo getNewFileVisibility() {
        return newFileVisibility;
    }

    public void setNewFileAccessibility(AccessibleTo access) {
        this.newFileAccessibility = access;
    }

    public AccessibleTo getNewFileAccessibility() {
        return newFileAccessibility;
    }

    public void setTargetFileVisibility(VisibleTo[] targetFilesVisibilty) {
        this.targetFileVisibility = targetFilesVisibilty;
    }

    public VisibleTo[] getTargetFileVisibility() {
        return targetFileVisibility;
    }

    public void setTargetFileAccessibility(AccessibleTo[] targetFileAccessibility) {
        this.targetFileAccessibility = targetFileAccessibility;
    }

    public AccessibleTo[] getTargetFileAccessibility() {
        return targetFileAccessibility;
    }

    /**
     * When we are finished looping through all datasets, log an overview.
     */
    @Override
    public void close() {
        RL.info(new Event(getTaskName(), "\n\nPID", "Dataset ID", "Title", "Depositor", "State", "(Copy) rights holder", "Access Rights", "# Files", "#Folders"));
        RL.info(new Event(getTaskName(), "\n\n\tFound: " + datasetCounter + " datasets.\n\tFiles affected: " + fileCounter + ".\n" + toString()));
    }
}
