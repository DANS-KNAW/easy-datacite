package nl.knaw.dans.easy.domain.collections;

import static org.junit.Assert.*;

import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata.PropertyName;
import nl.knaw.dans.common.lang.repo.relations.Relation;

import org.junit.Test;

public class SimpleCollectionImplTest
{
    
    private static final String ID_ROOT = EasyCollectionDmoDecorator.ROOT_ID;
    
    private SimpleCollectionImpl root;
    private SimpleCollectionImpl rootKid;
    private SimpleCollectionImpl kidKid;
    
    @Test(expected = IllegalArgumentException.class)
    public void illegalConstructor()
    {
        new SimpleCollectionImpl(null, null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void illegalConstructor2()
    {
        new SimpleCollectionImpl("foo", new EasyCollectionDmoDecorator());
    }
    
    @Test
    public void labelAndDcTitle()
    {
        SimpleCollectionImpl root = new SimpleCollectionImpl(ID_ROOT, new EasyCollectionDmoDecorator());
        
        root.setLabel("foo");
        assertEquals("foo", root.getLabel());
        assertEquals("foo", root.getDcMetadata().getFirst(PropertyName.Title));
        
        root.getDcMetadata().set(PropertyName.Title, "bar");
        assertEquals("bar", root.getLabel());
        assertEquals("bar", root.getDcMetadata().getFirst(PropertyName.Title));
        
        root.getDcMetadata().add(PropertyName.Title, "another title");
        assertEquals("bar", root.getLabel());
        assertEquals("bar", root.getDcMetadata().getFirst(PropertyName.Title));
        assertEquals("another title", root.getDcMetadata().get(PropertyName.Title).get(1));
    }
    
    @Test
    public void getSetElement()
    {
        SimpleCollectionImpl root = new SimpleCollectionImpl(ID_ROOT, new EasyCollectionDmoDecorator());
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
        root = new SimpleCollectionImpl(EasyCollectionDmoDecorator.ROOT_ID, new EasyCollectionDmoDecorator());
        root.setLabel("root of easy collections");        
        rootKid = new SimpleCollectionImpl(EasyCollectionDmoDecorator.NAMESPACE + ":1", new EasyCollectionDmoDecorator());
        rootKid.setLabel("Collection A");
        root.addChild(rootKid);
        kidKid = new SimpleCollectionImpl(EasyCollectionDmoDecorator.NAMESPACE + ":2", new EasyCollectionDmoDecorator());
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
