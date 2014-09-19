package nl.knaw.dans.common.lang.repo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.exception.ConcurrentUpdateException;
import nl.knaw.dans.common.lang.repo.exception.LockAcquireTimeoutException;
import nl.knaw.dans.common.lang.repo.exception.ObjectExistsException;
import nl.knaw.dans.common.lang.repo.exception.ObjectIsNotDeletableException;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.repo.exception.UnitOfWorkInterruptException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Maintains a list of {@link DataModelObject}s (dmo's) affected by a business transaction and coordinates the writing out of changes to the {@link DmoStore}.
 * It is basically a transaction object, but it does not implement a real rollback at the moment. A simple rollback exists for newly ingested objects. This
 * AbstractUnitOfWork makes sure that simultaneous updates of several objects are executed thread-safely. If one transaction needs objects of another
 * transaction the other transaction needs to wait until the first one has finished. This system works for single transaction done on the DmoStore as well as
 * for bulk transaction using this object. DmoStore and UnitOfWork can be used interchangeably as the mutexes are shared between these objects (a single
 * IdSynchronizer is used).
 * 
 * @author ecco Oct 29, 2009
 * @author lobo (thread safety)
 */
public abstract class AbstractUnitOfWork implements UnitOfWork {
    private static final long serialVersionUID = -2990678911442209199L;

    private static final Logger logger = LoggerFactory.getLogger(AbstractUnitOfWork.class);

    private final Map<DmoStoreId, DataModelObject> cloud = new HashMap<DmoStoreId, DataModelObject>();

    private final List<UnitOfWorkListener> listeners = new ArrayList<UnitOfWorkListener>();

    private boolean inCommitMode;

    private String updateOwner;

    public AbstractUnitOfWork(String updateOwner) {
        this.updateOwner = updateOwner;
    }

    public void addListener(UnitOfWorkListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void addListeners(UnitOfWorkListener... unitOfWorkListeners) {
        if (unitOfWorkListeners != null) {
            for (UnitOfWorkListener listener : unitOfWorkListeners) {
                addListener(listener);
            }
        }

    }

    public boolean removeListener(UnitOfWorkListener listener) {
        return listeners.remove(listener);
    }

    public void attach(DataModelObject dmo) throws RepositoryException {
        if (inCommitMode)
            throw new IllegalStateException("Cannot attach while in commitMode!");
        cloud.put(dmo.getDmoStoreId(), dmo);
        dmo.setUnitOfWork(this);
    }

    public DataModelObject detach(DataModelObject dmo) {
        if (inCommitMode)
            throw new IllegalStateException("Cannot detach while in commitMode!");
        AbstractDataModelObject removed = (AbstractDataModelObject) cloud.remove(dmo.getDmoStoreId());
        if (removed != null) {
            removed.setUnitOfWork(null);
        }
        return removed;
    }

    /**
     * Commits all attached DMO's to the DmoStore. Ingesting new objects, updating existing ones and deleting the DMO's that are registered for deletion.
     */
    public void commit() throws RepositoryException, UnitOfWorkInterruptException {
        inCommitMode = true;
        final Set<DmoStoreId> sids = cloud.keySet();

        // lock dmo objects by their id's
        IdSynchronizer<DmoStoreId> sidSynchronizer = getSidSynchronizer();
        try {
            sidSynchronizer.acquireLock(sids);
        }
        catch (InterruptedException e) {
            throw new UnitOfWorkInterruptException("UnitOfWork got interrupted while trying to a acquire a lock on " + StringUtils.join(sids, ", ") + ".", e);
        }
        catch (LockAcquireTimeoutException e) {
            throw new UnitOfWorkInterruptException("UnitOfWork timedout trying to get a lock on " + StringUtils.join(sids, ", ") + ".", e);
        }

        try {

            // check in advance if all objects are ready to be
            // ingested, updated or purged
            DmoStore store = getStore();
            for (DataModelObject dmo : cloud.values()) {
                // can delete?
                if (dmo.isRegisteredDeleted() && !dmo.isDeletable()) {
                    throw new ObjectIsNotDeletableException("Object " + dmo.toString() + " is not deletable.");
                }
                // can update?
                else if (dmo.isLoaded() && !store.isUpdateable(dmo, updateOwner)) {
                    throw new ConcurrentUpdateException("UnitOfWork aborting commit, because object " + dmo.toString() + " is not updateable.");
                }
                // ingest is currently always possible
            }

            logger.info("Now commiting: {}", StringUtils.join(sids, ", "));

            for (DataModelObject dmo : cloud.values()) {
                if (dmo.isRegisteredDeleted()) {
                    logger.debug("Dataset registered deleted. Purging.");
                    purge(dmo);
                } else if (dmo.isLoaded()) {
                    logger.debug("Dataset is loaded.  Updating");
                    update(dmo);
                } else {
                    logger.debug("Dataset is new.  Ingesting.");
                    ingest(dmo);
                }
            }
        }
        finally {
            sidSynchronizer.releaseLock(sids);
            inCommitMode = false;
        }
    }

    public DataModelObject saveAndDetach(DataModelObject dmo) throws RepositoryException, UnitOfWorkInterruptException {
        if (dmo.isRegisteredDeleted()) {
            purge(dmo);
        } else if (dmo.isLoaded()) {
            update(dmo);
        } else {
            ingest(dmo);
        }
        return detach(dmo);
    }

    // /**
    // * WARNING: This is not a true rollback as it only purges objects, but does
    // * not return objects to their original state if they were updated. True
    // * rollback would be expensive as it would require the original objects to
    // * be retrieved before commit.
    // */
    // public void rollBack(String logMessage) throws RepositoryException
    // {
    // for (DataModelObject dmo : ingestedObjects)
    // {
    // getStore().purge(dmo, false, logMessage);
    // for (UnitOfWorkListener listener : listeners)
    // {
    // listener.afterPurge(dmo);
    // }
    // }
    // }

    public void close() {
        for (DataModelObject dmo : cloud.values()) {
            ((AbstractDataModelObject) dmo).setUnitOfWork(null);
        }
        cloud.clear();
    }

    public DataModelObject getObject(DmoStoreId dmoStoreId) throws RepositoryException {
        return cloud.get(dmoStoreId);
    }

    public DataModelObject retrieveObject(DmoStoreId dmoStoreId) throws ObjectNotInStoreException, RepositoryException {
        DataModelObject dmo = getObject(dmoStoreId);
        if (dmo == null) {
            dmo = getStore().retrieve(dmoStoreId);

            logger.debug("Loaded DataModelObject with storeId " + dmoStoreId);

            dmo.setUnitOfWork(this);
            cloud.put(dmoStoreId, dmo);
            for (UnitOfWorkListener listener : listeners) {
                listener.afterRetrieveObject(dmo);
            }
        }

        return dmo;
    }

    private void update(DataModelObject dmo) throws RepositoryException, UnitOfWorkInterruptException {
        for (UnitOfWorkListener listener : listeners) {
            interruptOnCancel(listener.onUpdate(dmo));
        }
        getStore().update(dmo, false, getUpdateLogMessage(dmo), getUpdateOwner());
        for (UnitOfWorkListener listener : listeners) {
            listener.afterUpdate(dmo);
        }
    }

    private void ingest(DataModelObject dmo) throws ObjectExistsException, RepositoryException, UnitOfWorkInterruptException {
        for (UnitOfWorkListener listener : listeners) {
            interruptOnCancel(listener.onIngest(dmo));
        }
        getStore().ingest(dmo, getIngestLogMessage(dmo));
        for (UnitOfWorkListener listener : listeners) {
            listener.afterIngest(dmo);
        }
        // ingestedObjects.add(dmo);
    }

    private void purge(DataModelObject dmo) throws RepositoryException, UnitOfWorkInterruptException {
        for (UnitOfWorkListener listener : listeners) {
            interruptOnCancel(listener.onPurge(dmo));
        }
        getStore().purge(dmo, false, getPurgeLogMessage(dmo));

        for (UnitOfWorkListener listener : listeners) {
            listener.afterPurge(dmo);
        }
    }

    private void interruptOnCancel(boolean canceled) throws UnitOfWorkInterruptException {
        if (canceled) {
            throw new UnitOfWorkInterruptException("The process was canceled.");
        }
    }

    private IdSynchronizer<DmoStoreId> getSidSynchronizer() {
        DmoStore store = getStore();
        if (store instanceof AbstractDmoStore) {
            return ((AbstractDmoStore) store).getSidSynchronizer();
        } else {
            logger.warn("Store was not of type AbstractDmoStore, so no SidSynchronizer " + "could be found. This should currently happen only in junit tests.");
            return new IdSynchronizer<DmoStoreId>();
        }
    }

    @Override
    public Collection<DataModelObject> getAttachedObjects() {
        return cloud.values();
    }

    /**
     * @see DmoStore.update
     */
    @Override
    public String getUpdateOwner() {
        return updateOwner;
    }

    public abstract DmoStore getStore();

    protected abstract String getUpdateLogMessage(DataModelObject dmo);

    protected abstract String getIngestLogMessage(DataModelObject dmo);

    protected abstract String getPurgeLogMessage(DataModelObject dmo);
}
