package nl.knaw.dans.easy.sword;

import java.io.File;

import org.junit.BeforeClass;

/**
 * Fixture that performs general applicable mocks.<br>
 * Abstract to prevent execution by the JUnit framework.
 */
public abstract class Tester
{
    protected static final File META_DATA_FILE = new File("src/test/resources/input/metadata.xml");

    protected static final File ZIP_FILE = new File("src/test/resources/input/datasetPictures.zip");

    private TestOutput testOutput = new TestOutput(this.getClass());

    /** See {@link TestOutput#assertAsExpected(String, String)} */
    public void assertAsExpected(final String actualResults, final String baseFileName) throws Exception
    {
        testOutput.assertAsExpected(actualResults, baseFileName);
    }
}
