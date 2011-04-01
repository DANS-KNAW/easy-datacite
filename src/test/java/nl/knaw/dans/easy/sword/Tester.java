package nl.knaw.dans.easy.sword;

import static nl.knaw.dans.common.lang.util.FileUtil.readFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Fixture that can compare actual results with expectations. Abstract to prevent execution by the JUnit
 * framework.
 */
public abstract class Tester
{

    private static final String DIR_EXPECTED    = "src/test/resources/expected/";
    private static final String FORMAT_ACTUAL   = DIR_EXPECTED + "%s/%s";
    private static final String FORMAT_EXPECTED = DIR_EXPECTED + "%s/.svn/text-base/%s.svn-base";

    private static final String LINE_SEPARATOR  = System.getProperty("line.separator", "");

    protected void assertAsExpected(final String actualContent, final String baseFileName) throws Exception
    {
        final String actual = actualContent.replaceAll("(\\r|\\n)+", LINE_SEPARATOR);
        writeExpected(actual, baseFileName);
        final String expected = readExpected(baseFileName).replaceAll("(\\r|\\n)+", LINE_SEPARATOR);
        if (!actual.equals(expected))
            throw new Exception(baseFileName + " not as expected. Commit insignificant/expected changes and the test will succeed.");
    }

    private String readExpected(final String baseFileName) throws IOException
    {
        final File file = new File(createFileName(FORMAT_EXPECTED, baseFileName));
        if (!file.isFile())
            throw new IOException(baseFileName + " not found. Probably the test has not been commttied before."
                    + " Visually verify the generated result. Commit and the test will succeed.");
        return new String(readFile(file));
    }

    private void writeExpected(final String actualContent, final String baseFileName) throws FileNotFoundException, IOException, UnsupportedEncodingException
    {
        final String fileName = createFileName(FORMAT_ACTUAL, baseFileName);
        new File(new File(fileName).getParent()).mkdirs();
        final OutputStream outputStream = new FileOutputStream(fileName);
        try
        {
            outputStream.write(actualContent.getBytes("UTF-8"));
        }
        finally
        {
            outputStream.close();
        }
    }

    private String createFileName(final String formatActual, final String baseFileName)
    {
        return String.format(formatActual, getClass().getName(), baseFileName);
    }
}
