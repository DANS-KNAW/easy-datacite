package nl.knaw.dans.common.wicket.components.search;

import nl.knaw.dans.common.lang.search.SearchResult;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchRequest;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;

/**
 * This panel not only uses the SearchModel, but also updates it with every page
 * refresh if it has become dirty. It uses an abstract method for this that needs
 * to be implemented by the user of this component.
 *
 * @author lobo
 */
public abstract class SearchPanel extends BaseSearchPanel
{
    private static final long serialVersionUID = 229687220394997707L;

    /**
     * This method is called whenever the search model's search results need to be
     * updated (when the search model is dirty). This may happen  for example when a 
     * sort method has been selected, a new result page is selected  
     * or when a facet has been clicked. The search can easily be implemented using 
     * the DANS Commons search implementation, but one is of course free to use 
     * any method he/she finds suitable.
     * 
     * @param request
     *        the request
     * @return the result of the search engine
     */
    public abstract SearchResult<?> search(SimpleSearchRequest request);

    public SearchPanel(String wicketId)
    {
        super(wicketId);
    }

    public SearchPanel(String wicketId, SearchModel searchModel)
    {
        super(wicketId, searchModel);
    }

    @Override
    protected void onBeforeRender()
    {
        // update search results if needed with every page refresh
        if (getSearchData().isDirty())
        {
            doSearch();
        }

        super.onBeforeRender();
    }

    protected void doSearch()
    {
        SimpleSearchRequest request = prepareSearchRequest(getRequestBuilder().getRequest());
        getSearchData().setResult(search(request));
    }

    protected SimpleSearchRequest prepareSearchRequest(SimpleSearchRequest request)
    {
        return request;
    }

}
