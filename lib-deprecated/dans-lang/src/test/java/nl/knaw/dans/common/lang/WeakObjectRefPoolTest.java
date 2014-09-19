package nl.knaw.dans.common.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class WeakObjectRefPoolTest {

    @Test
    public void dmoPoolTest() throws RepositoryException, InterruptedException {
        WeakObjectRefPool<String, String, String> pool = new WeakObjectRefPool<String, String, String>();

        // call new otherwise these strings end up on the stack instead of the heap
        // and the garbage collector does not clean the stack
        String o1_1 = new String("hello");
        String i1_1 = new String("info of o1_1");
        String o1_2 = new String("world");
        String i1_2 = new String("info of o1_2");
        String o2 = new String("foo-bar");
        String i2 = new String("info of o2");

        pool.add("o1", o1_1, i1_1);
        pool.add("o1", o1_2, i1_2);
        pool.add("o2", o2, i2);

        assertEquals(2, pool.getObjectIdCount());
        assertEquals(3, pool.getReferenceCount());
        assertEquals(2, pool.get("o1").size());
        assertEquals(1, pool.get("o2").size());
        assertEquals(0, pool.get("bla").size());

        assertTrue(i1_1 == pool.get("o1").get(0).getInfo());
        assertTrue(i1_2 == pool.get("o1").get(1).getInfo());
        assertTrue(i2 == pool.get("o2").get(0).getInfo());

        o1_1 = null;
        System.gc();
        Thread.sleep(1000); // gc needs some time
        System.gc();

        assertEquals(2, pool.getReferenceCount());
        assertEquals(2, pool.getObjectIdCount());
        assertEquals(1, pool.get("o1").size());
        assertEquals(1, pool.get("o2").size());
        assertEquals(0, pool.get("bla").size());

        assertTrue(i1_2 == pool.get("o1").get(0).getInfo());
        assertTrue(i2 == pool.get("o2").get(0).getInfo());

        o2 = null;
        System.gc();
        Thread.sleep(1000); // gc needs some time
        System.gc();

        assertEquals(1, pool.getReferenceCount());
        assertEquals(1, pool.getObjectIdCount());
        assertEquals(1, pool.get("o1").size());
        assertEquals(0, pool.get("o2").size());
        assertEquals(0, pool.get("bla").size());

        assertTrue(i1_2 == pool.get("o1").get(0).getInfo());

        o1_2 = null;
        System.gc();
        Thread.sleep(1000); // gc needs some time
        System.gc();

        assertEquals(0, pool.getReferenceCount());
        assertEquals(0, pool.getObjectIdCount());
        assertEquals(0, pool.get("o1").size());
        assertEquals(0, pool.get("o2").size());
        assertEquals(0, pool.get("bla").size());
    }

}
