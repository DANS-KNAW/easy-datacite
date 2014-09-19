package nl.knaw.dans.common.wicket.components.search.model;

import java.io.Serializable;
import java.util.List;

import nl.knaw.dans.common.wicket.components.search.facets.FacetConfig;

/**
 * A CriteriumListener is notified of updates in searchCriteria and when an initial list of {@link FacetConfig}s is set on the {@link SearchRequestBuilder}.
 */
public interface CriteriumListener extends Serializable {

    /**
     * A searchCriterium was added to the requestBuilder.
     * 
     * @param searchCriterium
     *        criterium added.
     * @param searchRequestBuilder
     *        the active requestBuilder.
     */
    void onCriteriumAdded(SearchCriterium searchCriterium, SearchRequestBuilder searchRequestBuilder);

    void onCriteriumRemoved(SearchCriterium searchCriterium, SearchRequestBuilder searchRequestBuilder);

    void addFacets(List<FacetConfig> refineFacets, SearchRequestBuilder searchRequestBuilder);

}
