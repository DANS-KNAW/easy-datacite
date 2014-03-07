package nl.knaw.dans.common.lang;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Locale-sensitive locator of resources. Static methods of this class will locate resources on the
 * classpath, or, used with a HomeDirectory, on the system.
 * 
 * @author ecco Apr 30, 2009
 */
public final class ResourceLocator
{

    private static final Logger logger = LoggerFactory.getLogger(ResourceLocator.class);

    private static HomeDirectory[] HOME_DIRECTORIES;

    @SuppressWarnings("unused")
    private ResourceLocator()
    {

    }

    /**
     * Constructor used for constructing a ResourceLocator as a bean in an application context, using the
     * given HomeDirectory. Used with this constructor ResourceLocator will first look for the resource
     * in the home directory. If a resource is not found in the home directory it will look for the
     * resource on the classpath.
     * 
     * @param homeDirectory
     *        homeDirectory to set.
     */
    public ResourceLocator(HomeDirectory... homeDirectory)
    {
        HOME_DIRECTORIES = homeDirectory;
        logger.debug("Constructed ResourceLocator. HomeDirectory={}", HOME_DIRECTORIES);
    }

    /**
     * Get the URL for the given location, or <code>null</code> if no resource exists on given location.
     * 
     * @param location
     *        a relative path on the class path, separated with "/"
     * @return URL of the resource or <code>null</code>
     */
    public static URL getURL(final String location)
    {
        URL url = null;
        if (HOME_DIRECTORIES != null)
        {
            for (HomeDirectory home : HOME_DIRECTORIES)
            {

                File file = new File(home.getHomeDirectory(), location);
                if (file.exists())
                {
                    try
                    {
                        return file.toURI().toURL();
                    }
                    catch (MalformedURLException e)
                    {
                        // we return null "if no resource exists on given location."
                    }
                }
            }
        }

        if (url == null) // try to locate the resource on the classpath.
        {
            final ClassLoader classLoader = ResourceLocator.class.getClassLoader();
            if (classLoader != null)
            {
                url = classLoader.getResource(location);
            }
        }
        return url;
    }

    /**
     * Get the URL for a locale-specific resource. The algorithm conforms to java.util.ResourceBundle
     * policy. That is for given path x and language-code nl and country-code NL, will look for
     * <ul>
     * <li>x_nl_NL</li>
     * <li>x_nl</li>
     * <li>x</li>
     * </ul>
     * in that order.
     * 
     * @param path
     *        "/"-separated relative path, without extension
     * @param locale
     *        Locale, may be <code>null</code>
     * @param extension
     *        extension (without "."), may be <code>null</code>
     * @return URL of the resource or <code>null</code>
     */
    public static URL getURL(final String path, final Locale locale, final String extension)
    {
        URL url = null;

        final boolean language = locale != null && StringUtils.isNotBlank(locale.getLanguage());
        final boolean country = locale != null && StringUtils.isNotBlank(locale.getCountry());

        if (language && country)
        {
            // x_nl_NL.ext
            url = getURL(getFullPath(path, locale, extension));
        }

        if (url == null && language)
        {
            // x_nl.ext
            url = getURL(getLanguagePath(path, locale, extension));
        }

        if (url == null)
        {
            // x.ext
            url = getURL(getPath(path, extension));
        }

        return url;
    }

    /**
     * Gets a file from an URL. The URL is converted to a path.
     * 
     * @param url
     *        the url
     * @return the file
     * @throws ResourceNotFoundException
     *         if the file could not be found
     */
    private static File getFile(final URL url) throws ResourceNotFoundException
    {
        String path;
        try
        {
            path = URLDecoder.decode(url.getFile(), "UTF-8");
        }
        catch (final UnsupportedEncodingException e)
        {
            throw new ResourceNotFoundException(e);
        }
        return new File(path);
    }

    /**
     * Get File from given location. File can be directory or plain file.
     * 
     * @param location
     *        a relative path on the class path, separated with "/"
     * @return File on given location
     * @throws ResourceNotFoundException
     *         if no file exists on given location
     */
    public static File getFile(final String location) throws ResourceNotFoundException
    {
        final URL url = getURL(location);
        if (url == null)
        {
            throw new ResourceNotFoundException("Cannot locate the resource '" + location + "'");
        }
        return getFile(url);
    }

    /**
     * Get a locale-specific File.
     * 
     * @param path
     *        "/"-separated relative path, without extension
     * @param locale
     *        Locale, may be <code>null</code>
     * @param extension
     *        extension (without "."), may be <code>null</code>
     * @return locale-specific File on given location
     * @throws ResourceNotFoundException
     *         if no file exists on given path with given extension
     * @see #getURL(String, Locale, String)
     */
    public static File getFile(final String path, final Locale locale, final String extension) throws ResourceNotFoundException
    {
        final URL url = getURL(path, locale, extension);
        if (url == null)
        {
            throw new ResourceNotFoundException("Cannot locate the resource '" + path + "'");
        }

        return getFile(url);
    }

    /**
     * Get InputStream from given location. The caller is responsible for proper closing of the
     * InputStream.
     * 
     * @param location
     *        a relative path on the class path, separated with "/"
     * @return InputStream from given location
     * @throws IOException
     *         if such mishap occurs
     * @throws ResourceNotFoundException
     *         if no file exists on given location
     */
    public static InputStream getInputStream(final String location) throws IOException, ResourceNotFoundException
    {
        InputStream inStream = null;
        final URL url = getURL(location);
        if (url == null)
        {
            throw new ResourceNotFoundException("Cannot locate the resource '" + location + "'");
        }
        else
        {
            inStream = url.openStream();
        }
        return inStream;
    }

    /**
     * Get locale-specific InputStream. The caller is responsible for proper closing of the InputStream.
     * 
     * @param path
     *        "/"-separated relative path, without extension
     * @param locale
     *        Locale, may be <code>null</code>
     * @param extension
     *        extension (without "."), may be <code>null</code>
     * @return locale-specific InputStream
     * @throws IOException
     *         if such mishap occurs
     * @throws ResourceNotFoundException
     *         if no InputStream could be located
     * @see #getURL(String, Locale, String)
     */
    public static InputStream getInputStream(final String path, final Locale locale, final String extension) throws IOException, ResourceNotFoundException
    {
        InputStream inStream = null;
        final URL url = getURL(path, locale, extension);
        if (url == null)
        {
            throw new ResourceNotFoundException("Cannot locate the resource '" + path + "'");
        }
        else
        {
            inStream = url.openStream();
        }
        return inStream;
    }

    private static String getFullPath(final String path, final Locale locale, final String extension)
    {
        final StringBuilder sb = new StringBuilder(path);
        sb.append("_");
        sb.append(locale.getLanguage());
        sb.append("_");
        sb.append(locale.getCountry());
        addExtension(extension, sb);
        return sb.toString();
    }

    private static String getLanguagePath(final String path, final Locale locale, final String extension)
    {
        final StringBuilder sb = new StringBuilder(path);
        sb.append("_");
        sb.append(locale.getLanguage());
        addExtension(extension, sb);
        return sb.toString();
    }

    private static String getPath(final String path, final String extension)
    {
        final StringBuilder sb = new StringBuilder(path);
        addExtension(extension, sb);
        return sb.toString();
    }

    private static void addExtension(final String extension, final StringBuilder sb)
    {
        if (StringUtils.isNotBlank(extension))
        {
            sb.append(".");
            sb.append(extension);
        }
    }

}
