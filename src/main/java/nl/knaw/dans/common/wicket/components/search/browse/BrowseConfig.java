package nl.knaw.dans.common.wicket.components.search.browse;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import nl.knaw.dans.common.wicket.components.search.facets.FacetConfig;

/**
 * Configuration object for the BrowsePanel. 
 * 
 * @see BrowsePanel
 * 
 * @author lobo
 */
public class BrowseConfig implements Serializable
{
    private static final long serialVersionUID = -718705695086035147L;

    /**
     * The facets that need to be visible
     * 
     * @see nl.knaw.dans.common.wicket.components.search.facets.FacetConfig
     */
    private List<FacetConfig> facets;

    public BrowseConfig()
    {
    }

    public BrowseConfig(List<FacetConfig> facets)
    {
        setFacets(facets);
    }

    public List<FacetConfig> getFacets()
    {
        return (List<FacetConfig>) (facets != null ? facets : Collections.emptyList());
    }

    public void setFacets(List<FacetConfig> facets)
    {
        this.facets = facets;
    }

    public FacetConfig getFacetConfig(String facet)
    {
        if (facets != null)
        {
            for (FacetConfig config : facets)
            {
                if (config.getFacetName().equals(facet))
                    return config;
            }
        }
        return null;
    }

}
