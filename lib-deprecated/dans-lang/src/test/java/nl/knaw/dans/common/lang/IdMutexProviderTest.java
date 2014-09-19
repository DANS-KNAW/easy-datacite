package nl.knaw.dans.common.lang;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class IdMutexProviderTest {

    @Test
    public void testNPE() {
        IdMutexProvider imp = new IdMutexProvider();
        try {
            imp.getMutex(null);
            fail("Did not throw NullPointerException");
        }
        catch (NullPointerException e) {}
    }

    @Test
    public void testSynchObject() {
        IdMutexProvider imp = new IdMutexProvider();
        // an id
        String id1a = "id1";
        // same id value; different key instance
        String id1b = new String(id1a);
        // a different id
        String id2 = "id2";

        // assert inequality of String id reference values
        assertFalse(id1a == id1b);
        assertFalse(id1a == id2);

        IdMutexProvider.Mutex m1a = imp.getMutex(id1a);
        assertNotNull(m1a);
        assertTrue(imp.getMutexCount() == 1);

        IdMutexProvider.Mutex m1b = imp.getMutex(id1b);
        assertNotNull(m1b);
        assertTrue(m1a == m1b);
        assertTrue(imp.getMutexCount() == 1);

        IdMutexProvider.Mutex m2 = imp.getMutex(id2);
        assertNotNull(m2);
        assertTrue(imp.getMutexCount() == 2);
        assertFalse(m2 == m1a);
    }

}
