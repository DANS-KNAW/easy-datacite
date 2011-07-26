package nl.knaw.dans.easy.security;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.easy.domain.model.Dataset;

public class DatasetAccesRightsCheck extends AbstractCheck
{
    private final List<AccessCategory> allowedRights;

    public DatasetAccesRightsCheck(AccessCategory...states)
    {
        super();
        allowedRights = Collections.synchronizedList(Arrays.asList(states));
    }

    public String getProposition()
    {
        synchronized (allowedRights)
        {
            return PropositionBuilder.buildOrProposition("Dataset accessCategory is", allowedRights);
        }
    } 


    public boolean evaluate(ContextParameters ctxParameters)
    {
        boolean conditionMet = false;
        Dataset dataset = ctxParameters.getDataset();
        if (dataset != null)
        {
            synchronized (allowedRights)
            {
                Iterator<AccessCategory> iter = allowedRights.iterator();
                while (!conditionMet && iter.hasNext())
                {
                    conditionMet = iter.next().equals(dataset.getAccessCategory());
                }
            }
        }
        return conditionMet;
    }

    protected String explain(ContextParameters ctxParameters)
    {
        StringBuilder sb = super.startExplain(ctxParameters);

        Dataset dataset = ctxParameters.getDataset();
        if (dataset == null)
        {
            sb.append("\n\tdataset = null");
        }
        else
        {
            sb.append("\n\tdataset.accessCategory = " + dataset.getAccessCategory());
        }
        sb.append("\n\tcondition met = ");
        sb.append(evaluate(ctxParameters));
        return sb.toString();
    }
    
    @Override
    public boolean getHints(ContextParameters ctxParameters, List<Object> hints)
    {
        Dataset dataset = ctxParameters.getDataset();
        boolean conditionMet = false;
        if (dataset == null)
        {
            hints.add(CommonSecurityException.HINT_DATASET_NULL);
        }
        else
        {
            conditionMet = evaluate(ctxParameters);
            if (!conditionMet)
            {
                hints.add(dataset.getAccessCategory());
            }
        }
        return conditionMet;
    }

}
