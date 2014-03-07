package nl.knaw.dans.easy.security;

import java.util.List;

/**
 * Disjunction SecurityOfficer.
 * 
 * @author ecco Aug 1, 2009
 */
public class Or implements SecurityOfficer
{

    private final SecurityOfficer[] officers;

    /**
     * Constructs a new disjunction SecurityOfficer with the given officers as operands.
     * <p/>
     * This SecurityOfficer evaluates
     * 
     * <pre>
     * a &#8744; b &#8594; q
     * </pre>
     * 
     * @param officers
     *        the operands
     */
    public Or(SecurityOfficer... officers)
    {
        this.officers = officers;
    }

    public String getProposition()
    {
        return PropositionBuilder.buildOrProposition(officers);
    }

    public boolean isComponentVisible(ContextParameters ctxParameters)
    {
        boolean visible = false;
        int i = 0;
        while (!visible && i < officers.length)
        {
            visible = officers[i].isComponentVisible(ctxParameters);
            i++;
        }
        return visible;
    }

    public boolean isEnableAllowed(ContextParameters ctxParameters)
    {
        boolean allowed = false;
        int i = 0;
        while (!allowed && i < officers.length)
        {
            allowed = officers[i].isEnableAllowed(ctxParameters);
            i++;
        }
        return allowed;
    }

    public String explainEnableAllowed(ContextParameters ctxParameters)
    {
        StringBuilder sb = new StringBuilder();
        for (SecurityOfficer officer : officers)
        {
            sb.append(officer.explainEnableAllowed(ctxParameters));
        }
        sb.append("\n").append(ctxParameters.nextChar(this)).append(" = ");
        int i;
        for (i = 0; i < officers.length - 1; i++)
        {
            sb.append(ctxParameters.charFor(officers[i])).append(" OR ");
        }
        sb.append(ctxParameters.charFor(officers[i])).append(" --> ").append(isEnableAllowed(ctxParameters)).append("\n");

        return sb.toString();
    }

    public String explainComponentVisible(ContextParameters ctxParameters)
    {
        StringBuilder sb = new StringBuilder();
        for (SecurityOfficer officer : officers)
        {
            sb.append(officer.explainComponentVisible(ctxParameters));
        }
        sb.append("\n").append(ctxParameters.nextChar(this)).append(" = ");
        int i;
        for (i = 0; i < officers.length - 1; i++)
        {
            sb.append(ctxParameters.charFor(officers[i])).append(" OR ");
        }
        sb.append(ctxParameters.charFor(officers[i])).append(" --> ").append(isComponentVisible(ctxParameters)).append("\n");

        return sb.toString();
    }

    public boolean getHints(ContextParameters ctxParameters, List<Object> hints)
    {
        boolean foundHint = false;
        for (SecurityOfficer officer : officers)
        {
            foundHint |= !officer.getHints(ctxParameters, hints);
        }
        return foundHint;
    }

}
