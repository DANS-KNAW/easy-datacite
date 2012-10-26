package nl.knaw.dans.easy.web.search.pages;

import nl.knaw.dans.common.lang.dataset.DatasetSB;
import nl.knaw.dans.common.lang.search.SearchRequest;
import nl.knaw.dans.common.lang.search.SearchResult;
import nl.knaw.dans.common.lang.search.SortOrder;
import nl.knaw.dans.common.lang.search.simple.SimpleSortField;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.components.search.criteria.CriteriumLabel;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;
import nl.knaw.dans.easy.data.search.EasyDatasetSB;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.search.AbstractSearchResultPage;

import org.apache.wicket.PageParameters;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

public class SearchAllSearchResultPage extends AbstractSearchResultPage
{
    public SearchAllSearchResultPage()
    {
        super(true);
    }

    public SearchAllSearchResultPage(PageParameters pm)
    {
        super(pm);
    }

    public SearchAllSearchResultPage(SearchModel model)
    {
        super(model);
        model.getRequestBuilder().setFirstSortField(new SimpleSortField(EasyDatasetSB.DATE_CREATED_FIELD, SortOrder.DESC));
    }

    protected SearchResult<? extends DatasetSB> doSearch(SearchRequest request) throws ServiceException
    {
        return Services.getSearchService().searchAll(request, getSessionUser());
    }

    @Override
    protected IModel<String> getInitialCriteriumText()
    {
        return new ResourceModel("searchall.defaultbreadcrumbtext");
    }

    @Override
    protected IModel<String> getSearchCriteriumText(final String searchText)
    {
        return new AbstractReadOnlyModel<String>()
        {
            private static final long serialVersionUID = 3254972701101566016L;

            @Override
            public String getObject()
            {
                return CriteriumLabel.createFilterText(getString("searchall.searchbreadcrumbtext"), searchText);
            }
        };
    }
}
