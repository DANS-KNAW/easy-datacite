package nl.knaw.dans.common.lang.search.simple;

import nl.knaw.dans.common.lang.search.SearchQuery;

import org.apache.commons.lang.StringUtils;

public class EmptySearchQuery implements SearchQuery
{
    private static final long serialVersionUID = 5773833415962619342L;

    public static EmptySearchQuery instance = new EmptySearchQuery();

    public static EmptySearchQuery getInstance()
    {
        return instance;
    }

    public String getQueryString()
    {
        return StringUtils.EMPTY;
    }

}
