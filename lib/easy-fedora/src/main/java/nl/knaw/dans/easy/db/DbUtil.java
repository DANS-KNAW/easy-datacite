package nl.knaw.dans.easy.db;

import java.net.URL;

import nl.knaw.dans.common.lang.ResourceLocator;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbUtil {

    private static final String GLOBAL_CONFIG_FILE_PATH = "conf/hibernate.cfg.xml";

    private static DbLocalConfig localConfig = null;

    private static Object sessionFactoryCreateLock = new Object();

    private static SessionFactory sessionFactory;

    private static final Logger logger = LoggerFactory.getLogger(DbUtil.class);

    public static Configuration getConfiguration() {
        URL globalConfigFileURL = ResourceLocator.getURL(GLOBAL_CONFIG_FILE_PATH);
        Configuration config = new Configuration().configure(globalConfigFileURL);
        if (localConfig != null)
            localConfig.configure(config);
        return config;
    }

    private static SessionFactory createSessionFactory() {
        Configuration configuration = new Configuration().configure("conf/hibernate.cfg.xml");
        localConfig.configure(configuration);
        StandardServiceRegistryBuilder registry = new StandardServiceRegistryBuilder();
        registry.applySettings(configuration.getProperties());
        StandardServiceRegistry serviceRegistry = registry.build();
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        return sessionFactory;
    }

    public static void setSesssionFactory(SessionFactory sesFactory) {
        sessionFactory = sesFactory;
    }

    public static void setLocalConfig(DbLocalConfig config) {
        localConfig = config;
    }

    public static boolean hasLocalConfig() {
        return localConfig != null;
    }

    public static void resetSessionFactory() {
        sessionFactory = null;
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            synchronized (sessionFactoryCreateLock) {
                if (sessionFactory == null)
                    sessionFactory = createSessionFactory();
            }
        }

        return sessionFactory;
    }

    /**
     * Used by Spring.
     */
    public static class Registrator {

        public void setLocalConfig(DbLocalConfig localConfig) {
            DbUtil.setLocalConfig(localConfig);
            logger.info("Configured DbUtil for " + localConfig.getConnectionUrl());
        }
    }

}