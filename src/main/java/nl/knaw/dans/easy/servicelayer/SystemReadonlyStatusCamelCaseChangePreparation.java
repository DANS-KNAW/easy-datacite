package nl.knaw.dans.easy.servicelayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Maintains the flag whether easy is in read only mode in preparation for a shutdown.
 *
 */
public class SystemReadonlyStatusCamelCaseChangePreparation
{
    private static final String REFRESH_FREQUENCY = "refresh.frequency";
    private static final String IS_READ_ONLY = "is.read.only";
    private static final Logger logger = LoggerFactory.getLogger(SystemReadonlyStatusCamelCaseChangePreparation.class);
    private static final String DEFAULT_FREQUENCY = (1000 * 60 * 2) + "";
    private final Properties properties = new Properties();
    private File file;
    private long lastCheck = 0L;

    public boolean getReadOnly()
    {
        mandatoryFetch();
        String value = properties.getProperty(IS_READ_ONLY);
        return Boolean.parseBoolean(value);
    }

    public void setReadOnly(boolean isReadOnly)
    {
        properties.setProperty(IS_READ_ONLY, isReadOnly + "");
        flush();
    }

    public long getRefreshFrequency()
    {
        // fetch here would cause a stack overflow
        // changes will be read along with other properties
        String value = properties.getProperty(REFRESH_FREQUENCY, DEFAULT_FREQUENCY);
        return Integer.parseInt(value);
    }

    public void setCheckFrequency(long checkFrequency)
    {
        properties.setProperty(REFRESH_FREQUENCY, checkFrequency + "");
        flush();
    }

    public File getFile()
    {
        return file;
    }

    /**
     * Sets the file that communicates the status between the WebUI, sword or any other instance of easy.
     * It should be the first action performed on the object before setting or retrieving another
     * property. If the file does not exist, it is created with default values. Despite the content of
     * the file the readOnly property is set to false, so the system will startup in update mode. <br>
     * A constructor argument would have been more logical, but that doesn't marry well with SpringBean
     * injection by wicket.
     */
    public void setFile(File file)
    {
        this.file = file;

        // get the refresh frequency from a previous configuration
        optionalFetch();

        // start the system in update mode
        properties.setProperty(IS_READ_ONLY, "false");
        flush();
    }

    private void flush()
    {
        String property = properties.getProperty(REFRESH_FREQUENCY);
        if (property == null || property.trim().length() == 0)
            properties.setProperty(REFRESH_FREQUENCY, DEFAULT_FREQUENCY);
        try
        {
            FileOutputStream outputStream = new FileOutputStream(file);
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
    }

    private void optionalFetch()
    {
        try
        {
            fetch();
        }
        catch (FileNotFoundException e)
        {
            ;// settle for the defaults
        }
    }

    private void mandatoryFetch()
    {
        try
        {
            fetch();
        }
        catch (FileNotFoundException e)
        {
            logger.error("file not found " + file, e);
            throw new IllegalStateException(e);
        }
    }

    private void fetch() throws FileNotFoundException
    {
        // each WebApp has its own instance of the singleton
        // so we synchronize SWORD, web-ui and the rest interfaces via the file system
        if (System.currentTimeMillis() < lastCheck + getRefreshFrequency())
            return;
        try
        {
            FileInputStream inputStream = new FileInputStream(file);
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
            throw e;
        }
        catch (IOException e)
        {
            logger.error(e.getMessage(), e);
            throw new IllegalStateException(e);
        }
        lastCheck = System.currentTimeMillis();
    }
}
