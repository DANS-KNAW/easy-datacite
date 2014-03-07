package nl.knaw.dans.easy.util;

import static org.junit.Assert.*;
import java.io.File;
import java.net.URL;
import java.util.Locale;

import nl.knaw.dans.common.lang.ResourceLocator;
import nl.knaw.dans.common.lang.ResourceNotFoundException;

import org.junit.Test;

public class ResourceLocatorTest
{

    @Test
    public void getLocaleSpecificURL()
    {
        URL url = ResourceLocator.getURL("test/resource/x", null, "txt");
        assertTrue(url.toString().endsWith("test/resource/x.txt"));

        Locale locale = new Locale("nl");
        url = ResourceLocator.getURL("test/resource/x", locale, "txt");
        assertTrue(url.toString().endsWith("test/resource/x_nl.txt"));

        locale = new Locale("nl", "NL");
        url = ResourceLocator.getURL("test/resource/x", locale, "txt");
        assertTrue(url.toString().endsWith("test/resource/x_nl_NL.txt"));

        locale = new Locale("nl", "FR");
        url = ResourceLocator.getURL("test/resource/x", locale, "txt");
        assertTrue(url.toString().endsWith("test/resource/x_nl.txt"));

        locale = Locale.US;
        url = ResourceLocator.getURL("test/resource/x", locale, "txt");
        assertTrue(url.toString().endsWith("test/resource/x.txt"));

        url = ResourceLocator.getURL("test/resource/y", locale, "txt");
        assertTrue(url.toString().endsWith("test/resource/y.txt"));

        url = ResourceLocator.getURL("test/resource/z", locale, "txt");
        assertNull(url);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getFile1() throws ResourceNotFoundException
    {
        ResourceLocator.getFile("this/file/does/not/exist");
    }

    @Test(expected = ResourceNotFoundException.class)
    public void getFile2() throws ResourceNotFoundException
    {
        ResourceLocator.getFile("test/resource/x");
    }

    @Test
    public void getFile3() throws ResourceNotFoundException
    {
        File file = ResourceLocator.getFile("test/resource/x.txt");
        assertTrue(file.exists());
    }

    @Test
    public void getLocaleSpecificFile() throws ResourceNotFoundException
    {
        Locale locale = new Locale("nl");
        File file = ResourceLocator.getFile("test/resource/x", locale, "txt");
        assertTrue(file.exists());
        assertEquals("x_nl.txt", file.getName());
    }

}
