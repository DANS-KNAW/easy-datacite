package nl.knaw.dans.easy.business.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

import nl.knaw.dans.common.lang.exception.ConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum SystemStatus implements Serializable
{
    INSTANCE;
    private static final String REFRESH_FREQUENCY = "refresh.frequency";
    private static final String IS_READ_ONLY = "is.read.only";
    private static final Logger logger = LoggerFactory.getLogger(SystemStatus.class);
    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_FREQUENCY = (1000 * 60 * 2) + "";
    private static final Properties properties = new Properties();
    private File file = null;
    private long lastCheck = 0L;

    /** factory-method for spring */
    public static SystemStatus getInstance()
    {
        return INSTANCE;
    }

    public boolean getReadOnly()
    {
        fetch();
        String defaultValue = "false";
        String value = properties.getProperty(IS_READ_ONLY, defaultValue);
        return Boolean.parseBoolean(value);
    }

    public void setReadOnly(boolean isReadOnly)
    {
        properties.setProperty(IS_READ_ONLY, isReadOnly + "");
        flush();
    }

    public long getRefreshFrequency()
    {
        String value = properties.getProperty(REFRESH_FREQUENCY, DEFAULT_FREQUENCY);
        return Integer.parseInt(value);
    }

    public void setCheckFrequency(long checkFrequency)
    {
        properties.setProperty(REFRESH_FREQUENCY, checkFrequency + "");
        flush();
    }

    public File getFile() throws ConfigurationException
    {
        if (file == null)
        {
            String msg = "no file name configured to hold the SystemStatus (read-only flag)";
            logger.error(msg);
            throw new ConfigurationException(msg);
        }
        return file;
    }

    public void setFile(File file)
    {
        this.file = file;
    }

    private void flush()
    {
        String property = properties.getProperty(REFRESH_FREQUENCY);
        if (property == null || property.trim().length() == 0)
            properties.setProperty(REFRESH_FREQUENCY, DEFAULT_FREQUENCY);
        try
        {
            FileOutputStream outputStream = new FileOutputStream(getFile());
            try
            {
                properties.store(outputStream, "exchange system status (read only mode) between easy-business instances (e.g. web-ui, sword)");
            }
            finally
            {
                outputStream.close();
            }
        }
        catch (FileNotFoundException e)
        {
            logger.error("file not found " + file, e);
            throw new IllegalStateException(e);
        }
        catch (IOException e)
        {
            logger.error(e.getMessage(), e);
            throw new IllegalStateException(e);
        }
        catch (ConfigurationException e)
        {
            throw new IllegalStateException(e);
        }
    }

    private void fetch()
    {
        // each WebApp has its own instance of the singleton
        // so we synchronize SWORD, web-ui and the rest interfaces via the file system
        if (System.currentTimeMillis() < lastCheck + getRefreshFrequency())
            return;
        try
        {
            FileInputStream inputStream = new FileInputStream(getFile());
            try
            {
                properties.clear();
                properties.load(inputStream);
            }
            finally
            {
                inputStream.close();
            }
        }
        catch (FileNotFoundException e)
        {
            logger.error("file not found " + file, e);
            throw new IllegalStateException(e);
        }
        catch (IOException e)
        {
            logger.error(e.getMessage(), e);
            throw new IllegalStateException(e);
        }
        catch (ConfigurationException e)
        {
            throw new IllegalStateException(e);
        }
        lastCheck = System.currentTimeMillis();
    }
}
