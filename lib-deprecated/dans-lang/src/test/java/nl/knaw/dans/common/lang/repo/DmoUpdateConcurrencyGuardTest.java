package nl.knaw.dans.common.lang.repo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.dummy.DummyDmo;

import org.junit.Test;

public class DmoUpdateConcurrencyGuardTest
{

    @Test
    public void testGuardingChange() throws RepositoryException
    {
        DummyDmo sessionAInstanceA = new DummyDmo("123");
        DummyDmo sessionAInstanceB = new DummyDmo("123");
        DummyDmo sessionBInstanceA = new DummyDmo("123");

        DmoUpdateConcurrencyGuard guard = new DmoUpdateConcurrencyGuard();

        assertFalse(guard.isUpdateable(sessionAInstanceA, "A"));
        assertFalse(guard.isUpdateable(sessionAInstanceB, "A"));
        assertFalse(guard.isUpdateable(sessionBInstanceA, "B"));

        sessionAInstanceB.setLoaded(true);
        sessionAInstanceA.setLoaded(true);
        guard.onDmoUpdate(sessionAInstanceB, "A");
        assertTrue(guard.isUpdateable(sessionAInstanceA, "A"));
        assertTrue(guard.isUpdateable(sessionAInstanceB, "A"));
        assertFalse(guard.isUpdateable(sessionBInstanceA, "B"));

        // called on retrieve; sets the loadTime;
        sessionBInstanceA.setLoaded(true);
        assertTrue(guard.isUpdateable(sessionBInstanceA, "B"));

        guard.onDmoUpdate(sessionBInstanceA, "B");
        // this will now block instances from session A:
        assertFalse(guard.isUpdateable(sessionAInstanceA, "A"));
        assertFalse(guard.isUpdateable(sessionAInstanceB, "A"));

        // unless session A has a still newer version:
        sessionAInstanceA.setLoaded(true); // reload
        assertTrue(guard.isUpdateable(sessionAInstanceA, "A"));

        // session A not changeOwner, so older instances still not writable
        assertFalse(guard.isUpdateable(sessionAInstanceB, "A"));

        guard.onDmoUpdate(sessionAInstanceA, "A");
        // session A now changeOwner, but instanceB was loaded before
        // session B updated it and therefore it cannot be changed
        // note: instance A of session A newer than instance of session B
        assertFalse(guard.isUpdateable(sessionAInstanceB, "A"));
    }

    @Test
    public void testWithNullChangeOwner()
    {

    }

}
