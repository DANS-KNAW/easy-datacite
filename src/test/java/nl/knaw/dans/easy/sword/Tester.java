package nl.knaw.dans.easy.sword;

import java.io.File;

/**
 * Fixture that performs general applicable mocks.<br>
 * Abstract to prevent execution by the JUnit framework.
 */
public abstract class Tester
{
    protected static final File META_DATA_FILE = new File("src/test/resources/input/metadata.xml");

    protected static final File ZIP_FILE = new File("src/test/resources/input/datasetPictures.zip");

    private OutputUtil testOutput = new OutputUtil(this.getClass());

    /** See {@link OutputUtil#assertAsExpected(String, String)} */
    public void assertAsExpected(final String actualResults, final String baseFileName) throws Exception
    {
        testOutput.assertAsExpected(actualResults, baseFileName);
    }
}
