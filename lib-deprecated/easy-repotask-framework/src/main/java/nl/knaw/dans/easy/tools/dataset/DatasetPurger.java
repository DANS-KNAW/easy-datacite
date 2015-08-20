package nl.knaw.dans.easy.tools.dataset;

import java.util.List;

import nl.knaw.dans.common.fedora.fox.DobState;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.migration.MigrationRepo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.tools.exceptions.NoListenerException;
import nl.knaw.dans.easy.tools.exceptions.TaskExecutionException;
import nl.knaw.dans.easy.tools.util.Dialogue;
import nl.knaw.dans.easy.tools.util.RepoUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Purge datasets and all of its subordinates.
 * <p>
 * <b>Only use this class if you know what you are doing!</b>
 * </p>
 */
public class DatasetPurger {

    private static final Logger logger = LoggerFactory.getLogger(DatasetPurger.class);

    private final String logMessage;
    private final DatasetPurgerListener listener;

    private boolean ignoreDobState;

    public DatasetPurger(String logMessage) throws NoListenerException {
        this(logMessage, new DefaultDatasetPurgerListener());
    }

    public DatasetPurger(String logMessage, DatasetPurgerListener listener) throws NoListenerException {
        this.logMessage = logMessage;
        this.listener = listener;
        RepoUtil.checkListenersActive();
    }

    public boolean isIgnoreDobState() {
        return ignoreDobState;
    }

    public void setIgnoreDobState(boolean ignoreDobState) {
        this.ignoreDobState = ignoreDobState;
    }

    public void purgeDataset(String storeId) throws TaskExecutionException, RepositoryException {
        purgeDataset(storeId, false);
    }

    public void purgeDataset(String storeId, boolean force) throws TaskExecutionException, RepositoryException {
        try {
            Dataset dataset = (Dataset) Data.getEasyStore().retrieve(new DmoStoreId(storeId));
            purgeDataset(dataset, force);
        }
        catch (ObjectNotInStoreException e) {
            logger.info("A dataset with storeId " + storeId + " was not found");
            listener.onDatasetNotFound(storeId);
        }
    }

    public void purgeDataset(Dataset dataset) throws TaskExecutionException, RepositoryException {
        purgeDataset(dataset, false);
    }

    public void purgeDataset(Dataset dataset, boolean force) throws TaskExecutionException, RepositoryException {
        String storeId = dataset.getStoreId();

        DatasetState state = dataset.getAdministrativeState();
        if (!DatasetState.DELETED.equals(state) && !force) {
            throw new TaskExecutionException("Dataset " + storeId + " cannot be purged. It's administrative state is " + state);
        }
        if (!DobState.Deleted.equals(DobState.valueFor(dataset.getState())) && !ignoreDobState) {
            String msg = "Dataset " + storeId + " cannot be purged. It's Fedora state is " + dataset.getState();
            boolean confirmed = Dialogue.confirm(msg + "\nDo you want to purge it anyway?");
            if (!confirmed)
                throw new TaskExecutionException();
        }

        purgeSubordinates(storeId);

        purgeTheDataset(dataset);

        removeIdMap(storeId);

    }

    private void purgeSubordinates(String storeId) throws RepositoryException {
        List<DmoStoreId> subordinateIds = Data.getEasyStore().findSubordinates(new DmoStoreId(storeId));

        for (DmoStoreId subordinateId : subordinateIds) {
            DataModelObject dmo = Data.getEasyStore().retrieve(subordinateId);
            Data.getEasyStore().purge(dmo, true, logMessage);
            listener.onSubordinatePurged(dmo);
            logger.info(logMessage + " " + dmo);
        }
    }

    private void purgeTheDataset(Dataset dataset) throws RepositoryException {
        Data.getEasyStore().purge(dataset, true, logMessage);
        listener.onDatasetPurged(dataset);
        logger.info(logMessage + " " + dataset);
        logger.info("Purged dataset " + dataset.getStoreId() + " and all of its subordinates.");
    }

    private void removeIdMap(String storeId) throws RepositoryException {
        MigrationRepo migrationRepo = Data.getMigrationRepo();
        if (migrationRepo.exists(storeId)) {
            migrationRepo.delete(storeId);
            listener.onIdMapRemoval(storeId);
            logger.info("Removed the IdMap for " + storeId);
        }
    }

}
