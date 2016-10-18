package nl.knaw.dans.easy.web.search.custom;

import nl.knaw.dans.common.lang.search.FacetValue;
import org.apache.commons.collections.comparators.FixedOrderComparator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import java.io.ObjectInputStream;
import java.io.IOException;

/**
 * For sorting FacetValues in the given fixed order, by their inner value Objects. Unspecified FacetValues will be placed at the end of the sorted list. Note:
 * It would be strange to do a fixed order of the facetValue 'count' so it's implicitly a compare by the 'value' and not by 'count'.
 */
public class FixedOrderFacetValueComparator<T> implements Comparator<FacetValue<T>>, Serializable {
    private static final long serialVersionUID = 1L;

    // non Serializable before Apache Commons Collections 4.0
    private transient FixedOrderComparator fixedOrderComparator;

    private List<Object> valueObjectsInOrder;

    public FixedOrderFacetValueComparator() {}

    public FixedOrderFacetValueComparator(FacetValue[] facetValues) {
        valueObjectsInOrder = new ArrayList<Object>();
        for (FacetValue<?> facetValue : facetValues)
            valueObjectsInOrder.add(facetValue.getValue());

        fixedOrderComparator = new FixedOrderComparator(valueObjectsInOrder);
        fixedOrderComparator.setUnknownObjectBehavior(FixedOrderComparator.UNKNOWN_AFTER);
    }

    @Override
    public int compare(FacetValue<T> facetValue1, FacetValue<T> facetValue2) {
        Object o1 = facetValue1.getValue();
        Object o2 = facetValue2.getValue();

        return fixedOrderComparator.compare(o1, o2);
    }

    // provide deserialization, because FixedOrderComparator can not be deserialized
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        fixedOrderComparator = new FixedOrderComparator(valueObjectsInOrder);
        fixedOrderComparator.setUnknownObjectBehavior(FixedOrderComparator.UNKNOWN_AFTER);
    }

}
