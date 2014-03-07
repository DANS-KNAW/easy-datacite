package nl.knaw.dans.common.wicket.components.editablepanel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

@SuppressWarnings("serial")
public class ViewPanel extends Panel
{
    public ViewPanel(final String id, final IModel<String> model)
    {
        super(id);
        final Label text = new Label("text", model);
        text.setEscapeModelStrings(false);
        add(text);
    }
}
