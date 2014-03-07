package nl.knaw.dans.common.lang.search;

import java.io.Serializable;

/**
 * Search index documents contain fields which are basically named values (key value pairs). The value
 * can be of different types (T).
 * 
 * @author lobo
 * @param <T>
 *        the value type.
 */
public interface Field<T> extends Serializable
{
    T getValue();

    void setValue(T value);

    /**
     * @return the name of the field as specified by the search index schema. The name of a field must be
     *         unique in an index.
     */
    String getName();

    /**
     * Sets the name of the field.
     * 
     * @see #getName()
     */
    void setName(String name);

}
