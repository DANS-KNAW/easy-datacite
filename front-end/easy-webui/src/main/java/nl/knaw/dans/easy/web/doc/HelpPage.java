package nl.knaw.dans.easy.web.doc;

import nl.knaw.dans.easy.web.common.HelpFileReader;
import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;

public class HelpPage extends AbstractEasyNavPage
{
    public HelpPage(final PageParameters parameters)
    {
        // TODO rather use popup, but XSL for EmdDoc needs a bookmarkable link.
        // TODO create unit tests for all help links for all disciplines
        final String name = parameters.getString("emd");
        final String content = new HelpFileReader(name).read();
        add(new Label("title", name));
        add(new Label("content", content).setEscapeModelStrings(false));
    }
}
