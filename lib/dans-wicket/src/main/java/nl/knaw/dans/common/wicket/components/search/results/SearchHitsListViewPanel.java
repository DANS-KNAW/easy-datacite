package nl.knaw.dans.common.wicket.components.search.results;

import java.util.List;

import nl.knaw.dans.common.lang.search.SearchHit;
import nl.knaw.dans.common.lang.search.SearchResult;
import nl.knaw.dans.common.wicket.components.search.model.SearchData;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;

import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchHitsListViewPanel extends Panel {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchHitsListViewPanel.class);
    private static final long serialVersionUID = 9103802920938076495L;

    private SearchResultConfig config;

    public SearchResultConfig getConfig() {
        return config;
    }

    public SearchHitsListViewPanel(String id, SearchModel searchModel, final SearchResultConfig config) {
        super(id, searchModel);
        this.config = config;

        // search hits
        AbstractReadOnlyModel searchHitsReadOnlyModel = createSearchHitsReadOnlyModel();
        ListView<Panel> searchHitsList = createSearchHitsList("searchHits", searchHitsReadOnlyModel);
        searchHitsList.setRenderBodyOnly(true);
        add(searchHitsList);
    }

    protected AbstractReadOnlyModel<List> createSearchHitsReadOnlyModel() {
        return new AbstractReadOnlyModel<List>() {
            private static final long serialVersionUID = -8467661423061481825L;

            @Override
            public List getObject() {
                return getSearchResult().getHits();
            }
        };
    }

    protected ListView<Panel> createSearchHitsList(String id, AbstractReadOnlyModel searchHits) {
        return new ListView<Panel>(id, searchHits) {
            private static final long serialVersionUID = -6597598635055541684L;

            @Override
            protected void populateItem(ListItem<Panel> item) {
                final SearchHit<?> hit = (SearchHit<?>) item.getModelObject();

                Panel hitPanel = getConfig().getHitPanelFactory().createHitPanel("searchHit", hit, getSearchModel());
                if (hitPanel == null) {
                    LOGGER.error("Could not create hit panel for searchHit " + hit.toString() + ". Programmer mistake.");
                    throw new InternalWebError();
                }
                hitPanel.setRenderBodyOnly(true);
                item.add(hitPanel);
            }

            @Override
            public boolean isVisible() {
                return getSearchResult().getHits().size() > 0;
            }
        };
    }

    // search basics, we would have gotten if we extended searchpanel
    public SearchModel getSearchModel() {
        return (SearchModel) getDefaultModel();
    }

    protected SearchData getSearchData() {
        return getSearchModel().getObject();
    }

    protected SearchResult<?> getSearchResult() {
        return getSearchData().getResult();
    }

}
