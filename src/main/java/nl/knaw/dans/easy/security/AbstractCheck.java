package nl.knaw.dans.easy.security;

import java.util.List;

/**
 * Abstract SecurityOfficer that holds a basic proposition or sentence.
 * 
 * @author ecco Aug 5, 2009
 */
public abstract class AbstractCheck implements SecurityOfficer
{
    /**
     * {@inheritDoc}
     */
    public boolean isComponentVisible(ContextParameters ctxParameters)
    {
        return evaluate(ctxParameters);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnableAllowed(ContextParameters ctxParameters)
    {
        return evaluate(ctxParameters);
    }
    
    public String explainEnableAllowed(ContextParameters ctxParameters)
    {
        return explain(ctxParameters);
    }
    
    public String explainComponentVisible(ContextParameters ctxParameters)
    {
        return explain(ctxParameters);
    }

    /**
     * Evaluate the basic proposition or sentence of this Check against the given context.
     * 
     * @param ctxParameters
     *        the context
     * @return <code>true</code> if condition(s) as proposed by this check are met by the context, <code>false</code>
     *         otherwise
     */
    public abstract boolean evaluate(ContextParameters ctxParameters);
    
    protected abstract String explain(ContextParameters ctxParameters);
    
    protected StringBuilder startExplain(ContextParameters ctxParameters)
    {
        StringBuilder sb = new StringBuilder("\n" + ctxParameters.nextChar(this) + " = check ");
        sb.append(getProposition());
        sb.append(" (");
        sb.append(this.getClass().getSimpleName());
        sb.append(")");
        return sb;
    }
    
    public boolean getHints(ContextParameters ctxParameters, List<Object> hints)
    {
        boolean conditionMet = evaluate(ctxParameters);
        if (!conditionMet)
        {
            hints.add(this.getClass());
        }
        return conditionMet;
    }
    

}
