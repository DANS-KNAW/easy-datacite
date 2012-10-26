package nl.knaw.dans.common.wicket.components.search.criteria;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.knaw.dans.common.lang.search.FacetValue;
import nl.knaw.dans.common.lang.search.simple.SimpleField;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchRequest;
import nl.knaw.dans.common.wicket.components.search.facets.CollapsedFacetValue;

import org.apache.wicket.model.IModel;

/**
 * Selects a facet by using a field filter on the selected facet value. Also 
 * compatible with CollapsedFacetValues.
 * 
 * @author lobo
 */
public class FacetCriterium extends AbstractSearchCriterium
{
    private static final long serialVersionUID = -1946955118281043993L;

    private final FacetValue facetValue;
    private final String facetName;

    public FacetCriterium(String facetName, FacetValue facetValue, IModel<String> labelModel)
    {
        super(labelModel);
        this.facetName = facetName;
        this.facetValue = facetValue;
    }

    @Override
    public void apply(SimpleSearchRequest searchRequest)
    {
        if (facetValue instanceof CollapsedFacetValue)
        {
            CollapsedFacetValue collapsedFacetValue = (CollapsedFacetValue) facetValue;

            List collapsedValues = new ArrayList(collapsedFacetValue.getCollapsedValues());
            Iterator<FacetValue<?>> collapsedValuesIt = collapsedValues.iterator();
            String facetValuesQuery = "(" + facetValue.getValue().toString();
            if (collapsedValuesIt.hasNext())
                facetValuesQuery += " OR ";
            while (collapsedValuesIt.hasNext())
            {
                FacetValue<?> collapsedValue = collapsedValuesIt.next();
                facetValuesQuery += collapsedValue.getValue().toString();
                if (collapsedValuesIt.hasNext())
                    facetValuesQuery += " OR ";
            }
            facetValuesQuery += ")";

            SimpleField<String> facetFilter = new SimpleField<String>(facetName, facetValuesQuery);
            searchRequest.addFilterQuery(facetFilter);
        }
        else
        {
            SimpleField<String> facetFilter = new SimpleField<String>(facetName, facetValue.getValue().toString());
            searchRequest.addFilterQuery(facetFilter);
        }
    }

    public FacetValue getFacetValue()
    {
        return facetValue;
    }

    public String getFacetName()
    {
        return facetName;
    }

}
