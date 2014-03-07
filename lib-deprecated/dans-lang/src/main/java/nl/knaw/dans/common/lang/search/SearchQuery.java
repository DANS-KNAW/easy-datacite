package nl.knaw.dans.common.lang.search;

import java.io.Serializable;

/**
 * A search query is usually what the user wants to search for 
 * 
 * The result of the search query object is a string that contains the
 * query. This string might however be the result of some fancy 
 * query builder instead of directly working with a string.
 *  
 * @author lobo
 */
public interface SearchQuery extends Serializable
{

    /**
     * Gets the query string. 
     */
    String getQueryString();
}
