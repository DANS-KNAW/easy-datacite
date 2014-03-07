package nl.knaw.dans.i.dmo.collections;

import java.net.URL;

import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.bean.RecursiveList;
import nl.knaw.dans.common.lang.xml.XMLBean;
import nl.knaw.dans.i.dmo.collections.exceptions.CollectionsException;
import nl.knaw.dans.i.dmo.collections.exceptions.NoSuchCollectionException;
import nl.knaw.dans.i.security.annotations.SecuredOperation;

/**
 * Manages a collection tree.
 * 
 * @author henkb
 */
public interface CollectionManager
{

    /**
     * Is there a collection tree under the given namespace.
     * 
     * @param namespace
     *        DmoNamespace to test
     * @return <code>true</code> if there is a collection tree with the given namespace,
     *         <code>false</code> otherwise.
     * @throws CollectionsException
     */
    boolean exists(DmoNamespace namespace) throws CollectionsException;

    /**
     * Ingest and creates the collection tree described in the xml that the given URL is pointing to. The
     * xml is validated against the schema at
     * https://eof12.dans.knaw.nl/schema/collections/dmo-collection.xsd
     * <p/>
     * <p/>
     * {@literal @}SecuredOperation(id = "nl.knaw.dans.i.dmo.collections.CollectionManager.createRoot")
     * 
     * @param xmlTreeUrl
     *        URL pointing to xml
     * @param generateIds
     *        <code>true</code> if ids in the xml are to be ignored and generated, <code>false</code> for
     *        keeping the ids in the xml.
     * @return root of DmoCollection tree
     * @throws CollectionsException
     *         for invalid url, invalid xml; if the DmoNamespace for the new tree is already taken.
     * @see DmoCollections#validateXml(URL)
     * @see #createRoot(DmoNamespace)
     */
    @SecuredOperation
    DmoCollection createRoot(URL xmlTreeUrl, boolean generateIds) throws CollectionsException;

    /**
     * Creates and ingests the root of the collection tree with the given namespace.
     * <p/>
     * {@literal @}SecuredOperation(id = "nl.knaw.dans.i.dmo.collections.CollectionManager.createRoot")
     * 
     * @param namespace
     *        the DmoNamespace for the new collection root.
     * @return root of DmoCollection
     * @throws CollectionsException
     *         if the DmoNamespace for the new collection is already taken.
     * @see #createRoot(URL, boolean)
     */
    @SecuredOperation
    DmoCollection createRoot(DmoNamespace namespace) throws CollectionsException;

    /**
     * Create a new DmoCollection as a child of the given parent, with given label and short name.
     * <p/>
     * {@literal @}SecuredOperation(id =
     * "nl.knaw.dans.i.dmo.collections.CollectionManager.createCollection")
     * 
     * @param parent
     *        DmoCollection that is the parent of the newly created DmoCollection.
     * @param label
     *        label of the newly created DmoCollection.
     * @param shortName
     *        short name of the newly created DmoCollection.
     * @return new DmoCollection
     * @throws CollectionsException
     */
    @SecuredOperation
    DmoCollection createCollection(DmoCollection parent, String label, String shortName) throws CollectionsException;

    /**
     * Get the root of the collection tree with the given namespace.
     * 
     * @param namespace
     *        the namespace of the collection
     * @return root of the collection tree.
     * @throws NoSuchCollectionException
     *         if root for given namespace not found.
     * @throws CollectionsException
     */
    DmoCollection getRoot(DmoNamespace namespace) throws NoSuchCollectionException, CollectionsException;

    /**
     * Get the DmoCollection with the given DmoStoreId.
     * 
     * @param dmoStoreId
     *        dmoStoreId of the SimpleCollection.
     * @return SimpleCollection with the given DmoStoreId.
     * @throws NoSuchCollectionException
     *         if dmoCollection with given dmoStoreId not found.
     * @throws CollectionsException
     */
    DmoCollection getCollection(DmoStoreId dmoStoreId) throws NoSuchCollectionException, CollectionsException;

    /**
     * Update the given collection.
     * <p/>
     * {@literal @}SecuredOperation(id = "nl.knaw.dans.i.dmo.collections.CollectionManager.update")
     * 
     * @param collection
     *        DmoCollection to update.
     * @throws CollectionsException
     */
    @SecuredOperation
    void update(DmoCollection collection) throws CollectionsException;

    /**
     * Attach the given child to the given parent. The following conditions must be true:
     * <ul>
     * <li>parent and child should be of the same namespace;</li>
     * <li>the child is not published as OAI-set;</li>
     * <li>parent and child are not the same object.</li>
     * </ul>
     * <p/>
     * {@literal @}SecuredOperation(id =
     * "nl.knaw.dans.i.dmo.collections.CollectionManager.attachCollection")
     * 
     * @param parent
     *        parent DmoCollection
     * @param child
     *        child DmoCollection
     * @throws CollectionsException
     */
    @SecuredOperation
    void attachCollection(DmoCollection parent, DmoCollection child) throws CollectionsException;

    /**
     * Detach the given collection from its parent.
     * <p/>
     * {@literal @}SecuredOperation(id =
     * "nl.knaw.dans.i.dmo.collections.CollectionManager.detachCollection")
     * 
     * @param collection
     *        DmoCollection to detach from its parent
     * @throws CollectionsException
     */
    @SecuredOperation
    void detachCollection(DmoCollection collection) throws CollectionsException;

    /**
     * Publish the given collection as OAI-set. All ancestors of the given collection will be published
     * as OAI-set as well.
     * <p>
     * <b>NO GUARANTEE</b> is given that members of this collection will be published as OAI-set-member.
     * </p>
     * {@literal @}SecuredOperation(id =
     * "nl.knaw.dans.i.dmo.collections.CollectionManager.publishAsOAISet")
     * 
     * @param collection
     *        DmoCollection to publish
     * @throws CollectionsException
     */
    @SecuredOperation
    void publishAsOAISet(DmoCollection collection) throws CollectionsException;

    /**
     * Unpublish the given collection as OAI-set. All descendants of the given collection will be
     * unpublished as OAI-set as well.
     * <p>
     * <b>NO GUARANTEE</b> is given that members of this collection will be unpublished as
     * OAI-set-member.
     * </p>
     * {@literal @}SecuredOperation(id =
     * "nl.knaw.dans.i.dmo.collections.CollectionManager.unpublishAsOAISet")
     * 
     * @param collection
     *        DmoCollection to unpublish
     * @throws CollectionsException
     */
    @SecuredOperation
    void unpublishAsOAISet(DmoCollection collection) throws CollectionsException;

    /**
     * Purge the given collection and all of its descendants.
     * <p/>
     * {@literal @}SecuredOperation(id = "nl.knaw.dans.i.dmo.collections.CollectionManager.purge")
     * 
     * @param collection
     *        DmoCollection to purge.
     * @throws CollectionsException
     */
    @SecuredOperation
    void purge(DmoCollection collection) throws CollectionsException;

    /**
     * Get collection tree as XMLBean.
     * 
     * @param namespace
     * @return collection tree as XMLBean.
     * @throws CollectionsException
     */
    XMLBean getXmlBean(DmoNamespace namespace) throws CollectionsException;

    /**
     * Get branch, starting with DmoCollection with given dmoStoreid, as XMLBean.
     * 
     * @param dmoStoreId
     * @return branch as XMLBean.
     * @throws CollectionsException
     */
    XMLBean getXmlBean(DmoStoreId dmoStoreId) throws CollectionsException;

    /**
     * Get a RecursiveList representing the collection tree of the given namespace.
     * <p/>
     * A RecursiveList can be used in language-aware applications.
     * 
     * @param namespace
     * @return a RecursiveList
     * @throws CollectionsException
     */
    RecursiveList getRecursiveList(DmoNamespace namespace) throws CollectionsException;

}
