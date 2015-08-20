package nl.knaw.dans.easy.tools.task.am.permissions;

import java.util.Map;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.migration.IdMap;
import nl.knaw.dans.easy.domain.model.PermissionSequenceList;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskExecutionException;

public class PermissionRequestTask extends AbstractTask {

    private final String dmsaaLocation;
    private PermissionRequestReader reader;

    public PermissionRequestTask(String dmsaaLocation) {
        this.dmsaaLocation = dmsaaLocation;
    }

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        try {
            Map<String, PermissionSequenceList> sequenceList = getReader().getSequenceListMap();
            RL.info(new Event(RL.GLOBAL, "found " + sequenceList.size() + "permissionSequenceLists"));
            for (String aipId : sequenceList.keySet()) {
                PermissionSequenceList psl = sequenceList.get(aipId);
                setPermissionSequenceList(aipId, psl);
            }
        }
        catch (TaskExecutionException e) {
            throw new FatalTaskException(e, this);
        }

    }

    private void setPermissionSequenceList(String aipId, PermissionSequenceList psl) throws FatalTaskException {
        IdMap idMap = getMostRecentIdMap(aipId);
        if (idMap == null) {
            RL.error(new Event("idMap = null", aipId));
            return;
        }
        String storeId = idMap.getStoreId();
        try {
            DatasetImpl dataset = (DatasetImpl) Data.getEasyStore().retrieve(new DmoStoreId(storeId));
            dataset.setPermissionSequenceList(psl);
            Data.getEasyStore().update(dataset, getTaskName());
            RL.info(new Event("updated", aipId, storeId));
        }
        catch (ObjectNotInStoreException e) {
            RL.error(new Event("dataset not found", aipId, storeId));
        }
        catch (RepositoryException e) {
            throw new FatalTaskException(e, this);
        }

    }

    protected IdMap getMostRecentIdMap(String aipId) throws FatalTaskException {
        try {
            IdMap idMap = Data.getMigrationRepo().getMostRecentByAipId(aipId);
            return idMap;
        }
        catch (RepositoryException e) {
            throw new FatalTaskException(e, this);
        }
    }

    private PermissionRequestReader getReader() {
        if (reader == null) {
            reader = new PermissionRequestReader(dmsaaLocation);
        }
        return reader;
    }

}
