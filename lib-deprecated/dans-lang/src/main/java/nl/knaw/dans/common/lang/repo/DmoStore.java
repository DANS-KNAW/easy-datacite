package nl.knaw.dans.common.lang.repo;

import java.net.URL;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.exception.ConcurrentUpdateException;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.common.lang.repo.relations.Relation;

import org.joda.time.DateTime;

// TODO Move methods involving resource index (RDF, sparql) to another interface and (abstract)class(es).
// (hb)
public interface DmoStore extends SidDispenser {

    void setConcurrencyGuard(DmoUpdateConcurrencyGuard concurrencyGuard);

    /**
     * Add the StoreEventListeners listed in <code>storeEventListeners</code> to the event listeners of this Store. Use this method for dependency injection by
     * a framework.
     * 
     * @param storeEventListeners
     *        a list of StoreEventListeners
     */
    void setEventListeners(List<DmoStoreEventListener> storeEventListeners);

    /**
     * Add the given <code>storeEventListener</code> to the event listeners of this Store.
     * 
     * @param storeEventListener
     *        event listener to add
     */
    void addEventListener(DmoStoreEventListener storeEventListener);

    /**
     * Remove the given <code>storeEventListener</code> as an event listener of this Store.
     * 
     * @param storeEventListener
     *        event listener to remove
     * @return <code>true</code> if it was removed, <code>false</code> otherwise
     */
    boolean removeEventListener(DmoStoreEventListener storeEventListener);

    /**
     * Returns a copy of the list of listeners.
     * 
     * @return a copy of the list of listeners
     */
    List<DmoStoreEventListener> getListeners();

    /**
     * @return the name of this store. Each store has to be uniquely named and a single entrance to this repository.
     */
    String getName();

    /**
     * Ingest the given StorableObject.
     * 
     * @param storable
     *        the StorableObject to ingest
     * @param logMessage
     *        a log message
     * @return the storeId of the newly ingested object
     * @throws RepositoryException
     *         wrapper for exceptions
     */
    String ingest(DataModelObject storable, String logMessage) throws RepositoryException;

    /**
     * Retrieve the StorableObject with the given id from the Store.
     * 
     * @param dmoStoreId
     *        dmoStoreId of the object to be retrieved
     * @return object with the given dmoStoreId
     * @throws RepositoryException
     *         wrapper for exceptions
     */
    DataModelObject retrieve(DmoStoreId dmoStoreId) throws ObjectNotInStoreException, RepositoryException;

    /**
     * Update the given DataModelObject. If the <code>skipDirtyChecking</code> flag is set to <code>true</code>, will update the entire DataModelObject and all
     * of it's units indiscriminate of the state of dirty flags of the object and it's units.
     * 
     * @param dmo
     *        the DataModelObject to update
     * @param skipDirtyChecking
     *        <code>true</code> if dirty checking should be skipped, <code>false</code> otherwise
     * @param logMessage
     *        a log message
     * @return the timestamp of the update according to the store or <code>null</code> if no update took place
     * @throws ConcurrentUpdateException
     *         if a concurrent update took place
     * @throws RepositoryException
     *         wrapper for exceptions
     */
    DateTime update(DataModelObject dmo, boolean skipDirtyChecking, String logMessage) throws ConcurrentUpdateException, RepositoryException;

    /**
     * Update the given DataModelObject. If the <code>skipDirtyChecking</code> flag is set to <code>true</code>, will update the entire DataModelObject and all
     * of it's units indiscriminate of the state of dirty flags of the object and it's units.
     * 
     * @param dmo
     *        the DataModelObject to update
     * @param skipDirtyChecking
     *        <code>true</code> if dirty checking should be skipped, <code>false</code> otherwise
     * @param logMessage
     *        a log message
     * @param updateOwner
     *        the unique name or id of the person or session that is responsible for the change. This string is held to allowe this person/session to overwrite
     *        this dmo with an older version of itself without getting a concurrenct update exception. Get it?
     * @return the timestamp of the update according to the store or <code>null</code> if no update took place
     * @throws ConcurrentUpdateException
     *         if a concurrent update took place
     * @throws RepositoryException
     *         wrapper for exceptions
     */
    DateTime update(DataModelObject dmo, boolean skipDirtyChecking, String logMessage, String updateOwner) throws ConcurrentUpdateException,
            RepositoryException;

    /**
     * Update the given StorableObject.
     * 
     * @param storable
     *        the StorableObject to update
     * @param logMessage
     *        a log message
     * @return timestamp of update according to the store
     * @throws RepositoryException
     *         wrapper for exceptions
     */
    DateTime update(DataModelObject storable, String logMessage) throws RepositoryException;

    /**
     * WARN in case metadataUnit from dataset, searchEngine will not be updated!
     * 
     * @param storeId
     * @param metadataUnit
     * @param logMessage
     * @throws RepositoryException
     */
    void addOrUpdateMetadataUnit(DmoStoreId dmoStoreId, MetadataUnit metadataUnit, String logMessage) throws RepositoryException;

    void addOrUpdateBinaryUnit(DmoStoreId dmoStoreId, BinaryUnit binaryUnit, String logMessage) throws RepositoryException;

    /**
     * Purge the object with the given id permanently from this Store.
     * <p/>
     * Note that forced purge is not yet supported in Fedora: fedora.server.errors.GeneralException: Forced object removal is not yet supported. The parameter
     * <code>force</code> only affects on the level of DmoStore.
     * 
     * @param storeId
     *        id of the object to be removed
     * @param force
     *        force the purge, even if it would break a dependency. This can be used to ignore the fact that the object is registered for deletion or
     *        non-deletable.
     * @param logMessage
     *        a log message
     * @return timestamp of removal according to the store
     * @throws RepositoryException
     *         wrapper for exceptions
     */
    DateTime purge(DataModelObject object, boolean force, String logMessage) throws RepositoryException;

    DateTime purgeUnit(DmoStoreId dmoStoreId, DsUnitId unitId, DateTime creationDate, String logMessage) throws RepositoryException;

    JumpoffDmo findJumpoffDmoFor(DataModelObject dmo) throws ObjectNotInStoreException, RepositoryException;

    JumpoffDmo findJumpoffDmoFor(DmoStoreId dmoStoreId) throws ObjectNotInStoreException, RepositoryException;

    boolean exists(DmoStoreId dmoStoreId) throws RepositoryException;

    /**
     * Get the object xml of the object with the given storeId.
     * 
     * @param storeId
     *        id of the object
     * @return object xml of the object
     * @throws RepositoryException
     *         wrapper for other exceptions
     */
    byte[] getObjectXML(DmoStoreId dmoStoreId) throws ObjectNotInStoreException, RepositoryException;

    /**
     * @param storeId
     *        the id of the object
     * @return Returns from the repository the date and time on which the object was last modified
     * @throws RepositoryException
     *         wrapper for other exceptions
     */
    DateTime getLastModified(DmoStoreId dmoStoreId) throws RepositoryException;

    /**
     * Queries the repository for relations. The parameters that are left out by using null will be retrieved from the repository. If one or more parameters are
     * filled in they will be combined to search for relations.
     * 
     * @param subject
     *        null or a subject
     * @param predicate
     *        nul or a predicate
     * @param objectNode
     *        null or an object
     * @return a list of relations
     * @throws RepositoryException
     *         wrapper for exceptions.
     */
    List<Relation> getRelations(final String subject, final String predicate, final String object) throws RepositoryException;

    /**
     * Get the relations of a given object.
     * 
     * @param storeId
     *        storeId of the subject of the relation(s)
     * @param predicate
     *        filter on predicate. if null, get all relations
     * @return list of relations
     * @throws RepositoryException
     *         wrapper for exceptions
     */
    List<Relation> getRelations(DmoStoreId dmoStoreId, String predicate) throws RepositoryException;

    /**
     * Creates a new relationship in the dmo referenced with <code>storeId</code>.
     * 
     * @param storeId
     *        The subject. Only valid form: storeId of existing object i.e. test:123
     * @param relationship
     *        The predicate. Must conform to uri spec.
     * @param object
     *        The object (target). Either string literal or resource reference.
     * @param isLiteral
     *        A boolean value indicating whether the object is a literal
     * @param dataType
     *        The datatype of the literal. Can be <code>null</code>
     * @return True if and only if the relationship was added
     * @throws RepositoryException
     *         wrapper for exceptions.
     */
    boolean addRelationship(DmoStoreId dmoStoreId, String relationship, String object, boolean isLiteral, String dataType) throws RepositoryException;

    /**
     * Delete the specified relationship.
     * 
     * @param storeId
     *        The subject. Only valid form: storeId of existing object i.e. test:123
     * @param relationship
     *        The predicate. Must conform to uri spec.
     * @param object
     *        The object (target). Either string literal or resource reference.
     * @param isLiteral
     *        A boolean value indicating whether the object is a literal.
     * @param dataType
     *        The datatype of the literal. Can be <code>null</code>
     * @return True if and only if the relationship was purged.
     * @throws RepositoryException
     *         wrapper for exceptions
     */
    boolean purgeRelationship(DmoStoreId dmoStoreId, String relationship, String object, boolean isLiteral, String dataType) throws RepositoryException;

    /**
     * Returns a list of store ID's of all the objects that have a certain content model.
     * 
     * @param contentModel
     *        the content model to search for
     * @return a list of implementing data model objects
     * @throws RepositoryException
     *         wrapper for exceptions.
     */
    List<DmoStoreId> getSidsByContentModel(DmoStoreId contentModelId) throws RepositoryException;

    List<DmoStoreId> findSubordinates(DmoStoreId dmoStoreId) throws RepositoryException;

    /**
     * Get the URL for obtaining the contents of a unit.
     * 
     * @param storeId
     *        storeId of the Dmo
     * @param unitId
     *        unitId of the unit
     * @return URL for obtaining the contents of a unit
     */
    URL getFileURL(DmoStoreId dmoStoreId, DsUnitId unitId);

    URL getFileURL(DmoStoreId dmoStoreId, DsUnitId unitId, DateTime dateTime);

    List<UnitMetadata> getUnitMetadata(final DmoStoreId dmoStoreId, final DsUnitId unitId) throws RepositoryException;

    List<UnitMetadata> getUnitMetadata(final DmoStoreId dmoStoreId) throws RepositoryException;

    /**
     * Checks if a dmo is invalidated. Invalidation means that the object is not anymore in a synchronized state with the persisted dmo. If a dmo is invalidated
     * that means the persisted dmo has changed since the time the dmo was gotten from the repository.
     * 
     * @param dmo
     *        The dmo to check invalidation for
     * @return true is the dmo was invalidated
     * @throws RepositoryException
     *         wrapper for exceptions.
     */
    boolean isInvalidated(DataModelObject dmo) throws RepositoryException;

    /**
     * Can be used to check if a dmo can be changed. A dmo can be changed if it is not invalidated or if it is of it carries the same session token as the
     * previous dmo with the same store id that was modified.
     * 
     * @param dmo
     * @return
     * @throws RepositoryException
     */
    boolean isUpdateable(DataModelObject dmo) throws RepositoryException;

    /**
     * Can be used to check if a dmo can be changed. A dmo can be changed if it is not invalidated or if it is of it carries the same session token as the
     * previous dmo with the same store id that was modified.
     * 
     * @param dmo
     * @return
     * @throws RepositoryException
     */
    boolean isUpdateable(DataModelObject dmo, String updateOwner) throws RepositoryException;

}
