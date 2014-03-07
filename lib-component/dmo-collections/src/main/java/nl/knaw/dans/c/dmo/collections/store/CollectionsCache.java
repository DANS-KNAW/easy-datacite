package nl.knaw.dans.c.dmo.collections.store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.i.dmo.collections.DmoCollection;
import nl.knaw.dans.i.dmo.collections.exceptions.CollectionsException;
import nl.knaw.dans.i.store.StoreSession;

public class CollectionsCache
{
    
    private static CollectionsCache instance;
    
    private static final String ownerId = CollectionsCache.class.getSimpleName();
    
    private final Map<DmoStoreId, DmoCollection> cache
        = Collections.synchronizedMap(
            new HashMap<DmoStoreId, DmoCollection>());
    
    public static CollectionsCache instance()
    {
        if (instance == null)
        {
            instance = new CollectionsCache();
        }
        return instance;
    }
    
    private CollectionsCache()
    {
        
    }
    
    /**
     * 
     * @param namespace
     * @return could be <code>null</code>
     * @throws RepositoryException
     */
    public DmoCollection getRoot(DmoNamespace namespace) throws RepositoryException
    {
        DmoStoreId rootId = new DmoStoreId(namespace, DmoCollection.ROOT_ID);
        DmoCollection root;
        synchronized (cache)
        {
            root = cache.get(rootId);
            if (root == null)
            {
                root = getRootFromStore(rootId);
                if (root != null)
                {
                    addToCacheDescending(root);
                }
            }
        }
        return root;
    }
    
    /**
     * 
     * @param dmoStoreId
     * @return could be <code>null</code>
     * @throws RepositoryException
     */
    public DmoCollection getCollection(DmoStoreId dmoStoreId) throws RepositoryException
    {
        DmoCollection dmoCollection;
        synchronized (cache)
        {
            dmoCollection = cache.get(dmoStoreId);
        }
        if (dmoCollection == null)
        {
            getRoot(dmoStoreId.getNamespace());
            synchronized (cache)
            {
                dmoCollection = cache.get(dmoStoreId);
            }
        }
        return dmoCollection;
    }
    
    public boolean contains(DmoStoreId dmoStoreId)
    {
        synchronized (cache)
        {
            return cache.containsKey(dmoStoreId);
        }
    }
    
    public boolean checkContainsAllInstances(boolean throwException, DmoCollection... collections) throws CollectionsException
    {
        boolean containsAllInstances = true;
        synchronized (cache)
        {
            for (DmoCollection collection : collections)
            {
                containsAllInstances &= collection == cache.get(collection.getDmoStoreId());
                if (throwException && !containsAllInstances)
                {
                    throw new CollectionsException("Not in cache: " + collection);
                }
            }
        }
        return containsAllInstances;
    }
    
    public void invalidate()
    {
        synchronized (cache)
        {
            cache.clear();
        }
    }
    
    public void invalidate(DmoNamespace namespace)
    {
        synchronized (cache)
        {
            Iterator<DmoStoreId> iter = cache.keySet().iterator();
            List<DmoStoreId> invalidEntries = new ArrayList<DmoStoreId>();
            while (iter.hasNext())
            {
                DmoStoreId dmoStoreId = iter.next();
                if (dmoStoreId.getNamespace().equals(namespace))
                {
                    invalidEntries.add(dmoStoreId);
                }
            }
            for (DmoStoreId dmoStoreId : invalidEntries)
            {
                cache.remove(dmoStoreId);
            }
        }
    }
    
    public int size()
    {
        return cache.size();
    }
    
    public int size(DmoNamespace namespace)
    {
        int size = 0;
        for (DmoStoreId dmoStoreId : cache.keySet())
        {
            if (dmoStoreId.getNamespace().equals(namespace))
            {
                size++;
            }
        }
        return size;
    }
    
    public void putDescending(DmoCollection collection)
    {
        synchronized (cache)
        {
            addToCacheDescending(collection);
        }
    }

    private void addToCacheDescending(DmoCollection collection)
    {
        cache.put(collection.getDmoStoreId(), collection);
        for (DmoCollection kid : collection.getChildren())
        {
            addToCacheDescending(kid);
        }
    }
    
    public void put(DmoCollection collection)
    {
        synchronized (cache)
        {
            cache.put(collection.getDmoStoreId(), collection);
        }
    }
    
    public void putAscending(DmoCollection collection)
    {
        synchronized (cache)
        {
            DmoCollection parent = collection;
            while (parent != null)
            {
                cache.put(parent.getDmoStoreId(), parent);
                parent = parent.getParent();
            }
        }
    }
    
    public void remove(DmoStoreId dmoStoreId)
    {
        synchronized (cache)
        {
            cache.remove(dmoStoreId);
        }
    }

    private DmoCollection getRootFromStore(DmoStoreId rootId) throws RepositoryException
    {
        StoreSession session = Store.newStoreSession(ownerId);
        DmoCollection root = null;
        try
        {
            root = getRootFromStore(rootId, session);
            if (root != null)
            {
                getChildrenFromStore(root);
            }
        }
        finally
        {
            session.close();
        }
        
        return root;
    }

    protected DmoCollection getRootFromStore(DmoStoreId rootId, StoreSession session) throws RepositoryException
    {
        DmoCollection root = null;
        try
        {
            root = (DmoCollection) session.getDataModelObject(rootId);
        }
        catch (ObjectNotInStoreException e)
        {
            // not found: root = null
        }
        return root;
    }

    private void getChildrenFromStore(DmoCollection collection)
    {
        // datamodelobjects are in session.
        for (DmoCollection kid : collection.getChildren())
        {
            getChildrenFromStore(kid);
        }
    }


}
