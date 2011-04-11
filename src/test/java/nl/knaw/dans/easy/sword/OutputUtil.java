package nl.knaw.dans.easy.sword;

import static nl.knaw.dans.common.lang.util.FileUtil.readFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class OutputUtil
{
    private static final String           DIR_EXPECTED    = "src/test/resources/expected/";
    private static final String           FORMAT_ACTUAL   = DIR_EXPECTED + "%s/%s";
    private static final String           FORMAT_EXPECTED = DIR_EXPECTED + "%s/.svn/text-base/%s.svn-base";
    private static final String           LINE_SEPARATOR  = System.getProperty("line.separator", "");
    private final Class<? extends Object> testerClass;

    /**
     * @param tester
     *        the class containing the {@linkplain org.junit.Test} methods
     */
    public OutputUtil(Class<? extends Object> tester)
    {
        this.testerClass = tester;
    }

    /**
     * Compares actual results with results as committed in the base revision. If called as the very last
     * action of a unit test, the test will always succeed after commit. New or changed results should be
     * verified manually as explained by the thrown exceptions. If that verification is done sloppy,
     * comparing revisions of the expectations reveals which commit caused the unwanted results.
     * 
     * @param actualResults
     *        actual results produced by the test, will be saved as expected result but only becoming
     *        effective as such after committing to the subversion repository
     * @param baseFileName
     *        name of file with expected results, should be unique in the class passed to the constructor
     * @throws Exception
     *         if the actual results differ from the base revision of the expectations.
     */
    public void assertAsExpected(final String actualResults, final String baseFileName) throws Exception
    {
        writeExpected(localizeLineTerminators(actualResults), baseFileName);
        if (!localizeLineTerminators(actualResults).equals(readExpected(baseFileName)))
        {
            throw new Exception(errorIntro(baseFileName) + "not as expected. Commit insignificant/expected changes and the test will succeed next time.");
        }
    }

    private String readExpected(final String baseFileName) throws IOException
    {
        final File file = new File(createFileName(FORMAT_EXPECTED, baseFileName));
        if (!file.isFile())
            throw new IOException(errorIntro(baseFileName) + " not found. Probably the test has not been commttied before."
                    + " Visually verify the generated result. Commit and the test will succeed next time.");
        return localizeLineTerminators(new String(readFile(file)));
    }
    
    private String errorIntro(final String baseFileName)
    {
        return createFileName(FORMAT_ACTUAL, baseFileName) + LINE_SEPARATOR;
    }

    private String localizeLineTerminators(final String result)
    {
        // TODO properly deal with multiple lines
        if (result.contains("\\r\\n"))
            return result.replaceAll("(\\r\\n)+", LINE_SEPARATOR);
        if (result.contains("\\n\\r"))
            return result.replaceAll("(\\n\\r)+", LINE_SEPARATOR);
        return result.replaceAll("[\\n|\\r]+", LINE_SEPARATOR);
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
        return String.format(formatActual, testerClass.getName(), baseFileName);
    }
}
