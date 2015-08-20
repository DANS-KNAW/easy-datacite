package nl.knaw.dans.easy.ebiu.task;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.ebiu.AbstractTask;
import nl.knaw.dans.easy.ebiu.JointMap;
import nl.knaw.dans.easy.ebiu.exceptions.FatalTaskException;
import nl.knaw.dans.easy.ebiu.exceptions.TaskCycleException;
import nl.knaw.dans.easy.ebiu.exceptions.TaskException;

public class DatasetRetriever extends AbstractTask {

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        String storeId = joint.getCurrentDirectory().getName();
        if (!storeId.startsWith(Dataset.NAMESPACE.getValue())) {
            storeId = Dataset.NAMESPACE.getValue() + ":" + storeId;
        }

        try {
            Dataset dataset = (Dataset) Data.getEasyStore().retrieve(new DmoStoreId(storeId));
            joint.setDataset(dataset);
        }
        catch (ObjectNotInStoreException e) {
            RL.error(new Event("Dataset not found: " + storeId));
            throw new TaskCycleException(e, this);
        }
        catch (RepositoryException e) {
            throw new FatalTaskException(e, this);
        }

    }

}
