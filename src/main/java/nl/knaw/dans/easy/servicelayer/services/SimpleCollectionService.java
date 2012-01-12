package nl.knaw.dans.easy.servicelayer.services;

import java.util.Map;

import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.collections.SimpleCollection;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

public interface SimpleCollectionService extends EasyService
{
    
    SimpleCollection getRoot(DmoNamespace namespace) throws ServiceException;
    
    SimpleCollection getCollection(String storeId);
    
    SimpleCollection newCollection(EasyUser sessionUser, SimpleCollection parent, String title);
    
    void saveCollection(EasyUser sessionUser, SimpleCollection collection);
    
    boolean attachCollection(EasyUser sessionUser, SimpleCollection parent, SimpleCollection child);
    
    boolean detachCollection(EasyUser sessionUser, SimpleCollection collection);
    
    boolean publishAsOAISet(EasyUser sessionUser, SimpleCollection collection);
    
    boolean unpublishAsOAISet(EasyUser sessionUser, SimpleCollection collection);
    
    Map<String, SimpleCollection> getAllCollections();

}
