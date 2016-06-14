package nl.knaw.dans.common.wicket.components.search.results;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.search.SearchHit;
import nl.knaw.dans.common.lang.search.SearchResult;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchRequest;
import nl.knaw.dans.common.wicket.WicketUtil;
import nl.knaw.dans.common.wicket.components.SimpleTab;
import nl.knaw.dans.common.wicket.components.popup.HelpPopup;
import nl.knaw.dans.common.wicket.components.search.SearchBar;
import nl.knaw.dans.common.wicket.components.search.SearchPanel;
import nl.knaw.dans.common.wicket.components.search.criteria.CriteriumLabel;
import nl.knaw.dans.common.wicket.components.search.criteria.SearchCriteriaPanel;
import nl.knaw.dans.common.wicket.components.search.criteria.TextSearchCriterium;
import nl.knaw.dans.common.wicket.components.search.facets.FacetConfig;
import nl.knaw.dans.common.wicket.components.search.facets.FacetPanel;
import nl.knaw.dans.common.wicket.components.search.facets.FacetStrategy;
import nl.knaw.dans.common.wicket.components.search.model.SearchCriterium;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;
import nl.knaw.dans.common.wicket.components.search.model.SearchRequestBuilder;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.list.Loop.LoopItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This component shows an overview of search result and is able to paginate, sort, refine the search and use facets. It is configured through the
 * SearchResultConfig object. This component uses the SearchModel which contains the search request builder and the search results. If you pass this component
 * an existing SearchModel from another component, like the BrowsePanel, then it will continue where that component left off. This panel has two empty methods
 * that you might want to implement: - onAdvancedSearchClicked - onBrowseMoreClicked These methods can be used as hooks to implementations of a browse panel or
 * an advanced search form. Simply by passing the SearchModel around you can integrate this component with others that work on the SearchModel. In the
 * SearchResultConfig you will find option for making these hooks visible.
 * 
 * @see SearchModel
 * @see SearchResultConfig
 * @author lobo
 */
public abstract class SearchResultPanel extends SearchPanel {
    @Override
    public SearchResult<?> search(SimpleSearchRequest request) {
        return null;
    }

    @Override
    public boolean isVisible() {
        return getSearchResult().getHits().size() > 0;
    }

    protected void onAdvancedSearchClicked(SearchModel searchModel) {}

    protected void onBrowseMoreClicked(SearchModel searchModel) {}

    private static final long serialVersionUID = 2958372083781711450L;
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchResultPanel.class);

    private SearchResultConfig config;

    private TabbedPanel tabbedPanel;

    protected int storedOffset = -1;
    protected int storedLimit = -1;
    protected boolean pagingStored = false;

    /**
     * Initialize the search result panel with an empty search model.
     */
    public SearchResultPanel(final String wicketId, final SearchResultConfig config) {
        this(wicketId, new SearchModel(), config);
    }

    /**
     * Initialize the search result panel with an initial search criterium. It is impossible for the user to remove this initial criterium.
     */
    public SearchResultPanel(final String wicketId, final SearchCriterium searchCriterium, final SearchResultConfig config) {
        this(wicketId, new SearchModel(searchCriterium), config);
    }

    /**
     * Initialize the search result panel with an existing search model.
     */
    public SearchResultPanel(final String wicketId, final SearchModel model, final SearchResultConfig config) {
        super(wicketId, model);
        this.config = config;
        init();
    }

    private void initRequestBuilder(SearchRequestBuilder requestBuilder) {
        requestBuilder.setFacets(getConfig().getRefineFacets());
        requestBuilder.setLimit(getConfig().getResultCount());
        getRequestBuilder().setSortFields(getConfig().getInitialSortFields());
    }

    private void init() {
        initRequestBuilder(getRequestBuilder());
        doSearch();
        initComponents();
    }

    private AbstractTab getListViewTab() {
        return new AbstractTab(new Model<String>("<i class='fa fa-bars' aria-hidden='true'></i> List")) {
            private static final long serialVersionUID = 1L;

            @Override
            public Panel getPanel(final String panelId) {
                return new SearchHitsListViewPanel(panelId, getSearchModel(), getConfig());
            }
        };
    }

    private SimpleTab getMapViewTab() {
        return new SimpleTab(new Model<String>("<i class='fa fa-map-o' aria-hidden='true'></i> Map")) {
            private static final long serialVersionUID = 1L;

            @Override
            public Panel getPanel(final String panelId) {
                return new SearchHitsMapViewPanel(panelId, getSearchModel(), getConfig());
            }
        };
    }

    @Override
    protected SearchRequestBuilder getRequestBuilder() {
        SearchRequestBuilder requestBuilder = super.getRequestBuilder();
        if (tabbedPanel != null) {
            if (tabbedPanel.getSelectedTab() == 1) {
                // Map view
                disablePaging(requestBuilder);
            } else if (tabbedPanel.getSelectedTab() == 0) {
                // List view
                enablePaging(requestBuilder);
            }
        }
        return requestBuilder;
    }

    // Change paging to show all results
    protected void disablePaging(SearchRequestBuilder requestBuilder) {
        if (!pagingStored) {
            storePaging(requestBuilder);
        }
        int limitForAll = Integer.MAX_VALUE;
        requestBuilder.setLimit(limitForAll);
        requestBuilder.setOffset(0);
        LOGGER.info("Disabled paging:Offset={}, Limit={}", 0, limitForAll);
    }

    protected void storePaging(SearchRequestBuilder requestBuilder) {
        storedOffset = requestBuilder.getOffset();
        storedLimit = requestBuilder.getLimit();
        pagingStored = true;
        LOGGER.info("Stored paging:Offset={}, Limit={}", storedOffset, storedLimit);
    }

    protected void enablePaging(SearchRequestBuilder requestBuilder) {
        if (pagingStored) {
            restorePaging(requestBuilder);
        }
    }

    protected void restorePaging(SearchRequestBuilder requestBuilder) {
        // reset to first page if the stored one does not exist anymore.
        // this can happen when the search request changed while in Map view mode
        if (getSearchResult().getTotalHits() <= storedOffset)
            storedOffset = 0;
        requestBuilder.setOffset(storedOffset);
        requestBuilder.setLimit(storedLimit);
        pagingStored = false;
        LOGGER.info("Restored paging: Offset={}, Limit={}", storedOffset, storedLimit);
    }

    protected void initComponents() {
        // criteria
        add(new SearchCriteriaPanel("searchCriteria", getSearchModel()) {
            private static final long serialVersionUID = -6370349646809914607L;

            public boolean isVisible() {
                return super.isVisible() && getRequestBuilder().getCriteria().size() > 1;
            };
        });

        // sort fields
        add(new SearchSortPanel("sortPanel", getSearchModel(), getConfig().getSortLinks()));

        // add the tabs
        List<ITab> tabs = new ArrayList<ITab>();
        tabs.add(getListViewTab());
        tabs.add(getMapViewTab());
        tabbedPanel = new TabbedPanel("tabs", tabs) {
            private static final long serialVersionUID = 4905295669896593901L;

            @Override
            protected Component newTitle(String titleId, IModel<?> titleModel, int index) {
                // allow html for 'icon' on tab
                return super.newTitle(titleId, titleModel, index).setEscapeModelStrings(false);
            }

            @Override
            public void setSelectedTab(int arg0) {
                super.setSelectedTab(arg0);
                // force to redo the search
                doSearch();
            }

            // Fix the hard coded 'selected' class on active list items (Bootstrap uses .active).
            @Override
            protected LoopItem newTabContainer(final int tabIndex) {
                return new LoopItem(tabIndex) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void onComponentTag(ComponentTag tag) {
                        super.onComponentTag(tag);
                        String cssClass = (String) tag.getString("class");
                        if (cssClass == null) {
                            cssClass = "";
                        }
                        if (getIteration() == getSelectedTab()) {
                            cssClass += " active";
                        }
                        tag.put("class", cssClass.trim());
                    }

                    @Override
                    public boolean isVisible() {
                        return getTabs().get(tabIndex).isVisible();
                    }
                };
            }

        };
        add(tabbedPanel);
        add(createHelpPopup("refineHelpPopup"));

        WebMarkupContainer refineSearchContainer = createRefineSearchContainer("refineSearchContainer");
        add(refineSearchContainer);
        refineSearchContainer.add(createSearchBar("refineSearchPanel"));
        refineSearchContainer.add(createAdvancedSearch("advancedSearch"));

        /**
         * I had to make this enclosure by hand, because putting a wicket:enclosure in a wicket:enclosure caused a nasty bug when using the setResponsePage to
         * render a page with this component on it. Everytime it would say that the "browseMore" component was forgotten in the markup. After almost 2 hours of
         * searching it turned out to be a freaking bug in Wicket 1.4.7.
         */
        WebMarkupContainer refineFacets = new WebMarkupContainer("refineFacetsEnclosure") {
            private static final long serialVersionUID = 2474778991631709989L;

            public boolean isVisible() {
                for (FacetConfig facetConfig : getConfig().getRefineFacets()) {
                    if (FacetPanel.isVisible(facetConfig, getSearchModel()))
                        return true;
                }
                return false;
            };
        };
        add(refineFacets);

        final FacetStrategy facetStrategy = getConfig().getFacetStrategy();
        refineFacets.add(new ListView<FacetConfig>("refineFacets", getConfig().getRefineFacets()) {
            private static final long serialVersionUID = 7406250758535500272L;

            @Override
            protected void populateItem(ListItem<FacetConfig> item) {
                FacetConfig facetConfig = item.getModelObject();
                item.add(new FacetPanel("facet", getSearchModel(), facetConfig));
                item.setVisible(facetStrategy.isFacetVisible(facetConfig, getSearchData()));
            }
        });

        // browse more
        if (getConfig().showBrowseMore()) {
            refineFacets.add(new Link("browseMore") {
                private static final long serialVersionUID = -6803231407654989149L;

                public void onClick() {
                    onBrowseMoreClicked(getSearchModel());
                }
            });
        } else {
            WicketUtil.hide(refineFacets, "browseMore");
        }
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

    private SearchBar createSearchBar(String id) {
        return new SearchBar(id) {
            private static final long serialVersionUID = -5980195347064339476L;

            @Override
            public void onSearch(String searchText) {
                SearchResultPanel.this.getRequestBuilder().addCriterium(
                        new TextSearchCriterium(searchText, new Model<String>(CriteriumLabel.createFilterText(
                                SearchResultPanel.this.getString(SEARCHRESULTPANEL_CRITERIUMTEXT_REFINE_SEARCH), searchText))));
            }

            @Override
            public boolean isVisible() {
                return SearchResultPanel.this.getSearchResult().getHits().size() > 1;
            }
        };
    }

    private WebMarkupContainer createRefineSearchContainer(String id) {
        return new WebMarkupContainer(id) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isVisible() {
                return SearchResultPanel.this.getSearchResult().getHits().size() > 1;
            }
        };
    }

    private HelpPopup createHelpPopup(String id) {
        return new HelpPopup(id, "Refine", getRefineHelpContent());
    }

    @SuppressWarnings("rawtypes")
    private Link createAdvancedSearch(String id) {
        return new Link(id) {
            private static final long serialVersionUID = -1905413983732583324L;

            @Override
            public void onClick() {
                onAdvancedSearchClicked(getSearchModel());
            }

            @Override
            public boolean isVisible() {
                return getConfig().showAdvancedSearch();
            }
        };
    }

    public SearchResultConfig getConfig() {
        return config;
    }

    public void setConfig(SearchResultConfig config) {
        this.config = config;
    }

    /**
     * Override this method to return a different (real) help text.
     */
    protected String getRefineHelpContent() {
        return "No help defined";
    }

}
