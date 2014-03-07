package nl.knaw.dans.easy.security;

import java.util.List;

import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.easy.domain.model.Dataset;

import org.joda.time.DateTime;

public class EmbargoFreeCheck extends AbstractCheck
{

    @Override
    public boolean evaluate(ContextParameters ctxParameters)
    {
        return evaluate(ctxParameters.getDataset());
    }

    public boolean evaluate(Dataset dataset)
    {
        boolean conditionMet = false;
        if (dataset != null)
        {
            conditionMet = !dataset.isUnderEmbargo();
        }
        return conditionMet;
    }

    @Override
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
            sb.append("\n\tdataset.isUnderEmbargo = " + dataset.isUnderEmbargo(new DateTime()));
        }
        sb.append("\n\tcondition met = ");
        sb.append(evaluate(ctxParameters));
        return sb.toString();
    }

    public String getProposition()
    {
        return "[Dataset is not under embargo at current date]";
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
                hints.add(CommonSecurityException.HINT_DATASET_UNDER_EMBARGO);
            }
        }
        return conditionMet;
    }

}
