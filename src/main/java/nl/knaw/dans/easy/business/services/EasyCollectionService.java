package nl.knaw.dans.easy.business.services;

import java.util.Map;

import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.collections.SimpleCollection;
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
    public void doBeanPostProcessing() throws ServiceException
    {
        
    }
    
    @Override
    public DmoCollection createRoot(EasyUser sessionUser, String namespace) throws ServiceException
    {
        String ownerId = sessionUser.isAnonymous() ? null : sessionUser.getId();
        DmoNamespace dmoNamespace = new DmoNamespace(namespace);
        CollectionManager manager = dmoCollections.newManager(ownerId);
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
        String ownerId = sessionUser.isAnonymous() ? null : sessionUser.getId();
        CollectionManager manager = dmoCollections.newManager(ownerId);
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
    public SimpleCollection newCollection(EasyUser sessionUser, SimpleCollection parent, String title)
    {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public boolean attachCollection(EasyUser sessionUser, SimpleCollection parent, SimpleCollection child)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean detachCollection(EasyUser sessionUser, SimpleCollection collection)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean publishAsOAISet(EasyUser sessionUser, SimpleCollection collection)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean unpublishAsOAISet(EasyUser sessionUser, SimpleCollection collection)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Map<String, SimpleCollection> getAllCollections()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
