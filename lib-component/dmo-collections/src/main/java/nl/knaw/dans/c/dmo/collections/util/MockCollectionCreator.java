package nl.knaw.dans.c.dmo.collections.util;

import nl.knaw.dans.c.dmo.collections.core.DmoCollectionImpl;
import nl.knaw.dans.c.dmo.collections.core.Settings;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.i.dmo.collections.DmoCollection;
import nl.knaw.dans.i.dmo.collections.exceptions.CollectionsException;

public class MockCollectionCreator
{
    private static int idCounter;
    private static DmoStoreId contentModelOAISet;

    public static DmoStoreId getContentModelOAISet()
    {
        if (contentModelOAISet == null)
        {
            contentModelOAISet = new DmoStoreId("cm-oaiset:1");
        }
        return contentModelOAISet;
    }

    public static void setContentModelOAISet(DmoStoreId contentModelOAISet)
    {
        MockCollectionCreator.contentModelOAISet = contentModelOAISet;
    }

    public static DmoCollectionImpl createRoot(String namespace, int wide, int deep) throws CollectionsException
    {
        return createRoot(new DmoNamespace(namespace), wide, deep);
    }

    public static DmoCollectionImpl createRoot(DmoNamespace namespace, int wide, int deep) throws CollectionsException
    {
        Settings.instance().setContentModelOAISet(getContentModelOAISet());
        idCounter = 0;
        DmoStoreId rootId = new DmoStoreId(namespace, DmoCollection.ROOT_ID);
        DmoCollectionImpl root = new DmoCollectionImpl(rootId);
        root.setLabel("Root of " + namespace.getValue());
        root.setShortName("Shortname of " + root.getStoreId());
        root.publishAsOAISet();
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
            kid.setShortName("Shortname of " + kid.getDmoStoreId().getId());
            kid.publishAsOAISet();
            if (deep > 0)
            {
                createTree(kid, wide, deep - 1);
            }
        }
    }

    /**
     * Calculate the number of nodes in a tree of given (regular) dimensions.
     * <ul>
     * <li>A tree of 3 wide and 2 deep, will create 40 collections.</li>
     * <li>A tree of 5 wide and 3 deep, will create 781 collections.</li>
     * <li>A tree of 5 wide and 5 deep, will create 19531 collections.</li>
     * 
     * @param wide
     *        width of the tree
     * @param deep
     *        deepness of the tree
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

}
