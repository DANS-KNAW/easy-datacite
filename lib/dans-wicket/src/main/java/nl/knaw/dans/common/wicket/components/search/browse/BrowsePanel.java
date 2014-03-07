package nl.knaw.dans.common.wicket.components.search.browse;

import nl.knaw.dans.common.lang.search.simple.SimpleSearchRequest;
import nl.knaw.dans.common.wicket.components.search.SearchPanel;
import nl.knaw.dans.common.wicket.components.search.criteria.SearchCriteriaPanel;
import nl.knaw.dans.common.wicket.components.search.facets.FacetConfig;
import nl.knaw.dans.common.wicket.components.search.facets.FacetPanel;
import nl.knaw.dans.common.wicket.components.search.model.SearchCriterium;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;

/**
 * The browse panel shows one or more facets with the intention of letting the user pick one or more
 * browsing criteria. It can be configured with the BrowseConfig object. If the BrowsePanel is created
 * with an existing SearchModel, like one that already was modified by another component e.g. the
 * SearchResultPanel, it will just display the facets based on what previous criteria were already
 * entered. In other words you can start browsing with this panel where the user left off.
 * 
 * @author lobo
 */
public abstract class BrowsePanel extends SearchPanel
{
    /**
     * Gets called when a user after having browsed enough finally clicks on the 'show' button which
     * should lead to a page where the results are shown. The easiest way to do this is by passing the
     * SearchModel, as is, to a SearchResultPanel.
     * 
     * @param model
     *        the model in its current state. Pass to a SearchResultPanel for showing results.
     * @see nl.knaw.dans.common.wicket.components.search.results.SearchResultPanel
     */
    public abstract void onShowButtonClicked(SearchModel model);

    private static final long serialVersionUID = -7561319774611828836L;

    private final BrowseConfig browseConfig;

    public BrowsePanel(String id, BrowseConfig browseConfig)
    {
        this(id, new SearchModel(), browseConfig);
    }

    public BrowsePanel(String id, SearchCriterium criterium, BrowseConfig browseConfig)
    {
        this(id, new SearchModel(criterium), browseConfig);
    }

    public BrowsePanel(String id, SearchModel searchModel, BrowseConfig browseConfig)
    {
        super(id, searchModel);
        this.browseConfig = browseConfig;
        init();
    }

    public BrowseConfig getConfig()
    {
        return browseConfig;
    }

    private void init()
    {
        // init model
        getRequestBuilder().setFacets(browseConfig.getFacets());

        // search
        doSearch();

        initComponents();
    }

    private void initComponents()
    {
        // browse criteria
        add(new SearchCriteriaPanel("browseCriteria", getSearchModel()));

        // result count
        add(new Label("resultCount", new AbstractReadOnlyModel<String>()
        {
            private static final long serialVersionUID = 1L;

            public String getObject()
            {
                return BrowsePanel.this.getSearchData().getResult().getTotalHits() + "";
            };
        }));

        // show button
        add(new Link("showButton")
        {
            private static final long serialVersionUID = 1L;

            public void onClick()
            {
                onShowButtonClicked(getSearchModel());
            }
        });

        // browse facets
        add(new ListView<FacetConfig>("browseFacets", getConfig().getFacets())
        {
            private static final long serialVersionUID = 1L;

            protected void populateItem(ListItem<FacetConfig> item)
            {
                FacetPanel facetPanel = new FacetPanel("browseFacet", getSearchModel(), item.getModelObject())
                {
                    private static final long serialVersionUID = -5913133341105521215L;

                    protected void onFacetClick(nl.knaw.dans.common.lang.search.FacetValue<?> facetValue)
                    {
                        // check for browse dead end
                        if (getSearchData().isDirty())
                        {
                            doSearch();
                            if (getVisibleFacetCount() == 0)
                                onBrowseDeadEnd();
                        }
                    }
                };
                item.add(facetPanel);
            };
        });
    }

    private void onBrowseDeadEnd()
    {
        onShowButtonClicked(getSearchModel());
    }

    public int getVisibleFacetCount()
    {
        int count = 0;
        for (FacetConfig facetConfig : getConfig().getFacets())
        {
            if (FacetPanel.isVisible(facetConfig, getSearchModel()))
                count++;
        }
        return count;
    }

    @Override
    protected SimpleSearchRequest prepareSearchRequest(SimpleSearchRequest request)
    {
        // we don't need no damned results
        request.setLimit(0);
        request.setOffset(0);
        return request;
    }

}
