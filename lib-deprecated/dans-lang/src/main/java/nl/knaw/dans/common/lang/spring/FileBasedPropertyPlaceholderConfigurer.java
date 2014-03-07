package nl.knaw.dans.common.lang.spring;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;

/**
 * <p>
 * User name dependent PropertyPlaceholderConfigurer. Tries to find a placeholder properties file in the
 * following order:
 * <ol>
 * <li>[resourcePath]/[user.name].properties</li>
 * <li>[resourcePath/cfg/[user.name].properties</li>
 * <li>[resourcePath]/application.properties</li>
 * <li>[resourcePath]/cfg/application.properties</li>
 * </ol>
 * </p>
 * <p>
 * <code>resourcePath</code> is the value of the constructor parameter. If none is specified the
 * directory containing the calling application context is used. <code>user.name</code> is the value of
 * the corresponding Java system property.
 * 
 * @see <a
 *      href="http://static.springsource.org/spring/docs/2.5.x/reference/beans.html#beans-factory-extension-factory-postprocessors">Customizing
 *      configuration metadata with BeanFactoryPostProcessors</a>
 * @author ecco
 */
public class FileBasedPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer
{

    public static final String DEFAULT_RESOURCE_PATH = "cfg";
    public static final String DEFAULT_PROPERTIES_FILE = "application.properties";

    private static final Logger logger = LoggerFactory.getLogger(FileBasedPropertyPlaceholderConfigurer.class);

    public FileBasedPropertyPlaceholderConfigurer() throws IOException
    {
        this(DEFAULT_RESOURCE_PATH);
    }

    public FileBasedPropertyPlaceholderConfigurer(final String resourcePath) throws FileNotFoundException
    {
        final List<File> locationsToTry = new LinkedList<File>();
        final String filename = System.getProperty("user.name") + ".properties";
        locationsToTry.add(new File(resourcePath, filename));
        locationsToTry.add(new File(resourcePath, DEFAULT_RESOURCE_PATH + "/" + filename));
        locationsToTry.add(new File(resourcePath, DEFAULT_PROPERTIES_FILE));
        locationsToTry.add(new File(resourcePath, DEFAULT_RESOURCE_PATH + "/" + DEFAULT_PROPERTIES_FILE));
        for (final File file : locationsToTry)
        {
            if (file.exists())
            {
                logger.info("Found application properties at " + file.getAbsolutePath());
                setLocation(new FileSystemResource(file));
                return;
            }
        }
        throw new FileNotFoundException(String.format("No properties file found; tried: %s", locationsToTry.toString()));
    }
}
