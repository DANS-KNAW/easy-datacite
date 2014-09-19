package nl.knaw.dans.common.lang.repo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DsUnitIdTest {

    @Test
    public void testValidity() {
        assertTrue(DsUnitId.isValidId("a[b].0.9~+x_$(21)"));

        assertFalse(DsUnitId.isValidId("/"));
        assertFalse(DsUnitId.isValidId("\\"));
        assertFalse(DsUnitId.isValidId("?"));
        assertFalse(DsUnitId.isValidId("*"));
        assertFalse(DsUnitId.isValidId("="));
        assertFalse(DsUnitId.isValidId(""));
        assertFalse(DsUnitId.isValidId(" "));
        assertFalse(DsUnitId.isValidId("\""));
        assertFalse(DsUnitId.isValidId("'"));
        assertFalse(DsUnitId.isValidId("%"));
        assertFalse(DsUnitId.isValidId("!"));
        assertFalse(DsUnitId.isValidId("@"));
        assertFalse(DsUnitId.isValidId("#"));
        assertFalse(DsUnitId.isValidId("^"));
        assertFalse(DsUnitId.isValidId("&"));
        assertFalse(DsUnitId.isValidId("{"));
        assertFalse(DsUnitId.isValidId("}"));
        assertFalse(DsUnitId.isValidId(":"));
        assertFalse(DsUnitId.isValidId(";"));
        assertFalse(DsUnitId.isValidId("|"));
        assertFalse(DsUnitId.isValidId("<"));
        assertFalse(DsUnitId.isValidId(","));
        assertFalse(DsUnitId.isValidId(">"));
        assertFalse(DsUnitId.isValidId(null));

    }

}
