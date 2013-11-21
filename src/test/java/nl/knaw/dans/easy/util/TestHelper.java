package nl.knaw.dans.easy.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import nl.knaw.dans.common.lang.FileSystemHomeDirectory;
import nl.knaw.dans.common.lang.ResourceLocator;
import nl.knaw.dans.easy.business.bean.SystemStatus;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TestHelper
{

    public static final String OUTPUT_FOLDER = "src/test/resources/output/";

    public static final String INPUT_FOLDER = "src/test/resources/";

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TestHelper.class);

    /**
     * Logger for current class.
     */
    private static Logger currentLogger;

    private static Class<?> currentClass;

    private static final String LINE = "-----------------------------------------------------------------";
    private static final String NO_CURRENT_CLASS = "  ==! NO CURRENT CLASS SET ON TESTHELPER !==";

    private static final String BUNDLE_NAME = "test";

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    @BeforeClass
    public static void beforeTestClass()
    {
        new ResourceLocator(new FileSystemHomeDirectory(new File("src/test/resources/editable")));
        log().info("EasyHome-value has been set");
    }

    @BeforeClass
    public static void initReadOnly() throws Exception
    {
        File file = new File("target/SystemStatus.properties");
        file.getParentFile().mkdirs();
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(new byte[0]);
        fileOutputStream.close();
        SystemStatus.INSTANCE.setFile(file);
    }

    public TestHelper()
    {
        TestHelper.currentClass = this.getClass();
    }

    public static void before(Class<?> test)
    {
        currentClass = test;
        LOGGER.debug(LINE);
        LOGGER.debug("  start of " + getCurrentClass().getName() + " messages");
        LOGGER.debug(LINE);
    }

    @AfterClass
    public static void testHelperAfter()
    {
        if (getCurrentClass() != null)
        {
            LOGGER.debug(LINE);
            LOGGER.debug("  end of " + getCurrentClass().getName() + " messages");
            LOGGER.debug(LINE);
            currentClass = null;
            currentLogger = null;
        }
    }

    public static String getString(String key)
    {
        try
        {
            return RESOURCE_BUNDLE.getString(key);
        }
        catch (final MissingResourceException e)
        {
            return '!' + key + '!';
        }
    }

    public static String getInputFolderName(Class<?> clazz)
    {
        String path = INPUT_FOLDER + clazz.getName().replaceAll("\\.", "/") + "-files/";
        File file = new File(path);
        file.mkdirs();
        return path;
    }

    public static String getOutPutFolderName(Class<?> clazz)
    {
        String path = OUTPUT_FOLDER + clazz.getName().replaceAll("\\.", "/") + "/";
        File file = new File(path);
        file.mkdirs();
        return path;
    }

    public static Logger log()
    {
        if (currentLogger == null)
        {
            currentLogger = LoggerFactory.getLogger(getCurrentClass());
        }
        return currentLogger;
    }

    public String getInputFolderName()
    {
        return getInputFolderName(getCurrentClass());
    }

    public String getOutputFolderName()
    {
        return getOutPutFolderName(getCurrentClass());
    }

    public String getOutputFileName(String filename)
    {
        return getOutputFolderName() + filename;
    }

    public File getFile(String filename)
    {
        return new File(getInputFolderName() + filename);
    }

    public static File getFile(Class<?> clazz, String filename)
    {
        return new File(getInputFolderName(clazz) + filename);
    }

    public void startOfTest(String name)
    {
        log().debug(LINE);
        log().debug("       start " + name);
        log().debug(LINE);
    }

    private static Class<?> getCurrentClass()
    {
        if (currentClass == null)
        {
            LOGGER.warn(NO_CURRENT_CLASS);
            return TestHelper.class;
        }
        else
        {
            return currentClass;
        }

    }

}
