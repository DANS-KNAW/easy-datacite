package nl.knaw.dans.easy.web.search.pages;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.search.SearchResult;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchRequest;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.components.search.FieldNameResourceTranslator;
import nl.knaw.dans.common.wicket.components.search.FieldValueResourceTranslator;
import nl.knaw.dans.common.wicket.components.search.browse.BrowseConfig;
import nl.knaw.dans.common.wicket.components.search.browse.BrowsePanel;
import nl.knaw.dans.common.wicket.components.search.criteria.InitialSearchCriterium;
import nl.knaw.dans.common.wicket.components.search.facets.FacetConfig;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.data.search.EasyDatasetSB;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.search.AbstractSearchPage;
import nl.knaw.dans.easy.web.search.AbstractSearchResultPage;
import nl.knaw.dans.easy.web.search.DisciplineFacetValueCollapser;
import nl.knaw.dans.easy.web.search.DisciplineTranslator;
import nl.knaw.dans.easy.web.template.Style;

import org.apache.wicket.model.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrowsePage extends AbstractSearchPage {
    private static final Logger LOGGER = LoggerFactory.getLogger(BrowsePage.class);

    private Class<? extends AbstractSearchResultPage> resultPage;

    public BrowsePage() {
        this.resultPage = isArchivistOrAdmin() ? SearchAllSearchResultPage.class : PublicSearchResultPage.class;

        ResourceModel clabelModel;
        if (resultPage.equals(SearchAllSearchResultPage.class))
            clabelModel = new ResourceModel("searchall.defaultbreadcrumbtext");
        else
            clabelModel = new ResourceModel("publicsearch.defaultbreadcrumbtext");
        setSearchModel(new SearchModel(new InitialSearchCriterium(clabelModel)));

        // show resultPage immediately
        setResponsePage(AbstractSearchResultPage.instantiate(resultPage, getSearchModel()));
    }

    public BrowsePage(SearchModel model, Class<? extends AbstractSearchResultPage> resultPage) {
        super(model);
        this.resultPage = resultPage;
        init();
    }

    private void init() {
        addCommonFeedbackPanel();

        add(new BrowsePanel("browsePanel", getSearchModel(), getBrowseConfig()) {
            private static final long serialVersionUID = 6303400676242763157L;

            @Override
            public SearchResult<?> search(SimpleSearchRequest request) {
                try {
                    if (resultPage.equals(SearchAllSearchResultPage.class))
                        return Services.getSearchService().searchAll(request, getSessionUser());
                    else
                        return Services.getSearchService().searchPublished(request, getSessionUser());
                }
                catch (ServiceException e) {
                    String msg = errorMessage(EasyResources.BROWSE_SEARCH_FAILURE);
                    LOGGER.error(msg, e);
                    throw new InternalWebError();
                }
            }

            @Override
            public void onShowButtonClicked(SearchModel model) {
                setResponsePage(AbstractSearchResultPage.instantiate(resultPage, getSearchModel()));
            }
        });
    }

    protected BrowseConfig getBrowseConfig() {
        List<FacetConfig> facetConfigs = new ArrayList<FacetConfig>();

        FacetConfig facetConfig;

        facetConfig = new FacetConfig(EasyDatasetSB.AUDIENCE_FIELD);
        facetConfig.setOrder(FacetConfig.Order.BY_ALPHABET);
        facetConfig.setFacetNameTranslator(new FieldNameResourceTranslator());
        facetConfig.setFacetValueTranslator(new DisciplineTranslator());
        facetConfig.setFacetValueCollapser(new DisciplineFacetValueCollapser(true));
        facetConfig.setColumnCount(1);
        facetConfigs.add(facetConfig);

        facetConfig = new FacetConfig(EasyDatasetSB.DS_ACCESSCATEGORY_FIELD);
        facetConfig.setOrder(FacetConfig.Order.BY_COUNT);
        facetConfig.setFacetNameTranslator(new FieldNameResourceTranslator());
        facetConfig.setFacetValueTranslator(new FieldValueResourceTranslator());
        facetConfigs.add(facetConfig);

        if (resultPage.equals(SearchAllSearchResultPage.class)) {
            facetConfig = new FacetConfig(EasyDatasetSB.DS_STATE_FIELD);
            facetConfig.setOrder(FacetConfig.Order.BY_COUNT);
            facetConfig.setFacetNameTranslator(new FieldNameResourceTranslator());
            facetConfig.setFacetValueTranslator(new FieldValueResourceTranslator());
            facetConfigs.add(facetConfig);
        }

        return new BrowseConfig(facetConfigs);
    }

}
