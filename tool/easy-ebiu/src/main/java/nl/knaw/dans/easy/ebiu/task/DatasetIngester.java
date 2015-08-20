package nl.knaw.dans.easy.ebiu.task;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.exceptions.DataIntegrityException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.worker.DefaultWorkListener;
import nl.knaw.dans.easy.ebiu.AbstractTask;
import nl.knaw.dans.easy.ebiu.JointMap;
import nl.knaw.dans.easy.ebiu.exceptions.FatalTaskException;
import nl.knaw.dans.easy.ebiu.exceptions.TaskCycleException;
import nl.knaw.dans.easy.servicelayer.services.Services;

/**
 * Ingest datasets in state {@link DatasetState#DRAFT}. Original state is saved (see {@link JointMap#getOriginalState()}) to be handled later.
 */
public class DatasetIngester extends AbstractTask {

    @Override
    public void run(JointMap joint) throws FatalTaskException {
        if (!joint.isFitForDraft()) {
            String msg = "Dataset not fit for draft-ingestion due to previous errors.";
            RL.error(new Event(getTaskName(), "Not ingesting", msg));
            throw new TaskCycleException(msg, this);
        }

        try {
            Dataset dataset = Services.getDatasetService().newDataset(joint.getEasyMetadata(), joint.getAdministrativeMetadata());
            EasyUser easyUser = joint.getEasyUser();

            DatasetState datasetState = dataset.getAdministrativeState();
            joint.setOriginalState(datasetState);
            dataset.getAdministrativeMetadata().setAdministrativeState(DatasetState.DRAFT);

            IngestListener listener = new IngestListener(joint);
            Services.getDatasetService().saveEasyMetadata(easyUser, dataset, listener);

            joint.setDataset(dataset);

            DirectoryMarker.markAsIngested(joint);
        }
        catch (DataIntegrityException e) {
            RL.error(new Event(getTaskName(), e, "No valid dataset", e.printErrorMessages()));
            throw new TaskCycleException("No valid dataset", e, this);
        }
        catch (ServiceException e) {
            throw new FatalTaskException(e, this);
        }

    }

    private static class IngestListener extends DefaultWorkListener {

        private final JointMap joint;

        public IngestListener(JointMap joint) {
            this.joint = joint;
        }

        @Override
        public void afterIngest(DataModelObject dmo) {
            RL.info(new Event("Ingested dataset as draft", joint.getCurrentDirectory().getPath(), dmo.getStoreId()));
        }

        @Override
        public void onException(Throwable t) {
            RL.error(new Event("While ingesting dataset", t, joint.getCurrentDirectory().getPath()));
        }

    }

}
