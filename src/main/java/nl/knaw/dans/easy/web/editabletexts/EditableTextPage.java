package nl.knaw.dans.easy.web.editabletexts;

import org.apache.wicket.markup.html.basic.Label;

import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;

public class EditableTextPage extends AbstractEasyNavPage
{

    public EditableTextPage(final String contentPath, final Object... placeholders)
    {
        addCommonFeedbackPanel();
        add(new Label("filename", contentPath));
        add(new EasyEditablePanel("editablePanel", contentPath, placeholders));
    }
}
