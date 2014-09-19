package nl.knaw.dans.common.lang.repo;

import nl.knaw.dans.common.lang.RepositoryException;

public interface SidDispenser {
    /**
     * Get the next storeId for the given objectNamespace.
     * 
     * @param objectNamespace
     *        identifier of the object type
     * @return nest storeId
     * @throws RepositoryException
     *         wrapper for exceptions
     */
    String nextSid(DmoNamespace objectNamespace) throws RepositoryException;
}
