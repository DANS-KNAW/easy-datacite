package nl.knaw.dans.easy.business.services;

import java.util.Map;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.collections.SimpleCollection;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.SimpleCollectionService;

public class EasySimpleCollectionService extends AbstractEasyService implements SimpleCollectionService
{

    @Override
    public SimpleCollection getRoot() throws ServiceException
    {
//        SimpleCollection root;
//        try
//        {
//            root = Data.getSimpleCollectionFactory().getRootCollection(null);
//        }
//        catch (RepositoryException e)
//        {
//            throw new ServiceException(e);
//        }
        return null;
    }

    @Override
    public SimpleCollection getCollection(String storeId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SimpleCollection newCollection(EasyUser sessionUser, SimpleCollection parent, String title)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void saveCollection(EasyUser sessionUser, SimpleCollection collection)
    {
        // TODO Auto-generated method stub

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
