package nl.knaw.dans.common.wicket.components.search.facets;

import nl.knaw.dans.common.wicket.components.search.model.SearchData;

public class DefaultFacetStrategy implements FacetStrategy
{

    private static final long serialVersionUID = -419391018607011859L;

    @Override
    public boolean isFacetVisible(FacetConfig facetConfig, SearchData searchData)
    {
        return true;
    }

}
