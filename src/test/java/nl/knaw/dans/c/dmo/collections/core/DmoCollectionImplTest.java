package nl.knaw.dans.c.dmo.collections.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.UnitOfWork;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata.PropertyName;
import nl.knaw.dans.common.lang.repo.relations.Relation;
import nl.knaw.dans.i.dmo.collections.DmoCollection;
import nl.knaw.dans.i.dmo.collections.exceptions.CollectionsException;

import org.junit.BeforeClass;
import org.junit.Test;

public class DmoCollectionImplTest extends AbstractDmoCollectionsTest
{
    
    private static UnitOfWork uow;
    
    private DmoCollectionImpl root;
    private DmoCollectionImpl child;
    private DmoCollectionImpl grandChild;
    
    @BeforeClass
    public static void beforeClass()
    {
        initializeWithNoSecurity();
        uow = createMock(UnitOfWork.class);
    }
    
    @Test
    public void labelAndDcTitle()
    {
        DmoCollectionImpl root = new DmoCollectionImpl(new DmoStoreId("bar:root"));
        
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
        DmoCollectionImpl root = new DmoCollectionImpl(new DmoStoreId("bar:root"));
        assertEquals("bar", root.getOAISetElement());
    }
    
    @Test
    public void createSetSpec() throws Exception
    {
        createHierarchy();
        
        assertEquals("bar", root.createOAISetSpec(root.getOAISetElement()));
        assertEquals("bar:1", child.createOAISetSpec(child.getOAISetElement()));
        assertEquals("bar:1:2", grandChild.createOAISetSpec(grandChild.getOAISetElement()));
    }
    
    @Test
    public void publishAndUnPublishOAISet() throws Exception
    {
        createHierarchy();
        
        assertFalse(root.isPublishedAsOAISet());
        assertFalse(child.isPublishedAsOAISet());
        assertFalse(grandChild.isPublishedAsOAISet());
        DmoCollectionRelations relations = grandChild.getRelations();
        assertFalse(relations.hasRelation("http://www.openarchives.org/OAI/2.0/setSpec", null));
        assertFalse(relations.hasRelation("http://www.openarchives.org/OAI/2.0/setName", null));
        
        grandChild.publishAsOAISet();
        assertTrue(grandChild.isPublishedAsOAISet());
        assertTrue(child.isPublishedAsOAISet());
        assertTrue(root.isPublishedAsOAISet());
        assertTrue(relations.hasRelation("http://www.openarchives.org/OAI/2.0/setSpec", null));
        assertTrue(relations.hasRelation("http://www.openarchives.org/OAI/2.0/setSpec", "bar:1:2"));
        assertTrue(relations.hasRelation("http://www.openarchives.org/OAI/2.0/setName", "Collection AA"));
        assertFalse(relations.hasRelation("http://www.openarchives.org/OAI/2.0/setSpec", "bla bla"));
        //printRelations(grandChild);
        
        root.unpublishAsOAISet();
        assertFalse(root.isPublishedAsOAISet());
        assertFalse(child.isPublishedAsOAISet());
        assertFalse(grandChild.isPublishedAsOAISet());
        assertFalse(relations.hasRelation("http://www.openarchives.org/OAI/2.0/setSpec", null));
        assertFalse(relations.hasRelation("http://www.openarchives.org/OAI/2.0/setName", null));
    }
    
    @Test
    public void getParent() throws Exception
    {
        DmoCollectionImpl coll = new DmoCollectionImpl(new DmoStoreId("collection:42"));
        DmoCollectionImpl parent = new DmoCollectionImpl(new DmoStoreId("collection:23"));
        // set a parentId by the back door
        coll.getRelations().setParent(parent);
        
        assertEquals(new DmoStoreId("collection:23"), coll.getParentId());
        
        reset(uow);
        coll.setUnitOfWork(uow);
        expect(uow.retrieveObject(new DmoStoreId("collection:23"))).andReturn(parent);
        
        replay(uow);
        assertEquals(parent, coll.getParent());
        verify(uow);
    }
    
    @Test
    public void addChild() throws Exception
    {
        DmoCollectionImpl coll = new DmoCollectionImpl(new DmoStoreId("foo:23"));
        DmoCollection kid = new DmoCollectionImpl(new DmoStoreId("foo:42"));
        
        coll.addChild(kid);
        assertEquals(coll, kid.getParent());
        assertEquals(kid, coll.getChildren().get(0));
    }
    
    @Test(expected = CollectionsException.class)
    public void addChildToSelf() throws Exception
    {
        DmoCollectionImpl coll = new DmoCollectionImpl(new DmoStoreId("foo:23"));
        coll.addChild(coll);
    }
    
    @Test(expected = CollectionsException.class)
    public void addChildThatIsPublishedAsOAISet() throws Exception
    {
        DmoCollectionImpl coll = new DmoCollectionImpl(new DmoStoreId("foo:23"));
        DmoCollectionImpl kid = new DmoCollectionImpl(new DmoStoreId("foo:42"));
        kid.publishAsOAISet();
        
        coll.addChild(kid);
    }
    
    @Test(expected = CollectionsException.class)
    public void addChildWithDifferentNamespace() throws Exception
    {
        DmoCollectionImpl coll = new DmoCollectionImpl(new DmoStoreId("foo:23"));
        DmoCollectionImpl kid = new DmoCollectionImpl(new DmoStoreId("bar:42"));
        
        coll.addChild(kid);
    }
    
    @Test(expected = CollectionsException.class)
    public void addChildThatHasAParent() throws Exception
    {
        DmoCollectionImpl coll = new DmoCollectionImpl(new DmoStoreId("foo:23"));
        DmoCollectionImpl kid = new DmoCollectionImpl(new DmoStoreId("foo:42"));
        DmoCollectionImpl parent = new DmoCollectionImpl(new DmoStoreId("foo:11"));
        
        parent.addChild(kid);
        coll.addChild(kid);
    }
    
    @Test(expected = CollectionsException.class)
    public void addChildThatIsRoot() throws Exception
    {
        DmoCollectionImpl coll = new DmoCollectionImpl(new DmoStoreId("foo:23"));
        DmoCollectionImpl root = new DmoCollectionImpl(new DmoStoreId("foo:root"));
        
        coll.addChild(root);
    }
    
    @Test
    public void setShortName()
    {
        DmoCollection coll = new DmoCollectionImpl(new DmoStoreId("foo:42"));
        assertNull(coll.getShortName());
        coll.setShortName("korte naam");
        assertEquals("korte naam", coll.getShortName());
        coll.setShortName("");
        assertNull(coll.getShortName());
    }

    private void createHierarchy() throws Exception
    {
        root = new DmoCollectionImpl(new DmoStoreId("bar:root"));
        root.setLabel("root of bar collections");        
        child = new DmoCollectionImpl(new DmoStoreId("bar:1"));
        child.setLabel("Collection A");
        root.addChild(child);
        grandChild = new DmoCollectionImpl(new DmoStoreId("bar:2"));
        grandChild.setLabel("Collection AA");
        child.addChild(grandChild);
    }
    
    protected void printRelations(DmoCollection sc)
    {
        for (Relation r : sc.getRelations().getRelation(null, null))
        {
            System.err.println(r.toString());
        }
    }

}
