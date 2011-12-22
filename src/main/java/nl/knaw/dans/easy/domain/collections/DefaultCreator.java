package nl.knaw.dans.easy.domain.collections;

public class DefaultCreator implements CollectionCreator
{

    @Override
    public SimpleCollection createRoot()
    {
        SimpleCollection root = new SimpleCollectionImpl(SimpleCollection.ROOT_ID);
        root.setLabel("Root of collection hierarchy");
        root.setOwnerId("FedoraAdmin");
        root.setState("Active");
        return root;
    }

}
