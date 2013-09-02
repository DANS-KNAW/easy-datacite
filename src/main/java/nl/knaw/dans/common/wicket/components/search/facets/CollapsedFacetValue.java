package nl.knaw.dans.common.wicket.components.search.facets;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.search.FacetValue;
import nl.knaw.dans.common.lang.search.simple.SimpleFacetValue;

/**
 * A collapsed facet value contains one or more other facet values that have been collapsed into it. It
 * can be used to create a hierarchy of facet values.
 * 
 * @see FacetValueCollapser
 * @author lobo
 * @param <T>
 *        the type of the facet value
 */
public class CollapsedFacetValue<T> extends SimpleFacetValue<T>
{
    private static final long serialVersionUID = -5884375624040701495L;
    private List<FacetValue<T>> collapsedValues = new ArrayList<FacetValue<T>>();

    public List<FacetValue<T>> getCollapsedValues()
    {
        return collapsedValues;
    }

    public void setCollapsedValues(List<FacetValue<T>> collapsedValues)
    {
        this.collapsedValues = collapsedValues;
    }

    public void addFacetValue(FacetValue<T> facetValue)
    {
        collapsedValues.add(facetValue);
        addFacetCount(facetValue.getCount());
    }

    private void addFacetCount(int facetCount)
    {
        setCount(getCount() + facetCount);
    }
}
