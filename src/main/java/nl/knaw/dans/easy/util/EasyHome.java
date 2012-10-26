package nl.knaw.dans.easy.util;

import java.io.File;
import java.io.FileNotFoundException;

import nl.knaw.dans.common.lang.HomeDirectory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility to determine and provide the value of the "Easy Home" constant.
 */
public class EasyHome implements HomeDirectory
{

    public static final String EASY_HOME_KEY = "easy.home";

    private static Logger logger = LoggerFactory.getLogger(EasyHome.class);

    private static String value;

    /**
     * Determines the value of "Easy Home" based on the <code>easy.home</code> system property
     * <p>
     * Once successfully determined, the value is guaranteed not to change during the life of the
     * application.
     * 
     * @returns the value, or <code>null</code> if undefined in any way.
     */
    public final static String getValue()
    {
        if (value == null)
        {
            if (System.getProperty(EASY_HOME_KEY) != null)
            {
                value = System.getProperty(EASY_HOME_KEY);
            }
            else if (System.getenv("EASY_HOME") != null)
            {
                value = System.getenv("EASY_HOME");
            }
            else
            {
                logger.warn("CAUTION: no system property was found for the easy home directory!" + "\n\tPlease specify the system property '" + EASY_HOME_KEY
                        + "'" + "\nStacktrace:" + getStacktrace());
            }
        }
        return value;
    }

    public static void setValue(String v)
    {
        value = v;
    }

    public static File getLocation() throws FileNotFoundException
    {
        File file = new File(getValue());
        if (!file.exists())
        {
            throw new FileNotFoundException("File not found: " + getValue());
        }
        return file;
    }

    @Override
    public String getHome()
    {
        return getValue();
    }

    @Override
    public File getHomeDirectory()
    {
        return new File(getValue());
    }

    private static String getStacktrace()
    {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement ste : Thread.currentThread().getStackTrace())
        {
            String className = ste.getClassName();
            if (!className.equals(Thread.class.getName()) && !className.equals(EasyHome.class.getName()))
            {
                sb.append("\n\tat ").append(ste.getClassName()).append(".").append(ste.getMethodName()).append(" (").append(ste.getFileName()).append(":")
                        .append(ste.getLineNumber()).append(")");
            }
        }
        return sb.toString();
    }
}
