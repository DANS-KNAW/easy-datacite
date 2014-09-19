package nl.knaw.dans.common.lang.search.simple;

import nl.knaw.dans.common.lang.search.FacetValue;

public class SimpleFacetValue<T> implements FacetValue<T> {
    private static final long serialVersionUID = 7059135763075657957L;

    private int count;
    private T value;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

}
