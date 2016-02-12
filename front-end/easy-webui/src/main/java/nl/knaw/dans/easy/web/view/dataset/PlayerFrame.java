package nl.knaw.dans.easy.web.view.dataset;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;

public class PlayerFrame extends WebComponent {
    private String url;

    public PlayerFrame(String id, String url) {
        super(id);
        this.url = url;
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        checkComponentTag(tag, "iframe");
        tag.put("src", url);
    }
}
