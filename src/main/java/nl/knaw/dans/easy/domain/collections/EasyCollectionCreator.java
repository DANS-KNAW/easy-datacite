package nl.knaw.dans.easy.domain.collections;

import nl.knaw.dans.common.lang.repo.DmoDecorator;

public class EasyCollectionCreator implements CollectionCreator
{

    @Override
    public SimpleCollection createRoot()
    {
        DmoDecorator decorator = new EasyCollectionDmoDecorator();
        SimpleCollection root = new SimpleCollectionImpl(EasyCollectionDmoDecorator.ROOT_ID, decorator);
        root.setLabel("Root of easy collection hierarchy");
        root.setOwnerId("FedoraAdmin");
        root.setState("Active");
        return root;
    }

}
