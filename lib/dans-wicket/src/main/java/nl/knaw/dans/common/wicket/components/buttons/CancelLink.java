package nl.knaw.dans.common.wicket.components.buttons;

import nl.knaw.dans.common.wicket.WicketUtil;

import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.Link;

public class CancelLink extends Link<Void> {
    private static final long serialVersionUID = -6430995496157454477L;

    private Class<? extends Page> alternativePage;

    public CancelLink(String id) {
        super(id);
        this.alternativePage = Application.get().getHomePage();
    }

    public CancelLink(String id, Class<? extends Page> alternativePage) {
        super(id);
        this.alternativePage = alternativePage;
    }

    @Override
    public void onClick() {
        if (!WicketUtil.redirectToLastVisitedPage()) {
            setResponsePage(alternativePage);
        }
    }

}
