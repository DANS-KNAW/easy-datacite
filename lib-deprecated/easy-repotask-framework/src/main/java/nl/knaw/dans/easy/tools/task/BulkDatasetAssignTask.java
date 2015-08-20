package nl.knaw.dans.easy.tools.task;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedHashSet;
import java.util.Set;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.userrepo.EasyUserRepo;
import nl.knaw.dans.easy.domain.migration.IdMap;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;
import nl.knaw.dans.easy.tools.util.Reporter;

public class BulkDatasetAssignTask extends AbstractTask {
    private final Set<String> aipIdSet = new LinkedHashSet<String>();
    private final String assignee;
    private int correctedCount = 0;

    /**
     * @param assignee
     *        is the user to whom the datasets of this bulk task should be assigned. Arguments are set in the Spring-context.
     * @throws IOException
     * @throws RepositoryException
     */
    public BulkDatasetAssignTask(String assignee, String filePath) throws IOException, RepositoryException {
        this.assignee = assignee;
        // Read the file containing the aipIds
        readAipIdSet(filePath);
    }

    /**
     * Checks whether @param assignee exist in the user repository
     * 
     * @param assignee
     * @return
     */
    private boolean checkAssignee(String assignee) {
        boolean ok = false;
        EasyUserRepo repo = Data.getUserRepo();
        try {
            ok = repo.exists(assignee);
        }
        catch (ObjectNotInStoreException e) {
            RL.error(new Event("User {" + assignee + "} not found in User Repository"));
        }
        catch (RepositoryException e) {
            RL.error(new Event("Cannot access User Repository"));
        }
        return ok;
    }

    /**
     * Reads the @param filePath which is the path to the file containing the aipIds The filePath is known as 'bulk.reassign.file' in the
     * application.properties.
     * 
     * @param filePath
     * @throws IOException
     */
    private void readAipIdSet(String filePath) throws IOException {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(filePath, "r");
            String aipId;
            while ((aipId = raf.readLine()) != null) {
                aipIdSet.add(aipId.trim());
            }
        }
        finally {
            if (raf != null) {
                raf.close();
            }
        }
    }

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        // If the new assignee exist
        if (checkAssignee(assignee)) {
            RL.info(new Event("count", "number of aipId's to process", "" + aipIdSet.size()));
            for (String aipId : aipIdSet) {
                IdMap idMap = getMostRecentIdMap(aipId);
                if (idMap == null) {
                    RL.error(new Event("no idMap", aipId));
                } else {
                    process(idMap);
                }
            }
            RL.info(new Event("count", "count of corrected", "" + correctedCount));
        }
        Reporter.closeAllFiles();
    }

    private IdMap getMostRecentIdMap(String aipId) throws FatalTaskException {
        try {
            IdMap idMap = Data.getMigrationRepo().getMostRecentByAipId(aipId);
            return idMap;
        }
        catch (RepositoryException e) {
            throw new FatalTaskException(e, this);
        }
    }

    private void process(IdMap idMap) throws FatalTaskException {
        Dataset dataset;
        String storeId = idMap.getStoreId();
        try {
            // Get the dataset from the repository
            dataset = (Dataset) Data.getEasyStore().retrieve(new DmoStoreId(storeId));
            // Assign this dataset to the new assignee
            dataset.getAdministrativeMetadata().getWorkflowData().setAssigneeId(assignee);
            // Save the changes to the repository
            Data.getEasyStore().update(dataset, getTaskName());

            // Keep track of number of changes
            correctedCount++;
        }
        catch (ObjectNotInStoreException e) {
            RL.error(new Event("dataset not found", storeId));
            return;
        }
        catch (RepositoryException e) {
            throw new FatalTaskException(e, this);
        }
    }
}
