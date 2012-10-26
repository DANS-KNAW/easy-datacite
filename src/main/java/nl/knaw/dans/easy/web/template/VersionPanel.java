package nl.knaw.dans.easy.web.template;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VersionPanel extends Panel
{

    private static final String UNKNOWN = "unknown";

    public static final String VERSION_FILE = "version.properties";

    private static final long serialVersionUID = -8906326042896861618L;
    private static Logger logger = LoggerFactory.getLogger(VersionPanel.class);
    private static Properties versionProps;
    private static String version;
    private static String buildNumber;
    private static String buildDate;

    private boolean initiated;

    public VersionPanel(String id)
    {
        super(id);
    }

    @Override
    protected void onBeforeRender()
    {
        if (!initiated)
        {
            init();
            initiated = true;
        }
        super.onBeforeRender();
    }

    private void init()
    {
        add(new Label("version", getVersion()));
        add(new Label("buildNumber", getBuildNumber()));
        add(new Label("buildDate", getBuildDate()));
    }

    public static String getVersion()
    {
        if (version == null)
        {
            version = getVersionProps().getProperty("easy.version", UNKNOWN);
            if ("${project.version}".equals(version))
            {
                version = UNKNOWN;
            }
        }
        return version;
    }

    public static String getBuildNumber()
    {
        if (buildNumber == null)
        {
            buildNumber = getVersionProps().getProperty("easy.buildNumber", UNKNOWN);
            if ("${buildNumber}".equals(buildNumber))
            {
                buildNumber = UNKNOWN;
            }
        }
        return buildNumber;
    }

    public static String getBuildDate()
    {
        if (buildDate == null)
        {
            String buildTime = getVersionProps().getProperty("easy.buildTime");
            if (buildTime == null || "${timestamp}".equals(buildTime))
            {
                buildDate = UNKNOWN;
            }
            else
            {
                try
                {
                    long time = Long.parseLong(buildTime);
                    DateTime dateTime = new DateTime(time);
                    buildDate = dateTime.toString("yyyy-MM-dd HH:mm");
                }
                catch (NumberFormatException e)
                {
                    logger.warn("Unable to parse property 'easy.buildTime' from version.properties");
                    buildDate = UNKNOWN;
                }
            }
        }
        return buildDate;
    }

    public static Properties getVersionProps()
    {
        if (versionProps == null)
        {
            try
            {
                loadVersionProps();
            }
            catch (IOException e)
            {
                logger.error("Unable to close InputStream: ", e);
            }
        }
        return versionProps;
    }

    private static void loadVersionProps() throws IOException
    {
        versionProps = new Properties();
        InputStream inStream = null;
        try
        {
            inStream = VersionPanel.class.getClassLoader().getResourceAsStream(VERSION_FILE);
            versionProps.load(inStream);
        }
        catch (IOException e)
        {
            logger.error("Unable to load version properties: ", e);
        }
        finally
        {
            if (inStream != null)
            {
                inStream.close();
            }
        }
    }

}
