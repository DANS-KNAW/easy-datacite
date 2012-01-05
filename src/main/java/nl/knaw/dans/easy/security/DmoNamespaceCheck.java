package nl.knaw.dans.easy.security;

import nl.knaw.dans.common.lang.repo.DmoNamespace;


public class DmoNamespaceCheck extends AbstractCheck
{
    
    private final DmoNamespace[] allowedNamespaces;
    private final String proposition;
    
    public DmoNamespaceCheck(DmoNamespace...namespaces)
    {
        allowedNamespaces = namespaces;
        proposition = PropositionBuilder.buildOrProposition("storeId starts with", namespaces);
    }

    @Override
    public boolean evaluate(ContextParameters ctxParameters)
    {
        boolean conditionMet = false;
        String storeId = (String) ctxParameters.getObject(String.class, 0);
        if (storeId != null)
        {
            int i = 0;
            while (!conditionMet && i < allowedNamespaces.length)
            {
                conditionMet = storeId.startsWith(allowedNamespaces[i++].getValue());
            }
        }
        return conditionMet;
    }

    @Override
    protected String explain(ContextParameters ctxParameters)
    {
        StringBuilder sb = super.startExplain(ctxParameters);
        sb.append("\n\tstoreId = ");
        sb.append(ctxParameters.getObject(String.class, 0));
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
