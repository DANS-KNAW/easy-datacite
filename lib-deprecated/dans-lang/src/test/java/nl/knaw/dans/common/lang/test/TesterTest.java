package nl.knaw.dans.common.lang.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import nl.knaw.dans.common.lang.ResourceNotFoundException;

import org.junit.Test;

public class TesterTest {

    @Test
    public void getString() {
        assertEquals("testing nl.knaw.dans.common.lang", Tester.getString(Tester.KEY_TEST));
    }

    @Test
    public void getResource() {
        assertNull(Tester.getResource("this/resource/does/not/exist"));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getNoneExistingFile() throws ResourceNotFoundException {
        Tester.getFile("this/file/does/not/exist");
    }

    @Test
    public void getFile() throws ResourceNotFoundException {
        assertTrue(Tester.getFile("test.properties").exists());
    }

}
