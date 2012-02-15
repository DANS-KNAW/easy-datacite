package nl.knaw.dans.easy.business.services;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.CollectionService;
import nl.knaw.dans.i.dmo.collections.DmoCollection;
import nl.knaw.dans.i.dmo.collections.exceptions.CollectionsException;

public class EasyCollectionService extends AbstractEasyService implements CollectionService
{
    
    public EasyCollectionService()
    {

    }  
    
    @Override
    public DmoCollection createRoot(EasyUser sessionUser) throws ServiceException
    {
        DmoCollection root;
        try
        {
            root = Data.getEasyCollections().createRoot(sessionUser);
        }
        catch (CollectionsException e)
        {
            throw new ServiceException(e);
        }
        return root;
    }

    @Override
    public DmoCollection getRoot() throws ServiceException
    {
        DmoCollection root;
        try
        {
            root = Data.getEasyCollections().getRoot();
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
        DmoCollection collection;
        try
        {
            collection = Data.getEasyCollections().getCollection(dmoStoreId);
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
        try
        {
            Data.getEasyCollections().saveCollection(sessionUser, collection);
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
        try
        {
            collection = Data.getEasyCollections().createCollection(sessionUser, parent, label, shortName);
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
        try
        {
            Data.getEasyCollections().attachCollection(sessionUser, parent, child);
        }
        catch (CollectionsException e)
        {
            throw new ServiceException(e);
        }
    }

    @Override
    public void detachCollection(EasyUser sessionUser, DmoCollection collection) throws ServiceException
    {
        try
        {
            Data.getEasyCollections().detachCollection(sessionUser, collection);
        }
        catch (CollectionsException e)
        {
            throw new ServiceException(e);
        }
    }

    @Override
    public void publishAsOAISet(EasyUser sessionUser, DmoCollection collection) throws ServiceException
    {
        try
        {
            Data.getEasyCollections().publishAsOAISet(sessionUser, collection);
        }
        catch (CollectionsException e)
        {
            throw new ServiceException(e);
        }
    }

    @Override
    public void unpublishAsOAISet(EasyUser sessionUser, DmoCollection collection) throws ServiceException
    {
        try
        {
            Data.getEasyCollections().unpublishAsOAISet(sessionUser, collection);
        }
        catch (CollectionsException e)
        {
            throw new ServiceException(e);
        }
    }

}
