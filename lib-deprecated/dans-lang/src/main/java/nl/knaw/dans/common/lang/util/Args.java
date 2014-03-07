package nl.knaw.dans.common.lang.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import nl.knaw.dans.common.lang.exception.ConfigurationException;
import nl.knaw.dans.common.lang.exception.ReadOnlyException;

import org.joda.time.DateTime;

/**
 * A utility class for creating consistent arguments in the format <code>name=value</code>. This class
 * can be used either for reading or writing arguments.
 */
public class Args implements Serializable
{

    private static final long serialVersionUID = -6053529733434085165L;

    public static final String PROP_FILE_NAME = "prop.file.name";

    public static final String CONFIGURATION_CLASS_NAME = "configuration.class.name";

    public static final String APPLICATION_CONTEXT = "application.context";

    public static final String USER_NAME = "user.name";

    public static final String PROCESS_NAME = "process.name";

    public static final String LOG_CONFIG_FILE = "log.configuration";

    public static final String LOG_CONSOLE = "log.console";

    private static final String NO_NAME_ARG = "no.name.arg.";

    private final Map<String, String> arguments = new LinkedHashMap<String, String>();

    private final boolean readOnly;

    private final DateTime startDate;

    /**
     * Construct a writable Args.
     */
    public Args()
    {
        readOnly = false;
        startDate = new DateTime();
    }

    /**
     * Construct a read only Args from the given String array. If the String array contains the key
     * <code>"prop.file.name"</code> the properties file at the given location will also be loaded.
     * 
     * @param args
     *        String array
     * @throws ConfigurationException
     *         on Exceptions
     */
    public Args(String[] args) throws ConfigurationException
    {
        readOnly = true;
        startDate = new DateTime();
        for (int i = 0; i < args.length; i++)
        {
            String[] kv = args[i].split("=");
            if (kv.length == 2)
            {
                arguments.put(kv[0], kv[1]);
            }
            else
            {
                arguments.put(NO_NAME_ARG + i, args[i]);
            }
        }
        if (getPropFileName() != null)
        {
            loadFromFile(getPropFileName());
        }
    }

    /**
     * Construct a read only Args by reading the properties file at the given location.
     * 
     * @param propFileName
     *        path to the properties file
     * @throws ConfigurationException
     *         on Exceptions
     */
    public Args(String propFileName) throws ConfigurationException
    {
        readOnly = true;
        startDate = new DateTime();
        loadFromFile(propFileName);
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }

    public DateTime getStartDate()
    {
        return startDate;
    }

    public String printArguments()
    {
        StringWriter writer = new StringWriter();
        try
        {
            printArguments(writer);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not print arguments: ", e);
        }
        return writer.toString();
    }

    public void printArguments(Appendable appendable) throws IOException
    {
        appendable.append("# ARGUMENTS (" + this + ")\n").append("# STARTED AT " + getStartDate().toString("yyyy-MM-dd-HH:mm:ss.SSS") + "\n");
        Set<Entry<String, String>> entrySet = arguments.entrySet();
        for (Entry<String, String> entry : entrySet)
        {
            appendable.append(createArg(entry.getKey(), entry.getValue()));
            appendable.append("\n");
        }
    }

    public String asString()
    {
        StringBuilder sb = new StringBuilder();
        Set<Entry<String, String>> entrySet = arguments.entrySet();
        for (Entry<String, String> entry : entrySet)
        {
            sb.append(createArg(entry.getKey(), entry.getValue())).append(" ");
        }
        return sb.toString();
    }

    public String[] asStringArray()
    {
        String[] args = new String[arguments.size()];
        Iterator<Entry<String, String>> entryIter = arguments.entrySet().iterator();
        int i = 0;
        while (entryIter.hasNext())
        {
            Entry<String, String> entry = entryIter.next();
            args[i] = createArg(entry.getKey(), entry.getValue());
            i++;
        }
        return args;
    }

    public String getArgument(String key)
    {
        return getArgument(key, null);
    }

    public String getArgument(String key, String defaultValue)
    {
        String value = arguments.get(key);
        if (value == null)
        {
            value = defaultValue;
        }
        return value;
    }

    public String getArgument(int ordinal)
    {
        return getArgument(ordinal, null);
    }

    public String getArgument(int ordinal, String defaultValue)
    {
        return getArgument(NO_NAME_ARG + ordinal, defaultValue);
    }

    public void put(String key, String value)
    {
        throwExceptionIfReadOnly();
        arguments.put(key, value);
    }

    public void put(String key, boolean value)
    {
        put(key, Boolean.toString(value));
    }

    public void put(String key, int value)
    {
        put(key, String.valueOf(value));
    }

    public String getPropFileName()
    {
        return getArgument(PROP_FILE_NAME);
    }

    public void setPropFileName(String propFileName)
    {
        put(PROP_FILE_NAME, propFileName);
    }

    public String getConfigurationClassName()
    {
        return getArgument(CONFIGURATION_CLASS_NAME);
    }

    public void setConfigurationClassName(String className)
    {
        put(CONFIGURATION_CLASS_NAME, className);
    }

    public String getApplicationContext()
    {
        return getArgument(APPLICATION_CONTEXT, null);
    }

    public void setApplicationContext(String applicationContext)
    {
        put(APPLICATION_CONTEXT, applicationContext);
    }

    public String getUsername()
    {
        return getArgument(USER_NAME, null);
    }

    public void setUsername(String username)
    {
        put(USER_NAME, username);
    }

    public String getProcessName(String defaultValue)
    {
        return getArgument(PROCESS_NAME, defaultValue);
    }

    public void setProcessName(String processName)
    {
        put(PROCESS_NAME, processName);
    }

    public String getLogConfigFile(String defaultValue)
    {
        return getArgument(LOG_CONFIG_FILE, defaultValue);
    }

    public void setLogConfigFile(String logConfigFile)
    {
        put(LOG_CONFIG_FILE, logConfigFile);
    }

    public boolean isLoggingToConsole()
    {
        return getBooleanValue(LOG_CONSOLE);
    }

    public void setLoggingToConsole(boolean logToConsole)
    {
        put(LOG_CONSOLE, logToConsole);
    }

    private void throwExceptionIfReadOnly()
    {
        if (readOnly)
        {
            throw new ReadOnlyException(this + " is read only");
        }
    }

    public boolean getBooleanValue(String key)
    {
        return "true".equalsIgnoreCase(getArgument(key, "false"));
    }

    public int getIntValue(String key, int defaultValue)
    {
        String value = getArgument(key, String.valueOf(defaultValue));
        return Integer.parseInt(value);
    }

    private void loadFromFile(String propFileName) throws ConfigurationException
    {
        try
        {
            loadProperties(propFileName);
        }
        catch (IOException e)
        {
            throw new ConfigurationException("Could not load properties from file " + propFileName, e);
        }
    }

    private void loadProperties(String propFileName) throws IOException
    {
        Properties props = new Properties();
        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream(propFileName);
            props.load(fis);
            for (Entry<Object, Object> propEntry : props.entrySet())
            {
                arguments.put(((String) propEntry.getKey()), ((String) propEntry.getValue()));
            }
        }
        finally
        {
            if (fis != null)
            {
                fis.close();
            }
        }

    }

    // static methods

    public static String createArg(String name, String value)
    {
        return name + "=" + value;
    }

    public static String createArg(String name, boolean value)
    {
        return name + "=" + Boolean.toString(value);
    }

    public static String propFileName(String propFileName)
    {
        return createArg(PROP_FILE_NAME, propFileName);
    }

    public static String configurationClassName(String className)
    {
        return createArg(CONFIGURATION_CLASS_NAME, className);
    }

    public static String applicationContext(String context)
    {
        return createArg(APPLICATION_CONTEXT, context);
    }

    public static String username(String username)
    {
        return createArg(USER_NAME, username);
    }

    public static String processname(String processname)
    {
        return createArg(PROCESS_NAME, processname);
    }

    public static String logConfiguration(String logFile)
    {
        return createArg(LOG_CONFIG_FILE, logFile);
    }

    public static String logConsole(boolean value)
    {
        return createArg(LOG_CONSOLE, value);
    }

}
