package nl.knaw.dans.i.dmo.collections;

import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;

/**
 * Recursive object of a collection tree.
 * 
 * @author henkb
 */
public interface DmoCollection extends DataModelObject {

    String CONTENT_MODEL = "easy-model:CM_DMO_COLLECTION_1";

    /**
     * Id of a DmoCollection that is the root of a collection tree.
     */
    String ROOT_ID = "root";

    /**
     * Is this DmoCollection the root of a collection tree.
     * <p/>
     * A DmoCollection is root of a collection tree if it
     * <ul>
     * <li>does not have a parent;</li>
     * <li>its storeId equals {@link #ROOT_ID}.</li>
     * 
     * @return <code>true</code> if this DmoCollection is root of a collection tree, <code>false</code> otherwise.
     */
    boolean isRoot();

    /**
     * Does this DmoCollection have any children? If not, it is a leaf.
     * 
     * @see #isOAIendNode(Set)
     * @return <code>true</code> if this DmoCollection is a leaf, <code>false</code> otherwise.
     */
    boolean isLeaf();

    /**
     * Does this DmoCollection have a parent.
     * 
     * @return <code>true</code> if it has a parent, <code>false</code> otherwise.
     */
    boolean hasParent();

    /**
     * Get the parent of this DmoCollection.
     * 
     * @return parent of this DmoCollection or <code>null</code> if this DmoCollection has no parent.
     * @throws IllegalStateException
     *         if unable to get a persisted parent.
     */
    DmoCollection getParent();

    /**
     * Get the DmoStoreId of the parent of this DmoCollection.
     * 
     * @return DmoStoreId of the parent of this DmoCollection or <code>null</code> if this DmoCollection has no parent.
     */
    DmoStoreId getParentId();

    /**
     * Does this DmoCollection have children.
     * 
     * @return <code>true</code> if it has a children, <code>false</code> otherwise.
     */
    boolean hasChildren();

    /**
     * Get the direct children of this DmoCollection.
     * 
     * @return direct children of this DmoCollection.
     * @throws IllegalStateException
     *         if unable to get persisted children.
     */
    List<DmoCollection> getChildren();

    /**
     * Get the DmoStoreIds of the direct children of this DmoCollection.
     * 
     * @return storeIds of the direct children of this DmoCollection.
     */
    List<DmoStoreId> getChildIds();

    /**
     * Get Dublin Core metadata for this DmoCollection.
     * 
     * @return Dublin Core metadata
     */
    DublinCoreMetadata getDcMetadata();

    /**
     * Is this DmoCollection published as OAI set.
     * 
     * @return <code>true</code> if published, <code>false</code> otherwise.
     */
    boolean isPublishedAsOAISet();

    /**
     * Determine if there are any descendantIds in the given set of <code>memberIds</code> where descendant is published as OAI set. If none were found, than
     * this DmoCollection is an OAI end node relative to the given set.
     * 
     * @param memberIds
     *        set of DmoStoreIds
     * @return <code>true</code> if this DmoCollection is an OAI end node relative to the given set, <code>false</code> otherwise.
     */
    boolean isOAIendNode(Set<DmoStoreId> memberIds);

    /**
     * Get a flat list of all descendants of this DmoCollection.
     * 
     * @return flat list of all descendants.
     */
    List<DmoCollection> getDescendants();

    /**
     * Get a flat list of id's of all descendants of this DmoCollection.
     * 
     * @return flat list of id's of all descendants
     */
    List<DmoStoreId> getDescendantIds();

    /**
     * Get a flat list of id's of all descendants of this DmoCollection that are published as OAI set.
     * 
     * @return flat list of id's of all descendants published as OAI set.
     */
    List<DmoStoreId> getOAIPublishedDescendentIds();

    /**
     * Get the short name of this DmoCollection.
     * 
     * @return shortName
     */
    String getShortName();

    /**
     * Set the short name of this DmoCollection.
     * 
     * @param shortName
     */
    void setShortName(String shortName);

}
