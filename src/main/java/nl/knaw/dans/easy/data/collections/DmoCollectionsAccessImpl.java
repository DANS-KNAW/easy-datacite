package nl.knaw.dans.easy.data.collections;

import java.net.URL;
import java.util.Iterator;
import java.util.Set;

import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.xml.ValidatorException;
import nl.knaw.dans.common.lang.xml.XMLErrorHandler;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;
import nl.knaw.dans.easy.domain.collections.ECollection;
import nl.knaw.dans.easy.domain.model.Constants;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.i.dmo.collections.CollectionManager;
import nl.knaw.dans.i.dmo.collections.DmoCollection;
import nl.knaw.dans.i.dmo.collections.DmoCollections;
import nl.knaw.dans.i.dmo.collections.exceptions.CollectionsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DmoCollectionsAccessImpl implements DmoCollectionsAccess
{
    
    private static final Logger logger = LoggerFactory.getLogger(DmoCollectionsAccessImpl.class);
    
    private final DmoCollections dmoCollections;
    
    public DmoCollectionsAccessImpl(DmoCollections dmoCollections)
    {
        this.dmoCollections = dmoCollections;
        dmoCollections.setContentModelOAISet(new DmoStoreId(Constants.CM_OAI_SET_1));
        for (ECollection eColl : ECollection.values())
        {
            dmoCollections.registerNamespace(eColl.namespace);
        }
    }
    
    /* (non-Javadoc)
     * @see nl.knaw.dans.easy.data.collections.EasyCollections#createRoot(nl.knaw.dans.easy.domain.model.user.EasyUser, java.lang.String)
     */
    @Override
    public DmoCollection createRoot(EasyUser sessionUser, ECollection eColl) throws CollectionsException
    {
        CollectionManager manager = dmoCollections.newManager(getOwnerId(sessionUser));
        DmoCollection root = manager.createRoot(eColl.namespace);
        return root;
    }

    /* (non-Javadoc)
     * @see nl.knaw.dans.easy.data.collections.EasyCollections#getRoot(nl.knaw.dans.common.lang.repo.DmoNamespace)
     */
    @Override
    public DmoCollection getRoot(ECollection eColl) throws CollectionsException
    {
        CollectionManager manager = dmoCollections.newManager(null);
        DmoCollection root = manager.getRoot(eColl.namespace);
        return root;
    }

    /* (non-Javadoc)
     * @see nl.knaw.dans.easy.data.collections.EasyCollections#getCollection(nl.knaw.dans.common.lang.repo.DmoStoreId)
     */
    @Override
    public DmoCollection getCollection(DmoStoreId dmoStoreId) throws CollectionsException
    {
        assertECollection(dmoStoreId);
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
        assertECollection(collection);
        CollectionManager manager = dmoCollections.newManager(getOwnerId(sessionUser));
        manager.update(collection);
    }


    /* (non-Javadoc)
     * @see nl.knaw.dans.easy.data.collections.EasyCollections#createCollection(nl.knaw.dans.easy.domain.model.user.EasyUser, nl.knaw.dans.i.dmo.collections.DmoCollection, java.lang.String, java.lang.String)
     */
    @Override
    public DmoCollection createCollection(EasyUser sessionUser, DmoCollection parent, String label, String shortName) throws CollectionsException
    {
        assertECollection(parent);
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
        assertECollection(parent);
        CollectionManager manager = dmoCollections.newManager(getOwnerId(sessionUser));
        manager.attachCollection(parent, child);
    }

    /* (non-Javadoc)
     * @see nl.knaw.dans.easy.data.collections.EasyCollections#detachCollection(nl.knaw.dans.easy.domain.model.user.EasyUser, nl.knaw.dans.i.dmo.collections.DmoCollection)
     */
    @Override
    public void detachCollection(EasyUser sessionUser, DmoCollection collection) throws CollectionsException
    {
        assertECollection(collection);
        CollectionManager manager = dmoCollections.newManager(getOwnerId(sessionUser));
        manager.detachCollection(collection);
    }

    /* (non-Javadoc)
     * @see nl.knaw.dans.easy.data.collections.EasyCollections#publishAsOAISet(nl.knaw.dans.easy.domain.model.user.EasyUser, nl.knaw.dans.i.dmo.collections.DmoCollection)
     */
    @Override
    public void publishAsOAISet(EasyUser sessionUser, DmoCollection collection) throws CollectionsException
    {
        assertECollection(collection);
        CollectionManager manager = dmoCollections.newManager(getOwnerId(sessionUser));
        manager.publishAsOAISet(collection);
    }

    /* (non-Javadoc)
     * @see nl.knaw.dans.easy.data.collections.EasyCollections#unpublishAsOAISet(nl.knaw.dans.easy.domain.model.user.EasyUser, nl.knaw.dans.i.dmo.collections.DmoCollection)
     */
    @Override
    public void unpublishAsOAISet(EasyUser sessionUser, DmoCollection collection) throws CollectionsException
    {
        assertECollection(collection);
        CollectionManager manager = dmoCollections.newManager(getOwnerId(sessionUser));
        manager.unpublishAsOAISet(collection);
    }
    
    @Override
    public Set<DmoStoreId> filterOAIEndNodes(Set<DmoStoreId> memberIds) throws CollectionsException
    {
        Set<DmoStoreId> filteredIds = dmoCollections.filterOAIEndNodes(memberIds);
        return filteredIds;
    }
    
    // hack to get a collection tree into all Fedora repositories that are used.
    // !!WARNING!!
    // Causes deadlock at full server restart:
    // If Fedora not yet running,
    //      this method never returns
    //      Tomcat keeps waiting....
    //      ... and Fedora never starts.
//    public void initializeCollections() throws CollectionsException, ValidatorException, XMLSerializationException
//    {
//        CollectionManager manager = dmoCollections.newManager("migration");
//        Iterator<ECollection> collIter = ECollection.iterator();
//        while (collIter.hasNext())
//        {
//            ECollection eColl = collIter.next();
//            if (!manager.exists(eColl.namespace))
//            {
//                URL templateUrl = eColl.getTemplateURL();
//                if (templateUrl != null)
//                {
//                    XMLErrorHandler handler = dmoCollections.validateXml(templateUrl);
//                    if (handler.passed())
//                    {
//                        logger.info("Ingesting collection template for " + eColl.namespace.getValue());
//                        manager.createRoot(templateUrl, false);
//                    }
//                    else
//                    {
//                        throw new CollectionsException("Invallid xml in template for " + eColl.namespace.getValue() + handler.getMessages());
//                    }
//                    
//                }
//            }
//            
//        }
//    }
    
    private String getOwnerId(EasyUser sessionUser)
    {
        String ownerId = sessionUser.isAnonymous() ? null : sessionUser.getId();
        return ownerId;
    }
    
    private void assertECollection(DmoStoreId dmoStoreId) throws CollectionsException
    {
        if (!ECollection.isECollection(dmoStoreId))
        {
            throw new CollectionsException("Not an ECollection: " + dmoStoreId);
        }
    }
    
    private void assertECollection(DataModelObject dmo) throws CollectionsException
    {
        if (!ECollection.isECollection(dmo))
        {
            throw new CollectionsException("Not an ECollection: " + dmo);
        }
    }

}
