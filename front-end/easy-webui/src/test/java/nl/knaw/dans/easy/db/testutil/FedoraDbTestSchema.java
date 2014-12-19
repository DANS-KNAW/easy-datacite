package nl.knaw.dans.easy.db.testutil;

import net.rkbloom.logdriver.LogDriver;
import nl.knaw.dans.easy.db.DbLocalConfig;
import nl.knaw.dans.easy.db.DbUtil;

import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.HSQLDialect;

public class FedoraDbTestSchema {

    public static void init() {
        String userName = "sa";
        String password = "";
        String connectionURL = "jdbc:log:org.hsqldb.jdbcDriver:hsqldb:mem:easyfedoradb";
        String driverClass = LogDriver.class.getName();
        String dialect = HSQLDialect.class.getName();
        DbLocalConfig localConfig = new DbLocalConfig(userName, password, connectionURL, driverClass, dialect) {
            @Override
            public void configure(Configuration config) {
                super.configure(config);
                config.setProperty("hibernate.hbm2ddl.auto", "create-drop");
                config.setProperty("hibernate.show_sql", "false");
                config.setProperty("hibernate.connection.pool_size", "1");
                config.setProperty("hibernate.connection.autocommit", "true");
            }
        };

        DbUtil.setLocalConfig(localConfig);
    }

    public static void reset() {
        // SchemaExport schema = new SchemaExport(DbUtil.getConfiguration());
        //
        // schema.drop(false, true);
        // schema.create(false, true);
    }
}
