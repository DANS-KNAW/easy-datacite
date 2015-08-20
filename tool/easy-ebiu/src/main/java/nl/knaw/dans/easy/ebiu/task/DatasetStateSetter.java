package nl.knaw.dans.easy.ebiu.task;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.MetadataUnit;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.business.dataset.DatasetSubmissionImpl;
import nl.knaw.dans.easy.business.dataset.MetadataPidGenerator;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.dataset.DatasetSubmission;
import nl.knaw.dans.easy.domain.exceptions.DataIntegrityException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.worker.DefaultWorkListener;
import nl.knaw.dans.easy.ebiu.AbstractTask;
import nl.knaw.dans.easy.ebiu.JointMap;
import nl.knaw.dans.easy.ebiu.exceptions.FatalTaskException;
import nl.knaw.dans.easy.ebiu.exceptions.TaskCycleException;
import nl.knaw.dans.easy.ebiu.exceptions.TaskException;
import nl.knaw.dans.easy.servicelayer.services.Services;

/**
 * Sets the final state of the dataset. Takes care of persistent identifier and license if appropriate.
 */
public class DatasetStateSetter extends AbstractTask {

    public static final String PID_MAP = "pid-map";

    private JointMap joint;

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        this.joint = joint;

        DatasetState originalState = joint.getOriginalState();

        if (DatasetState.DRAFT.equals(originalState)) {
            String msg = "DatasetState is DRAFT already.";
            RL.info(new Event("Not altering state.", msg));
            joint.setCycleProcessingCompleted(isFinalStepInCycle());
            return;
        } else if (DatasetState.SUBMITTED.equals(originalState)) {
            boolean submitted = handleStateSubmitted();
            joint.setCycleProcessingCompleted(isFinalStepInCycle() && submitted);
        } else if (DatasetState.PUBLISHED.equals(originalState)) {
            boolean published = handleStatePublished();
            joint.setCycleProcessingCompleted(isFinalStepInCycle() && published);
        } else if (DatasetState.MAINTENANCE.equals(originalState)) {
            boolean inMaintenance = handleStateMaintenance();
            joint.setCycleProcessingCompleted(isFinalStepInCycle() && inMaintenance);
        } else if (DatasetState.DELETED.equals(originalState)) {
            boolean deleted = handleStateDeleted();
            joint.setCycleProcessingCompleted(isFinalStepInCycle() && deleted);
        } else {
            String msg = "No DatasetState found.";
            RL.warn(new Event("Not altering state.", msg));
        }
    }

    private boolean handleStateSubmitted() throws FatalTaskException {
        boolean submitted = false;
        if (!joint.isFitForSubmit()) {
            RL.warn(new Event("Not submitting dataset due to previous errors."));
            throw new TaskCycleException("Dataset not fit for submission", this);
        }

        Dataset dataset = joint.getDataset();

        DatasetSubmission datasetSubmission = new DatasetSubmissionImpl(null, dataset, getDepositor());
        // null in previous parameters: we do not need the FormDefinition as we do not need to provide
        // feedback for the web interface.
        try {
            Services.getDatasetService().submitDataset(datasetSubmission, new UpdateListener());
            submitted = datasetSubmission.isCompleted();
        }
        catch (DataIntegrityException e) {
            RL.error(new Event("No valid dataset", e, e.printErrorMessages()));
            throw new TaskCycleException("No valid dataset", e, this);
        }
        catch (CommonSecurityException e) {
            RL.error(new Event("Not fit for submission or submission not allowed", e));
            throw new TaskCycleException("Not fit for submission or submission not allowed", e, this);
        }
        catch (ServiceException e) {
            throw new FatalTaskException(e, this);
        }

        for (String infoMessage : datasetSubmission.getGlobalInfoMessages()) {
            RL.info(new Event("submission info", infoMessage));
        }

        for (String errorMessage : datasetSubmission.getGlobalErrorMessages()) {
            RL.error(new Event("submission error", errorMessage));
        }

        RL.info(new Event("submission state", "completed=" + datasetSubmission.isCompleted(), "mailSend=" + datasetSubmission.isMailSend()));

        // catch all clause in MetadataPidGenerator prevents normal error handling for all callers
        if (datasetSubmission.getGlobalErrorMessages().contains(MetadataPidGenerator.PID_ERROR)) {
            throw new FatalTaskException("Exception in MetadataPidGenerator", this);
        }

        String persistentIdentifier = dataset.getPersistentIdentifier();
        RL.info(new Event(PID_MAP, persistentIdentifier));

        return submitted;
    }

    private boolean handleStatePublished() throws FatalTaskException {
        boolean published = false;
        Dataset dataset = joint.getDataset();
        String persistentIdentifier = dataset.getPersistentIdentifier();
        boolean passedSubmit = persistentIdentifier != null;
        if (!passedSubmit) {
            passedSubmit = handleStateSubmitted();
        }

        if (!passedSubmit || !joint.isFitForPublication()) {
            RL.warn(new Event("Not publishing dataset due to previous errors."));
            throw new TaskCycleException("Dataset not fit for publication", this);
        }

        // completeness of required workflowSteps is checked by publishDatasetRule in CodedAuthz.
        // we'll repeat check for reporting
        if (!dataset.getAdministrativeMetadata().getWorkflowData().getWorkflow().areRequiredStepsCompleted()) {
            RL.error(new Event("Not all required workflowsteps are completed"));
            throw new TaskCycleException("Dataset not fit for publication", this);
        }

        try {
            Services.getDatasetService().publishDataset(joint.getEasyUser(), dataset, false, false);
            RL.info(new Event("published dataset"));
            published = true;
        }
        catch (DataIntegrityException e) {
            RL.error(new Event("No valid dataset", e, e.printErrorMessages()));
            throw new TaskCycleException("No valid dataset", e, this);
        }
        catch (CommonSecurityException e) {
            RL.error(new Event("Not fit for publication or publication not allowed", e));
            throw new TaskCycleException("Not fit for publication or publication not allowed", e, this);
        }
        catch (ServiceException e) {
            throw new FatalTaskException(e, this);
        }
        return published;
    }

    private boolean handleStateMaintenance() throws FatalTaskException {
        boolean inMaintenance = false;
        try {
            Services.getDatasetService().maintainDataset(joint.getEasyUser(), joint.getDataset(), false);
            inMaintenance = true;
        }
        catch (DataIntegrityException e) {
            RL.error(new Event("No valid dataset", e, e.printErrorMessages()));
            throw new TaskCycleException("No valid dataset", e, this);
        }
        catch (CommonSecurityException e) {
            RL.error(new Event("Not fit for maintenance or maintenance not allowed", e));
            throw new TaskCycleException("Not fit for maintenance or maintenance not allowed", e, this);
        }
        catch (ServiceException e) {
            throw new FatalTaskException(e, this);
        }
        return inMaintenance;
    }

    private boolean handleStateDeleted() throws FatalTaskException {
        boolean deleted = false;
        try {
            Services.getDatasetService().deleteDataset(joint.getEasyUser(), joint.getDataset());
            deleted = true;
        }
        catch (CommonSecurityException e) {
            RL.error(new Event("Not fit for deleting or deleting not allowed", e));
            throw new TaskCycleException("Not fit for deleting or deleting not allowed", e, this);
        }
        catch (ServiceException e) {
            throw new FatalTaskException(e, this);
        }
        return deleted;
    }

    private EasyUser getDepositor() throws FatalTaskException {
        String depositorId = joint.getDataset().getAdministrativeMetadata().getDepositorId();
        if (depositorId == null) {
            RL.error(new Event("No depositorId found"));
            throw new TaskCycleException("No depositorId found.", this);
        }

        EasyUser depositor;
        try {
            depositor = Data.getUserRepo().findById(depositorId);
        }
        catch (ObjectNotInStoreException e) {
            RL.error(new Event("Unknown depositor", depositorId));
            throw new TaskCycleException("Unknown depositor: " + depositorId, this);
        }
        catch (RepositoryException e) {
            throw new FatalTaskException(e, this);
        }

        return depositor;
    }

    private static class UpdateListener extends DefaultWorkListener {

        @Override
        public void afterUpdate(DataModelObject dmo) {
            RL.info(new Event("Updated dataset"));
        }

        @Override
        public void afterUpdateMetadataUnit(DataModelObject dmo, MetadataUnit mdUnit) {
            RL.info(new Event("Updated metadata-unit:" + mdUnit.getUnitId()));
        }

        @Override
        public void onException(Throwable t) {
            RL.error(new Event("While updating dataset", t));
        }
    }

}
