package nl.knaw.dans.common.wicket.components.editablepanel;

import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import wicket.contrib.tinymce.TinyMceBehavior;
import wicket.contrib.tinymce.settings.TinyMCESettings;

@SuppressWarnings("serial")
public class EditPanel extends Panel
{
    public EditPanel(String id, IModel<String> model, TinyMCESettings settings)
    {
        super(id, model);
        final TextArea<String> textArea = new TextArea<String>("text", model);

        if (settings != null)
        {
            textArea.add(new TinyMceBehavior(settings));
        }

        textArea.setEscapeModelStrings(false);
        add(textArea);
    }
}
