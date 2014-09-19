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
 */
public class SystemReadOnlyStatus {
    private static final String REFRESH_FREQUENCY = "refresh.frequency";
    private static final String IS_READ_ONLY = "is.read.only";
    private static final Logger logger = LoggerFactory.getLogger(SystemReadOnlyStatus.class);
    private static final String DEFAULT_FREQUENCY = (1000 * 60 * 2) + "";
    private final Properties properties = new Properties();

    /** File that communicates the status between the WebUI, sword or any other instance of easy. */
    private File file;

    private long lastCheck = 0L;

    /** Required for wicket's SpringBean annotation, do not use otherwise. */
    public SystemReadOnlyStatus() {}

    /**
     * Creates a bean to set the easy system in read only mode in preparation for a shutdown.
     * 
     * @param file
     *        communicates the status between the WebUI, sword or any other instance of easy that has the same file configured.
     */
    public SystemReadOnlyStatus(File file) {
        this.file = file;

        // get the refresh frequency from a previous configuration
        if (file.isFile())
            fetch();

        // start the system in update mode
        properties.setProperty(IS_READ_ONLY, "false");

        flush();
    }

    public boolean getReadOnly() {
        fetch();
        String value = properties.getProperty(IS_READ_ONLY);
        return Boolean.parseBoolean(value);
    }

    public void setReadOnly(boolean isReadOnly) {
        properties.setProperty(IS_READ_ONLY, isReadOnly + "");
        flush();
    }

    public long getRefreshFrequency() {
        // fetch here would cause a stack overflow
        // changes will be read along with other properties
        String value = properties.getProperty(REFRESH_FREQUENCY, DEFAULT_FREQUENCY);
        return Integer.parseInt(value);
    }

    public void setCheckFrequency(long checkFrequency) {
        properties.setProperty(REFRESH_FREQUENCY, checkFrequency + "");
        flush();
    }

    private File getFile() {
        if (file == null)
            throw new IllegalStateException("call the constructor with arguments, the default constructor is for wicket's SpringBean annotation");
        return file;
    }

    private void flush() {
        String property = properties.getProperty(REFRESH_FREQUENCY);
        if (property == null || property.trim().length() == 0)
            properties.setProperty(REFRESH_FREQUENCY, DEFAULT_FREQUENCY);
        try {
            FileOutputStream outputStream = new FileOutputStream(getFile());
            try {
                properties.store(outputStream, "exchange system status (read only mode) between easy-business instances (e.g. web-ui, sword)");
            }
            finally {
                outputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            logger.error("file not found " + getFile(), e);
            throw new IllegalStateException(e);
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new IllegalStateException(e);
        }
    }

    private void fetch() {
        // each WebApp has its own instance of the singleton
        // so we synchronize SWORD, web-ui and the rest interfaces via the file system
        if (System.currentTimeMillis() < lastCheck + getRefreshFrequency())
            return;
        try {
            FileInputStream inputStream = new FileInputStream(getFile());
            try {
                properties.clear();
                properties.load(inputStream);
            }
            finally {
                inputStream.close();
            }
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new IllegalStateException(e);
        }
        lastCheck = System.currentTimeMillis();
    }
}
