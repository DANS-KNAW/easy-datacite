/**
 * 
 */
package nl.knaw.dans.easy.web.template;


import org.apache.wicket.model.IModel;

/**
 * Default stateless panel for Easy.
 * 
 * @author Herman Suijs
 */
public abstract class AbstractEasyStatelessPanel extends AbstractEasyPanel
{

    /**
     * 
     */
    private static final long serialVersionUID = -8092666797589916733L;

    /**
     * Default constructor.
     * 
     * @param wicketId wicket id
     */
    public AbstractEasyStatelessPanel(final String wicketId)
    {
        super(wicketId);
    }

    /**
     * Constructor with model.
     * 
     * @param wicketId wicket id
     * @param model model
     */
    public AbstractEasyStatelessPanel(final String wicketId, final IModel model)
    {
        super(wicketId, model);
    }

    /**
     * Make Panel stateless.
     * 
     * @return true
     */
    @Override
    public boolean getStatelessHint() // NOPMD: wicket method
    {
        return true;
    }

}
