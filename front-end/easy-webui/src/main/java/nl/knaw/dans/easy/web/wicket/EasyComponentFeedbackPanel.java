package nl.knaw.dans.easy.web.wicket;

import org.apache.wicket.Component;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

public class EasyComponentFeedbackPanel extends FeedbackPanel {

    private static final long serialVersionUID = 3940824181325943838L;

    public EasyComponentFeedbackPanel(final String wicketId, final Component component) {
        super(wicketId, new ComponentFeedbackMessageFilter(component));
        setOutputMarkupId(true);
    }

    @Override
    public boolean isVisible() {
        return anyMessage();
    }

}
