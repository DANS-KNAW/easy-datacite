package nl.knaw.dans.easy.domain.collections;

import java.util.List;

import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;
import nl.knaw.dans.easy.domain.model.Constants;

public interface SimpleCollection extends DataModelObject
{

    String CONTENT_MODEL = Constants.CM_SIMPLE_COLLECTION_1;

    /**
     * Id of a SimpleCollection that is the root of a collection tree.
     */
    String ROOT_ID       = "root";

    /**
     * Is this SimpleCollection the root of a collection tree.
     * <p/>
     * A SimpleCollection is root of a collection tree if it 
     * <ul>
     * <li>does not have a parent;</li>
     * <li>its storeId equals {@link #ROOT_ID}.</li>
     * 
     * @return <code>true</code> if this SimpleCollection is root of a collection tree, <code>false</code>
     *         otherwise.
     */
    boolean isRoot();

    /**
     * Does this SimpleCollection have a parent.
     * 
     * @return <code>true</code> if it has a parent, <code>false</code> otherwise.
     */
    boolean hasParent();

    /**
     * Get the parent of this SimpleCollection.
     * 
     * @return parent of this SimpleCollection or <code>null</code> if this SimpleCollection has no
     *         parent.
     * @throws IllegalStateException
     *         if unable to get a persisted parent.
     */
    SimpleCollection getParent();

    /**
     * Get the DmoStoreId of the parent of this SimpleCollection.
     * 
     * @return DmoStoreId of the parent of this SimpleCollection or <code>null</code> if this
     *         SimpleCollection has no parent.
     */
    DmoStoreId getParentId();

    /**
     * Does this SimpleCollection have children.
     * 
     * @return <code>true</code> if it has a children, <code>false</code> otherwise.
     */
    boolean hasChildren();

    /**
     * Get the direct children of this SimpleCollection.
     * 
     * @return direct children of this SimpleCollection.
     * @throws IllegalStateException
     *         if unable to get a persisted children.
     */
    List<SimpleCollection> getChildren();

    /**
     * Get the storeIds of the direct children of this SimpleCollection.
     * 
     * @return storeIds of the direct children of this SimpleCollection.
     */
    List<DmoStoreId> getChildIds();

    /**
     * Get Dublin Core metadata for this SimpleCollection.
     * 
     * @return Dublin Core metadata
     */
    DublinCoreMetadata getDcMetadata();

    /**
     * WARNING: this method should not be called directly, as it affects the status of this
     * SimpleCollection's descendants. See the appropriate service to add and remove children.
     * <p/>
     * Add the given child to this SimpleCollection. The child should have no parent.
     * 
     * @param child
     *        the SimpleCollection to add as a descendant of this SimpleCollection.
     * @return <code>true</code> if the child was added, <code>false</code> otherwise.
     * @throws IllegalArgumentException
     *         if child == this SimpleCollection, if child is published as OAI set, if the specific class
     *         of the child cannot be handled.
     */
    boolean addChild(SimpleCollection child);

    /**
     * WARNING: this method should not be called directly, as it affects the status of this
     * SimpleCollection, the removed SimpleCollection and its descendants. See the appropriate service to
     * add and remove children.
     * <p/>
     * Remove the given child as descendant of this SimpleCollection. Child and its descendants will be
     * unpublished as OAI set.
     * 
     * @param child
     *        the SimpleCollection to remove from the descendants of this SimpleCollection.
     * @return <code>true</code> if child was removed, <code>false</code> otherwise.
     * @throws IllegalArgumentException
     *         if the specific class of the child cannot be handled.
     */
    boolean removeChild(SimpleCollection child);

    /**
     * WARNING: this method should not be called directly, as it affects the status of this
     * SimpleCollection's ancestors. See the appropriate service to publish and unpublish as OAI set.
     * <p/>
     * Publish this SimpleCollection and all of its ancestors as OAI set. The behavior is in accordance
     * with OAI set specifications.
     */
    void publishAsOAISet();

    /**
     * WARNING: this method should not be called directly, as it affects the status of this
     * SimpleCollection's descendants. See the appropriate service to publish and unpublish as OAI set.
     * <p/>
     * Unpublish this SimpleCollection and all of its descendants as OAI set. The behavior is in
     * accordance with OAI set specifications.
     */
    void unpublishAsOAISet();

    /**
     * Is this SimpleCollection published as OAI set.
     * 
     * @return <code>true</code> if published, <code>false</code> otherwise.
     */
    boolean isPublishedAsOAISet();

}
