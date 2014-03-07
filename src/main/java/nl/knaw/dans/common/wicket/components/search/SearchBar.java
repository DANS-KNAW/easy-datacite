package nl.knaw.dans.common.wicket.components.search;

import nl.knaw.dans.common.wicket.components.search.model.SearchData;
import nl.knaw.dans.common.wicket.components.search.model.SearchRequestBuilder;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;

/**
 * A text input field with a search button that hopefully leads to a page with search results on it. The
 * search bar can be used in two ways: 1. It can be used for a new search. A result page then needs to be
 * supplied to the constructor of this component. A 'q' parameter will then be send to the page using the
 * PageParameters mechanism of Wicket. You are then responsible for reading this q parameter and creating
 * a TextSearchCriterium which can be send to a SearchResultPanel. 2. It can be used to more freely by
 * implementing the onSearch method. This is used by the SearchResultPanel to refine the search results
 * by adding a TextSearchCriterium to the existing criteria.
 * 
 * @see nl.knaw.dans.common.wicket.components.search.criteria.TextSearchCriterium
 * @author lobo
 */
public class SearchBar extends BaseSearchPanel
{
    private static final long serialVersionUID = -2867738048812631601L;

    private static final String SIMPLE_SEARCH_FORM = "simpleSearchForm";

    public static final String QUERY_PARAM = "q";

    public SearchBar(String wicketId, Class<? extends Page> resultPage)
    {
        super(wicketId);
        init(wicketId, resultPage);
    }

    public SearchBar(String wicketId)
    {
        super(wicketId);
        init(wicketId, null);
    }

    public void init(String wicketId, Class<? extends Page> resultPage)
    {
        add(new SearchBarForm(SIMPLE_SEARCH_FORM, resultPage));
    }

    protected void onSearch(String searchText)
    {
    }

    class SearchBarForm extends Form<SearchData>
    {
        private static final String SEARCH_STRING_ID = "searchString";
        private static final long serialVersionUID = -188845913357841972L;

        private Class<? extends Page> resultPage;
        private TextField<String> queryField;

        public SearchBarForm(String id, final Class<? extends Page> resultPage)
        {
            super(id);
            this.resultPage = resultPage;

            queryField = new TextField<String>(SEARCH_STRING_ID, new Model<String>()
            {
                private static final long serialVersionUID = 7278417820455893064L;

                public String getObject()
                {
                    // fill in the query field with the value that was last entered
                    String q = super.getObject();
                    if (StringUtils.isEmpty(q) && getPage().getClass().equals(resultPage) && getPage().getPageParameters() != null)
                    {
                        q = getPage().getPageParameters().getString(QUERY_PARAM);
                        setObject(q);
                    }

                    return q;
                };
            });
            queryField.setEscapeModelStrings(false);
            add(new SubmitLink("submitLink"));
            add(queryField);

        }

        public void onSubmit()
        {
            String searchText = queryField.getModelObject();
            if (StringUtils.isBlank(searchText))
                return;

            if (resultPage != null)
            {
                PageParameters params = new PageParameters();
                params.add(QUERY_PARAM, searchText);
                setResponsePage(resultPage, params);
            }

            onSearch(searchText);
        }

        private SearchRequestBuilder getRequestBuilder()
        {
            return getModelObject().getRequestBuilder();
        }

    }

}
