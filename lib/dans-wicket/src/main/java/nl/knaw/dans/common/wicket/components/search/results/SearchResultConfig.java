package nl.knaw.dans.common.wicket.components.search.results;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.knaw.dans.common.lang.search.SortField;
import nl.knaw.dans.common.wicket.components.search.facets.DefaultFacetStrategy;
import nl.knaw.dans.common.wicket.components.search.facets.FacetConfig;
import nl.knaw.dans.common.wicket.components.search.facets.FacetStrategy;

/**
 * Configuration for the SearchResultPanel and its underlying components.
 * 
 * @author lobo
 */
public class SearchResultConfig implements Serializable {
    private static final long serialVersionUID = -9131907959762403710L;

    /**
     * This factory creates the (probably clickable) panels that show single search hits. Required.
     */
    private SearchHitPanelFactory hitPanelFactory;

    /**
     * Definitions for the fields that need to be made sortable. Optional.
     * 
     * @see SortLinkConfig
     */
    private List<SortLinkConfig> sortLinks = new ArrayList<SortLinkConfig>();;

    /**
     * A list of facets that must be shown as refinement options.
     */
    private List<FacetConfig> refineFacets = new ArrayList<FacetConfig>();

    private int resultCount = 10;

    private boolean showBrowseMore = true;

    private boolean showAdvancedSearch = true;

    private List<SortField> initialSortFields = new ArrayList<SortField>();

    private FacetStrategy facetStrategy;

    public SearchResultConfig() {

    }

    public void setHitPanelFactory(SearchHitPanelFactory hitPanelFactory) {
        this.hitPanelFactory = hitPanelFactory;
    }

    public SearchHitPanelFactory getHitPanelFactory() {
        return hitPanelFactory;
    }

    public void setSortLinks(List<SortLinkConfig> sortLinks) {
        this.sortLinks = sortLinks;
    }

    @SuppressWarnings("unchecked")
    public List<SortLinkConfig> getSortLinks() {
        return (List<SortLinkConfig>) (sortLinks != null ? sortLinks : Collections.emptyList());
    }

    public void setRefineFacets(List<FacetConfig> refineFacets) {
        this.refineFacets = refineFacets;
    }

    public List<FacetConfig> getRefineFacets() {
        return refineFacets;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setShowBrowseMore(boolean showBrowseMore) {
        this.showBrowseMore = showBrowseMore;
    }

    public boolean showBrowseMore() {
        return showBrowseMore;
    }

    public void setShowAdvancedSearch(boolean showAdvancedSearch) {
        this.showAdvancedSearch = showAdvancedSearch;
    }

    public boolean showAdvancedSearch() {
        return showAdvancedSearch;
    }

    public void setInitialSortFields(List<SortField> initialSortFields) {
        this.initialSortFields = initialSortFields;
    }

    public List<SortField> getInitialSortFields() {
        return initialSortFields;
    }

    public void addInitialSortField(SortField sortField) {
        initialSortFields.add(sortField);
    }

    public FacetStrategy getFacetStrategy() {
        if (facetStrategy == null) {
            facetStrategy = new DefaultFacetStrategy();
        }
        return facetStrategy;
    }

    public void setFacetStrategy(FacetStrategy facetStrategy) {
        this.facetStrategy = facetStrategy;
    }

}
