package nl.knaw.dans.easy.db;

import org.hibernate.cfg.Configuration;

/**
 * Local config of the database, meaning the config that is changeable per instance of the software.
 * Global config is written in the hibernate config file (hibernate.cfg.xml).
 * @author lobo
 *
 */
public class DbLocalConfig {

	private String username;

	private String password;

	private String connectionUrl;

	private String hbnDialect;

	private String hbnDriverClass;

	public DbLocalConfig() 
	{
		// null constructor provided for Spring
	}

	public DbLocalConfig(
			String username,
			String password,
			String connectionUrl,
			String hbnDriverClass,
			String hbnDialect)
	{
		this.username = username;
		this.password = password;
		this.connectionUrl  = connectionUrl;
		this.hbnDriverClass = hbnDriverClass;
		this.hbnDialect     = hbnDialect;
	}

	/**
	 * Set local properties to hibernate config object.
	 * @param config a hibernate config object
	 */
	public void configure(Configuration config)
	{
    	config.setProperty("hibernate.dialect", getHbnDialect());
    	config.setProperty("hibernate.connection.driver_class", getHbnDriverClass());
    	config.setProperty("hibernate.connection.username", getUsername()) ;
    	config.setProperty("hibernate.connection.password", getPassword());
    	config.setProperty("hibernate.connection.url", getConnectionUrl());
	}

	public String getUsername()
	{
		return username;
	}

	public String getPassword()
	{
		return password;
	}

	public String getConnectionUrl()
	{
		return connectionUrl;
	}

	public String getHbnDialect()
	{
		return hbnDialect;
	}

	public String getHbnDriverClass()
	{
		return hbnDriverClass;
	}
}
