package nl.knaw.dans.common.lang.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import nl.knaw.dans.common.lang.ResourceNotFoundException;
import nl.knaw.dans.common.lang.test.Tester;

import org.junit.Test;

public class FileUtilTest
{

    @Test
    public void getMimeType() throws ResourceNotFoundException, IOException
    {
        File docFile = Tester.getFile("test-files/fileUtil/kubler.doc");
        //File docFile = new File("src/test/resources/test-files/fileUtil/kubler.doc");
        String mimeType = FileUtil.getMimeType(docFile);
        assertEquals("application/vnd.ms-word", mimeType);

        docFile = Tester.getFile("test-files/fileUtil/p0059.frq");
        mimeType = FileUtil.getMimeType(docFile);
        assertEquals("text/plain", mimeType);
    }

    // A file that does not exist is neither file nor directory.
    @Test
    public void copyFile1()
    {
        File file = new File("doesnotexist");
        assertFalse(file.exists());
        assertFalse(file.isDirectory());
        assertFalse(file.isFile());
    }

    // If 'original' does not exist it cannot be copied.
    @Test(expected = FileNotFoundException.class)
    public void copyFile2() throws Exception
    {
        File original = new File("doesnotexist");
        File destination = new File("doesnotexisteither");
        FileUtil.copyFile(original, destination);
    }

    // If 'original' and 'destination' are equal, no copy can be made.
    @Test(expected = IOException.class)
    public void copyFile3() throws Exception
    {
        String filename = "src/test/resources/test-files/fileUtil/kubler.doc";
        File original = new File(filename);
        File destination = new File(filename);
        FileUtil.copyFile(original, destination);
    }

    // If 'original' is a directory and 'destination' is a file, it cannot be copied.
    @Test(expected = IOException.class)
    public void copyFile4() throws Exception
    {
        File original = new File("src/test/resources/test-files/fileUtil");
        File destination = new File("src/test/resources/test-files/fileUtil/kubler.doc");
        FileUtil.copyFile(original, destination);
    }

    // Copy a simple file in existing directory.
    @Test
    public void copyFile5() throws Exception
    {
        File original = new File("src/test/resources/test-files/fileUtil/kubler.doc");
        File destination = new File("target/test-files/fileUtil");
        if (destination.exists())
        {
            FileUtil.deleteDirectory(destination);
        }
        destination.mkdirs();
        FileUtil.copyFile(original, destination);
        File result = new File("target/test-files/fileUtil/kubler.doc");
        assertTrue(result.exists());
        assertTrue(result.isFile());
    }

    // Copy a simple file in existing directory.
    @Test
    public void copyFile6() throws Exception
    {
        File original = new File("src/test/resources/test-files/fileUtil/kubler.doc");
        File destination = new File("target/test-files/fileUtil/kublerCopy.doc");
        if (destination.exists())
        {
            assertTrue(destination.delete());
        }
        destination.getParentFile().mkdirs();
        FileUtil.copyFile(original, destination);
        File result = new File("target/test-files/fileUtil/kublerCopy.doc");
        assertTrue(result.exists());
        assertTrue(result.isFile());
    }

    // Copy a directory.
    @Test
    public void copyFile7() throws Exception
    {

        File destination = new File("target/test-files/fileUtilCopy");
        if (destination.exists())
        {
            for (File file : destination.listFiles())
            {
                if (file.isFile())
                {
                    assertTrue(file.delete());
                }
                else
                {
                    FileUtil.deleteDirectory(file);
                }
            }
        }
        destination.mkdirs();
        assertEquals(0, destination.listFiles().length);

        File original = new File("src/test/resources/test-files/fileUtil");
        FileUtil.copyFile(original, destination);

        File fileUtilCopy = new File("target/test-files/fileUtilCopy");
        assertTrue(fileUtilCopy.exists());
        assertTrue(fileUtilCopy.isDirectory());
        File fileUtil = new File(fileUtilCopy, "fileUtil");
        assertTrue(fileUtil.exists());
        assertTrue(fileUtil.isDirectory());
        File someDir = new File(fileUtil, "someDir");
        assertTrue(someDir.exists());
        assertTrue(someDir.isDirectory());

    }

}
