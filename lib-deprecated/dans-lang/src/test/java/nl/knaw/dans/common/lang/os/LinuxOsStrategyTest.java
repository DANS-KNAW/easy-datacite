package nl.knaw.dans.common.lang.os;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class LinuxOsStrategyTest
{

    @Test
    public void chmodLegal() throws Exception
    {
        LinuxOsStrategy los = new LinuxOsStrategy();
        File file = File.createTempFile("ostest", null);
        int exitValue = los.setAllRWX(file, System.out, System.err);
        assertEquals(0, exitValue);
        assertTrue(file.delete());
    }

    @Test
    public void chmodIllegal() throws Exception
    {
        LinuxOsStrategy los = new LinuxOsStrategy();
        File file = new File("/var/local/ostest");
        int exitValue = los.setAllRWX(file, System.out, System.err);
        assertFalse(exitValue == 0);
    }

}
