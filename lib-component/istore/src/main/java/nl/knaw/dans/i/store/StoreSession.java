package nl.knaw.dans.i.store;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;

/**
 * A {@link StoreSession} handles a cloud of DataModelObjects and can commit them to the repository. The
 * cloud of DataModelObjects is held in a temporary cache. Individual DataModelObjects can be attached to
 * this {@link StoreSession}. DataModelObjects can be retrieved, either from the cache, or from the
 * repository, if they were not yet in cache. All objects in the cloud can be persisted to the repository
 * with a call to {@link #commit()}. The method {@link #close()} detaches all DataModelObjects from this
 * StoreSession and must be called after the session is done, preferably in a <code>finally</code>
 * statement.
 * <p/>
 * Typical usage:
 * <pre>
 *         ...
 *     (1) StoreSession session = storeManager.newStoreSession("ownerId");
 *         try 
 *         {
 *             MyDataModelObject myDmo = (MyDataModelObject) session.getDataModelObject(myDmoStoreId);
 *             // ...
 *     (2)     // do things with myDmo and its associated classes
 *             // ...
 *     (3)     session.commit();
 *         }
 *         finally
 *         {
 *     (4)     session.close();
 *         }
 *         ...
 * </pre>
 * <ol>
 * <li>An instance of StoreSession is obtained from an implementation of StoreManager.</li>
 * <li>DataModelObjects are deserializations of xml. Associations to other DataModelObjects are not concrete.
 * However, during the time a DataModelObject is attached to a StoreSession, you can traverse the
 * object graph of associated DataModelObjects, and let them interact.</li>
 * <li>After the interaction, all affected DataModelObjects are committed to the repository.</li>
 * <li>In a final clause, the temporary association with the session is released. Associations from
 * one DataModelObject to another that were realized during the session, remain intact.</li>
 * </ol>
 * <p/>
 */
public interface StoreSession
{

    /**
     * Attach the given DataModelObject to this StoreSession. If the given DataModelObject has no
     * storeId, it will be given one.
     * 
     * @param dmo
     *        DataModelObject to attach
     * @throws RepositoryException
     *         for repository exceptions.
     */
    void attach(DataModelObject dmo) throws RepositoryException;

    /**
     * Get the DataModelObject with the given dmoStoreId. If the object is not in cache, it will be
     * gotten from store. If it was not attached, it will be attached to this StoreSession.
     * 
     * @param dmoStoreId
     *        DmoStoreId of the DataModelObject to get.
     * @return the DataModelObject with the given dmoStoreId.
     * @throws ObjectNotInStoreException
     *         if the given object was not in cache and not in store.
     * @throws RepositoryException
     *         for repository exceptions.
     */
    DataModelObject getDataModelObject(DmoStoreId dmoStoreId) throws ObjectNotInStoreException, RepositoryException;

    /**
     * Detach the given DataModelObject from this StoreSession.
     * 
     * @param dmo
     *        DataModelObject to detach
     * @return detached object or <code>null</code> if it was not attached.
     */
    DataModelObject detach(DataModelObject dmo);

    /**
     * Save the given DataModelObject to the repository and detach it from this StoreSession.
     * 
     * @param dmo
     *        DataModelObject to save and detach
     * @return detached object or <code>null</code> if it was not attached.
     * @throws RepositoryException
     *         for repository exceptions.
     */
    DataModelObject saveAndDetach(DataModelObject dmo) throws RepositoryException;

    /**
     * Commits all attached DataModelObjects to the repository. Ingesting new objects, updating existing
     * ones and deleting the DataModelObjects that are registered for deletion.
     * 
     * @throws RepositoryException
     *         for repository exceptions.
     */
    void commit() throws RepositoryException;

    /**
     * Detaches all objects from this StoreSession. After calling {@link #close()}, this StoreSession is
     * ready for reuse.
     * <p/>
     * <b>The close-method must be called to clear any references in attached DataModelObjects to this
     * StoreSession or underlying classes.</b> So this method is preferably called in a finally-statement.
     */
    void close();

}
