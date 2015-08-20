package nl.knaw.dans.easy.tools.task.am.dataset;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.exceptions.DataIntegrityException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;

/**
 * <p>
 * This Task publishes a dataset, if it can be published.
 * </p>
 */
public class PublishDatasetTask extends AbstractTask {
    private String archivistId;
    private EasyUser archivist;
    private int publishCounter;

    private Dataset dataset;

    private boolean publishDatasetInMaintenance = false;

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        dataset = joint.getDataset();

        if (canDatasetBePublished()) {
            publishDataset(dataset);
        } else {
            RL.warn(new Event(getTaskName(), String.format("Skipping dataset[%s] because it can't be published.", dataset.getDmoStoreId().getStoreId())));
        }
    }

    private boolean canDatasetBePublished() {
        return hasRequiredAdministrativeState() && hasRequiredAdministration();
    }

    private boolean hasRequiredAdministrativeState() {
        DatasetState state = dataset.getAdministrativeState();
        RL.info(new Event(getTaskName(), String.format("Dataset has State[%s]", state.toString())));
        switch (state) {
        case MAINTENANCE:
            return getPublishDatasetInMaintenance();
        case SUBMITTED:
            return true;
        default:
            return false;
        }
    }

    private boolean hasRequiredAdministration() {
        boolean completed = dataset.getAdministrativeMetadata().getWorkflowData().getWorkflow().areRequiredStepsCompleted();
        RL.info(new Event(getTaskName(), String.format("Required Administration Workflows completed? [%b]", completed)));
        return completed;
    }

    private void publishDataset(Dataset dataset) throws FatalTaskException {
        try {
            Services.getDatasetService().publishDataset(getArchivist(), dataset, false, false);
            publishCounter++;
            RL.info(new Event(getTaskName(), String.format("%d. Published dataset[%s]", publishCounter, dataset.getDmoStoreId().getStoreId())));
        }
        catch (DataIntegrityException e) {
            RL.error(new Event(getTaskName(), "Cannot publish the dataset", e.getMessage()));
            throw new TaskCycleException("Cannot publish the dataset" + e.getMessage(), this);
        }
        catch (ServiceException e) {
            RL.error(new Event(getTaskName(), "An error occured when trying to publish dataset ", dataset.getDmoStoreId().getStoreId()));
            throw new TaskCycleException("An error occured when trying to publish dataset " + dataset.getDmoStoreId().getStoreId(), this);
        }
    }

    public String getAchivistId() {
        return archivistId;
    }

    public void setArchivistId(String archivistId) {
        this.archivistId = archivistId;
    }

    private EasyUser getArchivist() throws FatalTaskException {
        if (archivist == null) {
            try {
                archivist = Data.getUserRepo().findById(getAchivistId());
            }
            catch (ObjectNotInStoreException e) {
                throw new FatalTaskException(e, this);
            }
            catch (RepositoryException e) {
                throw new FatalTaskException(e, this);
            }
        }
        return archivist;
    }

    public void setPublishDatasetInMaintenance(boolean publishDatasetInMaintenance) {
        this.publishDatasetInMaintenance = publishDatasetInMaintenance;
    }

    public boolean getPublishDatasetInMaintenance() {
        return publishDatasetInMaintenance;
    }

    /**
     * When we are finished looping through all datasets, log total number of published datasets.
     */
    @Override
    public void close() {
        RL.info(new Event(getTaskName(), "\n\n\tPublished total of: " + publishCounter + " datasets.\n"));
    }
}
