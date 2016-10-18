package nl.knaw.dans.easy.web.search.pages;

import nl.knaw.dans.common.lang.search.FacetValue;
import nl.knaw.dans.common.lang.search.simple.SimpleFacetValue;
import org.junit.Test;

import nl.knaw.dans.easy.web.search.custom.FixedOrderFacetValueComparator;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;

public class FixedOrderFacetValueComparatorTest {

    @Test
    public void sortFacetValues() {
        final String VALUES_SORT_ORDER = "A,B,C";
        final String UNSORTED_VALUES = "C,B,B,UNKNOWN_BY_COMPARATOR!,B,A";
        final String SORTED_VALUES = "A,B,B,B,C,UNKNOWN_BY_COMPARATOR!";

        FacetValue[] valuesOrder = getValuesFromStringAsArray(VALUES_SORT_ORDER);
        FixedOrderFacetValueComparator comparator = new FixedOrderFacetValueComparator(valuesOrder);

        List<FacetValue<?>> facetValues = new ArrayList<FacetValue<?>>();
        facetValues.addAll(getValuesFromStringAsList(UNSORTED_VALUES));

        String valuesStringBeforeSorting = getValuesAsString(facetValues);
        assertEquals(UNSORTED_VALUES, valuesStringBeforeSorting);

        Collections.sort(facetValues, comparator);

        String valuesStringAfterSorting = getValuesAsString(facetValues);
        assertEquals(SORTED_VALUES, valuesStringAfterSorting);
    }

    @Test(expected = ClassCastException.class)
    public void sortNonFacetValues() {
        FacetValue[] valuesOrder = getValuesFromStringAsArray("A,B,C");
        FixedOrderFacetValueComparator comparator = new FixedOrderFacetValueComparator(valuesOrder);

        List<String> nonFacetValues = new ArrayList<String>();
        nonFacetValues.add("A");
        nonFacetValues.add("B");

        Collections.sort(nonFacetValues, comparator);
    }

    private List<FacetValue<?>> getValuesFromStringAsList(final String valuesString) {
        List<FacetValue<?>> facetValues = new ArrayList<FacetValue<?>>();
        // split string
        String[] splitValuesString = valuesString.split(",");
        for (String valueString : splitValuesString) {
            SimpleFacetValue<String> value = new SimpleFacetValue<String>();
            value.setValue(valueString);
            facetValues.add(value);
        }
        return facetValues;
    }

    private FacetValue[] getValuesFromStringAsArray(final String valuesString) {
        List<FacetValue<?>> facetValueList = getValuesFromStringAsList(valuesString);
        FacetValue[] order = facetValueList.toArray(new FacetValue[facetValueList.size()]);

        return order;
    }

    private String getValuesAsString(final List<FacetValue<?>> facetValues) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (FacetValue<?> v : facetValues) {
            if (first)
                first = false;
            else
                sb.append(',');
            sb.append(v.getValue().toString());
        }
        return sb.toString();
    }
}
