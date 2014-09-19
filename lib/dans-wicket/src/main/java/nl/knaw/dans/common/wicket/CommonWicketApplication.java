package nl.knaw.dans.common.wicket;

import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;

public abstract class CommonWicketApplication extends WebApplication {
    public CommonWicketApplication() {
        super();
    }

    public boolean isInDevelopmentMode() {
        return DEVELOPMENT.equalsIgnoreCase(getConfigurationType());
    }

    public static String getUserIpAddress() {
        RequestCycle r = RequestCycle.get();
        // there might not be a requestcycle
        if (r == null)
            return "";
        Request req = r.getRequest();
        // request might not be a webrequest
        if ((req == null) || !(req instanceof WebRequest))
            return "";
        // (hb:) this will not work when the request is redirected. In this case it returns 127.0.0.01.
        return ((WebRequest) req).getHttpServletRequest().getRemoteAddr();
    }

    public static String getProperty(String key) {
        return (String) new ResourceModel(key).getObject();
    }

}
