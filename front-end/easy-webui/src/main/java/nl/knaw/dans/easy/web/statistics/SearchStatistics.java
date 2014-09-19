package nl.knaw.dans.easy.web.statistics;

import java.util.HashMap;

import nl.knaw.dans.common.lang.search.SearchRequest;

public class SearchStatistics extends StatisticsModel<SearchRequest> {

    public SearchStatistics(SearchRequest request) {
        super(request);
    }

    @Override
    public HashMap<String, String> getLogValues() {
        HashMap<String, String> res = new HashMap<String, String>();
        res.put("QUERY", getObject().getQuery().getQueryString());
        return res;
    }

    @Override
    public String getName() {
        return "searchrequest";
    }

}
