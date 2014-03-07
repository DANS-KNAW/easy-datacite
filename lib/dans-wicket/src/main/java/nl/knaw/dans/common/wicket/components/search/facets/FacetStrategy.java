package nl.knaw.dans.common.wicket.components.search.facets;

import java.io.Serializable;

import nl.knaw.dans.common.wicket.components.search.model.SearchData;

public interface FacetStrategy extends Serializable
{

    boolean isFacetVisible(FacetConfig facetConfig, SearchData searchData);

}
