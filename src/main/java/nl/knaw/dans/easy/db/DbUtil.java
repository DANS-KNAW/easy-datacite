package nl.knaw.dans.easy.db;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

import nl.knaw.dans.common.lang.ResourceLocator;
import nl.knaw.dans.easy.db.exceptions.CouldNotConnectToDatabaseException;
import nl.knaw.dans.easy.db.exceptions.DbException;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbUtil {

    private static final String GLOBAL_CONFIG_FILE_PATH = "conf/hibernate.cfg.xml";


    private static DbLocalConfig localConfig = null;


    private static Object sessionFactoryCreateLock = new Object();

    private static SessionFactory sessionFactory;
    
    private static final Logger logger = LoggerFactory.getLogger(DbUtil.class);
	


    public static Configuration getConfiguration() throws DbException
    {
    	try
	    {
	    	URL globalConfigFileURL = ResourceLocator.getURL(GLOBAL_CONFIG_FILE_PATH);
	    	Configuration config = new Configuration().configure(globalConfigFileURL);
	    	if (localConfig != null)
	    		localConfig.configure(config);
	    	return config;
	    }
	    catch (Throwable e)
	    {
	        throw new DbException(e);
	    }
    }

    private static SessionFactory createSessionFactory() throws DbException
	{
    	try
	    {
	        sessionFactory = getConfiguration().buildSessionFactory();
	    }
	    catch (Throwable e)
	    {
	        throw new DbException(e);
	    }

	    return sessionFactory;
	}
    
    public static void setSesssionFactory(SessionFactory sesFactory)
    {
    	sessionFactory = sesFactory;
    }

	public static void setLocalConfig(DbLocalConfig config)
	{
		localConfig = config;
	}
	
	public static boolean hasLocalConfig()
	{
		return localConfig != null;
	}

	public static void resetSessionFactory()
	{
		sessionFactory = null;
	}

	public static SessionFactory getSessionFactory() throws DbException
	{
		if (sessionFactory == null)
		{
			synchronized(sessionFactoryCreateLock)
			{
				if (sessionFactory == null)
					sessionFactory = createSessionFactory();
			}
		}

		return sessionFactory;
	}

	// This method, with the //conn = getSessionFactory().openSession().connection(); 
	// in the try clause out-commented for ages
	// does effectively nothing.
	public static void checkConnection() throws CouldNotConnectToDatabaseException, DbException
	{
		Connection conn = null;
		try
		{
			//conn = getSessionFactory().openSession().connection();
		}
		catch (HibernateException e)
		{
			throw new CouldNotConnectToDatabaseException(e);
		}
		finally
		{
			if (conn != null)
			{
				try
				{
					conn.close();
				} catch (SQLException e)
				{
					throw new DbException(e);
				}
			}
		}
	}
	
	/**
	 * 
	 * Used by Spring.
	 *
	 */
	public static class Registrator
	{
		
		public void setLocalConfig(DbLocalConfig localConfig)
		{
			DbUtil.setLocalConfig(localConfig);
			logger.info("Configured DbUtil for " + localConfig.getConnectionUrl());
		}
	}

}
