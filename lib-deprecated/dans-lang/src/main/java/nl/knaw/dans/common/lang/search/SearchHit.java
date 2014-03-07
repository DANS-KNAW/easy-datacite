package nl.knaw.dans.common.lang.search;

import java.io.Serializable;
import java.util.List;

/**
 * A search hit is one of the results of a search request on the search engine. It contains a document
 * (in either the form of a document or the form of a search bean) and some metadata about how the
 * document was found. A search hit is immutable.
 * 
 * @param <T>
 *        the type of the document that was found
 * @author lobo
 */
public interface SearchHit<T> extends Serializable
{
    T getData();

    /**
     * @return a list of snippet fields that highlight the reason why this hit was found. Snippets are
     *         currently only available if the SearchRequest had highlighting enabled.
     */
    List<SnippetField> getSnippets();

    /**
     * A convenience method for retrieving a snippet field by its name. Snippets are currently only
     * available if the SearchRequest had highlighting enabled.
     * 
     * @param fieldName
     *        the name of the snippet field
     * @return a snippet field
     */
    SnippetField getSnippetByName(String fieldName);

    /**
     * @return the relevance score the search engine returned for the this hit. Use the
     *         SearchResult.getMaxRelevanceScore() to calculate a relevance percentage.
     */
    float getRelevanceScore();
}
