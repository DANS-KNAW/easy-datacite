package nl.knaw.dans.common.lang.search.simple;

import nl.knaw.dans.common.lang.search.SearchQuery;

public class SimpleSearchQuery implements SearchQuery {
    private static final long serialVersionUID = 2826106086319493869L;

    private String queryString;

    public SimpleSearchQuery(String queryString) {
        this.queryString = queryString;
    }

    public String getQueryString() {
        return queryString;
    }

    /**
     * Puts an OR operator between the values.
     * 
     * @param values
     * @return
     */
    public static String OrValues(Object... values) {
        if (values.length == 0)
            return "";

        String q = values[0].toString();
        if (values.length > 1) {
            q = "(";
            for (int i = 0; i < values.length; i++) {
                q += values[i].toString();
                if (i + 1 < values.length)
                    q += " OR ";
            }
            q += ")";
        }

        return q;
    }

}
