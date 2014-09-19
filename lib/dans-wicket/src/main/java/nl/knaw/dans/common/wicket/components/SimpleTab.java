package nl.knaw.dans.common.wicket.components;

import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.model.IModel;

public abstract class SimpleTab implements ITab {
    private static final long serialVersionUID = -6675426500437551596L;

    private IModel<String> title;

    private boolean visible = true;

    public SimpleTab(IModel<String> title) {
        this.title = title;
    }

    public void setTitle(IModel<String> title) {
        this.title = title;
    }

    @Override
    public IModel<String> getTitle() {
        return title;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }
}
