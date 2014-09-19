package nl.knaw.dans.common.jibx;

import static org.junit.Assert.assertEquals;
import nl.knaw.dans.common.lang.test.Tester;

import org.junit.Test;

public class TesterTest {
    @Test
    public void getString() {
        assertEquals("testing nl.knaw.dans.common.jibx", Tester.getString(Tester.KEY_TEST));
    }
}
