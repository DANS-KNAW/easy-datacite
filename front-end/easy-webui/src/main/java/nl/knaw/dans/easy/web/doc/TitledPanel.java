package nl.knaw.dans.easy.web.doc;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

public class TitledPanel extends Panel {
    private static final long serialVersionUID = -3055366689126773995L;

    public TitledPanel(final String wicketId, final String title, final String htmlContent) {
        super(wicketId);
        add(new Label("title", title).setEscapeModelStrings(false));
        add(new Label("content", htmlContent).setEscapeModelStrings(false));
    }
}
