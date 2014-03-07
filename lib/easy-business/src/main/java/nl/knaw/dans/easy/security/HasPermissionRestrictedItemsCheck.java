package nl.knaw.dans.easy.security;

import nl.knaw.dans.easy.domain.model.Dataset;

public class HasPermissionRestrictedItemsCheck extends AbstractCheck
{

    public String getProposition()
    {
        return "[Dataset has items that require permission]";
    }

    public boolean evaluate(ContextParameters ctxParameters)
    {
        boolean conditionMet = false;

        Dataset dataset = ctxParameters.getDataset();
        if (dataset != null)
        {
            conditionMet = dataset.hasPermissionRestrictedItems();
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
            sb.append(", dataset = null");
        }
        else
        {
            sb.append(", dataset " + dataset.getStoreId() + " hasPermissionRestrictedItems = " + dataset.hasPermissionRestrictedItems());
        }

        sb.append("\n\tcondition met = ");
        sb.append(evaluate(ctxParameters));
        return sb.toString();
    }

}
