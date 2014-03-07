package nl.knaw.dans.common.lang.log;

import java.io.File;
import java.io.IOException;

import nl.knaw.dans.common.lang.exception.ConfigurationException;
import nl.knaw.dans.common.lang.os.OS;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RL is a configurable Report Logger. (Reporting to a Report Location and listening to Radio Luxemburg.)
 * <p/>
 * This semi-singleton class is constructed by a Spring application context
 * 
 * <pre>
 *  <bean name="rl" class="nl.knaw.dans.common.lang.log.RL">
 *        <!-- path to the directory where reports should be written -->
 *     <constructor-arg value="/home/easy/batch/reports/test/app/enz"/>
 *        <!-- should we execute 'chmod a=rwx'? -->
 *     <constructor-arg value="true"/>
 *        <!-- should we extend the report directory with an extra time-based directory? -->
 *     <constructor-arg value="true"/>
 *        <!-- (optional) reference to the nl.knaw.dans.common.lang.log.Reporter (or subclass) to use -->
 *     <property name="reporter" ref="reporter"/>
 *  </bean>
 * </pre>
 * 
 * or it can be initialized by a start-up routine, using one of the initialize methods. If no
 * initialization has been called, this class will be initialized after a call to one of the info(),
 * warn(), error() or close() methods.
 */
public class RL
{

    public static final String GLOBAL = "global";

    protected static final String DEFAULT_REPORT_LOCATION = "../reports";

    private static final Logger logger = LoggerFactory.getLogger(RL.class);

    private static RL INSTANCE;

    private Reporter reporter;
    private File reportLocation;
    private boolean allReadWrite;

    public static RL initialize() throws ConfigurationException
    {
        return initialize(DEFAULT_REPORT_LOCATION, false);
    }

    public static RL initialize(String reportLocationName, boolean allReadWrite) throws ConfigurationException
    {
        return initialize(new File(reportLocationName), allReadWrite);
    }

    public static RL initialize(File reportLocation, boolean allReadWrite) throws ConfigurationException
    {
        if (isInitialized())
        {
            throw new ConfigurationException("Already initialized: " + RL.class.getName());
        }
        return new RL(reportLocation, allReadWrite, false);
    }

    /**
     * No public constructor, use {@link #initialize()}.
     * @throws ConfigurationException
     */
    public RL(boolean dateLocation) throws ConfigurationException
    {
        this(DEFAULT_REPORT_LOCATION, false, dateLocation);
    }

    public RL(String reportLocationName) throws ConfigurationException
    {
        this(reportLocationName, false, true);
    }

    /**
     * No public constructor, use {@link #initialize(String, boolean)}.
     * @param reportLocationName
     * @param allReadWrite
     * @throws ConfigurationException
     */
    public RL(String reportLocationName, boolean allReadWrite, boolean dateLocation) throws ConfigurationException
    {
        this(new File(reportLocationName), allReadWrite, dateLocation);
    }

    /**
     * No public constructor, use {@link #initialize(File, boolean)}.
     * @param reportLocation
     * @param allReadWrite
     * @throws ConfigurationException
     */
    public RL(File reportLocation, boolean allReadWrite, boolean dateLocation) throws ConfigurationException
    {
        setReportLocation(reportLocation, allReadWrite, dateLocation);
        INSTANCE = this;
        logger.info("Instantiated " + this + " reporting at " + this.reportLocation.getAbsolutePath());
    }

    private void setReportLocation(File file, boolean allRW, boolean dateLocation) throws ConfigurationException
    {
        file.mkdirs();
        if (!file.exists())
        {
            throw new ConfigurationException("The file " + file.getPath() + " does not exist.");
        }
        if (!file.isDirectory())
        {
            throw new ConfigurationException("The file " + file.getPath() + " is not a directory.");
        }
        if (!file.canWrite())
        {
            throw new ConfigurationException("The file " + file.getPath() + " is not a writable.");
        }

        this.allReadWrite = allRW;
        this.reportLocation = file;
        setRestrictions(reportLocation);

        if (dateLocation)
        {
            reportLocation = new File(reportLocation, new DateTime().toString("yyyy-MM-dd-HH:mm:ss"));
            reportLocation.mkdirs();
            setRestrictions(reportLocation);
        }

    }

    private void setRestrictions(File file) throws ConfigurationException
    {
        try
        {
            prepareReportLocation(reportLocation, allReadWrite);
        }
        catch (IOException e)
        {
            throw new ConfigurationException(e);
        }
    }

    public static File getReportLocation()
    {
        return getInstance().reportLocation;
    }

    public Reporter getReporter()
    {
        if (reporter == null)
        {
            reporter = new Reporter(reportLocation, allReadWrite);
        }
        return reporter;
    }

    public void setReporter(Reporter reporter)
    {
        this.reporter = reporter;
        this.reporter.setReportLocation(reportLocation, allReadWrite);
        logger.info("Reporter set at " + this.reporter);
    }

    public static boolean isInitialized()
    {
        return INSTANCE != null;
    }

    private static RL getInstance()
    {
        if (!isInitialized())
        {
            try
            {
                new RL(true);
            }
            catch (ConfigurationException e)
            {
                throw new RLRuntimeException(e);
            }
        }
        return INSTANCE;
    }

    public static void info(Event event)
    {
        getInstance().getReporter().info(event);
    }

    public static void warn(Event event)
    {
        getInstance().getReporter().warn(event);
    }

    public static void error(Event event)
    {
        getInstance().getReporter().error(event);
    }

    public static void close()
    {
        getInstance().getReporter().close();
    }

    public static void prepareReportLocation(File location, boolean allReadWrite) throws IOException
    {
        location.mkdirs();
        if (allReadWrite)
        {
            OS.setAllRWX(location);
        }
    }

    /**
     * Only for tests!
     */
    static void reset()
    {
        INSTANCE = null;
    }

}
