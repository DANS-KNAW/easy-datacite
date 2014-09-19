package nl.knaw.dans.common.wicket.components;

import org.apache.wicket.model.IModel;

/**
 * Extending this class gives the extender access to common Wicket methods. It is recommended that users of the Dans Commons wicket use the CommonPanel or the
 * CommonGPanel as a base class for all Wicket panels.
 * 
 * @author lobo
 */
public abstract class CommonPanel<T> extends CommonBasePanel<T> {
    private static final long serialVersionUID = 3906988013645820611L;

    public CommonPanel(String id) {
        super(id);
    }

    public CommonPanel(String id, IModel<T> model) {
        super(id, model);
    }
}
