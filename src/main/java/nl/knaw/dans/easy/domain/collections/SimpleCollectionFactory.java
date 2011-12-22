package nl.knaw.dans.easy.domain.collections;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

public abstract class SimpleCollectionFactory
{
    
    public SimpleCollection getRootCollection(EasyUser user) throws ObjectNotInStoreException, RepositoryException
    {
        return doGetRootCollection(user);
    }
    
    protected abstract SimpleCollection doGetRootCollection(EasyUser user) throws ObjectNotInStoreException, RepositoryException;
    
    
}
