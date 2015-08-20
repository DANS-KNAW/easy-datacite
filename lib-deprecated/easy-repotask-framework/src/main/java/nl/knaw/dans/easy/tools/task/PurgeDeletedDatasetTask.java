package nl.knaw.dans.easy.tools.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.fedora.fox.DobState;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.migration.MigrationRepo;
import nl.knaw.dans.easy.domain.migration.IdMap;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.dataset.DatasetPurger;
import nl.knaw.dans.easy.tools.dataset.DatasetPurgerListener;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.NoListenerException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PurgeDeletedDatasetTask extends AbstractTask {
    private static final Logger logger = LoggerFactory.getLogger(PurgeDeletedDatasetTask.class);
    private final Map<String, List<String>> aipIdToStoreIdsMap = new HashMap<String, List<String>>();
    private boolean test = true;

    final DatasetPurgerListener purgeListener = createPurgeListener();

    @Override
    public void run(final JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        final String storeId = getDatasetStoredId(joint);
        final String aipId = getDatasetAipId(storeId);
        if (!"".equals(aipId)) {
            List<String> storeIds = aipIdToStoreIdsMap.get(aipId);
            if (storeIds == null) {
                storeIds = new ArrayList<String>();
            }
            storeIds.add(storeId);
            aipIdToStoreIdsMap.put(aipId, storeIds);
        }
    }

    private String getDatasetStoredId(final JointMap joint) {
        final Dataset dataset = joint.getDataset();
        return dataset.getStoreId();
    }

    private String getDatasetAipId(final String storeId) throws TaskCycleException {
        try {
            final MigrationRepo migrationRepo = Data.getMigrationRepo();
            final IdMap idMap = migrationRepo.findById(storeId);
            final String aipId = idMap.getAipId();
            if (aipId == null)
                return "";
            return aipId.trim();
        }
        catch (final RepositoryException e) {
            throw new TaskCycleException(String.format("Cannot get AIP-ID for dataset with store ID %s", storeId), e, this);
        }
    }

    @Override
    public void close() {
        for (final String aipId : aipIdToStoreIdsMap.keySet()) {
            final List<String> storeIds = aipIdToStoreIdsMap.get(aipId);
            logInfo("aip ID=" + aipId + " " + Arrays.toString(storeIds.toArray()));
            if (storeIds.size() > 1) {
                // only if an AipId has multiple StoreIDs.
                for (final String storeId : storeIds)
                    purgeIfDeleted(aipId, storeId);
            }
        }
    }

    private void purgeIfDeleted(final String aipId, final String storeId) {
        try {
            final Dataset dataset = (Dataset) Data.getEasyStore().retrieve(new DmoStoreId(storeId));
            final DatasetState administrativeState = dataset.getAdministrativeState();
            final String state = dataset.getState();
            logInfo("aip ID=" + aipId + " storeID=" + storeId + " " + administrativeState + " " + state);
            if (!administrativeState.equals(DatasetState.DELETED))
                logInfo("keeping storeID " + storeId + " states: " + administrativeState + " " + state);
            else if (!state.equals(DobState.Deleted.toString()))
                logWarn("inconsistent state for sotreID " + storeId + " states: " + administrativeState + " " + state);
            else if (test) {
                logInfo("Purging dataset (test-mode, not actually performing purge)" + dataset);
            } else {
                logInfo("Purging dataset " + dataset);
                createPurger(storeId).purgeDataset(dataset);
            }
        }
        catch (final ObjectNotInStoreException e) {
            logError(e);
        }
        catch (final RepositoryException e) {
            logError(e);
        }
        catch (final TaskExecutionException e) {
            logError(e);
        }
        catch (final NoListenerException e) {
            logError(e);
        }
    }

    private void logError(final Exception e) {
        logger.error("Exception: ", e);
        RL.error(new Event("Exception", e));
    }

    private void logInfo(String message) {
        logger.info(message);
        RL.info(new Event(message));
    }

    private void logWarn(String message) {
        logger.warn(message);
        RL.warn(new Event(message));
    }

    private DatasetPurger createPurger(final String storeId) throws NoListenerException {
        return new DatasetPurger("Purge the dataset where storeId is " + storeId, purgeListener);
    }

    private DatasetPurgerListener createPurgeListener() {
        return new DatasetPurgerListener() {

            @Override
            public void onSubordinatePurged(final DataModelObject dmo) {}

            @Override
            public void onDatasetPurged(final Dataset dataset) {
                logInfo("purged storeID: " + dataset.getStoreId());
            }

            @Override
            public void onDatasetNotFound(final String storeId) {}

            @Override
            public void onIdMapRemoval(final String storeId) {}
        };
    }

    public void setTest(boolean test) {
        this.test = test;
    }
}
