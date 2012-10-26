/**
 *
 */
package nl.knaw.dans.easy.web.template.emd.atomic;

import nl.knaw.dans.common.wicket.components.popup.HelpPopup;
import nl.knaw.dans.easy.web.common.HelpFileReader;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;

/**
 * @author Eko Indarto
 *
 */
public class SimpleLabelPanel extends AbstractAtomicPanel
{
    private static final long serialVersionUID = 5379953933385672968L;
    private static final String DEFAULT_RESOURCE_VALUE = "simpleLabelPanel.default_resource_value";

    private boolean initiated;
    private final String label;
    private final String anchorName;
    private final boolean required;

    private boolean popUpButtonIsVisible = true;;

    /**
     * Constructor.
     * @param id
     * @param label
     * @param anchorName
     * @param required
     */
    public SimpleLabelPanel(String id, String label, String anchorName, boolean required)
    {
        super(id);
        this.label = label;
        this.anchorName = anchorName;
        this.required = required;
    }

    public boolean isPopUpButtonIsVisible()
    {
        return popUpButtonIsVisible;
    }

    public void setPopUpButtonIsVisible(boolean popUpButtonIsVisible)
    {
        this.popUpButtonIsVisible = popUpButtonIsVisible;
    }

    @Override
    protected void onBeforeRender()
    {
        if (!initiated)
        {
            init();
            initiated = true;
        }
        super.onBeforeRender();
    }

    private void init()
    {
        this.add(new Label("label", new ResourceModel(label, getString(DEFAULT_RESOURCE_VALUE))).setEscapeModelStrings(false));
        this.add(new Label("requiredMark", "*").setVisible(required));
        HelpPopup popup = new HelpPopup("popup", anchorName, new HelpFileReader(anchorName).read());
        popup.setVisible(popUpButtonIsVisible);
        this.add(popup);
    }
}
