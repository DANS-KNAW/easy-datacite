package nl.knaw.dans.easy.security;

import java.util.List;

/**
 * A SecurityOfficer that has different answers for different questions.
 * 
 * @author ecco Aug 1, 2009
 */
public class SplitAnswer implements SecurityOfficer
{

    private final SecurityOfficer visibilityOfficer;
    private final SecurityOfficer enablingOfficer;

    /**
     * Constructs a new SplitAnswer with the given <code>visibilityOfficer</code> and <code>enablingOfficer</code> as respondents.
     * <p/>
     * This SecurityOfficer evaluates
     * <pre>
     * v &#8594; component is visible
     * e &#8594; enable is allowed
     * </pre>
     * 
     * where v: visibilityOfficer and e: enablingOfficer
     * <p/>
     * 
     * @param visibilityOfficer v
     * @param enablingOfficer e
     */
    public SplitAnswer(SecurityOfficer visibilityOfficer, SecurityOfficer enablingOfficer)
    {
        this.visibilityOfficer = visibilityOfficer;
        this.enablingOfficer = enablingOfficer;
    }

    public String getProposition()
    {
        StringBuilder sb = new StringBuilder("Split answer:");
        sb.append(" ComponentVisisble <== ");
        sb.append(visibilityOfficer.getProposition());
        sb.append(" EnableAllowed <== ");
        sb.append(enablingOfficer.getProposition());
        return sb.toString();
    }

    public boolean isComponentVisible(ContextParameters ctxParameters)
    {
        return visibilityOfficer.isComponentVisible(ctxParameters);
    }

    public boolean isEnableAllowed(ContextParameters ctxParameters)
    {
        return enablingOfficer.isEnableAllowed(ctxParameters);
    }
    
    public String explainEnableAllowed(ContextParameters ctxParameters)
    {
        return new StringBuilder()
        .append(enablingOfficer.explainEnableAllowed(ctxParameters))
        .append("\n") 
        .append(ctxParameters.nextChar(this))
        .append(" = ")
        .append(ctxParameters.charFor(enablingOfficer))
        .append(" --> ")
        .append(isEnableAllowed(ctxParameters))
        .append("\n") 
        .toString();
    }
    
    public String explainComponentVisible(ContextParameters ctxParameters)
    {
        return new StringBuilder()
        .append(visibilityOfficer.explainComponentVisible(ctxParameters))
        .append("\n") 
        .append(ctxParameters.nextChar(this))
        .append(" = ")
        .append(ctxParameters.charFor(visibilityOfficer))
        .append(" --> ")
        .append(isComponentVisible(ctxParameters))
        .append("\n") 
        .toString();
    }
    
    public boolean getHints(ContextParameters ctxParameters, List<Object> hints)
    {
        return enablingOfficer.getHints(ctxParameters, hints);
    }

}
