package nl.knaw.dans.easy.tools;

import nl.knaw.dans.common.lang.exception.ConfigurationException;
import nl.knaw.dans.common.lang.util.Args;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultConfiguration implements Configuration {

    public static final String DEFAULT_LOG_CONFIG_FILE = "cfg/log/log4j.xml";

    private static final Logger logger = LoggerFactory.getLogger(DefaultConfiguration.class);

    private final Args prArgs;

    public DefaultConfiguration(Args prArgs) {
        this.prArgs = prArgs;
    }

    public Args getProgramArgs() {
        return prArgs;
    }

    @Override
    public void configure() throws ConfigurationException {
        configureLogging();
        initializeApplication();
        doPostApplicationStartConfiguration();
    }

    protected void configureLogging() throws ConfigurationException {}

    protected void initializeApplication() throws ConfigurationException {
        Application.initialize(getProgramArgs());
    }

    protected void doPostApplicationStartConfiguration() throws ConfigurationException {
        // nothing yet.
    }

}
