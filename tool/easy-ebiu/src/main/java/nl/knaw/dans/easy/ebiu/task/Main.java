package nl.knaw.dans.easy.ebiu.task;

import java.lang.reflect.Constructor;

import nl.knaw.dans.common.lang.exception.ConfigurationException;
import nl.knaw.dans.common.lang.util.Args;
import nl.knaw.dans.easy.ebiu.Application;
import nl.knaw.dans.easy.ebiu.Configuration;
import nl.knaw.dans.easy.ebiu.MultiUserNixConfiguration;

public class Main {

    public static void main(String[] args) {
        try {
            Args prArgs = new Args(args);
            Configuration configuration = new MultiUserNixConfiguration(prArgs);
            configuration.configure();
        }
        catch (ConfigurationException e) {
            System.err.println("Unable to configure the application. System will exit.");
            e.printStackTrace();
            System.exit(-1);
        }

        // Run the application
        Application.run();
        System.exit(0); // triggers the shutdown hook for the spring application context
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
