package nl.knaw.dans.easy.store.fedora;

import nl.knaw.dans.easy.db.DbLocalConfig;
import nl.knaw.dans.easy.db.DbUtil;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class FedoraDbTestSchema
{

    public static void init()
    {
        DbLocalConfig localConfig = new DbLocalConfig("sa", "", "jdbc:log:org.hsqldb.jdbcDriver:hsqldb:mem:easyfedoradb", "net.rkbloom.logdriver.LogDriver",
                "org.hibernate.dialect.HSQLDialect")
        {
            @Override
            public void configure(Configuration config)
            {
                super.configure(config);
                config.setProperty("hibernate.hbm2ddl.auto", "create-drop").setProperty("hibernate.show_sql", "false")
                        .setProperty("hibernate.connection.pool_size", "1").setProperty("hibernate.connection.autocommit", "true");
            }
        };

        DbUtil.setLocalConfig(localConfig);
    }

    public static void reset()
    {
        SchemaExport schema = new SchemaExport(DbUtil.getConfiguration());

        schema.drop(false, true);
        schema.create(false, true);
    }

}
