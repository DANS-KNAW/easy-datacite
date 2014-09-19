package nl.knaw.dans.easy.business;

import static org.junit.Assert.assertEquals;
import nl.knaw.dans.common.lang.test.Tester;

import org.junit.Test;

public class TesterTest {
    @Test
    public void getString() {
        assertEquals("testing nl.knaw.dans.easy.business", Tester.getString(Tester.KEY_TEST));
    }
}
