package nl.knaw.dans.c.dmo.collections.core;

import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.i.dmo.collections.DmoCollection;
import nl.knaw.dans.i.dmo.collections.exceptions.CollectionsException;

public class MockRootCreator
{
    
    private static int idCounter;
    
    public static DmoCollectionImpl createRoot(String namespace, int wide, int deep) throws CollectionsException
    {
        return createRoot(new DmoNamespace(namespace), wide, deep);
    }
    
    public static DmoCollectionImpl createRoot(DmoNamespace namespace, int wide, int deep) throws CollectionsException
    {
        idCounter = 0;
        DmoStoreId rootId = new DmoStoreId(namespace, DmoCollection.ROOT_ID);
        DmoCollectionImpl root = new DmoCollectionImpl(rootId);
        root.setLabel("Root of " + namespace.getValue());
        root.setShortName("Shortname of " + root.getStoreId());
        root.publishAsOAISet();
        setDcMetadata(root);
        createTree(root, wide, deep);
        
        return root;
    }

    private static void createTree(DmoCollectionImpl parent, int wide, int deep) throws CollectionsException
    {
        for (int w = 0; w < wide; w++)
        {
            DmoStoreId dmoStoreId = new DmoStoreId(parent.getDmoNamespace(), "" + ++idCounter);
            DmoCollectionImpl kid = new DmoCollectionImpl(dmoStoreId);
            parent.addChild(kid);
            kid.setLabel("Label of " + kid.getStoreId());
            kid.setShortName("Shortname of " + kid.getStoreId());
            kid.publishAsOAISet();
            setDcMetadata(kid);
            if (deep > 0)
            {
                createTree(kid, wide, deep - 1);
            }
        }
    }

    private static void setDcMetadata(DmoCollectionImpl dmoCollection)
    {
        String storeId = "storeId=" + dmoCollection.getStoreId();
        String oaiSetspec = "oai-setspec=" + dmoCollection.createOAISetSpec(dmoCollection.getOAISetElement());
        dmoCollection.getDcMetadata().addIdentifier(storeId);
        dmoCollection.getDcMetadata().addIdentifier(oaiSetspec);
    }
    
    /**
     * Calculate the number of nodes in a tree of given (regular) dimensions.
     * <ul>
     * <li>A tree of 3 wide and 2 deep, will create 40 collections.</li>
     * <li>A tree of 5 wide and 3 deep, will create 781 collections.</li>
     * <li>A tree of 5 wide and 5 deep, will create 19531 collections.</li>
     * 
     * @param wide width of the tree
     * @param deep deepness of the tree
     * @return number of collections in the tree
     */
    public static int calculateItems(int wide, int deep)
    {
        int x = wide + 1;
        for (int i = 1; i <= deep; i++)
        {
            x = wide * x + 1;
        }
        return x;
    }
    
    public static void print(DmoCollection collection, String indent)
    {
        System.out.println(indent + "[" + collection.getStoreId() + "] [" + collection.getLabel() + "]");
        for (DmoCollection kid : collection.getChildren())
        {
            print(kid, indent + "-");
        }
    }
    
    public static void main(String[] args) throws CollectionsException
    {
        String namespace = "dmo-collection";
        int wide = 3;
        int deep = 2;
        if (args.length > 0)
        {
            namespace = args[0];
        }
        if (args.length > 1)
        {
            wide = Integer.parseInt(args[1]);
        }
        if (args.length > 2)
        {
            deep = Integer.parseInt(args[2]);
        }
        
        Settings.instance().configure(new MockCollectionsConfiguration());
        
        DmoCollection root = createRoot(namespace, wide, deep);
        print(root, "");
        System.out.println("\nA tree of " + wide + " wide and " + deep + " deep, will create " 
                + calculateItems(wide, deep) + " collections.");
    }

}
