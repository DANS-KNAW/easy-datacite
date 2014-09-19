package nl.knaw.dans.c.store;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import nl.knaw.dans.i.store.StoreManager;
import nl.knaw.dans.i.store.StoreSession;

import org.junit.Test;

public class StoreManagerImplTest {

    @Test
    public void newStoreManager() {
        StoreManager storeManager = new StoreManagerImpl();
        StoreSession session1 = storeManager.newStoreSession("foo");
        StoreSession session2 = storeManager.newStoreSession("foo");

        assertNotNull(session1);
        assertNotNull(session2);

        assertNotSame(session1, session2);

    }

}
