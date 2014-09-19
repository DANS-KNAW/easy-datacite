package nl.knaw.dans.common.wicket.components;

import org.apache.wicket.markup.html.WebMarkupContainer;

public class HiddenComponent extends WebMarkupContainer {
    private static final long serialVersionUID = -355698226200485108L;

    public HiddenComponent(String wicketId) {
        super(wicketId);
    }

    @Override
    protected boolean getStatelessHint() {
        return true;
    }

    @Override
    public boolean isVisible() {
        return false;
    }

}
