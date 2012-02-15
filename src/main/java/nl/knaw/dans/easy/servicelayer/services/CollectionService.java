package nl.knaw.dans.easy.servicelayer.services;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.i.dmo.collections.DmoCollection;

public interface CollectionService extends EasyService
{
    
    DmoCollection getRoot() throws ServiceException;
    
    DmoCollection getCollection(DmoStoreId dmoStoreId) throws ServiceException;
    
    DmoCollection createRoot(EasyUser sessionUser) throws ServiceException;
    
    DmoCollection createCollection(EasyUser sessionUser, DmoCollection parent, String label, String shortName) throws ServiceException;
    
    void saveCollection(EasyUser sessionUser, DmoCollection collection) throws ServiceException;
    
    void attachCollection(EasyUser sessionUser, DmoCollection parent, DmoCollection child) throws ServiceException;
    
    void detachCollection(EasyUser sessionUser, DmoCollection collection) throws ServiceException;
    
    void publishAsOAISet(EasyUser sessionUser, DmoCollection collection) throws ServiceException;
    
    void unpublishAsOAISet(EasyUser sessionUser, DmoCollection collection) throws ServiceException;

}
