package nl.knaw.dans.easy.web.wicket;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

public class SecureEasyPageLink extends BookmarkablePageLink {

    private static final long serialVersionUID = 3077012041469331285L;

    private final Class<? extends WebPage> target;

    @SuppressWarnings("unchecked")
    public SecureEasyPageLink(String id, Class<? extends WebPage> c) {
        super(id, c);
        this.target = c;
    }

    public Class<? extends WebPage> getTarget() {
        return target;
    }

}
