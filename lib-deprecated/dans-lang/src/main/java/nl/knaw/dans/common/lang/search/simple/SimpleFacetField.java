package nl.knaw.dans.common.lang.search.simple;

import java.util.List;

import nl.knaw.dans.common.lang.search.FacetField;
import nl.knaw.dans.common.lang.search.FacetValue;

public class SimpleFacetField extends SimpleField<List<FacetValue<?>>> implements FacetField
{
    private static final long serialVersionUID = -3468515003516678006L;

    public SimpleFacetField(String name, List<FacetValue<?>> value)
    {
        super(name, value);
    }

}
