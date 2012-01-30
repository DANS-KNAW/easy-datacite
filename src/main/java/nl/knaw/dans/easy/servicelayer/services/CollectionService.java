package nl.knaw.dans.easy.servicelayer.services;

import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.i.dmo.collections.DmoCollection;

public interface CollectionService extends EasyService
{
    
    DmoCollection getRoot(DmoNamespace namespace) throws ServiceException;
    
    DmoCollection getCollection(DmoStoreId dmoStoreId) throws ServiceException;
    
    DmoCollection createRoot(EasyUser sessionUser, String namespace) throws ServiceException;
    
    DmoCollection createCollection(EasyUser sessionUser, DmoCollection parent, String label, String shortName) throws ServiceException;
    
    void saveCollection(EasyUser sessionUser, DmoCollection collection) throws ServiceException;
    
    

}
