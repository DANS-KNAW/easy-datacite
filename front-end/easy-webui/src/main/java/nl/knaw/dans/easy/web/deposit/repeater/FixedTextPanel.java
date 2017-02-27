package nl.knaw.dans.easy.web.deposit.repeater;

import nl.knaw.dans.pf.language.emd.EasyMetadata;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

public class FixedTextPanel extends AbstractCustomPanel {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor that takes a model with a ListWrapper&lt;T> as model object.
     * 
     * @param wicketId
     *        id of this panel
     */
    public FixedTextPanel(String wicketId) {
        super(wicketId);
    }

    /**
     * Constructor that takes a model with a ListWrapper&lt;T> as model object.
     * 
     * @param wicketId
     *        id of this panel
     * @param model
     *        is ignored, using the label as fixed content
     */
    public FixedTextPanel(String wicketId, IModel<EasyMetadata> model) {
        super(wicketId, model);
    }

    @Override
    protected void init() {
        super.init();
        // don't know how to just disable "label:popup" component
        // would need to copy AbstractCustomPanel.html to FixedTextPanel.html too to make the first column wider
        // so we disable the label and abuse its value as content
        get("label").setVisible(false);
    }

    @Override
    protected Panel getCustomComponentPanel() {
        return new CustomPanel();
    }

    class CustomPanel extends Panel {

        private static final long serialVersionUID = 1L;

        CustomPanel() {
            super(CUSTOM_PANEL_ID);
            Label label = new Label("content", new ResourceModel(labelResourceKey, "need property " + labelResourceKey));
            add(label);
        }
    }
}
