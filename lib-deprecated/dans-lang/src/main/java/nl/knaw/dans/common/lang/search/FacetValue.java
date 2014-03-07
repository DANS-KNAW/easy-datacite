package nl.knaw.dans.common.lang.search;

import java.io.Serializable;

/**
 * Each facet value has a count and a value. The value can be 
 * of different kinds (T)
 *
 * @author lobo
 *
 * @param <T> the value type
 */
public interface FacetValue<T> extends Serializable
{
    int getCount();

    T getValue();
}
