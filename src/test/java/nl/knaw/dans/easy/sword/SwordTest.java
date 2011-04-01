package nl.knaw.dans.easy.sword;

import static nl.knaw.dans.common.lang.util.FileUtil.readFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.purl.sword.base.ServiceDocument;
import org.purl.sword.base.ServiceDocumentRequest;

public class SwordTest
{
    private static final String LINE_SEPARATOR = System.getProperty("line.separator", "");
    private static final String DIR_EXPECTED = "src/test/resources/expected/";
    private static EasySwordServer server;

    @BeforeClass
    public static void setup()
    {
        server = new EasySwordServer();
    }

    @Test
    public void serviceDocument() throws Exception
    {
        final ServiceDocumentRequest request = new ServiceDocumentRequest();
        request.setUsername("someUser");
        request.setPassword("someUser");
        request.setLocation("http://localhost:8080/easy-sword-0.0.1-SNAPSHOT/serviceDocument");
        assertAsExpected(server.doServiceDocument(request).toString(),"serviceDocument.xml");
    }

    private void assertAsExpected(final String actualContent, final String baseFileName) throws Exception
    {
        final String actual = actualContent.replaceAll("(\\r|\\n)+", LINE_SEPARATOR);
        writeExpected(actual, baseFileName);
        final String expected = readExpected(baseFileName).replaceAll("(\\r|\\n)+", LINE_SEPARATOR);
        if (!actual.equals(expected))
            throw new Exception(baseFileName + " not as expected. Commit insignificant/expected changes and the test will succeed.");
    }

    private String readExpected(final String baseFileName) throws IOException
    {
        final File file = new File(String.format(DIR_EXPECTED + "%s/.svn/text-base/%s.svn-base", getClass().getName(), baseFileName));
        if (!file.isFile())
            throw new IOException(baseFileName + " not found. Probably the test has not been commttied before."
                    + " Visually verify the generated result. After committing the test will succeed.");
        return new String(readFile(file));
    }

    private void writeExpected(final String actualContent, final String baseFileName) throws FileNotFoundException, IOException, UnsupportedEncodingException
    {
        final String file = String.format(DIR_EXPECTED + "%s/%s", getClass().getName(), baseFileName);
        new File(new File(file).getParent()).mkdirs();
        final OutputStream outputStream = new FileOutputStream(file);
        try
        {
            outputStream.write(actualContent.getBytes("UTF-8"));
        }
        finally
        {
            outputStream.close();
        }
    }
}
