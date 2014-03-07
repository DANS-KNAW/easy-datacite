package nl.knaw.dans.c.dmo.collections;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.c.dmo.collections.core.DmoCollectionImpl;
import nl.knaw.dans.c.dmo.collections.store.CollectionsCache;
import nl.knaw.dans.c.dmo.collections.store.Store;
import nl.knaw.dans.c.dmo.collections.xml.JiBXCollection;
import nl.knaw.dans.c.dmo.collections.xml.JiBXCollectionConverter;
import nl.knaw.dans.c.dmo.collections.xml.RecursiveListConverter;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.bean.RecursiveList;
import nl.knaw.dans.common.lang.xml.XMLBean;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.i.dmo.collections.CollectionManager;
import nl.knaw.dans.i.dmo.collections.DmoCollection;
import nl.knaw.dans.i.dmo.collections.exceptions.CollectionsException;
import nl.knaw.dans.i.dmo.collections.exceptions.NamespaceNotUniqueException;
import nl.knaw.dans.i.dmo.collections.exceptions.NoSuchCollectionException;
import nl.knaw.dans.i.security.annotations.SecuredOperation;
import nl.knaw.dans.i.store.StoreSession;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CollectionManagerImpl implements CollectionManager
{
    private static final Logger logger = LoggerFactory.getLogger(CollectionManagerImpl.class);

    private final String ownerId;

    protected CollectionManagerImpl(String ownerId)
    {
        this.ownerId = ownerId;
    }

    public String getOwnerId()
    {
        return ownerId;
    }

    @Override
    public boolean exists(DmoNamespace namespace) throws CollectionsException
    {
        boolean exists = false;
        try
        {
            DmoCollection root = CollectionsCache.instance().getRoot(namespace);
            exists = root != null;
        }
        catch (RepositoryException e)
        {
            throw new CollectionsException(e);
        }
        return exists;
    }

    @Override
    @SecuredOperation(id = "nl.knaw.dans.i.dmo.collections.CollectionManager.createRoot")
    public DmoCollection createRoot(URL xmlTree, boolean generateIds) throws CollectionsException
    {
        DmoCollection root;
        try
        {
            root = JiBXCollectionConverter.convert(xmlTree, generateIds);
            checkNamespaceUnique(root.getDmoNamespace());
            complementDcMetadata(root, new DateTime().toString());
            storeDescending(root);
            logger.debug("Stored DmoCollection tree with namespace '" + root.getDmoNamespace().getValue());
        }
        catch (XMLDeserializationException e)
        {
            throw new CollectionsException(e);
        }
        catch (IOException e)
        {
            throw new CollectionsException(e);
        }
        catch (RepositoryException e)
        {
            throw new CollectionsException(e);
        }
        return root;
    }

    private void complementDcMetadata(DmoCollection collection, String date)
    {
        collection.getDcMetadata().addCreator(getOwnerId());
        collection.getDcMetadata().addDate(date);
        collection.getDcMetadata().addIdentifier(collection.getStoreId());
        for (DmoCollection kid : collection.getChildren())
        {
            complementDcMetadata(kid, date);
        }
    }

    private void checkNamespaceUnique(DmoNamespace dmoNamespace) throws RepositoryException, NamespaceNotUniqueException
    {
        DmoCollection root = CollectionsCache.instance().getRoot(dmoNamespace);
        if (root != null)
        {
            throw new NamespaceNotUniqueException("The namespace '" + dmoNamespace + "' already exists and cannot be added.");
        }
    }

    @Override
    @SecuredOperation(id = "nl.knaw.dans.i.dmo.collections.CollectionManager.createRoot")
    public DmoCollection createRoot(DmoNamespace namespace) throws CollectionsException
    {
        DmoCollection root;
        try
        {
            checkNamespaceUnique(namespace);
            root = new DmoCollectionImpl(new DmoStoreId(namespace, DmoCollection.ROOT_ID));
            root.setLabel("Root of " + namespace + " collection");
            root.setShortName(root.getStoreId());
            root.getDcMetadata().addCreator(getOwnerId());
            store(root);
            logger.debug("Stored DmoCollection root with namespace '" + namespace + "'");
        }
        catch (RepositoryException e)
        {
            throw new CollectionsException(e);
        }
        return root;
    }

    @Override
    @SecuredOperation(id = "nl.knaw.dans.i.dmo.collections.CollectionManager.createCollection")
    public DmoCollection createCollection(DmoCollection parent, String label, String shortName) throws CollectionsException
    {
        DmoCollection child;
        CollectionsCache.instance().checkContainsAllInstances(true, parent);
        try
        {
            DmoStoreId dmoStoreId = Store.getStoreManager().nextDmoStoreId(parent.getDmoNamespace());
            child = new DmoCollectionImpl(dmoStoreId);
            child.setLabel(label);
            child.setShortName(shortName);
            child.getDcMetadata().addCreator(getOwnerId());
            ((DmoCollectionImpl) parent).addChild(child);
            storeDescending(parent);
            logger.debug("Stored DmoCollection '" + dmoStoreId.getStoreId());
        }
        catch (RepositoryException e)
        {
            throw new CollectionsException(e);
        }
        return child;
    }

    @Override
    public DmoCollection getRoot(DmoNamespace namespace) throws NoSuchCollectionException, CollectionsException
    {
        DmoCollection root;
        try
        {
            root = CollectionsCache.instance().getRoot(namespace);
            if (root == null)
            {
                throw new NoSuchCollectionException("Not found: " + namespace);
            }
        }
        catch (RepositoryException e)
        {
            throw new CollectionsException(e);
        }
        return root;
    }

    @Override
    public DmoCollection getCollection(DmoStoreId dmoStoreId) throws NoSuchCollectionException, CollectionsException
    {
        DmoCollection collection;
        try
        {
            collection = CollectionsCache.instance().getCollection(dmoStoreId);
            if (collection == null)
            {
                throw new NoSuchCollectionException("Not found: " + dmoStoreId);
            }
        }
        catch (RepositoryException e)
        {
            throw new CollectionsException(e);
        }
        return collection;
    }

    @Override
    @SecuredOperation(id = "nl.knaw.dans.i.dmo.collections.CollectionManager.update")
    public void update(DmoCollection collection) throws CollectionsException
    {
        try
        {
            store(collection);
        }
        catch (RepositoryException e)
        {
            throw new CollectionsException(e);
        }
    }

    @Override
    @SecuredOperation(id = "nl.knaw.dans.i.dmo.collections.CollectionManager.attachCollection")
    public void attachCollection(DmoCollection parent, DmoCollection child) throws CollectionsException
    {
        ((DmoCollectionImpl) parent).addChild(child);
        try
        {
            storeDescending(parent);
        }
        catch (RepositoryException e)
        {
            ((DmoCollectionImpl) parent).removeChild(child);
            throw new CollectionsException(e);
        }
    }

    @Override
    @SecuredOperation(id = "nl.knaw.dans.i.dmo.collections.CollectionManager.detachCollection")
    public void detachCollection(DmoCollection collection) throws CollectionsException
    {
        DmoCollection parent = collection.getParent();
        if (parent == null)
        {
            throw new CollectionsException("No parent to detach from: " + collection);
        }
        ((DmoCollectionImpl) parent).removeChild(collection);
        try
        {
            store(parent, collection);
        }
        catch (RepositoryException e)
        {
            ((DmoCollectionImpl) parent).addChild(collection);
            throw new CollectionsException(e);
        }
    }

    @Override
    @SecuredOperation(id = "nl.knaw.dans.i.dmo.collections.CollectionManager.publishAsOAISet")
    public void publishAsOAISet(DmoCollection collection) throws CollectionsException
    {
        boolean wasPublished = collection.isPublishedAsOAISet();
        ((DmoCollectionImpl) collection).publishAsOAISet();
        try
        {
            storeAscending(collection);
        }
        catch (RepositoryException e)
        {
            if (!wasPublished)
            {
                ((DmoCollectionImpl) collection).unpublishAsOAISet();
            }
            throw new CollectionsException(e);
        }
    }

    @Override
    @SecuredOperation(id = "nl.knaw.dans.i.dmo.collections.CollectionManager.unpublishAsOAISet")
    public void unpublishAsOAISet(DmoCollection collection) throws CollectionsException
    {
        boolean wasPublished = collection.isPublishedAsOAISet();
        ((DmoCollectionImpl) collection).unpublishAsOAISet();
        try
        {
            storeDescending(collection);
        }
        catch (RepositoryException e)
        {
            if (wasPublished)
            {
                ((DmoCollectionImpl) collection).publishAsOAISet();
            }
            throw new CollectionsException(e);
        }
    }

    @Override
    @SecuredOperation(id = "nl.knaw.dans.i.dmo.collections.CollectionManager.purge")
    public void purge(DmoCollection collection) throws CollectionsException
    {
        if (collection.hasParent())
        {
            throw new CollectionsException("Purge not allowed: collection has parent.");
        }

        if (collection.isPublishedAsOAISet())
        {
            throw new CollectionsException("Purge not allowed: collection is published as OAI-set.");
        }

        StoreSession session = Store.newStoreSession(getOwnerId());
        List<DmoStoreId> storeIds = new ArrayList<DmoStoreId>();
        try
        {
            purge((DmoCollectionImpl) collection, session, storeIds);
            session.commit();
        }
        catch (RepositoryException e)
        {
            throw new CollectionsException(e);
        }
        finally
        {
            session.close();
        }
        for (DmoStoreId dmoStoreId : storeIds)
        {
            CollectionsCache.instance().remove(dmoStoreId);
        }
    }

    private void purge(DmoCollectionImpl parent, StoreSession session, List<DmoStoreId> storeIds) throws RepositoryException, CollectionsException
    {
        parent.registerDeleted();
        storeIds.add(parent.getDmoStoreId());
        session.attach(parent);
        for (DmoCollection kid : parent.getChildren())
        {
            parent.removeChild(kid);
            purge((DmoCollectionImpl) kid, session, storeIds);
        }
    }

    @Override
    public XMLBean getXmlBean(DmoNamespace namespace) throws CollectionsException
    {
        DmoCollection root = getRoot(namespace);
        JiBXCollection jibRoot = JiBXCollectionConverter.convert(root, true);
        return jibRoot;
    }

    @Override
    public XMLBean getXmlBean(DmoStoreId dmoStoreId) throws CollectionsException
    {
        DmoCollection collection = getCollection(dmoStoreId);
        JiBXCollection jibCol = JiBXCollectionConverter.convert(collection, false);
        return jibCol;
    }

    @Override
    public RecursiveList getRecursiveList(DmoNamespace namespace) throws CollectionsException
    {
        DmoCollection root = getRoot(namespace);
        RecursiveList recursiveList = RecursiveListConverter.convert(root);
        return recursiveList;
    }

    private void storeDescending(DmoCollection collection) throws RepositoryException
    {
        StoreSession session = Store.newStoreSession(getOwnerId());
        try
        {
            storeDescending(session, collection);
            session.commit();
            CollectionsCache.instance().putDescending(collection);
        }
        finally
        {
            session.close();
        }
    }

    private void storeDescending(StoreSession session, DmoCollection collection) throws RepositoryException
    {
        session.attach(collection);
        for (DmoCollection kid : collection.getChildren())
        {
            storeDescending(session, kid);
        }
    }

    private void store(DmoCollection... collections) throws RepositoryException
    {
        StoreSession session = Store.newStoreSession(getOwnerId());
        try
        {
            for (DmoCollection collection : collections)
            {
                session.attach(collection);
            }
            session.commit();
            for (DmoCollection collection : collections)
            {
                CollectionsCache.instance().put(collection);
            }
        }
        finally
        {
            session.close();
        }
    }

    private void storeAscending(DmoCollection collection) throws RepositoryException
    {
        StoreSession session = Store.newStoreSession(getOwnerId());
        DmoCollection parent = collection;
        try
        {
            while (parent != null)
            {
                session.attach(parent);
                parent = parent.getParent();
            }
            session.commit();
            CollectionsCache.instance().putAscending(collection);
        }
        finally
        {
            session.close();
        }
    }

}
