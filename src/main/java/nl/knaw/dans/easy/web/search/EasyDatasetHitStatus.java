package nl.knaw.dans.easy.web.search;

import java.io.Serializable;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.easy.domain.workflow.WorkflowFactory;
import nl.knaw.dans.easy.domain.workflow.WorkflowStep;
import nl.knaw.dans.easy.web.EasyWicketApplication;
import nl.knaw.dans.easy.web.template.dates.EasyDate;

import org.joda.time.DateTime;

class EasyDatasetHitStatus implements Serializable
{
    private static final long serialVersionUID = -589814339512928067L;

    private DateTime date;
    private String assignee;
    private int workflowProgress;
    private DatasetState state;
    private String role;
    private String depositor;

    private static int maxWorkflowProgress = 0;

    public String getDate()
    {
        return EasyDate.toDateString(date);
    }

    public void setDate(DateTime date)
    {
        this.date = date;
    }

    public String getAssignee()
    {
        return assignee == null ? "" : assignee;
    }

    public void setAssignee(String assignee)
    {
        this.assignee = assignee;
    }

    public int getWorkflowProgress()
    {
        return workflowProgress;
    }

    public void setWorkflowProgress(int workflowProgress)
    {
        this.workflowProgress = workflowProgress;
    }

    /**
     * This retrieves the maximum workflow progress from the workflow factory, which gets its information
     * from the XML, but this is done only once, so if the XML changes the change is not reflected here
     * until you restart EASY. If it will be needed to hot swap the workflow steps then this code needs
     * to be changed.
     * 
     * @return the number of steps needed to be fulfilled before the dataset workflow is completed.
     */
    public int getMaxWorkflowProgress()
    {
        if (maxWorkflowProgress == 0)
        {
            WorkflowStep root = WorkflowFactory.newDatasetWorkflow();
            maxWorkflowProgress = root.countRequiredSteps();
        }
        return maxWorkflowProgress;
    }

    public void setState(DatasetState state)
    {
        this.state = state;
    }

    public DatasetState getState()
    {
        return state;
    }

    public void setRole(String role)
    {
        this.role = role;
    }

    public String getRole()
    {
        return role;
    }

    public void setDepositor(String depositor)
    {
        this.depositor = depositor;
    }

    public String getDepositor()
    {
        return depositor;
    }
}
