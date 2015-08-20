package nl.knaw.dans.easy.tools.task;

import java.io.IOException;
import java.util.Set;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.util.Pluralizer;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.tools.AbstractSidSetTask;
import nl.knaw.dans.easy.tools.IdConverter;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.dataset.DatasetPurger;
import nl.knaw.dans.easy.tools.dataset.DatasetPurgerListener;
import nl.knaw.dans.easy.tools.exceptions.FatalException;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.NoListenerException;
import nl.knaw.dans.easy.tools.exceptions.TaskExecutionException;
import nl.knaw.dans.easy.tools.util.Dialogue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PurgeDatasetsTask extends AbstractSidSetTask implements DatasetPurgerListener {

    private static final Logger logger = LoggerFactory.getLogger(PurgeDatasetsTask.class);

    private String logMessage;

    private int datasetsPurgedCount;
    private int subordinatesPurgedCount;
    private int datasetsNotFoundCount;
    private int idMapsRemovedCount;

    private boolean ignoreDobState;

    public PurgeDatasetsTask(String... idFilenames) {
        super(idFilenames);
    }

    public PurgeDatasetsTask(IdConverter idConverter, String... idFilenames) {
        super(idConverter, idFilenames);
    }

    public boolean isIgnoreDobState() {
        return ignoreDobState;
    }

    public void setIgnoreDobState(boolean ignoreDobState) {
        this.ignoreDobState = ignoreDobState;
    }

    @Override
    public void run(JointMap taskMap) throws FatalTaskException {
        boolean confirmed = Dialogue.confirm("Purge all datasets listed in " + getIdFilenamesToString() + "?");
        if (!confirmed) {
            logger.info("Aborting " + getTaskName());
            return;
        }

        Set<String> sidSet;
        try {
            sidSet = loadSids();
        }
        catch (IOException e) {
            throw new FatalTaskException("Cannot close file.", e, this);
        }
        catch (FatalException e) {
            throw new FatalTaskException("Cannot convert.", e, this);
        }
        confirmed = Dialogue.confirm(Pluralizer.formatToBe("There", sidSet.size(), "dataset", "to be purged.") + "\nDo you want to continue?");
        if (!confirmed) {
            logger.info("Aborting " + getTaskName());
            return;
        }

        logger.info(Pluralizer.format("A total of", sidSet.size(), "dataset", "will be purged."));
        DatasetPurger purger;
        try {
            purger = new DatasetPurger(getLogMessage(), this);
            purger.setIgnoreDobState(ignoreDobState);
        }
        catch (NoListenerException e) {
            throw new FatalTaskException(e, this);
        }

        for (String storeId : sidSet) {
            try {
                purger.purgeDataset(storeId);
            }
            catch (TaskExecutionException e) {
                String msg = "Unable to purge dataset " + storeId;
                logger.error(msg, e);
                RL.error(new Event("Purge dataset", e, msg));
            }
            catch (RepositoryException e) {
                RL.error(new Event("Purge dataset", e));
                throw new FatalTaskException(e, this);
            }
        }

        logger.info("\n\tPurged " + datasetsPurgedCount + " datasets of " + sidSet.size() + " datasets in " + getIdFilenamesToString() + "."
                + "\n\tA total of " + subordinatesPurgedCount + " subordinate objects was purged." + "\n\tA total of " + datasetsNotFoundCount
                + " datasets were not found on the system" + "\n\tA total of " + idMapsRemovedCount + " idMaps was removed.\n");
    }

    public String getLogMessage() {
        if (logMessage == null) {
            logMessage = "Purged by " + this.getClass().getSimpleName();
        }
        return logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }

    @Override
    public void onSubordinatePurged(DataModelObject dmo) {
        subordinatesPurgedCount++;
        RL.info(new Event("subordinate purged", dmo.getStoreId()));
    }

    @Override
    public void onDatasetPurged(Dataset dataset) {
        datasetsPurgedCount++;
        RL.info(new Event("dataset purged", dataset.getStoreId()));
    }

    @Override
    public void onDatasetNotFound(String storeId) {
        datasetsNotFoundCount++;
        RL.info(new Event("dataset not found", storeId));
    }

    @Override
    public void onIdMapRemoval(String storeId) {
        idMapsRemovedCount++;
        RL.info(new Event("idMap removed", storeId));

    }

}
