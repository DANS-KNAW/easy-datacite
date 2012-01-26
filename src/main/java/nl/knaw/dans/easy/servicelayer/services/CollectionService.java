package nl.knaw.dans.easy.servicelayer.services;

import java.util.Map;

import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.collections.SimpleCollection;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.i.dmo.collections.DmoCollection;

public interface CollectionService extends EasyService
{
    
    DmoCollection getRoot(DmoNamespace namespace) throws ServiceException;
    
    DmoCollection getCollection(DmoStoreId dmoStoreId) throws ServiceException;
    
    DmoCollection createRoot(EasyUser sessionUser, String namespace) throws ServiceException;
    
    void saveCollection(EasyUser sessionUser, DmoCollection collection) throws ServiceException;
    
    
    SimpleCollection newCollection(EasyUser sessionUser, SimpleCollection parent, String title);
    
    
    boolean attachCollection(EasyUser sessionUser, SimpleCollection parent, SimpleCollection child);
    
    boolean detachCollection(EasyUser sessionUser, SimpleCollection collection);
    
    boolean publishAsOAISet(EasyUser sessionUser, SimpleCollection collection);
    
    boolean unpublishAsOAISet(EasyUser sessionUser, SimpleCollection collection);
    
    Map<String, SimpleCollection> getAllCollections();

}
