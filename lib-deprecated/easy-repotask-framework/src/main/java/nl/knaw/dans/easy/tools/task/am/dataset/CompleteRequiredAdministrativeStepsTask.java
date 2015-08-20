package nl.knaw.dans.easy.tools.task.am.dataset;

import java.util.List;

import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.easy.domain.model.AdministrativeMetadata;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.WorkflowData;
import nl.knaw.dans.easy.domain.workflow.WorkflowStep;
import nl.knaw.dans.easy.tools.AbstractTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;

public class CompleteRequiredAdministrativeStepsTask extends AbstractTask {
    private String archivistId;
    private WorkflowStep steps;

    @Override
    public void run(JointMap jointMap) throws TaskException, TaskCycleException, FatalTaskException {
        setDatasetWorkflowStepsFrom(jointMap);

        if (!isRequiredWorkflowComplete()) {
            for (WorkflowStep requiredStep : getRequiredWorkflowSteps()) {
                if (!requiredStep.isCompleted()) {
                    requiredStep.setCompleted(true, getArchivistId());
                    RL.info(new Event(getTaskName(), String.format("Completed required step [%s]", requiredStep.getId())));
                }
            }
        } else {
            RL.info(new Event(getTaskName(), "All required steps are allready completed"));
        }
    }

    private void setDatasetWorkflowStepsFrom(JointMap joint) {
        Dataset dataset = joint.getDataset();
        AdministrativeMetadata adm = dataset.getAdministrativeMetadata();
        WorkflowData wfd = adm.getWorkflowData();
        steps = wfd.getWorkflow();
    }

    private boolean isRequiredWorkflowComplete() {
        return steps.areRequiredStepsCompleted();
    }

    private List<WorkflowStep> getRequiredWorkflowSteps() {
        return steps.getRequiredSteps();
    }

    public String getArchivistId() {
        return archivistId;
    }

    public void setArchivistId(String archivistId) {
        this.archivistId = archivistId;
    }
}
