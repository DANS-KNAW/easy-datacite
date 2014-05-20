package nl.knaw.dans.easy.business.item;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

/** Immutable properties for a FileItem with an external audio/video stream. */
public class AudioVideoProperties
{
    /** A required property, value should be "yes" */
    private static final String AUDIO_VIDEO_INSTRUCTIONS = "audio-video.ebiu-instructions";

    /** A required property, value should be a path on the configured streaming host */
    private static final String AUDIO_VIDEO_SPRINGFIELD_PATH = "audio-video.springfield-path";

    private final File file;
    private boolean available = false;
    private String streamingPath = "";

    /**
     * Constructs a possibly invalid instance.
     * 
     * @param file
     *        a properties file with instructions for audio/video streaming
     * @throws IOException
     * @throws URISyntaxException
     */
    public AudioVideoProperties(final File file) throws IOException
    {
        this.file = file;
        if (isPropertiesFile())
        {
            final Properties properties = readProperties(file);
            if (!hasAVInstructions(properties))
                setStreamingPath(getPath(properties));
        }
    }

    private String getPath(final Properties properties) throws IOException
    {
        final String pathProperty = properties.getProperty(AUDIO_VIDEO_SPRINGFIELD_PATH);
        if (pathProperty.toString().trim().length() != 0)
            return pathProperty.trim();
        // TODO abused IOExecption to abort ingest, but it is an input error
        throw new IOException("property '" + AUDIO_VIDEO_SPRINGFIELD_PATH + "' is missing in" + file);
    }

    private Properties readProperties(final File file) throws IOException
    {
        final FileInputStream fileInputStream = new FileInputStream(file);
        try
        {
            final Properties props = new Properties();
            props.load(fileInputStream);
            return props;
        }
        finally
        {
            fileInputStream.close();
        }
    }

    /**
     * Constructs the URL from the configured host and the path property.
     * 
     * @param value
     * @throws IOException
     */
    private void setStreamingPath(final String value) throws IOException
    {
        // clean code: don't return null
        if (value == null)
            streamingPath = "";
        else
            streamingPath = value;
        available = true;
    }

    private boolean hasAVInstructions(final Properties properties)
    {
        final String property = (String) properties.get(AUDIO_VIDEO_INSTRUCTIONS);
        return property == null || !property.trim().toLowerCase().equals("yes");
    }

    /** Checks the extension of the file. */
    private boolean isPropertiesFile()
    {
        return file.getName().toLowerCase().endsWith(".properties");
    }

    /** @return the external location that streams the audio/video content */
    public String getStreamingPath()
    {
        if (available)
            return streamingPath;
        else
            throw new IllegalStateException(file + " is not a valid AV properties file");
    }

    /**
     * @return false if
     *         <ul>
     *         <li>the file has another extension than "properties" (case insensitive)</li>
     *         <li>there is no property "audio-video.ebiu-instructions" with value yes (value is case
     *         insensitive and ignores leading/trailing white space)</li>
     *         <li>there is no property "audio-video.springfield-path" (leading/trailing white space is
     *         ignored)</li>
     *         <li>the path and configured host do not combine into a valid URL</li>
     *         </ul>
     */
    public boolean available()
    {
        return available;
    }
}
