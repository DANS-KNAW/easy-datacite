package nl.knaw.dans.easy.tools.task;

import java.io.IOException;
import java.util.Set;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.util.Pluralizer;
import nl.knaw.dans.easy.domain.exceptions.DataIntegrityException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.tools.AbstractSidSetTask;
import nl.knaw.dans.easy.tools.Application;
import nl.knaw.dans.easy.tools.IdConverter;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.dataset.DatasetStateChanger;
import nl.knaw.dans.easy.tools.dataset.DatasetStateChangerListener;
import nl.knaw.dans.easy.tools.exceptions.FatalException;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.NoListenerException;
import nl.knaw.dans.easy.tools.util.Dialogue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeDatasetStateTask extends AbstractSidSetTask implements DatasetStateChangerListener {
    private static final Logger logger = LoggerFactory.getLogger(ChangeDatasetStateTask.class);

    private final DatasetState newState;

    private int datasetsNotFoundCount;
    private int datasetStateChangeCount;

    public ChangeDatasetStateTask(DatasetState newState, String... idFilenames) {
        this(newState, null, idFilenames);
    }

    public ChangeDatasetStateTask(DatasetState newState, IdConverter idConverter, String... idFilenames) {
        super(idConverter, idFilenames);
        this.newState = newState;
    }

    @Override
    public boolean needsAuthentication() {
        return true;
    }

    @Override
    public void run(JointMap taskMap) throws FatalTaskException {
        boolean confirmed = Dialogue.confirm("Change state to " + newState + " of all datasets listed in " + getIdFilenamesToString() + "?");
        if (!confirmed) {
            logger.info("Aborting " + getTaskName());
            return;
        }

        EasyUser user = Application.authenticate();

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
        confirmed = Dialogue.confirm(Pluralizer.formatToBe("There", sidSet.size(), "dataset", "that will be updated to dataset state " + newState + ".")
                + "\nDo you want to continue?");
        if (!confirmed) {
            logger.info("Aborting " + getTaskName());
            return;
        }

        logger.info(Pluralizer.format("A total of", sidSet.size(), "dataset", "will have the dataset state changed to " + newState));
        DatasetStateChanger datasetStateChanger;
        try {
            datasetStateChanger = new DatasetStateChanger(user, this);
        }
        catch (NoListenerException e) {
            throw new FatalTaskException(e, this);
        }

        for (String storeId : sidSet) {
            try {
                datasetStateChanger.changeState(storeId, newState);
            }
            catch (RepositoryException e) {
                throw new FatalTaskException(e, this);
            }
            catch (ServiceException e) {
                throw new FatalTaskException(e, this);
            }
            catch (DataIntegrityException e) {
                throw new FatalTaskException(e, this);
            }
        }

        logger.info("\n\tChanged " + datasetStateChangeCount + " datasets of " + sidSet.size() + " datasets in " + getIdFilenamesToString() + "."
                + "\n\tA total of " + getOriginalIdNotFoundCount() + " originalIds was not found." + "\n\tA total of " + datasetsNotFoundCount
                + " datasets were not found on the system.");
    }

    @Override
    public void onDatasetNotFound(String storeId) {
        datasetsNotFoundCount++;
        RL.info(new Event("dataset not found", storeId));
    }

    @Override
    public void onDatasetStateChanged(Dataset dataset, DatasetState oldState, DatasetState newState, String oldDobState, String newDobState) {
        datasetStateChangeCount++;
        RL.info(new Event("datasetStateChanged", dataset.getStoreId(), oldState.toString(), newState.toString(), oldDobState, newDobState));
    }

}
