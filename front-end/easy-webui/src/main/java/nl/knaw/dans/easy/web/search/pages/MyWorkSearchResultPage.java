package nl.knaw.dans.easy.web.search.pages;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.dataset.DatasetSB;
import nl.knaw.dans.common.lang.search.SearchRequest;
import nl.knaw.dans.common.lang.search.SearchResult;
import nl.knaw.dans.common.lang.search.SortField;
import nl.knaw.dans.common.lang.search.SortOrder;
import nl.knaw.dans.common.lang.search.simple.SimpleSortField;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.components.search.criteria.CriteriumLabel;
import nl.knaw.dans.common.wicket.components.search.model.SearchCriterium;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;
import nl.knaw.dans.common.wicket.components.search.results.SearchResultConfig;
import nl.knaw.dans.easy.data.search.EasyDatasetSB;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.search.AbstractSearchResultPage;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

public class MyWorkSearchResultPage extends AbstractSearchResultPage
{
    public MyWorkSearchResultPage()
    {
        super(true);
    }

    public MyWorkSearchResultPage(SearchModel searchModel)
    {
        super(searchModel);
    }

    @Override
    protected SearchResult<? extends DatasetSB> doSearch(SearchRequest request) throws ServiceException
    {
        return Services.getSearchService().searchMyWork(request, getSessionUser());
    }

    @Override
    protected SearchResultConfig getSearchResultConfig()
    {
        SearchResultConfig config = super.getSearchResultConfig();
        List<SortField> initialSortFields = new ArrayList<SortField>();
        initialSortFields.add(new SimpleSortField(EasyDatasetSB.DATE_SUBMITTED_FIELD, SortOrder.DESC));
        config.setInitialSortFields(initialSortFields);
        return config;
    }

    @Override
    protected IModel<String> getInitialCriteriumText()
    {
        return new ResourceModel("mywork.defaultbreadcrumbtext");
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
                return CriteriumLabel.createFilterText(getString("mywork.searchbreadcrumbtext"), searchText);
            }
        };
    }

}
