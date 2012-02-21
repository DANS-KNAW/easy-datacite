package nl.knaw.dans.easy.security;

import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;


public class DmoNamespaceCheck extends AbstractCheck
{
    
    private final DmoNamespace[] allowedNamespaces;
    private final String proposition;
    
    public DmoNamespaceCheck(DmoNamespace...namespaces)
    {
        allowedNamespaces = namespaces;
        proposition = PropositionBuilder.buildOrProposition("storeId is within namespace", namespaces);
    }

    @Override
    public boolean evaluate(ContextParameters ctxParameters)
    {
        boolean conditionMet = false;
        DmoStoreId storeId = (DmoStoreId) ctxParameters.getObject(DmoStoreId.class, 0);
        if (storeId != null)
        {
            int i = 0;
            while (!conditionMet && i < allowedNamespaces.length)
            {
                conditionMet = storeId.isInNamespace(allowedNamespaces[i++]);
            }
        }
        return conditionMet;
    }

    @Override
    protected String explain(ContextParameters ctxParameters)
    {
        StringBuilder sb = super.startExplain(ctxParameters);
        sb.append("\n\tdmoStoreId = ");
        sb.append(ctxParameters.getObject(DmoStoreId.class, 0));
        sb.append("\n\tcondition met = ");
        sb.append(evaluate(ctxParameters));
        return sb.toString();
    }

    @Override
    public String getProposition()
    {
        return proposition;
    }

}
