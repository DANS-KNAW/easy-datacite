package nl.knaw.dans.common.lang.search;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * A search request object contains all information for the searchEngine.search*
 * operation to be performed properly. 
 * 
 * @author lobo
 */
public interface SearchRequest extends Serializable
{
    /**
     * @return the index on which the search is to be performed or null
     * if the search is to be performed in single index mode.
     * @see Index
     */
    Index getIndex();

    /**
     * Set the index on which the search is to be performed. The default
     * index is null, which means that the search engine is in single index
     * mode (solr single core mode). 
     * @param index the index object.
     * @see Index
     */
    void setIndex(Index index);

    /**
     * Gets the searchQuery 
     * @see SearchQuery
     */
    SearchQuery getQuery();

    /**
     * Sets the search query. Leaving the query empty means searching for EVERYTHING.
     * @see SearchQuery
     * @param query the search query
     */
    void setQuery(SearchQuery query);

    /**
     * Set the field queries. 
     *    
     * @param fieldQueries
     * @see #getFieldQueries()
     */
    void setFieldQueries(FieldSet<?> fieldQueries);

    /**
     * Convenience method for adding a filter query.
     * @see #getFieldQueries()
     */
    void addFieldQuery(Field<?> filterQuery);

    /**
     * Convenience method for clearing all filter queries.
     * @see #getFieldQueries()
     */
    void clearFieldQueries();

    /**
     * Field queries are searches for
     * specific fields as opposed to the query parameter which
     * searches for all fields.
     * 
     * @return all field queries
     */
    FieldSet<?> getFieldQueries();

    /**
     * Set the filter queries.
     * 
     * @param filterQueries the filter queries
     */
    void setFilterQueries(FieldSet<?> filterQueries);

    /**
     * Convenience method for adding a filter query.
     * @see #getFilterQueries()
     */
    void addFilterQuery(Field<?> filterQuery);

    /**
     * Convenience method for clearing all filter queries.
     * @see #getFilterQueries()
     */
    void clearFilterQueries();

    /**
     * Filter queries are almost the same as field queries.
     * They are just interpreted slightly different by the search
     * engine. 
     * 
     * Field queries are used for searching values, like in an 
     * advanced search. While filter queries are only
     * meant to make sure the search results include certain field
     * values. Filter queries are more primitive: they do not return
     * hit highlighting information and  do not affect the hit 
     * relevance score. 

     * @return the filter queries
     */
    FieldSet<?> getFilterQueries();

    /**
     * Sets the list of sort fields 
     * @param sortFields the sort fields
     * @see #getSortFields()
     */
    void setSortFields(List<SortField> sortFields);

    /**
     * A list of fields that need to be sorted by. This first
     * sort field in the list is sorted on first, the second
     * is a secondary sort field, etc, etc.
     * the list of sort fields
     * @return
     */
    List<SortField> getSortFields();

    /**
     * Convenience method to add a sort field
     * @param sortField the sort field to add
     * @see #getSortFields()
     */
    void addSortField(SortField sortField);

    /**
     * Convenience method to clear the sort fields
     * @see #getSortFields()
     */
    void clearSortFields();

    /**
     * Sets the filter list for search bean classes. 
     * @param filterBeans the search bean classes to filter on
     * @see #getFilterBeans()
     */
    void setFilterBeans(Set<Class<?>> filterBeans);

    /**
     * A set of search bean classes to filter the search for. This
     * means that if you add one or more search bean classes to this 
     * set the search operation will only return documents that 
     * can be converted to those search bean classes.
     * @return the filter set of search bean classes
     */
    Set<Class<?>> getFilterBeans();

    /**
     * Convenience method to add a filter bean
     * @param filterBean the filter bean to add
     * @see #getFilterBeans()
     */
    void addFilterBean(Class<?> filterBean);

    /**
     * Convenience method to clear the filter beans
     * @see #getFilterBeans()
     */
    void clearFilterBeans();

    /**
     * The offset
     * @see #setOffset(int)
     */
    int getOffset();

    /**
     * Sets the offset of the paging mechanism. For paging use this
     * in conjuction with the limit parameter.
     * @param offset the offset starting from 0
     * @see #setLimit(int) 
     */
    void setOffset(int offset);

    /**
     * Sets the amount of hits to limit the search to. For paging use
     * this in conjunction with the offset parameter. 
     * @param limit
     * @see #setOffset(int)
     */
    void setLimit(int limit);

    /**
     * The limit
     * @see #setLimit(int)
     */
    int getLimit();

    /**
     * @return if highlighting is enabled. Default = false.
     */
    boolean isHighlightingEnabled();

    /**
     * Enable highlighting
     * @param enabled true to enable, false to disable. 
     */
    void setHighlightingEnabled(boolean enabled);

    /**
     * @return the list of field names on which facets are enabled.
     */
    Set<String> getFacetFields();

    /**
     * Sets the list of field names on which facets are enabled.
     */
    void setFacetFields(Set<String> facetField);
}
