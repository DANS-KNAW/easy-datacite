package nl.knaw.dans.easy.web.search;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;

import nl.knaw.dans.common.lang.search.SortOrder;
import nl.knaw.dans.common.lang.search.simple.SimpleSortField;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;
import nl.knaw.dans.common.wicket.components.search.results.SearchResultConfig;
import nl.knaw.dans.common.wicket.components.search.results.SearchResultPanel;
import nl.knaw.dans.easy.data.search.EasyDatasetSB;
import nl.knaw.dans.easy.web.search.pages.BrowsePage;

public abstract class HomeSearchResultPanel extends SearchResultPanel {
    private static final long serialVersionUID = 1L;

    /**
     * Initialize the search result panel with an existing search model.
     */
    public HomeSearchResultPanel(String wicketId, SearchModel model, SearchResultConfig config) {
        super(wicketId, model, config);
        model.getRequestBuilder().setFirstSortField(new SimpleSortField(EasyDatasetSB.DATE_PUBLISHED_FIELD, SortOrder.DESC));
    }

    @Override
    protected void initComponents() {
        // search hits
        AbstractReadOnlyModel searchHitsReadOnlyModel = createSearchHitsReadOnlyModel();
        ListView<Panel> searchHitsList = createSearchHitsList("searchHits", searchHitsReadOnlyModel);
        searchHitsList.setRenderBodyOnly(true);
        add(searchHitsList);
        add(new BookmarkablePageLink("more", BrowsePage.class));
    }
}
