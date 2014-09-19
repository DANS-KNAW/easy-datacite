package nl.knaw.dans.common.wicket.components;

import org.apache.wicket.Page;
import org.apache.wicket.Request;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;

public class CommonSession extends WebSession {
    private static final long serialVersionUID = -7886316129048405422L;

    private String currentPageURL;

    private Class<? extends Page> currentPageClass;

    private String lastVisitedPageURL;

    private Class<? extends Page> lastVisitedPageClass;

    public CommonSession(Request request) {
        super(request);
    }

    public static CommonSession get() {
        return (CommonSession) Session.get();
    }

    public String getLastVisitedPageURL() {
        return lastVisitedPageURL;
    }

    public Class<? extends Page> getLastVisitedPageClass() {
        return lastVisitedPageClass;
    }

    public Class<? extends Page> getCurrentPageClass() {
        return currentPageClass;
    }

    public String getCurrentPageURL() {
        return currentPageURL;
    }

    public void setCurrentPage(String URL, Class<? extends CommonPage> pageClass) {
        if (!pageClass.equals(currentPageClass)) {
            this.lastVisitedPageURL = this.currentPageURL;
            this.currentPageURL = URL;
            this.lastVisitedPageClass = currentPageClass;
            this.currentPageClass = pageClass;
        }
    }
}
