package nl.knaw.dans.easy.web;

import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;

import org.apache.wicket.PageMap;
import org.apache.wicket.PageParameters;
import org.apache.wicket.model.IModel;

/**
 * Page to show when the session has expired.
 * 
 * @author Herman Suijs
 */
public class ExpiredPage extends AbstractEasyNavPage {

    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = -6445119080313912624L;

    /**
     * Initialize same for all constructors.
     */
    private void init() {
        // TODO: create contents of expired page.
    }

    /**
     * Default Constructor.
     */
    public ExpiredPage() {
        super();
        init();
    }

    /**
     * Constructor with IModel.
     * 
     * @param model
     *        IModel
     */
    public ExpiredPage(final IModel model) {
        super(model);
        init();
    }

    /**
     * Constructor with PageMap and IModel.
     * 
     * @param map
     *        PageMap
     * @param model
     *        IModel
     */
    public ExpiredPage(final PageMap map, final IModel model) {
        super(map, model);
        init();
    }

    /**
     * Constructor with PageMap.
     * 
     * @param map
     *        PageMap
     */
    public ExpiredPage(final PageMap map) {
        super(map);
        init();
    }

    /**
     * Constructor with parameters.
     * 
     * @param parameters
     *        Page parameters
     */
    public ExpiredPage(final PageParameters parameters) {
        super(parameters);
        // TODO Auto-generated constructor stub
    }

}
