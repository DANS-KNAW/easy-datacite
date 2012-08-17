package nl.knaw.dans.easy.sword;

import java.io.File;

import nl.knaw.dans.easy.business.services.EasyDepositService;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.junit.BeforeClass;

/**
 * Fixture that performs general applicable mocks.<br>
 * Abstract to prevent execution by the JUnit framework.
 */
public abstract class Fixture
{
    private static Services     services;
    public static final File    META_DATA_FILE = new File("src/test/resources/input/metadata.xml");
    protected static final File ZIP_FILE       = new File("src/test/resources/input/datasetPictures.zip");
    private final OutputUtil    testOutput     = new OutputUtil(this.getClass());

    @BeforeClass
    public static void setDepositService() throws Exception
    {
        if (services == null)
        {
            final EasyDepositService service = new EasyDepositService();
            service.doBeanPostProcessing();
            services = new Services();
            services.setDepositService(service);
        }
    }

    /** See {@link OutputUtil#assertAsExpected(String, String)} */
    public void assertAsExpected(final String actualResults, final String baseFileName) throws Exception
    {
        testOutput.assertAsExpected(actualResults, baseFileName);
    }
}
