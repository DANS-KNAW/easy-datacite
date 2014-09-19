package nl.knaw.dans.i.store;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;

/**
 * Manages a store or repository.
 */
public interface StoreManager {

    /**
     * Creates a new {@link StoreSession}.
     * 
     * @param ownerId
     *        preferably a unique id like userId.
     * @return a new StoreSession
     */
    StoreSession newStoreSession(String ownerId);

    /**
     * Get the next storeId for the given namespace.
     * 
     * @param dmoNamespace
     *        namespace for returned storeId.
     * @return next storeId
     * @throws RepositoryException
     *         for repository exceptions.
     */
    String nextStoreId(DmoNamespace dmoNamespace) throws RepositoryException;

    /**
     * Get the next dmoStoreId for the given namespace.
     * 
     * @param dmoNamespace
     *        namespace for returned dmoStoreId.
     * @return next dmoStoreId
     * @throws RepositoryException
     *         for repository exceptions.
     */
    DmoStoreId nextDmoStoreId(DmoNamespace dmoNamespace) throws RepositoryException;

}
