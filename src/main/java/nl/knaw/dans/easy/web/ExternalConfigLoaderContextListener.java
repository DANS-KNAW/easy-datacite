package nl.knaw.dans.easy.web;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import nl.knaw.dans.easy.util.EasyHome;

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
            new LogBackConfigLoader(new File(EasyHome.getValue(), "cfg/logback.xml"));
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
