package nl.knaw.dans.common.lang.search;

import java.io.Serializable;

/**
 * Refers to a single index in a search engine by name. This object is a container for additional descriptive metadata on the search index.
 * 
 * @author lobo
 */
public interface Index extends Serializable {
    /**
     * @return the name of the index
     */
    String getName();

    /**
     * @return the name of the primary key (a.k.a unique field) field of the documents in this index.
     */
    String getPrimaryKey();
}
