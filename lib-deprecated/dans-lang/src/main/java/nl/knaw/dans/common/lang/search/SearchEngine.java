package nl.knaw.dans.common.lang.search;

import java.util.Collection;

import nl.knaw.dans.common.lang.search.bean.SearchBeanFactory;
import nl.knaw.dans.common.lang.search.exceptions.SearchEngineException;

/**
 * The main search interface on which the three main operations
 * of the search engine are defined.
 *
 *  1. Search
 *
 *   Searches through a specific search index and returns the found document
 *   in the form of search beans or documents.
 *
 *  2. Index
 *
 *   Adds or updates new or existing documents into a specific search index.
 *
 *  3. Delete
 *
 *   Deletes documents from a search index.
 *
 * @author lobo
 */
public interface SearchEngine
{
    /**
     * Sets the current search bean factory. The search bean factory is responsible for the
     * creation of the search beans.
     */
    void setSearchBeanFactory(SearchBeanFactory seFactory);

    /**
     * Gets the current search bean factory. The search bean factory is responsible for the
     * creation of the search beans.
     */
    SearchBeanFactory getSearchBeanFactory();

    /**
     * Starts a transaction for this thread. If no transaction was started auto commit is assumed.
     */
    void beginTransaction();

    /**
     * Commits the transaction of this thread.
     * @throws SearchEngineException
     */
    void commit() throws SearchEngineException;

    /**
     * Rolls back transaction of this thread.
     * @throws SearchEngineException
     */
    void rollback() throws SearchEngineException;

    /**
     * Perform a search on the search engine and returns the search result in the form
     * of document objects.
     *
     * @param request the search request
     * @return search result object containing document objects
     * @throws SearchEngineException wrapper exception
     */
    SearchResult<? extends Document> searchDocuments(SearchRequest request) throws SearchEngineException;

    /**
     * Perform a search on the search engine and returns the search result in the form
     * of search beans.
     *
     * Note: It is advisable to use the SearchRequest.setFilterBeans() method
     * on the search request before calling this method. If a found document cannot
     * be converted to a search bean an exception of some sort will be thrown..
     *
     * @param request the search request
     * @return search result object containing search beans
     * @throws SearchEngineException wrapper exception
     */
    SearchResult<? extends Object> searchBeans(SearchRequest request) throws SearchEngineException;

    /**
     * Adds or updates a bunch of documents.
     * @param indexDocuments the documents
     * @throws SearchEngineException wrapper exception
     */
    void indexDocuments(Collection<? extends IndexDocument> indexDocuments) throws SearchEngineException;

    /**
     * Adds or updates a single document.
     * @param indexDocument the document
     * @throws SearchEngineException wrapper exception
     */
    void indexDocument(IndexDocument indexDocument) throws SearchEngineException;

    /**
     * Adds or updates a single search bean in the default search index of the search
     * bean.
     * @param searchBean the search bean
     * @throws SearchEngineException wrapper exception
     */
    void indexBean(Object searchBean) throws SearchEngineException;

    /**
     * Adds or updates a bunch of search beans in the default search indices of the search
     * beans.
     * @param searchBeans the search beans
     * @throws SearchEngineException wrapper exception
     */
    void indexBeans(Collection<? extends Object> searchBeans) throws SearchEngineException;

    /**
     * Deletes a single search bean from the default search index of the search
     * bean.
     * @param searchBean the search bean
     * @throws SearchEngineException wrapper exception
     */
    void deleteBean(Object searchBean) throws SearchEngineException;

    /**
     * Deletes a bunch of search beans from the default search indices of the search
     * beans.
     * @param searchBeans the search beans
     * @throws SearchEngineException wrapper exception
     */
    void deleteBeans(Collection<? extends Object> searchBeans) throws SearchEngineException;

    /**
     * Deletes a bunch of documents.
     * @param indexDocuments the documents
     * @throws SearchEngineException wrapper exception
     */
    void deleteDocuments(Collection<? extends IndexDocument> indexDocuments) throws SearchEngineException;

    /**
     * Deletes a document.
     * @param indexDocument the document
     * @throws SearchEngineException wrapper exception
     */
    void deleteDocument(IndexDocument indexDocument) throws SearchEngineException;
}
