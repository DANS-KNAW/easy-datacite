package nl.knaw.dans.common.lang.reposearch;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStore;
import nl.knaw.dans.common.lang.repo.DmoStoreEventListener;
import nl.knaw.dans.common.lang.repo.exception.DmoStoreEventListenerException;
import nl.knaw.dans.common.lang.search.SearchEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A DmoStore listener that keep data model objects that also implement the HasIndexableObjects interface
 * synchronized with the search index. Thus when the dmo gets ingested, updated or purged the search
 * index is updated accordingly. This event listeners acts only AFTER the dmo has been changed by the
 * store. Note: if this event listener throws an exception it is the task of the receiver of the
 * exception to make sure that at some point in the future the search index and the store are fully
 * synchronized again.
 * 
 * @author lobo
 */
public class RepoSearchListener implements DmoStoreEventListener
{
    private SearchEngine defaultSearchEngine;

    private static final Logger logger = LoggerFactory.getLogger(RepoSearchListener.class);

    public RepoSearchListener(SearchEngine defaultSearchEngine)
    {
        setDefaultSearchEngine(defaultSearchEngine);
    }

    public void setDefaultSearchEngine(SearchEngine searchEngine)
    {
        if (searchEngine == null)
            throw new NullPointerException("search engine cannot be null");
        this.defaultSearchEngine = searchEngine;
    }

    public SearchEngine getSearchEngine()
    {
        return defaultSearchEngine;
    }

    /**
     * override this method to provide fine grained control over which search bean goes to which search
     * engine. Returning null is a valid value, which means the search bean will not be indexed.
     * 
     * @param searchBean
     *        the search bean that will be indexed
     * @return the search engine in which the search bean should be indexed or null if the search bean
     *         should not be indexed.
     */
    public SearchEngine getSearchEngineBySearchBean(Object searchBean)
    {
        return getSearchEngine();
    }

    /**
     * This method updates the index based on an incoming data model object. If the dmo implements the
     * HaseSearchBeans interface then it will get try to get the searchbeans from the dmo. The
     * searchbeans are then send to a searchengine for indexing (or deleting). Searchbeans of the type
     * RepoSearchBean are updated by the dmo. The method setPropertiesByDmo is called on the
     * (repo)searchbean.
     * 
     * @param store
     *        the store from which the event came
     * @param dmo
     *        the data model object that needs to be indexed
     * @param delete
     *        if set to true a delete will be executed, otherwise an index.
     * @throws DmoStoreEventListenerException
     *         if something goes awry
     */
    protected void updateIndex(DmoStore store, DataModelObject dmo, boolean delete) throws DmoStoreEventListenerException
    {
        try
        {
            if (dmo instanceof HasSearchBeans)
            {
                Collection<? extends Object> searchBeans = ((HasSearchBeans) dmo).getSearchBeans();
                if (searchBeans == null)
                    return;

                Set<SearchEngine> searchEngines = new HashSet<SearchEngine>();
                try
                {
                    for (Object searchBean : searchBeans)
                    {
                        if (searchBean instanceof RepoSearchBean)
                        {
                            ((RepoSearchBean) searchBean).setPropertiesByDmo(dmo);
                        }

                        SearchEngine searchEngine = getSearchEngineBySearchBean(searchBean);
                        if (searchEngine != null)
                        {
                            if (searchEngines.add(searchEngine))
                            {
                                searchEngine.beginTransaction();
                            }

                            if (delete)
                                searchEngine.deleteBean(searchBean);
                            else
                                searchEngine.indexBean(searchBean);
                        }
                    }
                }
                finally
                {
                    for (SearchEngine searchEngine : searchEngines)
                    {
                        searchEngine.commit();
                    }
                }
            }
        }
        catch (Exception e)
        {
            logger.error("Caught exception while updating indexes:", e);
            throw new DmoStoreEventListenerException(e);
        }
    }

    public void afterIngest(DmoStore store, DataModelObject dmo) throws DmoStoreEventListenerException
    {
        updateIndex(store, dmo, false);
    }

    public void afterUpdate(DmoStore store, DataModelObject dmo) throws DmoStoreEventListenerException
    {
        updateIndex(store, dmo, false);
    }

    public void afterPartialUpdate(DmoStore store, DataModelObject dmo) throws DmoStoreEventListenerException
    {
        if (!(dmo instanceof HasSearchBeans))
            return;

        // retrieve partially updated object
        DataModelObject partiallyUpdatedObj;
        try
        {
            partiallyUpdatedObj = store.retrieve(dmo.getDmoStoreId());
        }
        catch (RepositoryException e)
        {
            throw new DmoStoreEventListenerException("could not retrieve partially updated object " + dmo.getStoreId(), e);
        }

        if (!(partiallyUpdatedObj instanceof HasSearchBeans))
            throw new DmoStoreEventListenerException("object on which the update occured was indexable (" + dmo.toString() + "), " + "but the object ("
                    + partiallyUpdatedObj.toString() + ") that came from the repository was not.");

        updateIndex(store, partiallyUpdatedObj, false);
    }

    public void afterPurge(DmoStore store, DataModelObject dmo) throws DmoStoreEventListenerException
    {
        updateIndex(store, dmo, true);

    }

    public void beforeIngest(DmoStore store, DataModelObject dmo) throws DmoStoreEventListenerException
    {
    }

    public void beforeUpdate(DmoStore store, DataModelObject dmo) throws DmoStoreEventListenerException
    {
    }

    public void beforePurge(DmoStore store, DataModelObject dmo) throws DmoStoreEventListenerException
    {
    }
}
