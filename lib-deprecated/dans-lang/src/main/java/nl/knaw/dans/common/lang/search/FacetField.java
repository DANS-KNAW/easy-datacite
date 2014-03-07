package nl.knaw.dans.common.lang.search;

import java.util.List;

/**
 * A facet field is basically a field which contains a list of facet values.
 * 
 * @author lobo
 */
public interface FacetField extends Field<List<FacetValue<?>>>
{
}
