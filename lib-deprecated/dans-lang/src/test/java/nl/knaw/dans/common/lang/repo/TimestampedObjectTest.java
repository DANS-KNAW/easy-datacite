package nl.knaw.dans.common.lang.repo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import nl.knaw.dans.common.lang.TimestampedObject;

import org.joda.time.DateTime;
import org.junit.Test;

public class TimestampedObjectTest {

    @Test(expected = IllegalArgumentException.class)
    public void testParameter() {
        TimestampedObject tso = new TSObject();
        tso.setTimestamp(1);
    }

    @Test
    public void testTimestamp() {
        TimestampedObject tso = new TSObject();

        tso.setTimestamp("2012");
        assertEquals(new DateTime("2012"), tso.getTimestamp());
        assertFalse(tso.isOlderThan(null));
        assertTrue(tso.isOlderThan("2013"));
        assertFalse(tso.isOlderThan("2011"));

        tso.setTimestamp(null);
        assertNull(tso.getTimestamp());
        assertFalse(tso.isOlderThan(null));
        assertFalse(tso.isOlderThan("1"));
    }

    private class TSObject extends AbstractTimestampedObject {
        private static final long serialVersionUID = 7928667274024259565L;
    }

}
