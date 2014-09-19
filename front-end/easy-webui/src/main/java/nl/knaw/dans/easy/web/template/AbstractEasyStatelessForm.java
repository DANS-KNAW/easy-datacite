/**
 * 
 */
package nl.knaw.dans.easy.web.template;

import org.apache.wicket.model.IModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
public abstract class AbstractEasyStatelessForm<T> extends AbstractEasyForm<T> {
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Logger for this class.
     */
    static final Logger LOGGER = LoggerFactory.getLogger(AbstractEasyStatelessForm.class);

    public AbstractEasyStatelessForm(final String wicketId, final IModel<T> model) {
        super(wicketId, model);
    }

    /**
     * Set redirect to true for a stateless form.
     * 
     * @return True if super.process is true.
     */
    @Override
    public boolean process() {
        // set redirect to true for a stateless form.
        setRedirect(true);
        return super.process();
    }

    /**
     * Set stateless hint to true for this form.
     * 
     * @return True
     */
    @Override
    protected boolean getStatelessHint() // NOPMD: wicket method.
    {
        return true;
    }

}
