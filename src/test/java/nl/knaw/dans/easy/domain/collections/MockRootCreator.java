package nl.knaw.dans.easy.domain.collections;

import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;

public class MockRootCreator extends SimpleCollectionCreator
{
    
    public static final String NAMESPACE_STRING = EasyCollectionDmoDecorator.NAMESPACE.getValue();
    
    private final int wide;
    private final int deep;
    
    private int idCounter;
    
    public MockRootCreator(int wide, int deep)
    {
        this.wide = wide;
        this.deep = deep;
    }

    @Override
    protected DmoNamespace getNamespace()
    {
        return EasyCollectionDmoDecorator.NAMESPACE;
    }

    @Override
    protected SimpleCollection createRoot()
    {
        EasyCollectionDmoDecorator decorator = new EasyCollectionDmoDecorator();
        SimpleCollectionImpl root = new SimpleCollectionImpl(EasyCollectionDmoDecorator.ROOT_ID, decorator);
        root.setLabel("setspec=" + root.createOAISetSpec(root.getOAISetElement()));
        createTree(root, wide, deep);
        return root;
    }
    
    private void createTree(SimpleCollectionImpl parent, int wide, int deep)
    {
        for (int w = 0; w < wide; w++)
        {
            EasyCollectionDmoDecorator decorator = new EasyCollectionDmoDecorator();
            String storeId = DmoStoreId.getStoreId(EasyCollectionDmoDecorator.NAMESPACE, "" + (++idCounter));
            SimpleCollectionImpl sc = new SimpleCollectionImpl(storeId, decorator);
            parent.addChild(sc);
            sc.setLabel("setspec=" + sc.createOAISetSpec(sc.getOAISetElement()));
            if (deep > 0)
            {
                createTree(sc, wide, deep - 1);
            }
                
        }
    }
    
    public static void print(SimpleCollection sc, String indent)
    {
        System.out.println(indent + "[" + sc.getStoreId() + "] [" + sc.getLabel() + "]");
        for (SimpleCollection kid : sc.getChildren())
        {
            print(kid, indent + "-");
        }
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
    
    public static void main(String[] args)
    {
        int wide = 2;
        int deep = 2;
        MockRootCreator creator = new MockRootCreator(wide, deep);
        SimpleCollection root = creator.createRoot();
        
        print(root, "");
        System.out.println("A tree of " + wide + " wide and " + deep + " deep, will create " 
                + calculateItems(wide, deep) + " collections.");
    }

}
