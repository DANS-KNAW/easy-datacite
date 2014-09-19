package nl.knaw.dans.common.lang.search;

import java.io.Serializable;

/**
 * Documents form the basis of the the content of the search index. They are stored as a collection of fields. An index can be likened to a single table of a
 * database, whereas the rows would be documents.
 * 
 * @author lobo
 */
public interface Document extends Serializable {
    /**
     * @return the fields contained by the document
     */
    FieldSet<?> getFields();

    /**
     * Convenience method for retrieving a specific field by name
     * 
     * @param fieldName
     * @return
     */
    Field<?> getFieldByName(String fieldName);
}
