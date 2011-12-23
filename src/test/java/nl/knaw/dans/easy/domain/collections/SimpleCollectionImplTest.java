package nl.knaw.dans.easy.domain.collections;

import static org.junit.Assert.*;

import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata.PropertyName;
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
    public void labelAndDcTitle()
    {
        SimpleCollectionImpl root = new SimpleCollectionImpl(ID_ROOT);
        
        root.setLabel("foo");
        assertEquals("foo", root.getLabel());
        assertEquals("foo", root.getDcMetadata().getFirst(PropertyName.Title));
        
        root.getDcMetadata().set(PropertyName.Title, "bar");
        assertEquals("bar", root.getLabel());
        assertEquals("bar", root.getDcMetadata().getFirst(PropertyName.Title));
    }
    
    @Test
    public void getSetElement()
    {
        SimpleCollectionImpl root = new SimpleCollectionImpl(ID_ROOT);
        assertEquals("esc", root.getOAISetElement());
    }
    
    @Test
    public void createSetSpec()
    {
        createHierarchy();
        
        assertEquals("esc", root.createOAISetSpec(root.getOAISetElement()));
        assertEquals("esc:1", rootKid.createOAISetSpec(rootKid.getOAISetElement()));
        assertEquals("esc:1:2", kidKid.createOAISetSpec(kidKid.getOAISetElement()));
        
        assertEquals("Collection A", rootKid.getDcMetadata().getFirst(PropertyName.Title));
    }
    
    @Test
    public void setOAISet()
    {
        createHierarchy();
        
        assertFalse(kidKid.isPublishedAsOAISet());
        
        kidKid.publishAsOAISet();
        assertTrue(kidKid.isPublishedAsOAISet());
        //printRelations(kidKid);
        
        kidKid.unpublishAsOAISet();
        assertFalse(kidKid.isPublishedAsOAISet());
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
