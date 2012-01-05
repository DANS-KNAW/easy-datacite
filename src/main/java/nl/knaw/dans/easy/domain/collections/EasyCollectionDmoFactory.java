package nl.knaw.dans.easy.domain.collections;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.AbstractDmoFactory;
import nl.knaw.dans.common.lang.repo.DmoDecorator;
import nl.knaw.dans.common.lang.repo.DmoNamespace;

public class EasyCollectionDmoFactory extends AbstractDmoFactory<SimpleCollection>
{

    @Override
    public SimpleCollection newDmo() throws RepositoryException
    {
        return createDmo(nextSid());
    }
    
    @Override
    public SimpleCollection createDmo(String storeId)
    {
        DmoDecorator decorator = new EasyCollectionDmoDecorator();
        SimpleCollection easyCollectionDmo = new SimpleCollectionImpl(storeId, decorator);
        return easyCollectionDmo;
    }

    @Override
    public DmoNamespace getNamespace()
    {
        return EasyCollectionDmoDecorator.NAMESPACE;
    }

}
