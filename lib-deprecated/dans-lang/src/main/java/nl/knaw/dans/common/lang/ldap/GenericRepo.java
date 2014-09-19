package nl.knaw.dans.common.lang.ldap;

import java.util.Collection;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.exception.ObjectExistsException;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;

/**
 * Generic Data Access Point.
 * 
 * @author ecco
 * @param <T>
 *        The type managed by this Data Access Point.
 */
public interface GenericRepo<T> {

    String getContext();

    /**
     * Find the entity that can be identified with the given id.
     * 
     * @param id
     *        the id of the entity
     * @return the entity
     * @throws ObjectNotInStoreException
     *         if the entity was not in the data store
     * @throws RepositoryException
     *         if there is an exception during data access
     */
    T findById(String id) throws ObjectNotInStoreException, RepositoryException;

    /**
     * Find the entities identified with the given id's.
     * 
     * @param ids
     *        list of id's
     * @return list of entries of type T
     * @throws ObjectNotInStoreException
     *         if the entity was not in the data store
     * @throws RepositoryException
     *         if there is an exception during data access
     */
    List<T> findById(Collection<String> ids) throws ObjectNotInStoreException, RepositoryException;

    /**
     * Add the given object to the data store.
     * 
     * @param object
     *        the entity that needs to be stored
     * @return the id of the stored object
     * @throws ObjectExistsException
     *         if the given object has an id that is already in use
     * @throws RepositoryException
     *         if there is an exception during data access
     */
    String add(T object) throws ObjectExistsException, RepositoryException;

    /**
     * Update the given object in the data store.
     * 
     * @param object
     *        the entity that needs to be updated
     * @return the id of the stored object
     * @throws RepositoryException
     *         if there is an exception during data access
     */
    String update(T object) throws RepositoryException;

    /**
     * Remove the given object from the data store.
     * 
     * @param object
     *        entity that needs to be removed
     * @throws RepositoryException
     *         if there is an exception during data access
     */
    void delete(T object) throws RepositoryException;

    /**
     * Remove the entity with the given id from the data store.
     * 
     * @param id
     *        the id of the entity that needs to be removed
     * @throws RepositoryException
     *         if there is an exception during data access
     */
    void delete(String id) throws RepositoryException;

    /**
     * Get all the objects in the data store.
     * <p/>
     * WARNING: This may be a time consuming operation.
     * <p/>
     * 
     * @return a list with all the objects in the data store
     * @throws RepositoryException
     *         if there is an exception during data access
     */
    List<T> findAll() throws RepositoryException;

    /**
     * Get all the id's (rdn's) in the context of this repo.
     * 
     * @param maxCount
     *        the maximum to fetch, or 0 for all entries
     * @return List of id's
     * @throws RepositoryException
     *         if there is an exception during data access
     */
    List<String> findAllEntries(int maxCount) throws RepositoryException;

    /**
     * Find out if the given id is already in use in the data store.
     * 
     * @param id
     *        the id to be tested
     * @return <code>true</code> if the given id is already in use, <code>false</code> otherwise
     * @throws RepositoryException
     *         if there is an exception during data access
     */
    boolean exists(String id) throws RepositoryException;

    /**
     * Get the operational attributes of the object with the given id.
     * 
     * @param id
     *        the id of the entry
     * @return operational attributes
     * @throws RepositoryException
     *         if there is an exception during data access
     */
    OperationalAttributes getOperationalAttributes(String id) throws RepositoryException;

    /**
     * Closes this Data Access Point and frees all resources previously obtained.
     */
    void close() throws RepositoryException;

}
