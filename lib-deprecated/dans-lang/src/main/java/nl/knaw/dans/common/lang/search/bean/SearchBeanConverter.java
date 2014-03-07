package nl.knaw.dans.common.lang.search.bean;

import nl.knaw.dans.common.lang.search.IndexDocument;
import nl.knaw.dans.common.lang.search.exceptions.SearchBeanConverterException;
import nl.knaw.dans.common.lang.search.exceptions.SearchBeanException;

/**
 * A search bean converter is responsible for converting a search bean to an 
 * index document that can be written to the search engine.
 *
 * @param <T> the search bean type
 *  
 * @author lobo
 */
public interface SearchBeanConverter<T>
{
    IndexDocument toIndexDocument(T searchBean) throws SearchBeanConverterException, SearchBeanException;
}
