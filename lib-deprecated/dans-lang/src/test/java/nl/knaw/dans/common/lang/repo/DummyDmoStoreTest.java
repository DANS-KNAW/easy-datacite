package nl.knaw.dans.common.lang.repo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;

import nl.knaw.dans.common.lang.repo.dummy.DummyDmo;
import nl.knaw.dans.common.lang.repo.dummy.DummyDmoStore;
import nl.knaw.dans.common.lang.repo.dummy.RepoTester;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class DummyDmoStoreTest {
    private static DummyDmoStore dmoStore;
    private static RepoTester repoTester;

    @BeforeClass
    public static void beforeClass() {
        dmoStore = new DummyDmoStore();
        repoTester = new RepoTester(dmoStore);
    }

    // @Test
    // public void constructor()
    // {
    // assertNotNull(dmoStore.getContext());
    // }

    @Test
    public void testBasicFunctions() throws Exception {
        DummyDmo dmo1 = (DummyDmo) repoTester.retrieve(new DmoStoreId("dummy-object:1"));
        repoTester.update(dmo1);
        repoTester.purge(dmo1);
    }

    @Ignore("Implementation changed")
    @Test
    /**
     * Tests the main invalidation mechanism (1)
     * 
     *  The store remembers all objects by a weak reference that are
     *  currently alive in the java runtime. If an object with a certain
     *  id is changed the store will set the state of all other 
     *  active objects with that id to invalidated.
     */
    public void testDmoInvalidation1() throws Exception {
        DummyDmo dmo1 = (DummyDmo) repoTester.retrieve(new DmoStoreId("dummy-object:1"));
        DummyDmo dmo2 = (DummyDmo) repoTester.retrieve(new DmoStoreId("dummy-object:1"));
        DummyDmo dmo3 = (DummyDmo) repoTester.retrieve(new DmoStoreId("dummy-object:2"));
        DummyDmo dmo4 = (DummyDmo) repoTester.retrieve(new DmoStoreId("dummy-object:2"));

        repoTester.update(dmo1);

        assertFalse(dmoStore.isInvalidated(dmo1));
        assertTrue(dmoStore.isInvalidated(dmo2));
        assertFalse(dmoStore.isInvalidated(dmo3));
        assertFalse(dmoStore.isInvalidated(dmo4));

        repoTester.update(dmo3);

        assertFalse(dmoStore.isInvalidated(dmo3));
        assertTrue(dmoStore.isInvalidated(dmo4));
    }

    @Ignore("Implementation changed")
    @Test
    /**
     * Tests the fallback invalidation mechanism (2)
     *
     * If the store cannot find the object in the pool of 
     * weakly referenced objects. It will check if the object
     * has changed in the last 2 hours by a list that it 
     * maintains.
     */
    public void testDmoInvalidation2() throws Exception {
        DummyDmo dmo1 = (DummyDmo) repoTester.retrieve(new DmoStoreId("dummy-object:1"));
        DummyDmo dmo3 = (DummyDmo) repoTester.retrieve(new DmoStoreId("dummy-object:2"));

        DummyDmo dmo5 = (DummyDmo) repoTester.retrieve(new DmoStoreId("dummy-object:1"));
        DummyDmo dmo6 = (DummyDmo) repoTester.retrieve(new DmoStoreId("dummy-object:2"));

        // by serializing and deserializing the system cannot track
        // the object via weakreferences, this will thus test the
        // secondary mechanism
        ByteArrayOutputStream sdmo5 = repoTester.serializeDmo(dmo5);
        ByteArrayOutputStream sdmo6 = repoTester.serializeDmo(dmo6);

        repoTester.update(dmo1);

        dmo5 = repoTester.deserializeDmo(sdmo5);
        dmo6 = repoTester.deserializeDmo(sdmo6);

        assertFalse(dmoStore.isInvalidated(dmo1));
        assertTrue(dmoStore.isInvalidated(dmo5));
        assertFalse(dmoStore.isInvalidated(dmo6));

        repoTester.purge(dmo3);

        assertTrue(dmoStore.isInvalidated(dmo3));
        assertTrue(dmoStore.isInvalidated(dmo6));
    }

    @Ignore("Implementation changed")
    @Test
    /**
     * Tests the fallback invalidation mechanism (3)
     *
     * If an object cannot be found in the list of active
     * objects and has not been changed for 2 hours then 
     * the store queries the repository to see if the 
     * last modified date has been changed.
     */
    public void testDmoInvalidation3() throws Exception {
        DummyDmo dmo1 = (DummyDmo) repoTester.retrieve(new DmoStoreId("dummy-object:1"));
        DummyDmo dmo2 = (DummyDmo) repoTester.retrieve(new DmoStoreId("dummy-object:1"));

        ByteArrayOutputStream sdmo2 = repoTester.serializeDmo(dmo2);

        repoTester.update(dmo1);

        dmo2 = repoTester.deserializeDmo(sdmo2);
        dmo2.setLoadTime(System.nanoTime() - DmoInvalidator.LAST_MODIFIED_CACHE_EXPIRES - 10);

        dmo2.setlastModified(new DateTime(1000));
        dmoStore.getLastModifiedReturns(new DateTime(2000));

        assertTrue(dmoStore.isInvalidated(dmo2));
    }
}
