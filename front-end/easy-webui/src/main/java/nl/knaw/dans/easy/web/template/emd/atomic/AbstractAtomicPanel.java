/**
 *
 */
package nl.knaw.dans.easy.web.template.emd.atomic;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 * Abstract base class for atomic panel.
 * 
 * @author Eko Indarto
 */
public abstract class AbstractAtomicPanel extends Panel {
    /**
     * serial UID.
     */
    private static final long serialVersionUID = -5078462969562888539L;

    /**
     * Constructor. All atomic panel have names. A component's id cannot be null. This is the minimal constructor of component. It does not register a model.
     * 
     * @param id
     *        wicket id
     */
    public AbstractAtomicPanel(String id) {
        super(id);
    }

    /**
     * Constructor. All atomic panel have names. A component's id cannot be null. This constructor includes a model.
     * 
     * @param id
     *        Wicket id
     * @param The
     *        component's model
     */
    public AbstractAtomicPanel(String id, IModel model) {
        super(id, model);
    }

}
