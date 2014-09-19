package nl.knaw.dans.easy.security;

import java.util.List;

/**
 * Negation SecurityOfficer.
 * 
 * @author ecco Aug 1, 2009
 */
public class Not implements SecurityOfficer {

    private final SecurityOfficer a;

    /**
     * Constructs a new negation SecurityOfficer with the given <code>a</code> as operand.
     * <p/>
     * This SecurityOfficer evaluates
     * 
     * <pre>
     *  &#172;a &#8594; q
     * </pre>
     * 
     * @param a
     *        SecurityOfficer a
     */
    public Not(SecurityOfficer a) {
        this.a = a;
    }

    public String getProposition() {
        StringBuilder sb = new StringBuilder("NOT(");
        sb.append(a.getProposition());
        sb.append(")");
        return sb.toString();
    }

    public boolean isComponentVisible(ContextParameters ctxParameters) {
        return !a.isComponentVisible(ctxParameters);
    }

    public boolean isEnableAllowed(ContextParameters ctxParameters) {
        return !a.isEnableAllowed(ctxParameters);
    }

    public String explainEnableAllowed(ContextParameters ctxParameters) {
        return new StringBuilder().append(a.explainEnableAllowed(ctxParameters)).append("\n").append(ctxParameters.nextChar(this)).append(" = !")
                .append(ctxParameters.charFor(a)).append(" --> ").append(isEnableAllowed(ctxParameters)).append("\n").toString();
    }

    public String explainComponentVisible(ContextParameters ctxParameters) {
        return new StringBuilder().append(a.explainComponentVisible(ctxParameters)).append("\n").append(ctxParameters.nextChar(this)).append(" = !")
                .append(ctxParameters.charFor(a)).append(" --> ").append(isComponentVisible(ctxParameters)).append("\n").toString();
    }

    public boolean getHints(ContextParameters ctxParameters, List<Object> hints) {
        return !a.getHints(ctxParameters, hints);
    }
}
