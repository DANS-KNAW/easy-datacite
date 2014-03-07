package nl.knaw.dans.common.lang.search.bean;

import nl.knaw.dans.common.lang.search.Document;
import nl.knaw.dans.common.lang.search.exceptions.SearchBeanException;
import nl.knaw.dans.common.lang.search.exceptions.SearchBeanFactoryException;

/**
 * The search bean factory is responsible for the creation of search bean based on an document coming
 * from the search index. The factory also is able to return the right search bean converter for a class
 * for the reverse process.
 * 
 * @author lobo
 */
public interface SearchBeanFactory
{
    Object createSearchBean(String typeIdentifier, Document document) throws SearchBeanFactoryException, SearchBeanException;

    SearchBeanConverter<?> getSearchBeanConverter(Class<?> clazz) throws SearchBeanFactoryException, SearchBeanException;
}
