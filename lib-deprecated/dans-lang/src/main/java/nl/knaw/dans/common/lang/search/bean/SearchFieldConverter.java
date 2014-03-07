package nl.knaw.dans.common.lang.search.bean;

import nl.knaw.dans.common.lang.search.exceptions.SearchBeanConverterException;

/**
 * A search field converter converts from and to a field value. The field value is gotten from and saved
 * into the search index. It currently depends on the search engine implementation how the object will
 * come out of the index.
 * 
 * @param <T>
 *        the type of the search field
 * @author lobo
 */
public interface SearchFieldConverter<T>
{
    T fromFieldValue(Object in) throws SearchBeanConverterException;

    Object toFieldValue(T in) throws SearchBeanConverterException;
}
