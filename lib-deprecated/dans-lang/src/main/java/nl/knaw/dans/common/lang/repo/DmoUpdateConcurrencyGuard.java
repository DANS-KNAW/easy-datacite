package nl.knaw.dans.common.lang.repo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import nl.knaw.dans.common.lang.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This object saves the necessary info about dmo's, so that it can determine if a dmo can be changed or
 * not. The main idea of this class is that a dmo can only be overwritten with an older version of itself
 * if the user who decides to do this was the same person who made the new version. This class also uses
 * the invalidation mechanism to check if a dmo is not already outdated.
 * 
 * @author ecco (initial version)
 * @author lobo (refactored to work in combination with the invalidation mechanism)
 */
public class DmoUpdateConcurrencyGuard
{

    private static final Logger logger = LoggerFactory.getLogger(DmoUpdateConcurrencyGuard.class);

    // Width of TIME_WINDOW should be > session.timeOut of user interface
    protected static final long TIME_WINDOW = TimeUnit.SECONDS.toNanos(60 * 130);

    private Map<String, UpdateRecord> updateRecords = Collections.synchronizedMap(new HashMap<String, UpdateRecord>());

    // could use a listener for this too...
    public void onDmoUpdate(DataModelObject dmo, String updateOwner)
    {
        String storeId = dmo.getStoreId();

        synchronized (updateRecords)
        {
            //
            cleanupExpiredInfo(); // could be done in hourly thread
            //
            UpdateRecord updateRecord = updateRecords.get(storeId);
            if (updateRecord == null)
            {
                updateRecord = new UpdateRecord(storeId, updateOwner);
                updateRecords.put(storeId, updateRecord);
            }
            else
            {
                updateRecord.setUpdated(updateOwner);
            }
        }
    }

    public boolean isUpdateable(DataModelObject dmo, String updateOwner) throws RepositoryException
    {
        if (!dmo.isLoaded())
            return false;

        synchronized (updateRecords)
        {
            UpdateRecord updateRecord = updateRecords.get(dmo.getStoreId());
            if (updateRecord != null)
            {
                return updateRecord.isUpdateable(dmo.getloadTime(), updateOwner);
            }
        }

        // if no update records could be found then simply check if
        // the object was not invalidated. If it is invalidated you
        // do not have the right to update it.
        return !dmo.isInvalidated();
    }

    private void cleanupExpiredInfo()
    {
        // remove entries that are too long in this map (cleanup)
        long now = System.nanoTime();
        Iterator<Entry<String, UpdateRecord>> it = updateRecords.entrySet().iterator();
        while (it.hasNext())
        {
            Entry<String, UpdateRecord> changeEntry = it.next();
            if (changeEntry.getValue().hasExpired(now))
                it.remove();
        }
    }

    private class UpdateRecord
    {
        private final String storeId;
        private String updateOwner = null;
        private long previousUpdateOwnerLastUpdateTime;
        private String previousUpdateOwner = null;
        private long lastUpdateTime;

        public UpdateRecord(String storeId, String updateOwner)
        {
            lastUpdateTime = System.nanoTime();
            this.storeId = storeId;
            this.updateOwner = updateOwner;
        }

        @SuppressWarnings("unused")
        public String getStoreId()
        {
            return storeId;
        }

        public void setUpdated(String newUpdateOwner)
        {
            lastUpdateTime = System.nanoTime();
            if (newUpdateOwner == null)
                return;

            if (this.updateOwner != null && !this.updateOwner.equals(newUpdateOwner))
            {
                previousUpdateOwner = new String(this.updateOwner);
                previousUpdateOwnerLastUpdateTime = lastUpdateTime;
            }
            updateOwner = newUpdateOwner;
        }

        /**
         * The main idea is that a dmo can only be overwritten with an older version of itself IF the
         * user who decides to do this was the same person who made the new version.
         */
        public boolean isUpdateable(long loadTime, String updater)
        {
            if (loadTime >= lastUpdateTime)
            {
                return true;
            }
            else if (updateOwner != null && updateOwner.equals(updater))
            {
                if (previousUpdateOwner != null && previousUpdateOwnerLastUpdateTime > loadTime)
                {
                    logger.debug("Dmo with storeId " + storeId + " is not updatable for " + updater + "," + "because the object was loaded (" + loadTime
                            + ") before the previous update owner (" + previousUpdateOwner + ") made " + "his or her last change ("
                            + previousUpdateOwnerLastUpdateTime + ") ." + toString());
                    return false;
                }

                logger.debug("Dmo with storeId " + storeId + " is updateable for " + updater + "." + toString());
                return true;
            }
            else
            {
                logger.debug("Dmo with storeId " + storeId + " is not updateable for " + updater + "." + toString());
                return false;
            }
        }

        public boolean hasExpired(long time)
        {
            long delta = time - lastUpdateTime;
            return delta > TIME_WINDOW;
        }

        @Override
        public String toString()
        {
            return super.toString() + " lastChangeTime=" + lastUpdateTime + " changeOwner=" + updateOwner + " previousChangeOwnerLastChangeTime="
                    + previousUpdateOwnerLastUpdateTime + " previousChangeOwner=" + previousUpdateOwner;
        }
    }

}
