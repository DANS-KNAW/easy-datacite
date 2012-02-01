package nl.knaw.dans.easy.business.services;

import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.CollectionService;
import nl.knaw.dans.i.dmo.collections.CollectionManager;
import nl.knaw.dans.i.dmo.collections.DmoCollection;
import nl.knaw.dans.i.dmo.collections.DmoCollections;
import nl.knaw.dans.i.dmo.collections.exceptions.CollectionsException;
import nl.knaw.dans.i.dmo.collections.exceptions.NoSuchCollectionException;

public class EasyCollectionService extends AbstractEasyService implements CollectionService
{
    
    private final DmoCollections dmoCollections;
    
    public EasyCollectionService(DmoCollections dmoCollections)
    {
        this.dmoCollections = dmoCollections;
    }
    
    @Override
    public DmoCollection createRoot(EasyUser sessionUser, String namespace) throws ServiceException
    {
        DmoNamespace dmoNamespace = new DmoNamespace(namespace);
        CollectionManager manager = dmoCollections.newManager(getOwnerId(sessionUser));
        DmoCollection root;
        try
        {
            root = manager.createRoot(dmoNamespace);
        }
        catch (CollectionsException e)
        {
            throw new ServiceException(e);
        }
        return root;
    }

    @Override
    public DmoCollection getRoot(DmoNamespace namespace) throws ServiceException
    {
        CollectionManager manager = dmoCollections.newManager(null);
        DmoCollection root;
        try
        {
            root = manager.getRoot(namespace);
        }
        catch (NoSuchCollectionException e)
        {
            throw new ServiceException(e);
        }
        catch (CollectionsException e)
        {
            throw new ServiceException(e);
        }
        return root;
    }

    @Override
    public DmoCollection getCollection(DmoStoreId dmoStoreId) throws ServiceException
    {
        CollectionManager manager = dmoCollections.newManager(null);
        DmoCollection collection;
        try
        {
            collection = manager.getCollection(dmoStoreId);
        }
        catch (NoSuchCollectionException e)
        {
            throw new ServiceException(e);
        }
        catch (CollectionsException e)
        {
            throw new ServiceException(e);
        }
        return collection;
    }
    
    @Override
    public void saveCollection(EasyUser sessionUser, DmoCollection collection) throws ServiceException
    {
        CollectionManager manager = dmoCollections.newManager(getOwnerId(sessionUser));
        try
        {
            manager.update(collection);
        }
        catch (CollectionsException e)
        {
            throw new ServiceException(e);
        }
    }

    @Override
    public DmoCollection createCollection(EasyUser sessionUser, DmoCollection parent, String label, String shortName) throws ServiceException
    {
        DmoCollection collection;
        CollectionManager manager = dmoCollections.newManager(getOwnerId(sessionUser));
        try
        {
            collection = manager.createCollection(parent, label, shortName);
        }
        catch (CollectionsException e)
        {
            throw new ServiceException(e);
        }
        return collection;
    }

    @Override
    public void attachCollection(EasyUser sessionUser, DmoCollection parent, DmoCollection child) throws ServiceException
    {
        CollectionManager manager = dmoCollections.newManager(getOwnerId(sessionUser));
        try
        {
            manager.attachCollection(parent, child);
        }
        catch (CollectionsException e)
        {
            throw new ServiceException(e);
        }
    }

    @Override
    public void detachCollection(EasyUser sessionUser, DmoCollection collection) throws ServiceException
    {
        CollectionManager manager = dmoCollections.newManager(getOwnerId(sessionUser));
        try
        {
            manager.detachCollection(collection);
        }
        catch (CollectionsException e)
        {
            throw new ServiceException(e);
        }
    }

    @Override
    public void publishAsOAISet(EasyUser sessionUser, DmoCollection collection) throws ServiceException
    {
        CollectionManager manager = dmoCollections.newManager(getOwnerId(sessionUser));
        try
        {
            manager.publishAsOAISet(collection);
        }
        catch (CollectionsException e)
        {
            throw new ServiceException(e);
        }
    }

    @Override
    public void unpublishAsOAISet(EasyUser sessionUser, DmoCollection collection) throws ServiceException
    {
        CollectionManager manager = dmoCollections.newManager(getOwnerId(sessionUser));
        try
        {
            manager.unpublishAsOAISet(collection);
        }
        catch (CollectionsException e)
        {
            throw new ServiceException(e);
        }
    }
    
    private String getOwnerId(EasyUser sessionUser)
    {
        String ownerId = sessionUser.isAnonymous() ? null : sessionUser.getId();
        return ownerId;
    }

}
