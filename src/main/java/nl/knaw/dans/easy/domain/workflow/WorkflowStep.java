package nl.knaw.dans.easy.domain.workflow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.knaw.dans.common.jibx.AbstractJiBXObject;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.RepoAccess;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

public class WorkflowStep extends AbstractJiBXObject<WorkflowStep>
{

    private static final long  serialVersionUID = 353239544598848090L;

    private boolean            template;
    private String             id;
    private boolean            required;
    private boolean            completed;
    private String             doneById;
    private DateTime           completionTime;
    private double             timeSpent;
    private boolean            timeSpentWritable;
    private List<Remark>       remarks          = new ArrayList<Remark>();
    private List<WorkflowStep> steps            = new ArrayList<WorkflowStep>();
    private WorkflowStep       parent;
    private EasyUser               whoDidIt;
    private boolean            dirty;

    protected WorkflowStep()
    {

    }

    public WorkflowStep(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        String thisId = id;
        if (template && parent != null)
        {
            thisId = parent.getId() + "." + id;
        }
        return thisId;
    }

    protected boolean isTemplate()
    {
        return false;
    }

    public boolean isDirty()
    {
        return dirty;
    }

    public void setDirty(boolean dirty)
    {
        if (this.dirty == dirty)
            return;
        this.dirty = dirty;
        if (dirty)
        {
            if (getParent() != null)
            {
                getParent().setDirty(dirty);
            }
        }
        else
        {
            for (WorkflowStep step : steps)
            {
                step.setDirty(dirty);
            }
        }
    }

    public boolean isRequired()
    {
        return required;
    }

    public boolean isCompleted()
    {
        return isCompleted(true);
    }

    public boolean areRequiredStepsCompleted()
    {
        return isCompleted(false);
    }

    public boolean isCompleted(boolean checkAllSteps)
    {
        boolean isComplete;
        if (hasSubsteps())
        {
            isComplete = stepsCompleted(checkAllSteps);
        }
        else
        {
            isComplete = checkAllSteps ? completed : completed || !isRequired();
        }
        return isComplete;
    }

    private boolean stepsCompleted(boolean checkAllSteps)
    {
        boolean stepsCompleted = true;
        Iterator<WorkflowStep> iter = steps.iterator();
        while (stepsCompleted && iter.hasNext())
        {
            WorkflowStep step = iter.next();
            stepsCompleted = step.isCompleted(checkAllSteps);
        }
        return stepsCompleted;
    }

    public void setCompleted(boolean completed, String userId)
    {
        setCompleted(completed);
        if (completed)
        {
            setDoneById(userId);
        }
        else
        {
            setDoneById(null);
        }
    }

    public void setCompleted(boolean stepCompleted)
    {
        if (hasSubsteps())
        {
            for (WorkflowStep step : steps)
            {
                step.setCompleted(stepCompleted);
            }
        }
        else
        {
            this.completed = stepCompleted;
            if (stepCompleted)
            {
                if (completionTime == null)
                {
                    completionTime = new DateTime();
                } // else this step was already completed, so don't change completionTime
            }
            else
            {
                completionTime = null;
            }
        }
        setDirty(true);
    }
    
    // Temporary method. Use only for migration!
    public void setRequiredCompleted(String doneById, DateTime timeCompleted)
    {
        for (WorkflowStep step : steps)
        {
            step.setRequiredCompleted(doneById, timeCompleted);
        }
        if (isRequired())
        {
            this.completed = true;
            this.doneById = doneById;
            this.completionTime = timeCompleted;
        }
        setDirty(true);
    }
    
    // Temporary method. Use only for migration!
    public void setCompleted(String doneById, DateTime timeCompleted)
    {
        this.completed = true;
        this.doneById = doneById;
        this.completionTime = timeCompleted;
    }

    public DateTime getCompletionTimeRequiredSteps()
    {
        return getCompletionTime(false);
    }

    public DateTime getCompletionTimeAllSteps()
    {
        return getCompletionTime(true);
    }

    public DateTime getCompletionTime(boolean checkAllSteps)
    {
        DateTime dateCompleted = null;
        if (hasSubsteps())
        {
            if (isCompleted(checkAllSteps))
            {
                dateCompleted = lastCompleted(checkAllSteps);
            }
        }
        else
        {
            dateCompleted = completionTime;
        }
        return dateCompleted;
    }

    private DateTime lastCompleted(boolean checkAllSteps)
    {
        DateTime lastCompleted = null;

        for (WorkflowStep step : steps)
        {
            DateTime stepCompletion = step.getCompletionTime(checkAllSteps);
            if (stepCompletion != null)
            {
                if (lastCompleted == null)
                {
                    lastCompleted = stepCompletion;
                }
                else
                {
                    lastCompleted = stepCompletion.isAfter(lastCompleted) ? stepCompletion : lastCompleted;
                }
            }
        }
        return lastCompleted;
    }

    /**
     * Get time spent on this WorkflowStep + the total time spent on sub steps (if any).
     * 
     * @return time spent on this WorkflowStep + the total time spent on sub steps
     */
    public double getTimeSpent()
    {
        double subStepsTime = 0.0d;
        for (WorkflowStep step : steps)
        {
            subStepsTime += step.getTimeSpent();
        }
        return timeSpent + subStepsTime;
    }

    /**
     * Set time spent on this WorkflowStep.
     * <p/>
     * Whether time spent on this level is read-only or also writable is dictated by timeSpentWritable. Either this
     * level is writable <b>or</b> sub levels are writable, not both.
     * 
     * @param timeSpent
     * @throws IllegalStateException
     *         if time spent is not writable
     * @see #isTimeSpentWritable()
     */
    public void setTimeSpent(double timeSpent)
    {
        if (!timeSpentWritable)
        {
            throw new IllegalStateException("Time spent is not writable on this level: " + getId());
        }
        evaluateDirty(this.timeSpent, timeSpent);
        this.timeSpent = timeSpent;
    }

    public boolean isTimeSpentWritable()
    {
        return timeSpentWritable;
    }

    public boolean hasSubsteps()
    {
        return steps.size() > 0;
    }

    public boolean isLeaf()
    {
        return !hasSubsteps();
    }

    public String getDoneById()
    {
        return doneById;
    }

    public void setDoneById(String doneById)
    {
        if (evaluateDirty(this.doneById, doneById))
        {
            whoDidIt = null;
        }
        this.doneById = doneById;
        for (WorkflowStep step : steps)
        {
            if (step.getDoneById() == null || doneById == null)
            {
                step.setDoneById(doneById);
            }
        }
    }

    public EasyUser getWhoDidIt()
    {
        if (whoDidIt == null && doneById != null)
        {
            whoDidIt = RepoAccess.getDelegator().getUser(doneById);
        }
        return whoDidIt;
    }

    public void setWhoDidIt(EasyUser whoDidIt)
    {
        this.whoDidIt = whoDidIt;
        setDoneById(whoDidIt == null ? null : whoDidIt.getId());
    }

    public List<Remark> getRemarks()
    {
        return remarks;
    }

    public void setRemarks(List<Remark> remarks)
    {
        this.remarks = remarks;
        setDirty(true);
    }

    public void addRemark(Remark remark)
    {
        remarks.add(remark);
        setDirty(true);
    }

    public List<WorkflowStep> getSteps()
    {
        return steps;
    }

    protected void setSteps(List<WorkflowStep> subSteps)
    {
        this.steps = subSteps;
        for (WorkflowStep step : steps)
        {
            step.setParent(this);
        }
    }

    public void addStep(WorkflowStep step)
    {
        if (this == step)
        {
            throw new IllegalArgumentException("Cannot add this " + this.getClass().getSimpleName() + " to it self.");
        }
        steps.add(step);
        step.setParent(this);
    }

    public WorkflowStep getParent()
    {
        return parent;
    }

    protected void setParent(WorkflowStep parent)
    {
        this.parent = parent;
    }

    public List<WorkflowStep> getRequiredSteps()
    {
        List<WorkflowStep> requiredSteps = new ArrayList<WorkflowStep>();
        collectRequiredSteps(requiredSteps);
        return requiredSteps;
    }

    protected void collectRequiredSteps(List<WorkflowStep> requiredSteps)
    {
        if (required)
        {
            requiredSteps.add(this);
        }
        for (WorkflowStep step : steps)
        {
            step.collectRequiredSteps(requiredSteps);
        }
    }
    
    public WorkflowStep getStep(String id)
    {
        WorkflowStep step = null;
        if (getId().equals(id))
        {
            step = this;
        }
        else
        {
            Iterator<WorkflowStep> iter = steps.iterator();
            while (iter.hasNext() && step == null)
            {
                WorkflowStep kidStep = iter.next();
                step = kidStep.getStep(id);
            }
        }
        return step;
    }
    
    public void copyValues(WorkflowStep wfs)
    {
        this.completed = wfs.completed;
        this.completionTime = wfs.completionTime;
        this.doneById = wfs.doneById;
        this.remarks = wfs.remarks;
        this.timeSpent = wfs.timeSpent;
    }

    public String printStructure(int indent)
    {
        StringBuilder sb = new StringBuilder();
        getStructure(sb, indent, 0);
        return sb.toString();
    }

    protected void getStructure(StringBuilder sb, int indent, int level)
    {
        String prefix = StringUtils.repeat(" ", indent * level);
        level++;
        sb.append(prefix);
        sb.append(getId());
        sb.append(isRequired() ? " *" : "");
        sb.append("\n");
        for (WorkflowStep step : steps)
        {
            step.getStructure(sb, indent, level);
        }
    }

    public int countRequiredSteps()
    {
        int count = 0;
        if (required)
        {
            count++;
        }
        for (WorkflowStep step : steps)
        {
            count += step.countRequiredSteps();
        }
        return count;
    }

    public int countSteps()
    {
        int count = 1;
        for (WorkflowStep step : steps)
        {
            count += step.countSteps();
        }
        return count;
    }

    public int countLeaves()
    {
        int count = 0;
        if (isLeaf())
        {
            count++;
        }
        for (WorkflowStep step : steps)
        {
            count += step.countLeaves();
        }
        return count;
    }

    public int countRequiredStepsCompleted()
    {
        int count = 0;
        if (required && completed)
        {
            count++;
        }
        for (WorkflowStep step : steps)
        {
            count += step.countRequiredStepsCompleted();
        }
        return count;

    }

    protected boolean evaluateDirty(Object obj1, Object obj2)
    {
        boolean dirty = false;
        if (obj2 == null)
        {
            dirty = obj1 != null;
        }
        else
        {
            dirty = !obj2.equals(obj1);
        }
        if (dirty)
            setDirty(true);
        return dirty;
    }

}
