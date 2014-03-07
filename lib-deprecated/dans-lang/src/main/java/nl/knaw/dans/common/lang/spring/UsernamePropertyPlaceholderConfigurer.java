package nl.knaw.dans.common.lang.spring;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

/**
 * User name dependent PropertyPlaceholderConfigurer. This class looks for a property file <b>on the
 * classpath</b> under the name conf/app/[user.name].properties, where [user.name] is defined by the value of the
 * System property 'user.name'. If no user-specific file is found, this class uses the property file
 * under conf/app/application.properties.
 * 
 * @see <a
 *      href="http://static.springsource.org/spring/docs/2.5.x/reference/beans.html#beans-factory-extension-factory-postprocessors">Customizing
 *      configuration metadata with BeanFactoryPostProcessors</a>
 * @author ecco Sep 28, 2009
 */
public class UsernamePropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer
{
    private static final Logger logger = LoggerFactory.getLogger(UsernamePropertyPlaceholderConfigurer.class);

    public static final String RESOURCE_PATH = "conf/app/";

    public static final String DEFAULT_APP_PROPERTIES = RESOURCE_PATH + "application.properties";

    public UsernamePropertyPlaceholderConfigurer() throws IOException
    {
        String filename = RESOURCE_PATH + System.getProperty("user.name") + ".properties";
        ClassPathResource resource = new ClassPathResource(filename);
        if (!resource.exists())
        {
            resource = new ClassPathResource(DEFAULT_APP_PROPERTIES);
        }
        setLocation(resource);
        logger.info("Found application properties at " + resource.getFile().getAbsolutePath());
    }

}
