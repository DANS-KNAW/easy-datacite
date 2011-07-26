package nl.knaw.dans.easy.security;

import nl.knaw.dans.easy.domain.model.Dataset;

public class WorkflowCheck extends AbstractCheck
{
    private final boolean allSteps;
    
    public WorkflowCheck()
    {
        this(false);
    }
    
    public WorkflowCheck(boolean allSteps)
    {
        this.allSteps = allSteps;
    }

    @Override
    public boolean evaluate(ContextParameters ctxParameters)
    {
        boolean conditionMet = false;
        Dataset dataset = ctxParameters.getDataset();
        if (dataset != null)
        {
            conditionMet = dataset.getAdministrativeMetadata().getWorkflowData().getWorkflow().isCompleted(allSteps);
        }
        return conditionMet;
    }

    public String getProposition()
    {
        return "[" + (allSteps ? "All steps" : "Required steps") + " of workflow are completed]";
    }
    
    @Override
    protected String explain(ContextParameters ctxParameters)
    {
        boolean conditionMet = false;
        StringBuilder sb = super.startExplain(ctxParameters);
        
        Dataset dataset = ctxParameters.getDataset();
        if (dataset == null)
        {
            sb.append("\n\tdataset = null");
        }
        else
        {
            conditionMet = evaluate(ctxParameters);
            sb.append("\n\t" + (allSteps ? "All steps" : "Required steps") + " of workflow are " + (conditionMet ? "" : "not ") + "completed");
        }
        sb.append("\n\tcondition met = ");
        sb.append(conditionMet);
        return sb.toString();
    }

}
