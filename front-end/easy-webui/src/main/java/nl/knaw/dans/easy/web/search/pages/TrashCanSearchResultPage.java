package nl.knaw.dans.easy.web.search.pages;

import nl.knaw.dans.common.lang.dataset.DatasetSB;
import nl.knaw.dans.common.lang.search.SearchRequest;
import nl.knaw.dans.common.lang.search.SearchResult;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.components.search.criteria.CriteriumLabel;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.search.AbstractSearchResultPage;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

public class TrashCanSearchResultPage extends AbstractSearchResultPage
{
    public TrashCanSearchResultPage()
    {
        super(true);
    }

    public TrashCanSearchResultPage(SearchModel searchModel)
    {
        super(searchModel);
    }

    @Override
    protected SearchResult<? extends DatasetSB> doSearch(SearchRequest request) throws ServiceException
    {
        return Services.getSearchService().searchTrashcan(request, getSessionUser());
    }

    @Override
    protected IModel<String> getInitialCriteriumText()
    {
        return new ResourceModel("trashcan.defaultbreadcrumbtext");
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
                return CriteriumLabel.createFilterText(getString("trashcan.searchbreadcrumbtext"), searchText);
            }
        };
    }

}
