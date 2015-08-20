package nl.knaw.dans.easy.tools.task.am.dataset;

import java.util.List;

import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.WorkflowData;
import nl.knaw.dans.easy.domain.workflow.Remark;
import nl.knaw.dans.easy.domain.workflow.WorkflowStep;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.easy.tools.exceptions.FatalTaskException;
import nl.knaw.dans.easy.tools.exceptions.TaskCycleException;
import nl.knaw.dans.easy.tools.exceptions.TaskException;

import org.apache.commons.lang.StringUtils;

public class WorkflowCorrectorTask extends AbstractDatasetTask {

    @Override
    public void run(JointMap joint) throws TaskException, TaskCycleException, FatalTaskException {
        abbortIfNotMigration(joint);

        if (hasTaskStamp(joint)) {
            return; // previously corrected by this task.
        }

        Dataset dataset = joint.getDataset();
        WorkflowData wfd = dataset.getAdministrativeMetadata().getWorkflowData();

        WorkflowStep root = wfd.getWorkflow();

        for (WorkflowStep step : root.getSteps()) {
            copyRemarksToRoot(joint, root, step);
        }

    }

    private void copyRemarksToRoot(JointMap joint, WorkflowStep root, WorkflowStep step) {
        for (Remark remark : step.getRemarks()) {
            if (!StringUtils.isBlank(remark.getText())) {
                // Stupid jerks want everything in one field, so they can have it in one field.
                List<Remark> remarks = root.getRemarks();
                Remark r0;
                if (remarks.isEmpty()) {
                    r0 = new Remark();
                    remarks.add(r0);
                } else {
                    r0 = remarks.get(0);
                }
                String originalText = r0.getText() == null ? "" : r0.getText() + "\n\n";
                r0.setText(originalText + "stepId: " + step.getId() + "\n" + "remarkerId: " + remark.getRemarkerId() + "\n" + "date: " + remark.getRemarkDate()
                        + "\n" + remark.getText());

                joint.setCycleSubjectDirty(true);
                setTaskStamp(joint);
                RL.info(new Event(getTaskName(), "Copied remark", joint.getDataset().getStoreId()));
            }
        }

        for (WorkflowStep kid : step.getSteps()) {
            copyRemarksToRoot(joint, root, kid);
        }

    }

}
