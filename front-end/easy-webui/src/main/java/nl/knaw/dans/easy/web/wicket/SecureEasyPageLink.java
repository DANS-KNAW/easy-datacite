package nl.knaw.dans.easy.web.wicket;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

public class SecureEasyPageLink extends BookmarkablePageLink<WebPage> {

    private static final long serialVersionUID = 3077012041469331285L;

    private final Class<? extends WebPage> target;

    public SecureEasyPageLink(String id, Class<? extends WebPage> pageClassc) {
        super(id, pageClassc);
        this.target = pageClassc;
    }

    public SecureEasyPageLink(String id, Class<? extends WebPage> pageClass, PageParameters pageParams) {
        super(id, pageClass, pageParams);
        this.target = pageClass;
    }

    public Class<? extends WebPage> getTarget() {
        return target;
    }
}
