package nl.knaw.dans.easy.web;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import nl.knaw.dans.common.lang.HomeDirectory;
import nl.knaw.dans.easy.util.EasyHome;
import nl.knaw.dans.easy.util.EnvironmentVariableBasedHomeDirectory;

import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalConfigLoaderContextListener implements ServletContextListener
{
    private static final Logger logger = LoggerFactory.getLogger(ExternalConfigLoaderContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        try
        {
            Log.debug("Creating temporary homedirectory bean, because Spring initialization hasn't run yet");
            EnvironmentVariableBasedHomeDirectory home = new EnvironmentVariableBasedHomeDirectory();
            home.setEnvironmentVariableName("EASY_HOME");
            new LogBackConfigLoader(new File(home.getHomeDirectory(), "cfg/logback.xml"));
        }
        catch (Exception e)
        {
            logger.error("Unable to read logback config file");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
    }
}
