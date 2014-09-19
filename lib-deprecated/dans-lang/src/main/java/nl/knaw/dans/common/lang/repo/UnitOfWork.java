package nl.knaw.dans.common.lang.repo;

import java.io.Serializable;
import java.util.Collection;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.repo.exception.UnitOfWorkInterruptException;

/**
 * Maintains a list of objects affected by a business transaction and coordinates the writing out of changes to a store, database or repository. (partly from
 * Fowler).
 * 
 * @author ecco Oct 29, 2009
 */
public interface UnitOfWork extends Serializable {
    void addListener(UnitOfWorkListener listener);

    void addListeners(UnitOfWorkListener... unitOfWorkListeners);

    boolean removeListener(UnitOfWorkListener listener);

    void attach(DataModelObject object) throws RepositoryException;

    /**
     * WARNING: Due to quirck in implementation, only gets an object if it's already in the cloud.
     * 
     * @see #retrieveObject(String)
     * @param storeId
     *        id of the object to get
     * @return object or null if not in cloud
     * @throws RepositoryException
     *         never thrown
     */
    DataModelObject getObject(DmoStoreId dmoStoreId) throws RepositoryException;

    DataModelObject retrieveObject(DmoStoreId dmoStoreId) throws ObjectNotInStoreException, RepositoryException;

    DataModelObject detach(DataModelObject object);

    DataModelObject saveAndDetach(DataModelObject dmo) throws RepositoryException, UnitOfWorkInterruptException;

    void commit() throws RepositoryException, UnitOfWorkInterruptException;

    // /**
    // * RollBack may be partly implemented, affecting only newly ingested objects.
    // *
    // * @param logMessage
    // * a message, can be null
    // * @throws RepositoryException
    // * as wrapper for exceptions
    // */
    // void rollBack(String logMessage) throws RepositoryException;

    void close();

    DmoStore getStore();

    Collection<DataModelObject> getAttachedObjects();

    /**
     * Gets the unique name or id of the person or session that is responsible for the update.
     */
    String getUpdateOwner();
}
