package nl.knaw.dans.easy.domain.collections;

import static org.junit.Assert.*;

import nl.knaw.dans.common.lang.repo.relations.Relation;

import org.junit.Test;

public class SimpleCollectionImplTest
{
    
    private static final String ID_ROOT = SimpleCollection.ROOT_ID;
    
    private SimpleCollectionImpl root;
    private SimpleCollectionImpl rootKid;
    private SimpleCollectionImpl kidKid;
    
    @Test(expected = IllegalArgumentException.class)
    public void illegalConstructor()
    {
        new SimpleCollectionImpl(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void illegalConstructor2()
    {
        new SimpleCollectionImpl("foo");
    }
    
    @Test
    public void getSetElement()
    {
        SimpleCollectionImpl root = new SimpleCollectionImpl(ID_ROOT);
        assertEquals("ec", root.getSetElement());
    }
    
    @Test
    public void createSetSpec()
    {
        createHierarchy();
        
        assertEquals("ec", root.createSetSpec(root.getSetElement()));
        assertEquals("ec:1", rootKid.createSetSpec(rootKid.getSetElement()));
        assertEquals("ec:1:2", kidKid.createSetSpec(kidKid.getSetElement()));
    }
    
    @Test
    public void setOAISet()
    {
        createHierarchy();
        
        assertFalse(kidKid.isOAISet());
        
        kidKid.setOAISet(true);
        assertTrue(kidKid.isOAISet());
        //printRelations(kidKid);
        
        kidKid.setOAISet(false);
        assertFalse(kidKid.isOAISet());
        //printRelations(kidKid);
    }

    private void createHierarchy()
    {
        root = new SimpleCollectionImpl(SimpleCollection.ROOT_ID);
        root.setLabel("root of easy collections");        
        rootKid = new SimpleCollectionImpl(SimpleCollection.NAMESPACE + ":1");
        rootKid.setLabel("Collection A");
        root.addChild(rootKid);
        kidKid = new SimpleCollectionImpl(SimpleCollection.NAMESPACE + ":2");
        kidKid.setLabel("Collection AA");
        rootKid.addChild(kidKid);
    }
    
    protected void printRelations(SimpleCollection sc)
    {
        for (Relation r : sc.getRelations().getRelation(null, null))
        {
            System.err.println(r.toString());
        }
    }

}
