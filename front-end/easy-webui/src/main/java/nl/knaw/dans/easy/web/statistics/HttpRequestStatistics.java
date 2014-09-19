package nl.knaw.dans.easy.web.statistics;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

public class HttpRequestStatistics extends StatisticsModel<HttpServletRequest> {

    public HttpRequestStatistics(HttpServletRequest request) {
        super(request);
    }

    @Override
    public HashMap<String, String> getLogValues() {
        String ref_url = this.getObject().getHeader("Referer");
        HashMap<String, String> res = new HashMap<String, String>();
        if (ref_url != null) {
            res.put("REFERRER_URL", ref_url);
        }
        String request_url = this.getObject().getRequestURL().toString();
        if (request_url != null) {
            res.put("REQUEST_URL", request_url);
        }
        return res;
    }

    @Override
    public String getName() {
        return "HttpRequest";
    }

}
