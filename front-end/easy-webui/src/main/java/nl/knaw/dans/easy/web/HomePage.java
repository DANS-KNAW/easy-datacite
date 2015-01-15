package nl.knaw.dans.easy.web;

import nl.knaw.dans.common.lang.dataset.DatasetSB;
import nl.knaw.dans.common.lang.search.SearchRequest;
import nl.knaw.dans.common.lang.search.SearchResult;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchRequest;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;
import nl.knaw.dans.common.wicket.components.search.results.SearchResultConfig;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.web.deposit.DepositIntroPage;
import nl.knaw.dans.easy.web.search.AbstractSearchPage;
import nl.knaw.dans.easy.web.search.AbstractSearchResultPage;
import nl.knaw.dans.easy.web.search.HomeSearchHitPanelFactory;
import nl.knaw.dans.easy.web.search.HomeSearchResultPanel;
import nl.knaw.dans.easy.web.statistics.StatisticsEvent;
import nl.knaw.dans.easy.web.statistics.StatisticsLogger;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Easy web application home page.
 */
public class HomePage extends AbstractSearchPage {
    private static final String DEPOSIT = "navDeposit";
    private static final String SEARCHRESULT_PANEL = "homeSearchResultPanel";
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSearchResultPage.class);

    public HomePage() {
        super();
        init();
    }

    protected void init() {
        addCommonFeedbackPanel();
        add(new BookmarkablePageLink<DepositIntroPage>(DEPOSIT, DepositIntroPage.class));
        StatisticsLogger.getInstance().logEvent(StatisticsEvent.START_PAGE_VISIT);

        setSearchModel(new SearchModel());
        add(createHomeSearchResultPanel(SEARCHRESULT_PANEL));
    }

    protected HomeSearchResultPanel createHomeSearchResultPanel(String id) {
        return new HomeSearchResultPanel(id, getSearchModel(), getSearchResultConfig()) {
            private static final long serialVersionUID = 1L;

            @Override
            public SearchResult<?> search(SimpleSearchRequest request) {
                try {
                    return HomePage.this.doSearch(request);
                }
                catch (ServiceException e) {
                    String msg = errorMessage(SEARCH_FAILURE);
                    LOGGER.error(msg, e);
                    throw new InternalWebError();
                }
            }
        };
    }

    protected SearchResult<? extends DatasetSB> doSearch(SearchRequest request) throws ServiceException {
        return searchService.searchPublished(request, getSessionUser());
    }

    protected SearchResultConfig getSearchResultConfig() {
        SearchResultConfig config = new SearchResultConfig();
        config.setResultCount(5);
        config.setShowBrowseMore(false);
        config.setShowAdvancedSearch(false);
        config.setHitPanelFactory(HomeSearchHitPanelFactory.getInstance());
        return config;
    }
}
