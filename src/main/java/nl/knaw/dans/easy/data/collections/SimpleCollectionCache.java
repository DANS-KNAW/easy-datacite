package nl.knaw.dans.easy.data.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.UnitOfWork;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.repo.exception.UnitOfWorkInterruptException;
import nl.knaw.dans.easy.data.store.EasyUnitOfWork;
import nl.knaw.dans.easy.domain.collections.SimpleCollection;
import nl.knaw.dans.easy.domain.collections.SimpleCollectionCreator;

public class SimpleCollectionCache
{
    
    private static SimpleCollectionCache instance;
    
    private final Map<DmoStoreId, SimpleCollection> cache
        = Collections.synchronizedMap(
            new HashMap<DmoStoreId, SimpleCollection>());
    
    public static SimpleCollectionCache instance()
    {
        if (instance == null)
        {
            instance = new SimpleCollectionCache();
        }
        return instance;
    }
    
    private SimpleCollectionCache()
    {
        
    }
    
    public SimpleCollection getRoot(DmoNamespace namespace) throws RepositoryException
    {
        DmoStoreId rootId = new DmoStoreId(namespace, SimpleCollection.ROOT_ID);
        SimpleCollection root;
        synchronized (cache)
        {
            root = cache.get(rootId);
            if (root == null)
            {
                root = getRootFromStore(rootId);
                addToCache(root);
            }
        }
        return root;
    }
    
    public SimpleCollection getSimpleCollection(DmoStoreId dmoStoreId) throws RepositoryException
    {
        SimpleCollection simpleCollection;
        synchronized (cache)
        {
            simpleCollection = cache.get(dmoStoreId);
        }
        if (simpleCollection == null)
        {
            getRoot(dmoStoreId.getNamespace());
            synchronized (cache)
            {
                simpleCollection = cache.get(dmoStoreId);
            }
        }
        return simpleCollection;
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

    private void addToCache(SimpleCollection sc)
    {
        cache.put(sc.getDmoStoreId(), sc);
        for (SimpleCollection kid : sc.getChildren())
        {
            addToCache(kid);
        }
    }

    private SimpleCollection getRootFromStore(DmoStoreId rootId) throws RepositoryException
    {
        UnitOfWork uow = new EasyUnitOfWork(null);
        SimpleCollection root = null;
        try
        {
            root = getRootFromStore(rootId, uow);
            if (root != null)
            {
                getChildrenFromStore(root);
            }
            else
            {
                root = createRoot(rootId, uow);
            }
        }
        finally
        {
            uow.close();
        }
        
        return root;
    }

    protected SimpleCollection getRootFromStore(DmoStoreId rootId, UnitOfWork uow) throws RepositoryException
    {
        SimpleCollection root = null;
        try
        {
            root = (SimpleCollection) uow.retrieveObject(rootId.getStoreId());
        }
        catch (ObjectNotInStoreException e)
        {
            // not found, so try to create it
        }
        return root;
    }

    private void getChildrenFromStore(SimpleCollection sc)
    {
        for (SimpleCollection kid : sc.getChildren())
        {
            getChildrenFromStore(kid);
        }
    }

    private SimpleCollection createRoot(DmoStoreId rootId, UnitOfWork uow) throws RepositoryException
    {
        SimpleCollection root = SimpleCollectionCreator.createRoot(rootId.getNamespace());
        addToStore(uow, root);
        try
        {
            uow.commit();
        }
        catch (UnitOfWorkInterruptException e)
        {
            // should not happen, because we are not interrupting.
            // in case of the unforseen:
            throw new RepositoryException("?? how come ??", e);
        }
        return root;
    }

    private void addToStore(UnitOfWork uow, SimpleCollection sc) throws RepositoryException
    {
        uow.attach(sc);
        for (SimpleCollection kid : sc.getChildren())
        {
            addToStore(uow, kid);
        }
    }





}
