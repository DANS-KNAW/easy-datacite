package nl.knaw.dans.c.dmo.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import nl.knaw.dans.c.dmo.collections.core.AbstractDmoCollectionsTest;
import nl.knaw.dans.c.dmo.collections.core.DmoCollectionImpl;
import nl.knaw.dans.c.dmo.collections.store.CollectionsCache;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.i.dmo.collections.CollectionManager;
import nl.knaw.dans.i.dmo.collections.DmoCollections;
import nl.knaw.dans.i.security.annotations.SecuredOperationUtil;

import org.junit.Test;

public class DmoCollectionsImplTest extends AbstractDmoCollectionsTest
{
    
    private static DmoCollections dmoCols = new DmoCollectionsImpl();

    
    @Test
    public void checkSecuredOperationIds()
    {
        SecuredOperationUtil.checkSecurityIds(CollectionManagerImpl.class);
    }
    
    @Test
    public void newManager()
    {
        initializeWithNoSecurity();
        CollectionManager manager = dmoCols.newManager(null);
        assertTrue(manager instanceof CollectionManager);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void validateXmlWithNull() throws Exception
    {
        DmoCollections dmoCollections = new DmoCollectionsImpl();
        dmoCollections.validateXml(null);
    }
    
    @Test
    public void validateXmlNotInitialized() throws Exception
    {
        DmoCollections dmoCollections = new DmoCollectionsImpl();
        assertTrue(dmoCollections.validateXml(getUrlForXml()).passed());
    }
    
    @Test
    public void filterOAIEndPoints() throws Exception
    {
        initializeWithNoSecurity();        
        DmoStoreId rootId = new DmoStoreId("test:root");
        DmoStoreId id0 = new DmoStoreId("test:0:");
        DmoStoreId id1 = new DmoStoreId("test:1");
        
        DmoCollectionImpl root = new DmoCollectionImpl(rootId);
        root.addChild(new DmoCollectionImpl(id0))
            .addChild(new DmoCollectionImpl(id1));
        CollectionsCache.instance().putDescending(root);
        DmoCollectionImpl coll_1 = (DmoCollectionImpl) CollectionsCache.instance().getCollection(id1);
        coll_1.publishAsOAISet(); // all collections are OAISet
        
        Set<DmoStoreId> memberIds = new HashSet<DmoStoreId>();
        memberIds.add(rootId);
        memberIds.add(id0);
        
        Set<DmoStoreId> storeIds = dmoCols.filterOAIEndNodes(memberIds);
        assertEquals(1, storeIds.size());
        assertTrue(storeIds.contains(id0));
        
        memberIds.add(id1);
        
        storeIds = dmoCols.filterOAIEndNodes(memberIds);
        assertEquals(1, storeIds.size());
        assertTrue(storeIds.contains(id1));
        
    }
    
    @Test
    public void filterOAIEndPoints2() throws Exception
    {
        initializeWithNoSecurity();        
        DmoStoreId rootId = new DmoStoreId("test:root");
        DmoStoreId id0 = new DmoStoreId("test:0:");
        DmoStoreId id1 = new DmoStoreId("test:1");
        
        DmoCollectionImpl root = new DmoCollectionImpl(rootId);
        root.addChild(new DmoCollectionImpl(id0))
            .addChild(new DmoCollectionImpl(id1));
        CollectionsCache.instance().putDescending(root);
        DmoCollectionImpl coll_0 = (DmoCollectionImpl) CollectionsCache.instance().getCollection(id0);
        coll_0.publishAsOAISet(); // id1 is not an OAISET
        
        Set<DmoStoreId> memberIds = new HashSet<DmoStoreId>();
        memberIds.add(rootId);
        memberIds.add(id0);
        memberIds.add(id1);
        
        Set<DmoStoreId> storeIds = dmoCols.filterOAIEndNodes(memberIds);
        assertEquals(1, storeIds.size());
        assertTrue(storeIds.contains(id0));
        
    }

}
