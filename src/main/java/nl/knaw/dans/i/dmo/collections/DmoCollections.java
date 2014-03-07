package nl.knaw.dans.i.dmo.collections;

import java.net.URL;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.xml.ValidatorException;
import nl.knaw.dans.common.lang.xml.XMLErrorHandler;
import nl.knaw.dans.i.dmo.collections.config.Configuration;
import nl.knaw.dans.i.dmo.collections.exceptions.CollectionsException;
import nl.knaw.dans.i.dmo.collections.exceptions.NoSuchCollectionException;
import nl.knaw.dans.i.security.SecurityAgent;

/**
 * @author henkb
 */
public interface DmoCollections
{

    /**
     * Get a set of ids of secured operations.
     * 
     * @return set with secured operation ids.
     */
    Set<String> getSecuredOperationIds();

    /**
     * Configure this DmoCollections with the given configuration. DmoCollections can also be configured
     * with the three operations {@link #registerNamespace(DmoNamespace)},
     * {@link #setContentModelOAISet(DmoStoreId)} and {@link #setSecurityAgents(List)}.
     * 
     * @param configuration
     *        configuration used for initializing this DmoCollections.
     */
    void setConfiguration(Configuration configuration);

    /**
     * Register the namespace for a collection-tree. Partial configuration. Only registered namespaces
     * can be managed by the {@link CollectionManager}.
     * 
     * @param namespace
     *        DmoNamespace to register.
     * @see #setConfiguration(Configuration)
     */
    void registerNamespace(DmoNamespace namespace);

    /**
     * Set the id of the content model used for OAI-sets.
     * 
     * @param dmoStoreId
     *        DmoStoreId of the content model.
     * @see #setConfiguration(Configuration)
     */
    void setContentModelOAISet(DmoStoreId dmoStoreId);

    /**
     * Set the {@link SecurityAgent}s for secured operations.
     * 
     * @param agents
     * @see #getSecuredOperationIds()
     * @see #setConfiguration(Configuration)
     */
    void setSecurityAgents(List<SecurityAgent> agents);

    /**
     * Get a new CollectionManager for the given ownerId.
     * 
     * @param ownerId
     *        the id of the user handling collections with the given CollectionManager
     * @return CollectionManager to manage a tree of collections
     * @throws IllegalStateException
     *         if not configured.
     */
    CollectionManager newManager(String ownerId);

    /**
     * Validate a collection tree represented in xml-format against the schema. The schema location is
     * https://eof12.dans.knaw.nl/schema/collections/dmo-collection.xsd.
     * 
     * @param xmlTreeUrl
     *        URL that points to the location of the collection tree.
     * @return {@link XMLErrorHandler} with messages and/or passed sign
     * @throws ValidatorException
     *         if anything goes wrong during validation
     * @see XMLErrorHandler#passed()
     * @see XMLErrorHandler#getMessages()
     */
    XMLErrorHandler validateXml(URL xmlTreeUrl) throws ValidatorException;

    /**
     * Filter the given list for OAI end nodes. A DmoCollection is an OAI end node if it is published
     * as OAI set and none of its descendant id's are in the given set of <code>memeberIds</code>.
     * 
     * @param memberIds
     *        set of DmoStoreIds
     * @return set of DmoStoreIds of collections that are published as OAI set and are end nodes
     *         relative to the given set of <code>memberIds</code>.
     * @throws NoSuchCollectionException
     * @throws CollectionsException
     */
    Set<DmoStoreId> filterOAIEndNodes(Set<DmoStoreId> memberIds) throws NoSuchCollectionException, CollectionsException;

}
