package nl.knaw.dans.easy.web.statistics;

import java.util.HashMap;

import nl.knaw.dans.common.wicket.components.search.model.SearchModel;

public class BrowseStatistics extends StatisticsModel<SearchModel>
{
    public BrowseStatistics(SearchModel sm)
    {
        super(sm);
    }

    @Override
    public HashMap<String, String> getLogValues()
    {
        HashMap<String, String> res = new HashMap<String, String>();
        
        return res;
    }

    @Override
    public String getName()
    {
        return "Browse";
    }

}
