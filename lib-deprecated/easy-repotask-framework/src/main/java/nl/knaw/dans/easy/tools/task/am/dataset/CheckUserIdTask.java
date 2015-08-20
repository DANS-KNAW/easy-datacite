package nl.knaw.dans.easy.tools.task.am.dataset;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.WorkflowData;
import nl.knaw.dans.easy.domain.workflow.WorkflowStep;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;

import org.apache.commons.lang.StringUtils;

public class CheckUserIdTask extends AbstractDatasetTask {

    private String currentStoreId;

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        Dataset dataset = joint.getDataset();
        currentStoreId = dataset.getStoreId();

        String depositorId = dataset.getAdministrativeMetadata().getDepositorId();
        if (!userIdExists(depositorId)) {
            RL.warn(new Event(getTaskName(), "Unknown depositor", currentStoreId, depositorId));
        }

        WorkflowData wfd = dataset.getAdministrativeMetadata().getWorkflowData();
        String assigneeId = wfd.getAssigneeId();
        if (!StringUtils.isBlank(assigneeId) && !"NOT_ASSIGNED".equals(assigneeId) && !userIdExists(assigneeId)) {
            RL.warn(new Event(getTaskName(), "Unknown assignee", currentStoreId, assigneeId));
        }

        WorkflowStep root = wfd.getWorkflow();
        checkUserIds(root);

    }

    private void checkUserIds(WorkflowStep step) throws FatalTaskException {
        if (!StringUtils.isBlank(step.getDoneById()) && !userIdExists(step.getDoneById())) {
            RL.warn(new Event(getTaskName(), "Unknown archivist", currentStoreId, step.getDoneById()));
        }
        for (WorkflowStep kid : step.getSteps()) {
            checkUserIds(kid);
        }
    }

    private boolean userIdExists(String userId) throws FatalTaskException {
        try {
            return Data.getUserRepo().exists(userId);
        }
        catch (RepositoryException e) {
            throw new FatalTaskException(e, this);
        }
    }

}
