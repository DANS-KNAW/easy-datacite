package nl.knaw.dans.easy.data.collections;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.UnitOfWork;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.repo.exception.UnitOfWorkInterruptException;
import nl.knaw.dans.easy.data.store.EasyUnitOfWork;
import nl.knaw.dans.easy.domain.collections.CollectionCreator;
import nl.knaw.dans.easy.domain.collections.SimpleCollection;
import nl.knaw.dans.easy.domain.collections.SimpleCollectionFactory;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleCollectionFactoryImpl extends SimpleCollectionFactory
{
    private static final Logger logger = LoggerFactory.getLogger(SimpleCollectionFactoryImpl.class);
    
    private final CollectionCreator collectionCreator;
    
    public SimpleCollectionFactoryImpl(CollectionCreator collectionCreator)
    {
        this.collectionCreator = collectionCreator;
    }
    
    private SimpleCollection root;
    
    @Override
    protected SimpleCollection doGetRootCollection(EasyUser user) throws ObjectNotInStoreException, RepositoryException
    {
        if (root == null)
        {
            UnitOfWork uow = new EasyUnitOfWork(user);
            try
            {
                root = (SimpleCollection) uow.retrieveObject(SimpleCollection.ROOT_ID);
                getChildren(root);
            }
            catch (ObjectNotInStoreException e)
            {
                logger.warn("Root of simple collection not found. Creating new root collection");
                root = createRoot(uow);
            }
            finally
            {
                uow.close();
            }
        }
        return root;
    }

    private void getChildren(SimpleCollection simpleCollection)
    {
        for (SimpleCollection child : simpleCollection.getChildren())
        {
            getChildren(child);
        }
    }
    
    private SimpleCollection createRoot(UnitOfWork uow) throws RepositoryException
    {
        SimpleCollection newRoot = collectionCreator.createRoot();
        uow.attach(newRoot);
        attachChildren(uow, newRoot);
        try
        {
            uow.commit();
        }
        catch (UnitOfWorkInterruptException e)
        {
            // not relevant
        }
        return newRoot;
    }

    private void attachChildren(UnitOfWork uow, SimpleCollection simpleCollection) throws RepositoryException
    {
        for (SimpleCollection child : simpleCollection.getChildren())
        {
            uow.attach(child);
            attachChildren(uow, child);
        }
        
    }
    

}
