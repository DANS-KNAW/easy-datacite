package nl.knaw.dans.common.lang.repo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import nl.knaw.dans.common.lang.PooledObject;
import nl.knaw.dans.common.lang.RepositoryException;

import org.joda.time.DateTime;

/**
 * This class is responsible for keeping track of which data model object have been invalidated. It is a
 * package level class that works in close cooporation with AbstractDmoStore. It is not to be used by the
 * public, instead the public currently use the DmoStore.
 * 
 * @author lobo
 */
abstract class DmoInvalidator
{
    /**
     * A huge pool in which weak-references data model objects get stored and information about their
     * invalidation status get stored.
     */
    private final DmoPool<DmoInvalidationInfo> dmoPool = new DmoPool<DmoInvalidationInfo>(10000);

    protected static final long LAST_MODIFIED_CACHE_EXPIRES = TimeUnit.SECONDS.toNanos(60 * 130);

    private Map<DmoStoreId, Long> lastModifiedCache = new HashMap<DmoStoreId, Long>();

    public void setInvalidated(DataModelObject dmo, boolean invalidated)
    {
        dmoPool.add(dmo, new DmoInvalidationInfo(invalidated));
    }

    /**
     * Abstract method that needs to be implemented to retrieve the last modified date from the server.
     * 
     * @param storeId
     *        the store id to retrieve the last modified date from
     * @return last modified date
     * @throws RepositoryException
     *         wrapper exception
     */
    abstract protected DateTime getLastModified(DmoStoreId dmoStoreId) throws RepositoryException;

    /**
     * Invalidates all active objects of a certain sid with possible exceptions
     * 
     * @param storeId
     *        the sid to invalidate
     * @param exceptions
     *        objects to make exceptions for
     */
    public void invalidate(DmoStoreId dmoStoreId, DataModelObject... exceptions)
    {
        setLastChangedTime(dmoStoreId, System.nanoTime());

        List<PooledObject<DataModelObject, DmoInvalidationInfo>> activeDmoList = dmoPool.get(dmoStoreId);
        for (PooledObject<DataModelObject, DmoInvalidationInfo> activeDmo : activeDmoList)
        {
            boolean isException = false;
            if (exceptions != null)
            {
                for (DataModelObject exception : exceptions)
                {
                    if (activeDmo.getObject() == exception)
                        isException = true;
                }
            }

            if (!isException)
                activeDmo.getInfo().setInvalidated(true);
        }
    }

    private void setLastChangedTime(DmoStoreId dmoStoreId, long updateNanoTime)
    {
        lastModifiedCache.put(dmoStoreId, updateNanoTime);

        // remove entries that are too long in this map (cleanup)
        long now = System.nanoTime();
        Iterator<Entry<DmoStoreId, Long>> it = lastModifiedCache.entrySet().iterator();
        while (it.hasNext())
        {
            Entry<DmoStoreId, Long> timeEntry = it.next();
            long delta = now - timeEntry.getValue();
            if (delta > LAST_MODIFIED_CACHE_EXPIRES)
                it.remove();
        }
    }

    /**
     * Implements a three-fold mechanism. First of all it checks if the object is known in the weak
     * referenced pool of active objects in which the invalidation status is actively kept. Then it
     * checks the last modified cache to see if the object has been changed recently. If that is not
     * possible, because the object is older than the cache, then the repository is queried for the last
     * modified date.
     */
    public boolean isInvalidated(DataModelObject dmo) throws RepositoryException
    {
        // if the object is known we can trust that is has a correct validation value
        DmoStoreId dmoStoreId = dmo.getDmoStoreId();
        List<PooledObject<DataModelObject, DmoInvalidationInfo>> activeDmoList = dmoPool.get(dmoStoreId);
        for (PooledObject<DataModelObject, DmoInvalidationInfo> activeDmo : activeDmoList)
        {
            if (activeDmo.getObject() == dmo)
                return activeDmo.getInfo().isInvalidated();
        }

        DmoInvalidationInfo invalidationInfo = new DmoInvalidationInfo(false);
        dmoPool.add(dmo, invalidationInfo);
        if (!dmo.isLoaded())
            return false;

        // is the load time inside of the boundaries of what is stored in the
        // lastChangedTime map?
        long loadTime = dmo.getloadTime();
        long delta = System.nanoTime() - loadTime;
        if (delta < LAST_MODIFIED_CACHE_EXPIRES)
        {
            Long lastChanged = lastModifiedCache.get(dmoStoreId);
            if (lastChanged != null)
                invalidationInfo.setInvalidated(lastChanged > loadTime);
        }
        else
        {
            // in the last case check the repository for a change in the last modified date
            DateTime lastModified = getLastModified(dmoStoreId);
            invalidationInfo.setInvalidated(lastModified.isAfter(dmo.getLastModified()));
        }
        return invalidationInfo.isInvalidated();
    }

    public int getReferenceCount()
    {
        return dmoPool.getReferenceCount();
    }

    public int getObjectCount()
    {
        return dmoPool.getObjectIdCount();
    }

    protected DmoPool<DmoInvalidationInfo> getDmoPool()
    {
        return dmoPool;
    }

    /**
     * Kept with each weak-referenced dmo in the pool. Stores a simple boolean on whether the dmo is
     * invalidated.
     * 
     * @author lobo
     */
    public class DmoInvalidationInfo implements Serializable
    {
        private static final long serialVersionUID = -8578764605337913196L;

        private boolean invalidated = false;

        public DmoInvalidationInfo(boolean invalidated)
        {
            this.invalidated = invalidated;
        }

        public void setInvalidated(boolean invalidated)
        {
            this.invalidated = invalidated;
        }

        public boolean isInvalidated()
        {
            return invalidated;
        }
    }

}
