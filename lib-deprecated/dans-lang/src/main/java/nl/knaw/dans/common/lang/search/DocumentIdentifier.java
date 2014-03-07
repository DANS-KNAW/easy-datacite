package nl.knaw.dans.common.lang.search;

/**
 * A document is identified by its index and its primary key.
 * 
 * @author lobo
 */
public interface DocumentIdentifier
{
    Index getIndex();

    Field<?> getPrimaryKey();
}
