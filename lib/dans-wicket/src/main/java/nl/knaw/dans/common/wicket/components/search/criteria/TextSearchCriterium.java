package nl.knaw.dans.common.wicket.components.search.criteria;

import nl.knaw.dans.common.lang.search.simple.SimpleSearchQuery;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.model.IModel;

/**
 * A simple search criterium based on a text based search query.
 * 
 * @author lobo
 */
public class TextSearchCriterium extends AbstractSearchCriterium {
    private static final long serialVersionUID = 8932075173626854009L;

    private final String searchText;

    public TextSearchCriterium(String searchText, IModel<String> labelModel) {
        super(labelModel);
        this.searchText = searchText;
    }

    @Override
    public void apply(SimpleSearchRequest sr) {
        String q = sr.getQuery().getQueryString();
        if (StringUtils.isEmpty(q))
            q = searchText;
        else
            q = q + " " + searchText;
        sr.setQuery(new SimpleSearchQuery(q));
    }

    public String getSearchText() {
        return searchText;
    }

}
