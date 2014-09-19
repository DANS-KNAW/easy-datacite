package nl.knaw.dans.common.lang.repo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.dummy.DummyDmo;

import org.joda.time.DateTime;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DmoInvalidatorTest {

    private static final Logger logger = LoggerFactory.getLogger(DmoInvalidatorTest.class);

    private DmoInvalidator invalidator = new DmoInvalidator() {

        @Override
        protected DateTime getLastModified(DmoStoreId dmoStoreId) throws RepositoryException {
            logger.debug("getLastModified called");
            return null;
        }

    };

    @Test
    public void invalidate() throws RepositoryException {
        DummyDmo dmo = new DummyDmo("bla-bla:1");

        assertEquals("bla-bla:1", dmo.getStoreId());

        assertEquals(0, invalidator.getReferenceCount());
        assertEquals(0, invalidator.getObjectCount());
        invalidator.setInvalidated(dmo, false);
        assertEquals(1, invalidator.getReferenceCount());
        assertEquals(1, invalidator.getObjectCount());
        assertFalse(invalidator.isInvalidated(dmo));

        invalidator.setInvalidated(dmo, true);
        assertEquals(1, invalidator.getReferenceCount());
        assertEquals(1, invalidator.getObjectCount());
        assertTrue(invalidator.isInvalidated(dmo));

        invalidator.setInvalidated(dmo, false);
        assertFalse(invalidator.isInvalidated(dmo));

        invalidator.invalidate(dmo.getDmoStoreId(), dmo);
        assertFalse(invalidator.isInvalidated(dmo));

        invalidator.invalidate(dmo.getDmoStoreId());
        assertTrue(invalidator.isInvalidated(dmo));

        invalidator.setInvalidated(dmo, false);
        assertFalse(invalidator.isInvalidated(dmo));

        invalidator.invalidate(dmo.getDmoStoreId(), dmo);
        assertFalse(invalidator.isInvalidated(dmo));
    }

}
