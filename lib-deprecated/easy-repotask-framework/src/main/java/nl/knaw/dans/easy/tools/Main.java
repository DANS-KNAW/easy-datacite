package nl.knaw.dans.easy.tools;

import java.lang.reflect.Constructor;

import nl.knaw.dans.common.lang.exception.ConfigurationException;
import nl.knaw.dans.common.lang.util.Args;

/**
 * Main class to start a TaskRunner. There are several ways to configure the application.
 * <p/>
 * <b>Minimal configuration</b> has one key-value parameter: <br/>
 * <code>application.context=path/to/the/spring/context.xml</code>
 * <p/>
 * Configuration with a <b>properties file</b> can be achieved with the single key-value pair <br/>
 * <code>prop.file.name=path/to/my.properties</code> <br/>
 * where the properties file specifies at least <code>application.context</code>.
 * <p/>
 * Whether directly in the String array <code>args</code> or by means of a properties file, you can specify the name of the {@link Configuration} class with the
 * key-value pair <br/>
 * <code>configuration.class.name=com.acme.MyConfiguration</code> <br/>
 * where MyConfiguration implements {@link Configuration}. If no configuration class is given the {@link DefaultConfiguration} will be used.
 * 
 * @see Args
 */
public class Main {

    /**
     * @param args
     * @see Args
     */
    public static void main(String[] args) {
        try {
            Args prArgs = new Args(args);
            Configuration configuration;
            String configurationClassName = prArgs.getConfigurationClassName();
            if (configurationClassName == null) {
                configuration = new DefaultConfiguration(prArgs);
            } else {
                configuration = instantiateConfiguration(prArgs);
            }

            configuration.configure();
        }
        catch (ConfigurationException e) {
            System.err.println("Unable to configure the application. System will exit.");
            e.printStackTrace();
            System.exit(-1);
        }

        // Run the application
        Application.run();
    }

    protected static Configuration instantiateConfiguration(Args prArgs) throws ConfigurationException {
        String configurationClassName = prArgs.getConfigurationClassName();

        Class<?> configurationClass;
        try {
            configurationClass = Class.forName(configurationClassName, false, Thread.currentThread().getContextClassLoader());
        }
        catch (ClassNotFoundException e) {
            throw new ConfigurationException("Class not found: " + configurationClassName);
        }

        Configuration configuration = constructWithArgs(configurationClass, prArgs);
        if (configuration == null) {
            configuration = constructNoArgs(configurationClass);
        }
        return configuration;
    }

    private static Configuration constructWithArgs(Class<?> configurationClass, Args prArgs) throws ConfigurationException {
        Configuration configuration = null;
        Constructor<?> constructor = null;
        try {
            constructor = configurationClass.getConstructor(Args.class);
        }
        catch (SecurityException e) {
            throw new ConfigurationException(e);
        }
        catch (NoSuchMethodException e) {
            //
        }
        if (constructor != null) {
            try {
                configuration = (Configuration) constructor.newInstance(prArgs);
            }
            catch (Exception e) {
                throw new ConfigurationException(e);
            }
        }
        return configuration;
    }

    private static Configuration constructNoArgs(Class<?> configurationClass) throws ConfigurationException {
        Configuration configuration;
        try {
            Constructor<?> constructor = configurationClass.getConstructor();
            configuration = (Configuration) constructor.newInstance();
        }
        catch (Exception e) {
            throw new ConfigurationException(e);
        }

        return configuration;
    }

}
