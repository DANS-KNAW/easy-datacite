package nl.knaw.dans.easy.web.deposit.repeater;

import nl.knaw.dans.pf.language.emd.EasyMetadata;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public abstract class AbstractCustomPanel extends SkeletonPanel
{

    public static final String CUSTOM_PANEL_ID = "customPanel";

    private static final long serialVersionUID = 5018898387039551789L;

    public AbstractCustomPanel(String id)
    {
        super(id);
    }

    public AbstractCustomPanel(String id, IModel<EasyMetadata> model)
    {
        super(id, model);
    }

    /**
     * Contribute the custom component(s) on a panel.
     * 
     * @return a panel with wicketId {@link #CUSTOM_PANEL_ID}
     */
    protected abstract Panel getCustomComponentPanel();

    protected void init()
    {
        super.init(); // skeletonPanel
        Panel customComponent = getCustomComponentPanel();
        add(customComponent);
        setVisible(customComponent.isVisible());
    }

}
