package nl.knaw.dans.common.lang;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Maintains a thread-safe pool of weak-referenced identifiable objects. Objects with the same identifier
 * will be pooled. An info object can be added to each object allowing the object to be sort-of extended
 * without changing the object. Weak-referenced - Objects that are added to this pool may be collected by
 * the garbage collector. Identifiable - It is assumed that the objects stored in this pool can be
 * identified by some kind of an identifier. Objects of the same identifier will then be pooled.
 * 
 * @param <ID>
 *        The type of the object identifier
 * @param <T>
 *        The type of the object stored in the pool
 * @param <I>
 *        An additional info object, which can store information about the object that cannot be stored
 *        in the object itself.
 * @author lobo, 25-01-2010
 */
public class WeakObjectRefPool<ID, T, I>
{
    private static final int DEFAULT_INITIAL_CAPACITY = 1000;

    /**
     * the pool based on the sid of the data model object
     */
    private final Map<ID, List<PooledObjectRef>> pool;

    /**
     * the queue in which the dead objects will magically appear
     */
    private final ReferenceQueue<T> deadObjects = new ReferenceQueue<T>();

    /**
     * An index to keep track of which pool object was stored where. This aids in speeding up the
     * cleaning process of the references.
     */
    private final IdentityHashMap<WeakReference<T>, PoolIndex> poolIndex;

    private class PooledObjectRef extends PooledObject<WeakReference<T>, I>
    {
        private static final long serialVersionUID = 3000628487640557589L;
    }

    private class PoolIndex
    {
        public ID ojbectId;
        public List<PooledObjectRef> refs;
        public int index;
    }

    public WeakObjectRefPool()
    {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    public WeakObjectRefPool(int initialCapactiy)
    {
        pool = new HashMap<ID, List<PooledObjectRef>>(initialCapactiy);
        poolIndex = new IdentityHashMap<WeakReference<T>, PoolIndex>(initialCapactiy);
    }

    public synchronized void add(ID objectId, PooledObject<T, I> obj)
    {
        add(objectId, obj.getObject(), obj.getInfo());
    }

    /**
     * Adds a weak reference of this object to the pool.
     * 
     * @param objectId
     *        the of the object
     * @param obj
     *        the object
     */
    public synchronized void add(ID objectId, T obj, I info)
    {
        // do this regularly
        removeDeadObjects();
        // do this regularly

        List<PooledObjectRef> poolObjs = pool.get(objectId);
        if (poolObjs == null)
            poolObjs = new ArrayList<PooledObjectRef>();
        else
        {
            for (PooledObjectRef poolObj : poolObjs)
            {
                if (poolObj != null && obj == poolObj.getObject().get())
                {
                    poolObj.setInfo(info);
                    // duplicate entry, do not add
                    return;
                }
            }
        }

        // add reference to pool
        PooledObjectRef poolObj = new PooledObjectRef();
        WeakReference<T> weakRef = new WeakReference<T>(obj, deadObjects);
        poolObj.setObject(weakRef);
        poolObj.setInfo(info);
        poolObjs.add(poolObj);
        pool.put(objectId, poolObjs);

        // add pool index to reference index
        PoolIndex pIdx = new PoolIndex();
        pIdx.refs = poolObjs;
        pIdx.ojbectId = objectId;
        pIdx.index = poolObjs.size() - 1;
        poolIndex.put(weakRef, pIdx);
    }

    /**
     * Gets all stored references to still existing objects from the object pool
     * 
     * @param objectId
     *        the id of the objects for which the reference need to be gotten
     */
    public synchronized List<PooledObject<T, I>> get(ID objectId)
    {
        // do this regularly
        removeDeadObjects();
        // do this regularly

        List<PooledObjectRef> poolObjs = pool.get(objectId);
        if (poolObjs == null)
            return Collections.emptyList();

        // copy hard refs
        List<PooledObject<T, I>> objList = new ArrayList<PooledObject<T, I>>(poolObjs.size());
        Iterator<PooledObjectRef> poolObjRefIt = poolObjs.iterator();
        while (poolObjRefIt.hasNext())
        {
            PooledObjectRef poolObj = poolObjRefIt.next();
            if (poolObj == null)
                continue;

            T object = poolObj.getObject().get();
            if (object != null)
            {
                // add active references
                objList.add(new PooledObject<T, I>(object, poolObj.getInfo()));
            }
        }

        return objList;
    }

    /**
     * @return the number of uniquely identified objects are stored in the pool at this moment
     */
    public synchronized int getObjectIdCount()
    {
        // do this regularly
        removeDeadObjects();
        // do this regularly

        return pool.size();
    }

    /**
     * @return the number of valid references held in the pool at this moment
     */
    public synchronized int getReferenceCount()
    {
        // do this regularly
        removeDeadObjects();
        // do this regularly

        int count = 0;
        for (List<PooledObjectRef> poolObjs : pool.values())
        {
            // count all non-null objs
            for (PooledObjectRef pooledObj : poolObjs)
            {
                if (pooledObj != null)
                    count++;
            }
        }
        return count;
    }

    /**
     * Called internally for removing dead object references from the pool. This method was sped up by
     * the refIndex which stores a list of which references lists exist for which object identifiers.
     */
    private void removeDeadObjects()
    {
        Reference weakReference = deadObjects.poll();
        while (weakReference != null)
        {
            // search the index of the reference in the pool by the reference
            PoolIndex pIdx = poolIndex.get(weakReference);

            // set object to null instead of removing it, so other indices
            // do not corrupt
            pIdx.refs.set(pIdx.index, null);
            boolean allNull = true;
            for (PooledObjectRef ref : pIdx.refs)
            {
                if (ref != null)
                {
                    allNull = false;
                    break;
                }
            }
            if (allNull)
                pool.remove(pIdx.ojbectId);

            poolIndex.remove(weakReference);

            weakReference = deadObjects.poll();
        }
    }

    @Override
    public String toString()
    {
        return super.toString() + "[object id count=" + getObjectIdCount() + ", reference count=" + getReferenceCount() + "]";
    }

}
