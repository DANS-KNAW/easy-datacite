package nl.knaw.dans.common.lang.repo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.exception.ConcurrentUpdateException;
import nl.knaw.dans.common.lang.repo.exception.DmoStoreEventListenerException;
import nl.knaw.dans.common.lang.repo.exception.LockAcquireTimeoutException;
import nl.knaw.dans.common.lang.repo.exception.ObjectDeserializationException;
import nl.knaw.dans.common.lang.repo.exception.ObjectExistsException;
import nl.knaw.dans.common.lang.repo.exception.ObjectIsNotDeletableException;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.repo.exception.ObjectSerializationException;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract Data Model Object store
 * 
 * @see FedoraDmoStore
 * @author lobo Nov 12, 2009
 */
public abstract class AbstractDmoStore implements DmoStore {

    /**
     * The time in milliseconds this DmoStore gets to try to acquire a lock on a certain data model object. Since the locking mechanism is currently being
     * shared with the AbstractUnitOfWork this does not apply to single operations only, but also means waiting for transactions to finish.
     */
    private static final int DEFAULT_LOCK_TIMEOUT = 1000 * 5 * 60; // 5 mins

    private static final Logger logger = LoggerFactory.getLogger(AbstractDmoStore.class);

    /**
     * Used for synchronizing concurrent operations on DMO's of a certain StoreId
     */
    private final IdSynchronizer<DmoStoreId> sidSynchronizer = new IdSynchronizer<DmoStoreId>(DEFAULT_LOCK_TIMEOUT, true);

    private final List<DmoStoreEventListener> listeners = Collections.synchronizedList(new ArrayList<DmoStoreEventListener>());

    private DmoInvalidator invalidator = new DmoInvalidator() {
        @Override
        protected DateTime getLastModified(DmoStoreId dmoStoreId) throws RepositoryException {
            return AbstractDmoStore.this.getLastModified(dmoStoreId);
        }
    };

    private DmoUpdateConcurrencyGuard concurrencyGuard = new DmoUpdateConcurrencyGuard();

    private String name;

    protected abstract String doIngest(DataModelObject dmo, String logMessage) throws RepositoryException, ObjectSerializationException,
            DmoStoreEventListenerException, ObjectExistsException;

    protected abstract DateTime doUpdate(DataModelObject dmo, boolean skipChangeChecking, String logMessage) throws DmoStoreEventListenerException,
            RepositoryException;

    protected abstract DataModelObject doRetrieve(DmoStoreId dmoStoreId) throws ObjectNotInStoreException, RepositoryException, ObjectDeserializationException;

    protected abstract DateTime doPurge(DataModelObject dmo, boolean force, String logMessage) throws DmoStoreEventListenerException, RepositoryException;

    public AbstractDmoStore(String name) {
        init(name);
    }

    private void init(String name) {
        AbstractDmoFactory.setSidDispenser(this);
        this.name = name;
        DmoStores.get().register(this);
    }

    @Override
    public void setConcurrencyGuard(DmoUpdateConcurrencyGuard concurrencyGuard) {
        this.concurrencyGuard = concurrencyGuard;
    }

    /**
     * {@inheritDoc}
     */
    public String ingest(DataModelObject dmo, String logMessage) throws ObjectExistsException, RepositoryException {
        String returnId = "";
        DmoStoreId dmoStoreId = dmo.getDmoStoreId();
        acquireLock(dmoStoreId, "ingest");
        try {
            // call listeners
            beforeIngest(dmo);

            // do the ingest
            returnId = doIngest(dmo, logMessage);

            // set state
            ((AbstractDataModelObject) dmo).setStoreName(this.getName());
            dmo.setLoaded(true);
            dmo.setDirty(false);
            invalidator.setInvalidated(dmo, false);
            DateTime ingestTime = new DateTime();
            for (MetadataUnit mdUnit : dmo.getMetadataUnits()) {
                mdUnit.setDirty(false);
                mdUnit.setTimestamp(ingestTime);
            }

            // call listeners
            informIngested(dmo);
        }
        finally {
            sidSynchronizer.releaseLock(dmoStoreId);
        }

        return returnId;
    }

    /**
     * Update the entire DataModelObject and all of it's units indiscriminate of the state of dirty flags of the object and it's units. Notice that this
     * operation may be time consuming and inefficient.
     * 
     * @see #update(DataModelObject, boolean, String)
     * @param dmo
     *        the DataModelObject to update
     * @param logMessage
     *        a log message
     * @return the timestamp of the update according to the store or <code>null</code> if no update took place
     * @throws ConcurrentUpdateException
     *         if a concurrent update took place
     * @throws RepositoryException
     *         wrapper for exceptions
     */
    public DateTime update(DataModelObject dmo, String logMessage) throws ConcurrentUpdateException, RepositoryException {
        return update(dmo, false, logMessage);
    }

    /**
     * {@inheritDoc}
     */
    public DateTime update(DataModelObject dmo, boolean skipChangeChecking, String logMessage) throws ConcurrentUpdateException, RepositoryException {
        return update(dmo, skipChangeChecking, logMessage, null);
    }

    /**
     * {@inheritDoc}
     */
    public DateTime update(DataModelObject dmo, boolean skipChangeChecking, String logMessage, String updateOwner) throws ConcurrentUpdateException,
            RepositoryException
    {
        DmoStoreId dmoStoreId = dmo.getDmoStoreId();
        acquireLock(dmoStoreId, "update");
        try {
            if (!isUpdateable(dmo, updateOwner)) {
                throw new ConcurrentUpdateException(dmo.toString() + " is not updateable. Update process halted.");
            }

            // call listeners
            beforeUpdate(dmo);

            // update
            DateTime returnDateTime = doUpdate(dmo, skipChangeChecking, logMessage);

            if (returnDateTime != null) {
                // set state
                ((AbstractDataModelObject) dmo).setStoreName(this.getName());
                // make sure this next statement is called before
                dmo.setLoaded(true);
                invalidator.invalidate(dmoStoreId, dmo);
                if (concurrencyGuard != null)
                    concurrencyGuard.onDmoUpdate(dmo, updateOwner);

                dmo.setLoaded(true);
                dmo.setDirty(false);

                // call listeners
                informUpdated(dmo);
            }

            return returnDateTime;
        }
        finally {
            sidSynchronizer.releaseLock(dmoStoreId);
        }
    }

    /**
     * {@inheritDoc}
     */
    public DataModelObject retrieve(DmoStoreId dmoStoreId) throws ObjectNotInStoreException, RepositoryException {
        DataModelObject dmo = null;
        acquireLock(dmoStoreId, "retrieve");
        try {
            dmo = doRetrieve(dmoStoreId);

            invalidator.setInvalidated(dmo, false);
            ((AbstractDataModelObject) dmo).setStoreName(this.getName());
            dmo.setDirty(false);
            dmo.setLoaded(true);
        }
        catch (ObjectDeserializationException e) {
            logger.error("Could not retrieve DMO by its id: {}", dmoStoreId.toString());
            throw e;
        }
        finally {
            sidSynchronizer.releaseLock(dmoStoreId);
        }

        return dmo;
    }

    /**
     * {@inheritDoc}
     */
    public DateTime purge(DataModelObject dmo, boolean force, String logMessage) throws ObjectNotInStoreException, ObjectIsNotDeletableException,
            RepositoryException
    {
        DmoStoreId dmoStoreId = dmo.getDmoStoreId();
        acquireLock(dmoStoreId, "purge");
        try {
            /*
             * No check on deletability because it depends on previous deletes from the database which at this point have not been processed yet.
             */

            if (dmo.isInvalidated()) {
                throw new ConcurrentUpdateException(dmo.toString() + " is not up to date. As a rule it can therefore not be purged.");
            }

            beforePurged(dmo);

            // fedora.server.errors.GeneralException: Forced object removal is not yet supported.
            // therefore 'force' on this level always false.
            DateTime purgeTime = doPurge(dmo, false, logMessage);

            if (purgeTime != null) {
                // set state
                invalidator.invalidate(dmoStoreId);

                // call listeners
                informPurged(dmo);
            }

            return purgeTime;
        }
        finally {
            sidSynchronizer.releaseLock(dmoStoreId);
        }
    }

    private void acquireLock(DmoStoreId dmoStoreId, String operationName) throws RepositoryException {
        try {
            sidSynchronizer.acquireLock(dmoStoreId);
        }
        catch (InterruptedException e) {
            throw new RepositoryException("DmoStore " + getName() + " was interrupted before finishing " + operationName + " operation on " + dmoStoreId + ".",
                    e);
        }
        catch (LockAcquireTimeoutException e) {
            throw new RepositoryException("DmoStore " + getName() + " timedout trying to get a lock for operation " + operationName + "  on " + dmoStoreId
                    + ".", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInvalidated(DataModelObject dmo) throws RepositoryException {
        return invalidator.isInvalidated(dmo);
    }

    public boolean isUpdateable(DataModelObject dmo) throws RepositoryException {
        return isUpdateable(dmo, null);
    }

    public boolean isUpdateable(DataModelObject dmo, String updateOwner) throws RepositoryException {
        if (concurrencyGuard == null)
            return true;
        else
            return concurrencyGuard.isUpdateable(dmo, updateOwner);
    }

    /**
     * {@inheritDoc}
     */
    public void setEventListeners(List<DmoStoreEventListener> storeEventListeners) {
        for (DmoStoreEventListener listener : storeEventListeners) {
            addEventListener(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addEventListener(DmoStoreEventListener storeEventListener) {
        synchronized (listeners) {
            listeners.add(storeEventListener);
            logger.info("Registered DmoStoreEventListener " + storeEventListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean removeEventListener(DmoStoreEventListener storeEventListener) {
        synchronized (listeners) {
            boolean removed = listeners.remove(storeEventListener);
            if (removed) {
                logger.info("Removed DmoSoreEventListener " + storeEventListener);
            }
            return removed;
        }
    }

    /**
     * Returns a copy of the listeners. This enables thread-safe iteration, without having to lock the calls on the listeners as well.
     */
    public List<DmoStoreEventListener> getListeners() {
        synchronized (listeners) {
            return new ArrayList<DmoStoreEventListener>(listeners);
        }
    }

    /**
     * Inform registered listeners of a DataModeObject ingest that is about to happen.
     * 
     * @param dmo
     *        the ingested DataModelObject
     * @throws DmoStoreEventListenerException
     */
    protected void beforeIngest(DataModelObject dmo) throws DmoStoreEventListenerException {
        synchronized (listeners) {
            for (DmoStoreEventListener listener : getListeners()) {
                listener.beforeIngest(this, dmo);
            }
        }
    }

    /**
     * Inform registered listeners of the successful ingest of a DataModeObject.
     * 
     * @param dmo
     *        the ingested DataModelObject
     * @throws DmoStoreEventListenerException
     */
    protected void informIngested(DataModelObject dmo) throws DmoStoreEventListenerException {
        synchronized (listeners) {
            for (DmoStoreEventListener listener : getListeners()) {
                listener.afterIngest(this, dmo);
            }
        }
    }

    /**
     * Inform registered listeners of an update on a DataModeObject that is about to happen.
     * 
     * @param dmo
     *        the updated DataModelObject
     * @throws DmoStoreEventListenerException
     */
    protected void beforeUpdate(DataModelObject dmo) throws DmoStoreEventListenerException {
        synchronized (listeners) {
            for (DmoStoreEventListener listener : getListeners()) {
                listener.beforeUpdate(this, dmo);
            }
        }
    }

    /**
     * Inform registered listeners of the successful update of a DataModeObject.
     * 
     * @param dmo
     *        the updated DataModelObject
     * @throws DmoStoreEventListenerException
     */
    protected void informUpdated(DataModelObject dmo) throws DmoStoreEventListenerException {
        synchronized (listeners) {
            for (DmoStoreEventListener listener : getListeners()) {
                listener.afterUpdate(this, dmo);
            }
        }
    }

    /**
     * Inform registered listeners of an erroneous, possibly partial update of a DataModeObject.
     * 
     * @param storeId
     *        the storeId of the partially updated DataModelObject
     * @throws DmoStoreEventListenerException
     */
    protected void informPartialUpdated(DataModelObject dmo) throws DmoStoreEventListenerException {
        synchronized (listeners) {
            for (DmoStoreEventListener listener : getListeners()) {
                listener.afterPartialUpdate(this, dmo);
            }
        }
    }

    /**
     * Inform registered listeners of a purge on a id that is about to happen.
     * 
     * @throws DmoStoreEventListenerException
     */
    protected void beforePurged(DataModelObject dmo) throws DmoStoreEventListenerException {
        synchronized (listeners) {
            for (DmoStoreEventListener listener : getListeners()) {
                listener.beforePurge(this, dmo);
            }
        }
    }

    /**
     * Inform registered listeners of the successful purge of a DataModeObject.
     * 
     * @throws DmoStoreEventListenerException
     */
    protected void informPurged(DataModelObject dmo) throws DmoStoreEventListenerException {
        synchronized (listeners) {
            for (DmoStoreEventListener listener : getListeners()) {
                listener.afterPurge(this, dmo);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return name;
    }

    public IdSynchronizer<DmoStoreId> getSidSynchronizer() {
        return sidSynchronizer;
    }

}
