package nl.knaw.dans.easy.sword;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;

import org.purl.sword.base.SWORDErrorException;
import org.purl.sword.base.SWORDException;

/**
 * Fixture that performs general applicable mocks.<br>
 * Abstract to prevent execution by the JUnit framework.
 */
public abstract class Tester
{
    protected static final File META_DATA_FILE = new File("src/test/resources/input/metadata.xml");

    protected static final File ZIP_FILE = new File("src/test/resources/input/datasetPictures.zip");

    private final OutputUtil testOutput = new OutputUtil(this.getClass());

    /** See {@link OutputUtil#assertAsExpected(String, String)} */
    public void assertAsExpected(final String actualResults, final String baseFileName) throws Exception
    {
        testOutput.assertAsExpected(actualResults, baseFileName);
    }

    protected Dataset createMockedDataset(final EasyUserImpl user, final String file) throws SWORDException, SWORDErrorException, FileNotFoundException
    {
        final FileInputStream inputStream = new FileInputStream(file);
        return new UnzipResult(inputStream).submit(user, true);
    }
}
