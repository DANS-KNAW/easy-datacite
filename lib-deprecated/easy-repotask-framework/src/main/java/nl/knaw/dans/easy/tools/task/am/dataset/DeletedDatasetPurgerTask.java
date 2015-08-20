package nl.knaw.dans.easy.tools.task.am.dataset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.dataset.DatasetPurger;
import nl.knaw.dans.easy.tools.dataset.DatasetPurgerListener;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.NoListenerException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskExecutionException;
import nl.knaw.dans.easy.tools.util.Reporter;

public class DeletedDatasetPurgerTask extends AbstractDatasetTask implements DatasetPurgerListener {

    private DatasetPurger purger;
    private List<String> deletables = new ArrayList<String>();

    protected DatasetPurger getPurger() throws FatalTaskException {
        if (purger == null) {
            try {
                purger = new DatasetPurger("deleted by migration", this);
                purger.setIgnoreDobState(true);
            }
            catch (NoListenerException e) {
                throw new FatalTaskException(e, this);
            }
        }
        return purger;
    }

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        abbortIfNotMigration(joint);
        Dataset dataset = joint.getDataset();
        if (DatasetState.DELETED.equals(dataset.getAdministrativeState())) {
            deletables.add(dataset.getStoreId());
            joint.setCycleAbborted(true);
        }
    }

    @Override
    public void close() throws FatalTaskException {
        writeDeletables();
        for (String storeId : deletables) {
            try {
                getPurger().purgeDataset(storeId);
            }
            catch (TaskExecutionException e) {
                throw new TaskException(e, this);
            }
            catch (RepositoryException e) {
                throw new FatalTaskException(e, this);
            }
        }
    }

    private void writeDeletables() throws FatalTaskException {
        for (String storeId : deletables) {
            try {
                Reporter.appendReport("sids-to-purge.txt", storeId);
            }
            catch (IOException e) {
                throw new FatalTaskException(e, this);
            }
        }
        Reporter.closeAllFiles();
    }

    @Override
    public void onSubordinatePurged(DataModelObject dmo) {
        //
    }

    @Override
    public void onDatasetPurged(Dataset dataset) {
        RL.info(new Event(getTaskName(), "Deleted", dataset.getStoreId()));
    }

    @Override
    public void onDatasetNotFound(String storeId) {
        RL.info(new Event(getTaskName(), "Not found", storeId));
    }

    @Override
    public void onIdMapRemoval(String storeId) {
        RL.info(new Event(getTaskName(), "Removed IdMap", storeId));
    }

}
