package nl.knaw.dans.easy.data.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.EasyStore;
import nl.knaw.dans.easy.domain.collections.MockRootCreator;
import nl.knaw.dans.easy.domain.collections.SimpleCollection;
import nl.knaw.dans.easy.domain.collections.SimpleCollectionCreator;

import org.easymock.EasyMock;
import org.junit.BeforeClass;
import org.junit.Test;

public class SimpleCollectionCacheTest
{
    
    private static EasyStore easyStore;
    
    @BeforeClass
    public static void beforeClass()
    {
        easyStore = EasyMock.createMock(EasyStore.class);
        Data data = new Data();
        data.setEasyStore(easyStore);
        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void rootNotInStoreAndNoCreator() throws Exception
    {
        EasyMock.reset(easyStore);
        DmoNamespace nonExistent = expectNotInStore("not-instore");
        SimpleCollectionCreator.unRegister(nonExistent);
        
        EasyMock.replay(easyStore);
        SimpleCollectionCache.instance().getRoot(nonExistent);
    }
    
    @Test
    public void rootNotInStoreSoCreateRootAndIngest() throws Exception
    {
        EasyMock.reset(easyStore);
        DmoNamespace namespace = expectNotInStore(MockRootCreator.NAMESPACE_STRING);
        SimpleCollectionCache.instance().invalidate(namespace);
        
        assertEquals(0, SimpleCollectionCache.instance().size(namespace));
        
        int itemsToIngest = registerCreator(3, 2);
        expectIngest(itemsToIngest);
        
        EasyMock.replay(easyStore);
        SimpleCollection root = SimpleCollectionCache.instance().getRoot(namespace);
        EasyMock.verify(easyStore);
        
        assertEquals(itemsToIngest, SimpleCollectionCache.instance().size(namespace));
        
        SimpleCollection root2 = SimpleCollectionCache.instance().getRoot(namespace);
        
        assertNotNull(root);
        assertEquals(root, root2);
    }
    
    @Test
    public void collectionNotInStoreSoCreateRootAndIngest() throws Exception
    {
        EasyMock.reset(easyStore);
        DmoNamespace namespace = expectNotInStore(MockRootCreator.NAMESPACE_STRING);
        DmoStoreId dmoStoreId = new DmoStoreId(namespace, "" + 5);
        SimpleCollectionCache.instance().invalidate(namespace);
        
        assertEquals(0, SimpleCollectionCache.instance().size(namespace));
        
        int itemsToIngest = registerCreator(2, 2);
        expectIngest(itemsToIngest);
        
        EasyMock.replay(easyStore);
        SimpleCollection sc = SimpleCollectionCache.instance().getSimpleCollection(dmoStoreId);
        EasyMock.verify(easyStore);
        
        assertEquals(itemsToIngest, SimpleCollectionCache.instance().size(namespace));
        
        assertNotNull(sc);
        assertEquals(dmoStoreId, sc.getDmoStoreId());
    }

    private void expectIngest(int itemsToIngest) throws Exception
    {
        EasyMock.expect(easyStore.ingest(EasyMock.isA(SimpleCollection.class), EasyMock.isA(String.class)))
            .andReturn("foo").times(itemsToIngest);
    }

    private DmoNamespace expectNotInStore(String namespace) throws Exception
    {
        DmoNamespace nonExistent = new DmoNamespace(namespace);
        String rootId = DmoStoreId.getStoreId(nonExistent, SimpleCollection.ROOT_ID);
        EasyMock.expect(easyStore.retrieve(rootId)).andThrow(new ObjectNotInStoreException());
        return nonExistent;
    }
    
    private int registerCreator(int wide, int deep)
    {
        MockRootCreator creator = new MockRootCreator(wide, deep);
        SimpleCollectionCreator.register(creator);
        
        return MockRootCreator.calculateItems(wide, deep);
    }

}
