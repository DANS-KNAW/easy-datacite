package nl.knaw.dans.pf.language.emd.types;

import java.io.Serializable;

/**
 * Wrapper of type-save values.
 * <p/>
 * The meaningful part of an element in xml is in essence a string value that can be serialized to a java
 * type. Classes that implement this interface make sure that the meaningful part can be set and obtained
 * through the methods {@link #setValue(Object)} and {@link #getValue()}.
 * 
 * @author ecco
 * @param <T>
 *        the wrapped type
 */
public interface SimpleElement<T> extends Serializable
{

    /**
     * Type-save method to get the meaningful part of an element.
     * 
     * @return the meaningful part of an element
     */
    T getValue();

    /**
     * Type-save method to set the meaningful part of an element.
     * 
     * @param value
     *        the meaningful part of an element
     */
    void setValue(T value);
}
