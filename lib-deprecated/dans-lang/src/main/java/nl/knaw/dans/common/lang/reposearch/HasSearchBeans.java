package nl.knaw.dans.common.lang.reposearch;

import java.util.Collection;

/**
 * DataModelObject that implement this interface are recognized
 * by the RepoSearchListener and are queried for their searchable
 * material for synchronization with the search index.
 *
 * @author lobo
 */
public interface HasSearchBeans
{
    Collection<? extends Object> getSearchBeans();
}
