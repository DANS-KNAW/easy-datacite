package nl.knaw.dans.easy.domain.collections;

import nl.knaw.dans.common.lang.repo.DmoDecorator;
import nl.knaw.dans.common.lang.repo.DmoNamespace;

public class EasyCollectionCreator extends SimpleCollectionCreator
{

    @Override
    protected SimpleCollection createRoot()
    {
        DmoDecorator decorator = new EasyCollectionDmoDecorator();
        SimpleCollection root = new SimpleCollectionImpl(EasyCollectionDmoDecorator.ROOT_ID, decorator);
        root.setLabel("Root of easy collection hierarchy");
        root.setOwnerId("FedoraAdmin");
        root.setState("Active");
        return root;
    }

    @Override
    protected DmoNamespace getNamespace()
    {
        return EasyCollectionDmoDecorator.NAMESPACE;
    }

}
