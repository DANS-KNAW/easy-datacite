package nl.knaw.dans.easy.security;

import java.util.List;

/**
 * A SecurityOfficer evaluates it's proposition against a context.
 * 
 * @author ecco Aug 1, 2009
 */
public interface SecurityOfficer
{

    /**
     * Is a component visible, given the context.
     * <p/>
     * Implicates
     * 
     * <pre>
     * p &#8594; q
     * </pre>
     * 
     * where p: the evaluation of this SecurityOfficer's proposition (as expressed in
     * {@link #getProposition()}) against the given context;<br/>
     * and q: component is visible.
     * <p/>
     * 
     * @param ctxParameters
     *        parameters necessary for evaluating the proposition
     * @return <code>true</code> if the component should be visible, <code>false</code> otherwise
     */
    boolean isComponentVisible(ContextParameters ctxParameters);

    /**
     * Is an action allowed, given the context.
     * <p/>
     * Implicates
     * 
     * <pre>
     * p &#8594; q
     * </pre>
     * 
     * where p: the evaluation of this SecurityOfficer's proposition (as expressed in
     * {@link #getProposition()}) against the given context;<br/>
     * and q: action is allowed.
     * <p/>
     * 
     * @param ctxParameters
     *        parameters necessary for evaluating the proposition
     * @return <code>true</code> if allowed, <code>false</code> otherwise
     */
    boolean isEnableAllowed(ContextParameters ctxParameters);

    /**
     * Get the proposition evaluated by this SecurityOfficer.
     * 
     * @return the proposition evaluated by this SecurityOfficer
     */
    String getProposition();

    /**
     * Explain why an action is allowed -or not allowed- given the context.
     * 
     * @param ctxParameters
     *        the given context
     * @return explanation
     */
    String explainEnableAllowed(ContextParameters ctxParameters);

    /**
     * Explain why a component is visible -or not visible- given the context.
     * 
     * @param ctxParameters
     *        the given context
     * @return explanation
     */
    String explainComponentVisible(ContextParameters ctxParameters);

    /**
     * This SecurityOfficer is given a chance to hint why she refuses to allow things - given the
     * context. If a SecurityOfficer advises negative, she should add some object that hints the reason
     * for negating.
     * 
     * @param ctxParameters
     *        the given context
     * @param hints
     *        a list of objects
     * @return the outcome of the evaluation
     */
    boolean getHints(ContextParameters ctxParameters, List<Object> hints);

}
