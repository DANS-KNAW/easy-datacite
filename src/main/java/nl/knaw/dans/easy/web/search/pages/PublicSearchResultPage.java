package nl.knaw.dans.easy.web.search.pages;

import java.util.Iterator;
import java.util.List;

import nl.knaw.dans.common.lang.dataset.DatasetSB;
import nl.knaw.dans.common.lang.search.SearchRequest;
import nl.knaw.dans.common.lang.search.SearchResult;
import nl.knaw.dans.common.lang.search.SortOrder;
import nl.knaw.dans.common.lang.search.simple.SimpleSortField;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.components.search.criteria.CriteriumLabel;
import nl.knaw.dans.common.wicket.components.search.facets.FacetConfig;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;
import nl.knaw.dans.common.wicket.components.search.model.SearchRequestBuilder;
import nl.knaw.dans.easy.data.search.EasyDatasetSB;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.search.AbstractSearchResultPage;
import nl.knaw.dans.easy.web.statistics.SearchStatistics;
import nl.knaw.dans.easy.web.statistics.StatisticsEvent;
import nl.knaw.dans.easy.web.statistics.StatisticsLogger;

import org.apache.wicket.PageParameters;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

public class PublicSearchResultPage extends AbstractSearchResultPage
{
	public PublicSearchResultPage()
	{
		super(false);
	}
	
	public PublicSearchResultPage(SearchModel model)
	{
		super(model);
		model.getRequestBuilder().setFirstSortField(
                new SimpleSortField(EasyDatasetSB.DATE_CREATED_FIELD, SortOrder.DESC));
	}
	
	public PublicSearchResultPage(PageParameters pm)
	{
		super(pm);
	}
	
	@Override
	protected boolean showTips()
	{
		return true;
	}
	
	protected SearchResult<? extends DatasetSB> doSearch(SearchRequest request)
			throws ServiceException
	{
	    // logging for statistics
	    if(request.getFilterQueries().size() == 0)
	    {
	        StatisticsLogger.getInstance().logEvent(StatisticsEvent.SEARCH_TERM, new SearchStatistics(request));   
	    }
		return Services.getSearchService().searchPublished(request, getSessionUser());
	}

	@Override
	protected IModel<String> getInitialCriteriumText()
	{
		return new ResourceModel("publicsearch.defaultbreadcrumbtext");
	}

	@Override
	protected IModel<String> getSearchCriteriumText(final String searchText)
	{
		return new AbstractReadOnlyModel<String>()
		{
			private static final long	serialVersionUID	= 3254972701101566016L;

			@Override
			public String getObject()
			{
				return CriteriumLabel.createFilterText(getString("publicsearch.searchbreadcrumbtext"), searchText);
			}
		};
	}   
	
	@Override
	protected List<FacetConfig> getFacets()
	{
		// remove the state facet from the list 
		List<FacetConfig> facets = super.getFacets();
		Iterator<FacetConfig> facetIt = facets.iterator();
		while(facetIt.hasNext())
		{
		    FacetConfig facetConfig = facetIt.next();
			if (facetConfig.getFacetName().equals(DatasetSB.DS_STATE_FIELD))
			{
				facetIt.remove();
				break;
			}
		}
		return facets;
	}
}
