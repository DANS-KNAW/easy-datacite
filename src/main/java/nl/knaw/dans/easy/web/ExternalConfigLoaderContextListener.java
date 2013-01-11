package nl.knaw.dans.easy.web;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import nl.knaw.dans.common.lang.EnvironmentVariableBasedHomeDirectory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalConfigLoaderContextListener implements ServletContextListener
{
    private static final Logger log = LoggerFactory.getLogger(ExternalConfigLoaderContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        try
        {
            log.debug("Creating temporary homedirectory bean, because Spring initialization hasn't run yet");
            EnvironmentVariableBasedHomeDirectory home = new EnvironmentVariableBasedHomeDirectory();
            home.setEnvironmentVariableName("EASY_WEBUI_HOME");
            new LogBackConfigLoader(new File(home.getHomeDirectory(), "cfg/logback.xml"));
        }
        catch (Exception e)
        {
            log.error("Unable to read logback config file");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
    }
}
