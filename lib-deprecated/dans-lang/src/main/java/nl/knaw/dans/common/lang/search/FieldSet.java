package nl.knaw.dans.common.lang.search;

import java.io.Serializable;
import java.util.Set;

/**
 * A set of fields. The set is kept unique by the field name. Inherits from the Set interface, so it can be used as a collection.
 * 
 * @param <T>
 *        the type of the field value (might be mixed)
 * @author lobo
 */
public interface FieldSet<T> extends Set<Field<T>>, Serializable {

    /**
     * Gets a field by its name. If it cannot be found it returns null.
     * 
     * @param name
     *        the name of the field
     * @return the field
     */
    Field<T> getByFieldName(String name);
}
