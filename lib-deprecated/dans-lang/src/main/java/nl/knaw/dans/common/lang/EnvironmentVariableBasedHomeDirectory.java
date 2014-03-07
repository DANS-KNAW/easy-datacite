package nl.knaw.dans.common.lang;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

public class EnvironmentVariableBasedHomeDirectory implements HomeDirectory
{
    private static final Logger log = LoggerFactory.getLogger(EnvironmentVariableBasedHomeDirectory.class);
    private String environmentVariableName;

    @Override
    public File getHomeDirectory()
    {
        /*
         * Setting the "environment variable" through a system property is supported, mainly for testing
         * purposes, as we cannot set an environment variable through Java code.
         */
        String homeDirectoryPath = System.getProperty(environmentVariableName);
        if (homeDirectoryPath == null)
        {
            homeDirectoryPath = System.getenv(environmentVariableName);
        }
        if (homeDirectoryPath == null)
        {
            error("Evironment variable {} is not set", environmentVariableName);
        }
        File homeDirectory = new File(homeDirectoryPath);
        if (!homeDirectory.exists())
        {
            error("Home diretory {} does not exist", homeDirectory.getAbsolutePath());
        }
        if (!homeDirectory.isDirectory())
        {
            error("Home directory {} exists but is a regular file, not a directory", homeDirectory.getAbsolutePath());
        }
        return homeDirectory;
    }

    private void error(String msg, String... params)
    {
        String m = MessageFormatter.format(msg, params).getMessage();
        log.error(m);
        throw new RuntimeException(m);
    }

    @Override
    public String getHome()
    {
        return System.getenv(environmentVariableName);
    }

    public String getEnvironmentVariableName()
    {
        return environmentVariableName;
    }

    public void setEnvironmentVariableName(String environmentVariableName)
    {
        this.environmentVariableName = environmentVariableName;
    }
}
