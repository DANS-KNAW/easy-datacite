package nl.knaw.dans.c.dmo.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

import nl.knaw.dans.c.dmo.collections.core.AbstractDmoCollectionsTest;
import nl.knaw.dans.c.dmo.collections.core.DmoCollectionImpl;
import nl.knaw.dans.c.dmo.collections.core.Settings;
import nl.knaw.dans.c.dmo.collections.store.CollectionsCache;
import nl.knaw.dans.c.dmo.collections.store.Store;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.i.dmo.collections.CollectionManager;
import nl.knaw.dans.i.dmo.collections.DmoCollection;
import nl.knaw.dans.i.dmo.collections.exceptions.CollectionsException;
import nl.knaw.dans.i.dmo.collections.exceptions.NamespaceNotUniqueException;
import nl.knaw.dans.i.dmo.collections.exceptions.SecurityViolationException;
import nl.knaw.dans.i.security.SecurityAgent;
import nl.knaw.dans.i.security.annotations.SecuredOperation;
import nl.knaw.dans.i.security.annotations.SecuredOperationUtil;
import nl.knaw.dans.i.store.StoreManager;
import nl.knaw.dans.i.store.StoreSession;

import org.junit.BeforeClass;
import org.junit.Test;

public class CollectionManagerImplTest extends AbstractDmoCollectionsTest
{ 
    
    private static final boolean verbose = false;
    private static final String ownerIdCollectionsCache = "CollectionsCache";
    private static StoreManager storeManager;
    private static StoreSession storeSession;
    
    private static String ownerIdManager = "theTester";
    private static DmoNamespace testNS = new DmoNamespace("test-collection");
    private static DmoStoreId testStoreId = new DmoStoreId(testNS, "42");
    
    @BeforeClass
    public static void beforeClass()
    {
        storeManager = createMock(StoreManager.class);
        storeSession = createMock(StoreSession.class);
        Store.register(storeManager);
    }
    
    @Test
    public void checkSecuredOperationIds()
    {
        SecuredOperationUtil.checkSecurityIds(CollectionManagerImpl.class);
    }
    
    @Test(expected = CollectionsException.class)
    public void createRootWithNullUrl() throws Exception
    {
        initializeWithNoSecurity();
        CollectionManager colMan = new CollectionManagerImpl(ownerIdManager);
        URL url = null;
        colMan.createRoot(url, false);
    }
    
    @Test
    public void createRootFromUrl() throws Exception
    {
        initializeWithNoSecurity();
        CollectionsCache.instance().invalidate();
        
        CollectionManager colMan = new CollectionManagerImpl(ownerIdManager);
        URL xml = getUrlForXml();
        
        reset(storeManager, storeSession);
        // first from cache
        expect(storeManager.newStoreSession(ownerIdCollectionsCache)).andReturn(storeSession);
        expect(storeSession.getDataModelObject(isA(DmoStoreId.class))).andThrow(new ObjectNotInStoreException());
        storeSession.close();
        // than from manager
        expect(storeManager.newStoreSession(ownerIdManager)).andReturn(storeSession);
        storeSession.attach(isA(DataModelObject.class));
        expectLastCall().times(15);
        storeSession.commit();
        storeSession.close();
        
        replay(storeManager, storeSession);
        DmoCollection root = colMan.createRoot(xml, false);
        verify(storeManager, storeSession);
        
        assertEquals("jib-col:root", root.getStoreId());
        DublinCoreMetadata dcmd = root.getDcMetadata();
        assertEquals(ownerIdManager, dcmd.getCreator().get(0));
        assertEquals(root, CollectionsCache.instance().getRoot(root.getDmoNamespace()));
        assertEquals(15, CollectionsCache.instance().size());
        
        if (verbose)
            System.err.println(colMan.getXmlBean(root.getDmoNamespace()).asXMLString(4));
    }
    
    @Test(expected = NamespaceNotUniqueException.class)
    public void createRootFromUrlAndNamespaceNotUnique() throws Exception
    {
        initializeWithNoSecurity();
        CollectionsCache.instance().invalidate();
        CollectionsCache.instance().putDescending(new DmoCollectionImpl(new DmoStoreId("jib-col:root")));
        
        CollectionManager colMan = new CollectionManagerImpl(ownerIdManager);
        URL xml = getUrlForXml();
        
        colMan.createRoot(xml, false);
    }
    
    @Test
    public void createRoot() throws Exception
    {
        initializeWithNoSecurity();
        CollectionsCache.instance().invalidate();
        
        CollectionManager colMan = new CollectionManagerImpl(ownerIdManager);
        reset(storeManager, storeSession);
        // first from cache
        expect(storeManager.newStoreSession(ownerIdCollectionsCache)).andReturn(storeSession);
        expect(storeSession.getDataModelObject(isA(DmoStoreId.class))).andThrow(new ObjectNotInStoreException());
        storeSession.close();
        // than from manager
        expect(storeManager.newStoreSession(ownerIdManager)).andReturn(storeSession);
        storeSession.attach(isA(DataModelObject.class));
        storeSession.commit();
        storeSession.close();
        
        replay(storeManager, storeSession);
        DmoCollection root = colMan.createRoot(testNS);
        verify(storeManager, storeSession);
        
        assertEquals(testNS, root.getDmoNamespace());
        assertEquals("test-collection:root", root.getStoreId());
        assertEquals(root, CollectionsCache.instance().getRoot(testNS));
        assertEquals(1, CollectionsCache.instance().size());
    }
    
    @Test(expected = NamespaceNotUniqueException.class)
    public void createRootAndNamespaceNotUnique() throws Exception
    {
        initializeWithNoSecurity();
        CollectionsCache.instance().invalidate();
        CollectionsCache.instance().putDescending(new DmoCollectionImpl(new DmoStoreId("test-collection:root")));
        
        CollectionManager colMan = new CollectionManagerImpl(ownerIdManager);
        
        colMan.createRoot(testNS);
    }
    
    @Test
    public void createCollection() throws Exception
    {
        initializeWithNoSecurity();
        CollectionsCache.instance().invalidate();
        
        DmoCollection parent = new DmoCollectionImpl(testStoreId);
        CollectionsCache.instance().putDescending(parent);
        
        CollectionManager colMan = new CollectionManagerImpl(ownerIdManager);
        reset(storeManager, storeSession);
        expect(storeManager.nextDmoStoreId(testStoreId.getNamespace()))
            .andReturn(new DmoStoreId(testStoreId.getNamespace(), "43"));
        expect(storeManager.newStoreSession(ownerIdManager)).andReturn(storeSession);
        storeSession.attach(isA(DataModelObject.class));
        storeSession.commit();
        storeSession.close();
        
        replay(storeManager, storeSession);
        DmoCollection child = colMan.createCollection(parent, "Label of child", "ShortName of child");
        verify(storeManager, storeSession);
        
        assertEquals("test-collection:43", child.getStoreId());
        assertEquals(parent, child.getParent());
        assertEquals(child, parent.getChildren().get(0));
        assertEquals(2, CollectionsCache.instance().size());
        assertTrue(CollectionsCache.instance().checkContainsAllInstances(true, parent, child));
    }
    
    @Test
    public void getRoot() throws Exception
    {
        initializeWithNoSecurity();
        CollectionsCache.instance().invalidate();
        
        DmoStoreId rootId = new DmoStoreId("test:root");
        DmoStoreId childId = new DmoStoreId("test:1");
        DmoCollectionImpl root = new DmoCollectionImpl(rootId);
        DmoCollection child = new DmoCollectionImpl(childId);
        root.addChild(child);
        
        CollectionManager colMan = new CollectionManagerImpl(ownerIdManager);
        reset(storeManager, storeSession);
        expect(storeManager.newStoreSession(ownerIdCollectionsCache)).andReturn(storeSession);
        expect(storeSession.getDataModelObject(rootId)).andReturn(root);
        // work done in UnitOfWork: out of scope of this component.
        //expect(storeSession.getDataModelObject(childId)).andReturn(child);
        storeSession.close();
        
        replay(storeManager, storeSession);
        DmoCollection returned = colMan.getRoot(new DmoNamespace("test"));
        verify(storeManager, storeSession);
        
        assertEquals(root, returned);
        assertEquals(2, CollectionsCache.instance().size());
    }
    
    @Test
    public void publishAsOAISet() throws Exception
    {
        initializeWithNoSecurity();
        CollectionsCache.instance().invalidate();
        
        DmoCollectionImpl root = new DmoCollectionImpl(new DmoStoreId(testNS, "root"));
        DmoCollection kid = new DmoCollectionImpl(new DmoStoreId(testNS, "42"));
        root.addChild(kid);
        
        assertFalse(root.isPublishedAsOAISet());
        assertFalse(kid.isPublishedAsOAISet());
        
        CollectionManager colMan = new CollectionManagerImpl(ownerIdManager);
        reset(storeManager, storeSession);
        expect(storeManager.newStoreSession(ownerIdManager)).andReturn(storeSession);
        storeSession.attach(kid);
        storeSession.attach(root);
        storeSession.commit();
        storeSession.close();
        
        replay(storeManager, storeSession);
        colMan.publishAsOAISet(kid);
        verify(storeManager, storeSession);
        
        assertTrue(root.isPublishedAsOAISet());
        assertTrue(kid.isPublishedAsOAISet());
        
    }
    
    @Test
    public void publishAsOAISetwithSecurity() throws Exception
    {
        String securityId = SecuredOperationUtil.getSecurityId(CollectionManagerImpl.class, "publishAsOAISet");
        assertNotNull("Wrong set-up: securityId is null", securityId);
        
        SecurityAgent mockAgent = createMock(SecurityAgent.class);
        expect(mockAgent.getSecurityId()).andReturn(securityId);
        replay(mockAgent);
        initializeWithSecurity(mockAgent);
        verify(mockAgent);
        
        DmoCollection collection = new DmoCollectionImpl(testStoreId);
        CollectionManager colMan = new CollectionManagerImpl(ownerIdManager);

        boolean thrown = false;
        reset(mockAgent);
        expect(mockAgent.isAllowed(ownerIdManager, collection)).andReturn(false);
        replay(mockAgent);
        try
        {
            colMan.publishAsOAISet(collection);
        }
        catch (SecurityViolationException e)
        {
            thrown = true;
        }
        verify(mockAgent);
        
        assertTrue(thrown);
    }
    
    
    @Test
    public void allSecuredOperations() throws Exception
    {
        initializeWithNoSecurity();
        Settings.instance().setAllowSecuredMethods(false);
        CollectionManager colMan = new CollectionManagerImpl(ownerIdManager);
        
        for (Method method : colMan.getClass().getDeclaredMethods())
        {
            if (method.getAnnotation(SecuredOperation.class) != null)
            {
                boolean thrown = false;
                Class<?>[] types = method.getParameterTypes();
                Object[] params = new Object[types.length];
                for (int i = 0; i < types.length; i++)
                {
                    if (Boolean.TYPE.equals(types[i]))
                    {
                        params[i] = Boolean.FALSE;
                    }
                }
                try
                {
                    method.invoke(colMan, params);
                }
                catch (InvocationTargetException e)
                {
                    thrown = (e.getCause() instanceof SecurityViolationException);
                }
                assertTrue("Not secured: " + method.getName(), thrown);
            }
        }
    }
    
    
}
