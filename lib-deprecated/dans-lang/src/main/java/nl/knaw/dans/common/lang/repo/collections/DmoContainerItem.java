package nl.knaw.dans.common.lang.repo.collections;

import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoStoreId;

/**
 * A container item may be child to one or more parent containers.  
 *
 * @author lobo
 */
public interface DmoContainerItem extends DmoCollectionMember
{
    DmoStoreId getParentSid() throws RepositoryException;

    Set<DmoStoreId> getParentSids() throws RepositoryException;

    DmoContainer getParent() throws RepositoryException;

    List<? extends DmoContainer> getParents() throws RepositoryException;

    void setParentSid(DmoStoreId dmoStoreId) throws RepositoryException;

    void setParentSids(Set<DmoStoreId> parentSids) throws RepositoryException;

    void setParent(DmoContainer container) throws RepositoryException;

    void setParents(List<? extends DmoContainer> container) throws RepositoryException;

    void addParent(DmoContainer container) throws RepositoryException;

    void addParentSid(DmoStoreId dmoStoreId) throws RepositoryException;

    void removeParent(DmoContainer container) throws RepositoryException;

    void removeParentSid(DmoStoreId parentDmoStoreId) throws RepositoryException;
}
