package nl.knaw.dans.easy.web.search;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.dataset.DatasetSB;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.search.SearchRequest;
import nl.knaw.dans.common.lang.search.SearchResult;
import nl.knaw.dans.common.lang.search.SortOrder;
import nl.knaw.dans.common.lang.search.SortType;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchRequest;
import nl.knaw.dans.common.lang.search.simple.SimpleSortField;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.components.search.FieldNameResourceTranslator;
import nl.knaw.dans.common.wicket.components.search.FieldValueResourceTranslator;
import nl.knaw.dans.common.wicket.components.search.SearchBar;
import nl.knaw.dans.common.wicket.components.search.criteria.CriteriumLabel;
import nl.knaw.dans.common.wicket.components.search.criteria.InitialSearchCriterium;
import nl.knaw.dans.common.wicket.components.search.criteria.TextSearchCriterium;
import nl.knaw.dans.common.wicket.components.search.facets.FacetConfig;
import nl.knaw.dans.common.wicket.components.search.model.SearchCriterium;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;
import nl.knaw.dans.common.wicket.components.search.model.SearchRequestBuilder;
import nl.knaw.dans.common.wicket.components.search.results.SearchResultConfig;
import nl.knaw.dans.common.wicket.components.search.results.SortLinkConfig;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.data.search.EasyDatasetSB;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.search.RecursiveListCache;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.authn.login.LoginPage;
import nl.knaw.dans.easy.web.search.custom.RecursiveListTranslator;
import nl.knaw.dans.easy.web.search.custom.RecursiveListValueCollapser;
import nl.knaw.dans.easy.web.search.pages.AdvSearchPage;
import nl.knaw.dans.easy.web.search.pages.BrowsePage;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlling class for performing searches and viewing the results.
 * 
 * @author lobo
 */
public abstract class AbstractSearchResultPage extends AbstractSearchPage
{
    /*--------------------------------------------------------
     * --- PROTECTED METHODS TO OVERRIDE OR IMPLEMENT --------
     *-------------------------------------------------------*/

    protected abstract IModel<String> getInitialCriteriumText();

    protected IModel<String> getSearchCriteriumText(String searchText)
    {
        return new Model<String>(CriteriumLabel.createFilterText("Search", searchText));
    }

    protected boolean showTips()
    {
        return false;
    }

    /**
     * Implement search here
     * 
     * @throws ServiceException
     */
    protected abstract SearchResult<? extends DatasetSB> doSearch(SearchRequest request) throws ServiceException;

    /*--------------------------------------------------------
     * ------------------- CLASS INTERNALS -------------------
     *-------------------------------------------------------*/

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSearchResultPage.class);

    private static final long serialVersionUID = 8501036308620025068L;

    private static final String SEARCHRESULT_PANEL = "searchResultPanel";

    public AbstractSearchResultPage(boolean needAuthentication)
    {
        super();
        if (needAuthentication && !getEasySession().isAuthenticated())
        {
            /* might be a link from a notification */
            redirectToInterceptPage(new LoginPage());
            return;
        }
        init(null);
    }

    public AbstractSearchResultPage(SearchModel model)
    {
        super(model);
        init(null);
    }

    public AbstractSearchResultPage(final PageParameters parameters)
    {
        super(parameters);
        String queryString = parameters.getString(SearchBar.QUERY_PARAM);
        if (getDefaultModel() == null)
            init(queryString);
    }

    protected void init(final String searchText)
    {
        add(new Label("headerLabel", getInitialCriteriumText()));

        if (getSearchModel() == null)
        {
            SearchCriterium criterium;
            if (!StringUtils.isBlank(searchText))
                criterium = new TextSearchCriterium(searchText, getSearchCriteriumText(searchText));
            else
                criterium = new InitialSearchCriterium(getInitialCriteriumText());
            setSearchModel(new SearchModel(criterium));
        }

        EasySearchResultPanel panel = new EasySearchResultPanel(SEARCHRESULT_PANEL, getSearchModel(), showTips(), getSearchResultConfig())
        {
            private static final long serialVersionUID = 4389340592804783670L;

            @Override
            public SearchResult<?> search(SimpleSearchRequest request)
            {
                try
                {
                    return AbstractSearchResultPage.this.doSearch(request);
                }
                catch (ServiceException e)
                {
                    String msg = errorMessage(SEARCH_FAILURE);
                    LOGGER.error(msg, e);
                    throw new InternalWebError();
                }
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void onBrowseMoreClicked(SearchModel searchModel)
            {
                setResponsePage(new BrowsePage(searchModel, (Class<? extends AbstractSearchResultPage>) getPage().getClass()));
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void onAdvancedSearchClicked(SearchModel searchModel)
            {
                setResponsePage(new AdvSearchPage(searchModel, (Class<? extends AbstractSearchResultPage>) getPage().getClass()));
            }
        };
        add(panel);
    }

    protected SearchResultConfig getSearchResultConfig()
    {
        SearchResultConfig config = new SearchResultConfig();
        config.setResultCount(10);
        config.setShowBrowseMore(false);
        config.setShowAdvancedSearch(true);
        config.setHitPanelFactory(EasySearchHitPanelFactory.getInstance());
        config.setSortLinks(getSortLinks());
        config.setRefineFacets(getFacets());

        return config;
    }

    protected List<FacetConfig> getFacets()
    {
        FacetConfig facetConfig;
        ArrayList<FacetConfig> refineFacets = new ArrayList<FacetConfig>();

        facetConfig = new FacetConfig(EasyDatasetSB.AUDIENCE_FIELD);
        facetConfig.setOrder(FacetConfig.Order.BY_ALPHABET);
        facetConfig.setShowParentFacet(true);
        facetConfig.setFacetNameTranslator(new FieldNameResourceTranslator());
        facetConfig.setFacetValueTranslator(new DisciplineTranslator());
        facetConfig.setFacetValueCollapser(new DisciplineFacetValueCollapser(true));
        refineFacets.add(facetConfig);

        facetConfig = new FacetConfig(EasyDatasetSB.EASY_COLLECTIONS_FIELD);
        facetConfig.setOrder(FacetConfig.Order.BY_ALPHABET);
        facetConfig.setShowParentFacet(true);
        facetConfig.setFacetNameTranslator(new FieldNameResourceTranslator());
        facetConfig.setFacetValueTranslator(new RecursiveListTranslator(RecursiveListCache.LID_EASY_COLLECTIONS));
        facetConfig.setFacetValueCollapser(new RecursiveListValueCollapser(RecursiveListCache.LID_EASY_COLLECTIONS, true));
        refineFacets.add(facetConfig);

        facetConfig = new FacetConfig(EasyDatasetSB.DS_ACCESSCATEGORY_FIELD);
        facetConfig.setOrder(FacetConfig.Order.BY_COUNT);
        facetConfig.setFacetNameTranslator(new FieldNameResourceTranslator());
        facetConfig.setFacetValueTranslator(new FieldValueResourceTranslator());
        refineFacets.add(facetConfig);

        facetConfig = new FacetConfig(EasyDatasetSB.DS_STATE_FIELD);
        facetConfig.setOrder(FacetConfig.Order.BY_COUNT);
        facetConfig.setFacetNameTranslator(new FieldNameResourceTranslator());
        facetConfig.setFacetValueTranslator(new FieldValueResourceTranslator());
        refineFacets.add(facetConfig);

        return refineFacets;
    }

    protected List<SortLinkConfig> getSortLinks()
    {
        List<SortLinkConfig> sortLinks = new ArrayList<SortLinkConfig>();
        sortLinks.add(new SortLinkConfig("relevance", SortType.BY_RELEVANCE_SCORE, SortOrder.DESC));
        sortLinks.add(new SortLinkConfig(DatasetSB.DC_TITLE_SORTFIELD, SortType.BY_VALUE));
        sortLinks.add(new SortLinkConfig(DatasetSB.DC_CREATOR_SORTFIELD, SortType.BY_VALUE));
        sortLinks.add(new SortLinkConfig(EasyDatasetSB.DATE_CREATED_FIELD, SortType.BY_VALUE, SortOrder.DESC));
        sortLinks.add(new SortLinkConfig(EasyDatasetSB.DS_ACCESSCATEGORY_FIELD, SortType.BY_VALUE));
        sortLinks.add(new SortLinkConfig(EasyDatasetSB.DATE_SUBMITTED_FIELD, SortType.BY_VALUE, SortOrder.DESC));
        sortLinks.add(new StateSortLinkConfig(EasyDatasetSB.DATE_DRAFT_SAVED_FIELD, SortType.BY_VALUE, SortOrder.DESC, DatasetState.DRAFT));
        sortLinks.add(new StateSortLinkConfig(EasyDatasetSB.DATE_PUBLISHED_FIELD, SortType.BY_VALUE, SortOrder.DESC, DatasetState.PUBLISHED,
                DatasetState.MAINTENANCE));
        sortLinks.add(new StateSortLinkConfig(EasyDatasetSB.DS_STATE_FIELD, SortType.BY_VALUE));
        if (EasySession.get().getUser().hasRole(Role.ARCHIVIST))
        {
            sortLinks.add(new SortLinkConfig(EasyDatasetSB.DEPOSITOR_ID_FIELD, SortType.BY_VALUE));
            sortLinks.add(new SortLinkConfig(EasyDatasetSB.ASSIGNEE_ID_FIELD, SortType.BY_VALUE));
        }
        return sortLinks;
    }

    @Override
    public String getPageTitlePostfix()
    {
        return getInitialCriteriumText().getObject();
    }

    @Override
    public void refresh()
    {
        SearchModel model = getSearchModel();
        if (model != null)
            model.getObject().setDirty(true);
    }

    protected void setSorting(SearchRequestBuilder builder)
    {
        builder.setFirstSortField(new SimpleSortField(EasyDatasetSB.DATE_DRAFT_SAVED_FIELD, SortOrder.DESC));
    }

}
