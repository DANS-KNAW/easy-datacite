package nl.knaw.dans.common.wicket.components.search.facets;

import java.io.Serializable;
import java.util.List;

import nl.knaw.dans.common.lang.search.FacetValue;

/**
 * A facet value collapser can be used to display facet values as part of a hierarchy. The facet
 * values come in as a single flat list (unfortunately solr 1.4 does not support hierarchical facets).
 * Then the implementor of this object takes those original values and collapses them all for 
 * a single node in the hierarchy (the selected value).
 * 
 * An example:
 *  
 *  if I have a hierarchy of:
 *  <pre>
 *  A 
 *  |_ B
 *     |_C
 *     |_D
 *  E
 *  |_F
 *  </pre>
 *  
 *  And I get A, B, C, D, E and F as a flat list of facets with facet counts:
 *  <pre>
 *  A = 3
 *  B = 1
 *  C = 2
 *  D = 2
 *  E = 1
 *  F = 5
 *  </pre>
 *  
 *  And I want to collapse that to the root level (selectedValue == null) then I should get:
 *  <pre>
 *  A = 8 (= A + B + C + D)  
 *  E = 6 (= E + F)
 *  </pre>
 *  
 *  And if I would select A then this should collapse to:
 *  <pre>
 *  B = 5 (= B + C + D)
 *  </pre>
 *  
 *  Selecting B would collapse to:
 *  <pre>
 *  C = 2
 *  D = 2
 *  </pre>
 *
 *  It is up to the implementor of this interface to understand the hierarchy and how the values
 *  map onto it. 
 *  
 * @param <T> the facet value type
 * 
 * @author lobo
 */
public interface FacetValueCollapser<T> extends Serializable
{
	List<CollapsedFacetValue<T>> collapse(List<FacetValue<T>> originalValues, FacetValue<T> selectedValue);
}
