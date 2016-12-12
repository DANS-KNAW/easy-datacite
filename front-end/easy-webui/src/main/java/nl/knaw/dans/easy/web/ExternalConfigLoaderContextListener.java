package nl.knaw.dans.easy.web;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import nl.knaw.dans.common.lang.EnvironmentVariableBasedHomeDirectory;
import nl.knaw.dans.common.lang.util.LogBackConfigLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalConfigLoaderContextListener implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(ExternalConfigLoaderContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            log.debug("Creating temporary homedirectory bean, because Spring initialization hasn't run yet");
            /*
             * This is a DRY-violation, because the name of the environment variable pointing to the home directory, EASY_WEBUI_HOME, is also defined in
             * applicationContext.xml. However, we want to configure logging as soon as possible, i.e. before Spring initializes. Otherwise we won't be able to
             * configure how Spring initialization is logged in $EASY_WEBUI_HOME/cfg/logback.xml. Unless you see a better way to do this (and that doesn't make
             * it necessary to configure logging in multiple places), please leave this small DRY-violation in place.
             */
            EnvironmentVariableBasedHomeDirectory home = new EnvironmentVariableBasedHomeDirectory();
            home.setEnvironmentVariableName("EASY_WEBUI_HOME");
            File config = new File(home.getHomeDirectory(), "cfg/logback.xml");
            new LogBackConfigLoader(config);
            log.info("Loaded logback configuration from: {}", config);
        }
        catch (Exception e) {
            /*
             * Writing to STDERR because logging will probably not work as expected without the configuration file loaded.
             */
            System.err.println("Unable to read logback config file");
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {}
}