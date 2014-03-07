package nl.knaw.dans.common.fedora.store;

// Test is not working (hb)
//
//
// import static org.junit.Assert.*;
//
// import java.util.List;
//
// import nl.knaw.dans.common.fedora.Fedora;
// import nl.knaw.dans.common.lang.RepositoryException;
// import nl.knaw.dans.common.lang.repo.DataModelObject;
// import nl.knaw.dans.common.lang.repo.collections.AbstractDmoContainer;
// import nl.knaw.dans.common.lang.repo.collections.AbstractDmoContainerItem;
// import nl.knaw.dans.common.lang.repo.collections.AbstractDmoRecursiveItem;
// import nl.knaw.dans.common.lang.repo.dummy.DummyDmoContainer;
// import nl.knaw.dans.common.lang.repo.dummy.DummyDmoContainerItem;
// import nl.knaw.dans.common.lang.repo.dummy.DummyDmoContext;
// import nl.knaw.dans.common.lang.repo.dummy.DummyDmoRecursiveItem;
// import nl.knaw.dans.common.lang.repo.dummy.DummyUnitOfWork;
// import nl.knaw.dans.common.lang.repo.exception.ConcurrentUpdateException;
// import nl.knaw.dans.common.lang.repo.exception.ObjectExistsException;
// import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
// import nl.knaw.dans.common.lang.repo.relations.Relation;
// import nl.knaw.dans.common.lang.repo.relations.RelsConstants;
// import nl.knaw.dans.common.lang.test.Tester;
// import nl.knaw.dans.common.lang.xml.XMLSerializationException;
//
// import org.junit.AfterClass;
// import org.junit.BeforeClass;
// import org.junit.Ignore;
// import org.junit.Test;
//
// public class FedoraStoreOnlineTest
// {
//    
// private static final String KEY_FEDORA_BASE_URL = "fedora.base.url";
// private static final String KEY_FEDORA_ADMIN_NAME = "fedora.admin.username";
// private static final String KEY_FEDORA_ADMIN_PASS = "fedora.admin.userpass";
//
// private static final int WAIT_FOR_RESOURCEINDEX = 6000; // if you put syncUpdates to true you can set
// this one to 0
//    
// private static FedoraDmoStore fedoraStore;
//
// private static DummyDmoContainerItem i1;
// private static DummyDmoContainerItem i2;
// private static DummyDmoRecursiveItem r1;
// private static DummyDmoContainer c1;
// private static DummyDmoContainer c2;
// private static DummyDmoContainer c3;
//
//
// public static String getBaseUrl()
// {
// return Tester.getString(KEY_FEDORA_BASE_URL);
// }
//    
// public static String getAdminName()
// {
// return Tester.getString(KEY_FEDORA_ADMIN_NAME);
// }
//    
// public static String getAdminPass()
// {
// return Tester.getString(KEY_FEDORA_ADMIN_PASS);
// }
//    
// @BeforeClass
// public static void beforeClass() throws Exception
// {
// // setup FedoraStore
// Fedora fedora = new Fedora(getBaseUrl(), getAdminName(), getAdminPass());
// fedoraStore = new FedoraDmoStore("store", fedora, new DummyDmoContext());
//    	
// DummyConverter<DummyDmoContainer> containerConverter = new
// DummyConverter<DummyDmoContainer>(DummyDmoContainer.class);
// fedoraStore.addConverter(containerConverter);
//
// DummyConverter<DummyDmoContainerItem> itemConverter = new
// DummyConverter<DummyDmoContainerItem>(DummyDmoContainerItem.class);
// fedoraStore.addConverter(itemConverter);
//    	
// DummyConverter<DummyDmoRecursiveItem> ritemConverter = new
// DummyConverter<DummyDmoRecursiveItem>(DummyDmoRecursiveItem.class);
// fedoraStore.addConverter(ritemConverter);
//    	
// DummyUnitOfWork dmoUow = new DummyUnitOfWork(fedoraStore);
//
// // setup some dummy objects
// i1 = new DummyDmoContainerItem("dummy-item:1");
// dmoUow.attach(i1);
// i2 = new DummyDmoContainerItem("dummy-item:2");
// dmoUow.attach(i2);
//		
// c1 = new DummyDmoContainer("dummy-container:1");
// dmoUow.attach(c1);
// c2 = new DummyDmoContainer("dummy-container:2");
// dmoUow.attach(c2);
// c3 = new DummyDmoContainer("dummy-container:3");
// dmoUow.attach(c3);
//			
// r1 = new DummyDmoRecursiveItem("dummy-recursiveitem:1");
// dmoUow.attach(r1);
//		
// c1.addChild(i1);
// c2.addChild(i1);
// c2.addChild(i2);
// r1.addChild(i2);
// r1.addParent(c3);
//		
// purgeDummyObjects();
// ingestDummyObjects();
// }
//
//
// private static void ingestDummyObjects() throws ObjectExistsException, RepositoryException,
// InterruptedException
// {
// fedoraStore.ingest(c1, "ingest container 1");
// fedoraStore.ingest(c2, "ingest container 2");
// fedoraStore.ingest(c3, "ingest container 3");
// fedoraStore.ingest(i1, "ingest container item 1");
// fedoraStore.ingest(i2, "ingest container item 2");
// fedoraStore.ingest(r1, "ingest recursive item 1");
//	
// // if Fedora is configured with syncUpdates false then it will take
// // Fedora 5 seconds before it updates the resource index.
// Thread.sleep(WAIT_FOR_RESOURCEINDEX);
// }
//    
// @AfterClass
// public static void afterClass() throws Exception, RepositoryException
// {
// purgeDummyObjects();
// }
//
// private static void purgeDummyObjects()
// {
// tryPurge(c1);
// tryPurge(c2);
// tryPurge(c3);
// tryPurge(i1);
// tryPurge(i2);
// tryPurge(r1);
// }
//
// private static void tryPurge(DataModelObject dmo)
// {
// try{
// fedoraStore.purge(dmo, false, "purge");
// }
// catch(Throwable t)
// {
// t.printStackTrace();
// fail("Could not purge: " + dmo);
// }
// }
//
//
// @Test
// public void getRelationshipsTest() throws RepositoryException, XMLSerializationException,
// InterruptedException
// {
// List<Relation> rels = fedoraStore.getRelations(
// null,
// RelsConstants.DANS_NS.IS_MEMBER_OF.toString(),
// c1.getStoreId()
// );
//		
// assertEquals(1, rels.size());
// assertEquals(i1.getStoreId(), rels.get(0).subject);
//
// rels = fedoraStore.getRelations(
// i1.getStoreId(),
// RelsConstants.DANS_NS.IS_MEMBER_OF.toString(),
// null
// );
//		
// assertEquals(2, rels.size());
// assertTrue(relListContains(rels, i1.getStoreId(), RelsConstants.DANS_NS.IS_MEMBER_OF.toString(),
// c1.getStoreId()));
// assertTrue(relListContains(rels, i1.getStoreId(), RelsConstants.DANS_NS.IS_MEMBER_OF.toString(),
// c2.getStoreId()));
//
// rels = fedoraStore.getRelations(
// null,
// RelsConstants.DANS_NS.IS_MEMBER_OF.toString(),
// null
// );
//		
// assertTrue(relListContains(rels, i1.getStoreId(), RelsConstants.DANS_NS.IS_MEMBER_OF.toString(),
// c1.getStoreId()));
// assertTrue(relListContains(rels, i1.getStoreId(), RelsConstants.DANS_NS.IS_MEMBER_OF.toString(),
// c2.getStoreId()));
// assertTrue(relListContains(rels, i2.getStoreId(), RelsConstants.DANS_NS.IS_MEMBER_OF.toString(),
// c2.getStoreId()));
// assertTrue(relListContains(rels, i2.getStoreId(), RelsConstants.DANS_NS.IS_MEMBER_OF.toString(),
// r1.getStoreId()));
// assertTrue(relListContains(rels, r1.getStoreId(), RelsConstants.DANS_NS.IS_MEMBER_OF.toString(),
// c3.getStoreId()));
// }
//
// private boolean relListContains(List<Relation> rels, String subject,
// String predicate, String object)
// {
// for(Relation rel : rels)
// {
// if (rel.subject.equals(subject) && rel.predicate.equals(predicate)
// && rel.object.equals(object))
// return true;
// }
// return false;
// }
//	
// @Test // DmoContainer.CONTENTMODEL id no longer. (++)
// public void getSidsByContentModelTest() throws RepositoryException
// {
// List<String> containers = fedoraStore.getSidsByContentModel(AbstractDmoContainer.CONTENTMODEL);
//		
// assertTrue(containers.contains(c1.getStoreId()));
// assertTrue(containers.contains(c2.getStoreId()));
// assertTrue(containers.contains(c3.getStoreId()));
//
// List<String> items = fedoraStore.getSidsByContentModel(AbstractDmoContainerItem.CONTENT_MODEL);
//
// assertTrue(items.contains(i1.getStoreId()));
// assertTrue(items.contains(i2.getStoreId()));
//	
// List<String> ritems = fedoraStore.getSidsByContentModel(AbstractDmoRecursiveItem.CONTENT_MODEL);
//
// assertTrue(ritems.contains(r1.getStoreId()));
// }
//
//
// @Test
// @Ignore
// public void updateRelationsTest() throws RepositoryException, XMLSerializationException,
// InterruptedException
// {
// c1.addChild(r1);
//    	
// fedoraStore.update(c1, "updated object "+ c1.toString());
// fedoraStore.update(r1, "updated object "+ r1.toString());
// Thread.sleep(WAIT_FOR_RESOURCEINDEX);
//    	
// List<Relation> rels = fedoraStore.getRelations(
// null,
// RelsConstants.DANS_NS.IS_MEMBER_OF.toString(),
// c1.getStoreId()
// );
//
// assertEquals(2, rels.size());
// assertTrue(relListContains(rels, r1.getStoreId(), RelsConstants.DANS_NS.IS_MEMBER_OF.toString(),
// c1.getStoreId()));
// assertTrue(relListContains(rels, i1.getStoreId(), RelsConstants.DANS_NS.IS_MEMBER_OF.toString(),
// c1.getStoreId()));
// }
//    
// @Test(expected=ConcurrentUpdateException.class)
// public void testInvalidation() throws ObjectNotInStoreException, RepositoryException
// {
// DummyDmoContainerItem t1 = (DummyDmoContainerItem) fedoraStore.retrieve("dummy-item:1");
// DummyDmoContainerItem t2 = (DummyDmoContainerItem) fedoraStore.retrieve("dummy-item:1");
// DummyDmoContainerItem t3 = (DummyDmoContainerItem) fedoraStore.retrieve("dummy-item:1");
// DummyDmoContainerItem t4 = (DummyDmoContainerItem) fedoraStore.retrieve("dummy-item:1");
//    	
// t1.setLabel("test dummy item");
//    	
// assertFalse(t1.isInvalidated());
// assertFalse(t2.isInvalidated());
// assertFalse(t3.isInvalidated());
// assertFalse(t4.isInvalidated());
//
// fedoraStore.update(t1, "testing 1 2 3");
//    	
// assertFalse(t1.isInvalidated());
// assertTrue(t2.isInvalidated());
// assertTrue(t3.isInvalidated());
// assertTrue(t4.isInvalidated());
//
// t3 = (DummyDmoContainerItem) fedoraStore.retrieve("dummy-item:1");
//    	
// assertFalse(t1.isInvalidated());
// assertTrue(t2.isInvalidated());
// assertFalse(t3.isInvalidated());
// assertTrue(t4.isInvalidated());
// assertEquals(t3.getLabel(), "test dummy item");
//    	
// // test throwing away a reference
// t4 = null;
// System.gc();
// t3.setLabel("test t3 item");
//    	
// fedoraStore.update(t3, "dummy-item:1");
//    	
// assertTrue(t1.isInvalidated());
// assertTrue(t2.isInvalidated());
// assertFalse(t3.isInvalidated());
//    	
// // throws exception
// fedoraStore.update(t1, "dummy-item:1");
// }
//}
