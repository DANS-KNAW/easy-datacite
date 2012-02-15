package nl.knaw.dans.easy.data.collections;

import java.util.Set;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.domain.model.Constants;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.i.dmo.collections.CollectionManager;
import nl.knaw.dans.i.dmo.collections.DmoCollection;
import nl.knaw.dans.i.dmo.collections.DmoCollections;
import nl.knaw.dans.i.dmo.collections.exceptions.CollectionsException;

public class EasyCollectionsImpl implements EasyCollections
{
    
    private final DmoCollections dmoCollections;
    
    public EasyCollectionsImpl(DmoCollections dmoCollections)
    {
        this.dmoCollections = dmoCollections;
        dmoCollections.setContentModelOAISet(new DmoStoreId(Constants.CM_EASY_COLLECTION_1));
        dmoCollections.registerNamespace(DMO_NAMESPACE_EASY_COLLECTION);
    }
    
    /* (non-Javadoc)
     * @see nl.knaw.dans.easy.data.collections.EasyCollections#createRoot(nl.knaw.dans.easy.domain.model.user.EasyUser, java.lang.String)
     */
    @Override
    public DmoCollection createRoot(EasyUser sessionUser) throws CollectionsException
    {
        CollectionManager manager = dmoCollections.newManager(getOwnerId(sessionUser));
        DmoCollection root = manager.createRoot(DMO_NAMESPACE_EASY_COLLECTION);
        return root;
    }

    /* (non-Javadoc)
     * @see nl.knaw.dans.easy.data.collections.EasyCollections#getRoot(nl.knaw.dans.common.lang.repo.DmoNamespace)
     */
    @Override
    public DmoCollection getRoot() throws CollectionsException
    {
        CollectionManager manager = dmoCollections.newManager(null);
        DmoCollection root = manager.getRoot(DMO_NAMESPACE_EASY_COLLECTION);
        return root;
    }

    /* (non-Javadoc)
     * @see nl.knaw.dans.easy.data.collections.EasyCollections#getCollection(nl.knaw.dans.common.lang.repo.DmoStoreId)
     */
    @Override
    public DmoCollection getCollection(DmoStoreId dmoStoreId) throws CollectionsException
    {
        checkNamepace(dmoStoreId);
        CollectionManager manager = dmoCollections.newManager(null);
        DmoCollection collection = manager.getCollection(dmoStoreId);
        return collection;
    }
    
    /* (non-Javadoc)
     * @see nl.knaw.dans.easy.data.collections.EasyCollections#saveCollection(nl.knaw.dans.easy.domain.model.user.EasyUser, nl.knaw.dans.i.dmo.collections.DmoCollection)
     */
    @Override
    public void saveCollection(EasyUser sessionUser, DmoCollection collection) throws CollectionsException
    {
        checkNamespace(collection);
        CollectionManager manager = dmoCollections.newManager(getOwnerId(sessionUser));
        manager.update(collection);
    }


    /* (non-Javadoc)
     * @see nl.knaw.dans.easy.data.collections.EasyCollections#createCollection(nl.knaw.dans.easy.domain.model.user.EasyUser, nl.knaw.dans.i.dmo.collections.DmoCollection, java.lang.String, java.lang.String)
     */
    @Override
    public DmoCollection createCollection(EasyUser sessionUser, DmoCollection parent, String label, String shortName) throws CollectionsException
    {
        checkNamespace(parent);
        CollectionManager manager = dmoCollections.newManager(getOwnerId(sessionUser));
        DmoCollection collection = manager.createCollection(parent, label, shortName);
        return collection;
    }

    /* (non-Javadoc)
     * @see nl.knaw.dans.easy.data.collections.EasyCollections#attachCollection(nl.knaw.dans.easy.domain.model.user.EasyUser, nl.knaw.dans.i.dmo.collections.DmoCollection, nl.knaw.dans.i.dmo.collections.DmoCollection)
     */
    @Override
    public void attachCollection(EasyUser sessionUser, DmoCollection parent, DmoCollection child) throws CollectionsException
    {
        checkNamespace(parent);
        CollectionManager manager = dmoCollections.newManager(getOwnerId(sessionUser));
        manager.attachCollection(parent, child);
    }

    /* (non-Javadoc)
     * @see nl.knaw.dans.easy.data.collections.EasyCollections#detachCollection(nl.knaw.dans.easy.domain.model.user.EasyUser, nl.knaw.dans.i.dmo.collections.DmoCollection)
     */
    @Override
    public void detachCollection(EasyUser sessionUser, DmoCollection collection) throws CollectionsException
    {
        checkNamespace(collection);
        CollectionManager manager = dmoCollections.newManager(getOwnerId(sessionUser));
        manager.detachCollection(collection);
    }

    /* (non-Javadoc)
     * @see nl.knaw.dans.easy.data.collections.EasyCollections#publishAsOAISet(nl.knaw.dans.easy.domain.model.user.EasyUser, nl.knaw.dans.i.dmo.collections.DmoCollection)
     */
    @Override
    public void publishAsOAISet(EasyUser sessionUser, DmoCollection collection) throws CollectionsException
    {
        checkNamespace(collection);
        CollectionManager manager = dmoCollections.newManager(getOwnerId(sessionUser));
        manager.publishAsOAISet(collection);
    }

    /* (non-Javadoc)
     * @see nl.knaw.dans.easy.data.collections.EasyCollections#unpublishAsOAISet(nl.knaw.dans.easy.domain.model.user.EasyUser, nl.knaw.dans.i.dmo.collections.DmoCollection)
     */
    @Override
    public void unpublishAsOAISet(EasyUser sessionUser, DmoCollection collection) throws CollectionsException
    {
        checkNamespace(collection);
        CollectionManager manager = dmoCollections.newManager(getOwnerId(sessionUser));
        manager.unpublishAsOAISet(collection);
    }
    
    @Override
    public Set<DmoStoreId> filterOAIEndNodes(Set<DmoStoreId> memberIds) throws CollectionsException
    {
        Set<DmoStoreId> filteredIds = dmoCollections.filterOAIEndNodes(memberIds);
        return filteredIds;
    }
    
    private String getOwnerId(EasyUser sessionUser)
    {
        String ownerId = sessionUser.isAnonymous() ? null : sessionUser.getId();
        return ownerId;
    }
    
    private void checkNamespace(DmoCollection collection)
    {
        if (!collection.getDmoStoreId().isInNamespace(DMO_NAMESPACE_EASY_COLLECTION))
        {
            throw new IllegalArgumentException("The dmoStoreId " + collection.getDmoStoreId() + " is not in namespace " + DMO_NAMESPACE_EASY_COLLECTION);
        }
    }
    
    private void checkNamepace(DmoStoreId dmoStoreId)
    {
        if (!dmoStoreId.isInNamespace(DMO_NAMESPACE_EASY_COLLECTION))
        {
            throw new IllegalArgumentException("The dmoStoreId " + dmoStoreId + " is not in namespace " + DMO_NAMESPACE_EASY_COLLECTION);
        }
    }

}
