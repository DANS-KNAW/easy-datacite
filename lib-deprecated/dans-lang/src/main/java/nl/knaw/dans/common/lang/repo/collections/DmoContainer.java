package nl.knaw.dans.common.lang.repo.collections;

import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoStoreId;

/**
 * A container object can contain and maintains relations with container items. This object is the parent to the child container items.
 * 
 * @author lobo
 */
public interface DmoContainer extends DmoCollectionMember {
    List<? extends DmoContainerItem> getChildren() throws RepositoryException;

    void setChildren(List<? extends DmoContainerItem> children) throws RepositoryException;

    Set<DmoStoreId> getChildSids() throws RepositoryException;

    void addChild(DmoContainerItem item) throws RepositoryException;

    void removeChild(DmoContainerItem item) throws RepositoryException;

    void removeAndDeleteChild(DmoContainerItem item) throws RepositoryException;
}
