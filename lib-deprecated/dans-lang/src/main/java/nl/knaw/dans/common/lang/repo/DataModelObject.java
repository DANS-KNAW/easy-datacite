package nl.knaw.dans.common.lang.repo;

import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.exception.CouldNotGetStoreException;
import nl.knaw.dans.common.lang.repo.exception.NoStoreAttachedException;
import nl.knaw.dans.common.lang.repo.exception.NoUnitOfWorkAttachedException;
import nl.knaw.dans.common.lang.repo.relations.Relations;
import nl.knaw.dans.common.lang.security.authz.AuthzStrategy;

/**
 * A DataModelObject is a StorableObject that aggregates zero or more MetadataUnits and/or BinaryUnits. A
 * dmo can be in three states: not loaded, loaded and registered for deletion. Currently not loaded means
 * not stored in the repository and loaded means stored in the repository and retrieved. Registered for
 * deletion means that the status of the object has been set to deleted. A type of DataModelObject can be
 * identified via its content model and its object namespace. Each DataModelObject should have its own
 * unique object namespace as well as provide at least one unique content model. The content model allows
 * inheritance and extension, but the object namespace does not.
 * 
 * @author ecco Oct 9, 2009
 * @author lobo
 */
public interface DataModelObject extends StorableObject
{
    /**
     * The object namespace is prefixed before the storeId of the data model object
     */
    DmoNamespace getDmoNamespace();

    /**
     * Get the {@link DmoStoreId} of this DataModelObject or <code>null</code> if this DataModelObject
     * has no storeId.
     * 
     * @return the DmoStoreId or <code>null</code>
     */
    DmoStoreId getDmoStoreId();

    /**
     * Each data model object can have several content models, which identify the kind of operations the
     * object can do. This method should be implemented by each abstract or non-abstract class and add
     * one ore more content models to the content models from the super class. Currently the content
     * model is nothing more but a string, but in the future it might become a repository level class
     * descriptor for the data model object.
     */
    Set<String> getContentModels();

    /**
     * @return a list of implementing MetadataUnit objects. It is called by the dmoStore at ingest or
     *         update time.
     */
    List<MetadataUnit> getMetadataUnits();

    /**
     * @return a list of implementing BinaryUnit objects. It is called by the dmoStore at ingest or
     *         update time.
     */
    List<BinaryUnit> getBinaryUnits();

    /**
     * @return a relations object that contains all relations this dmo has with other dmo's.
     */
    Relations getRelations();

    /**
     * @return true if this object may be deleted
     */
    boolean isDeletable();

    /**
     * @return true if this object is registered for deletion. If the object is not registered for
     *         deletion it cannot be purged (see dmoStore.purge)
     */
    boolean isRegisteredDeleted();

    /**
     * Sets the object to the deleted state. This does not mean it has been deleted, but simply means it
     * is ready to be deleted (i.e. purged).
     * 
     * @throws RepositoryException
     */
    void registerDeleted();

    void setUnitOfWork(UnitOfWork uow);

    UnitOfWork getUnitOfWork() throws NoUnitOfWorkAttachedException;

    /**
     * Tries to retrieve the store from which this dmo was loaded. If the dmo was not loaded it will
     * throw a NoStoreAttachedException
     * 
     * @return the store from which this dmo was loaded.
     * @throws NoStoreAttachedException
     *         if the dmo was not loaded from a store
     * @throws CouldNotGetStoreException
     *         if the store could not be gotten from the DmoStores registry
     */
    DmoStore getStore() throws NoStoreAttachedException, CouldNotGetStoreException;

    /**
     * @return true if this object is outdated. This means the object, but not this instance, has been
     *         updated in the store.
     * @throws RepositoryException
     *         a wrapper exception
     */
    boolean isInvalidated() throws RepositoryException;

    /**
     * @return the name of the store this object was loaded from.
     */
    String getStoreName() throws NoStoreAttachedException;

    /**
     * Get the authzStrategy for this DataModelObject.
     * 
     * @return the authzStrategy for this DataModelObject
     */
    AuthzStrategy getAuthzStrategy();

    /**
     * Set the authzStrategy for this DataModelObject.
     * 
     * @param authzStrategy
     *        the authzStrategy for this DataModelObject
     */
    void setAuthzStrategy(AuthzStrategy authzStrategy);

    /**
     * Get the name of the authzStrategy. A AuthzStrategyProvider is responsible for mapping the name to
     * an implementing class. F.i. name "foo" may be mapped to MyAuthzStrategy.
     * 
     * @return the name of the authzStrategy
     */
    String getAutzStrategyName();
}
