package nl.knaw.dans.easy.domain.deposit.discipline;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.common.lang.AbstractCache;
import nl.knaw.dans.common.lang.CacheException;
import nl.knaw.dans.common.lang.ResourceLocator;
import nl.knaw.dans.common.lang.ResourceNotFoundException;

public abstract class AbstractListCache<V> extends AbstractCache<String, V>
{
    public static final String EXTENSION = "xml";
    
    private static final Logger logger = LoggerFactory.getLogger(AbstractListCache.class);
    
    protected AbstractListCache()
    {
        super();
    }

    protected abstract String getBaseFolder();
    
    protected abstract V getObjectForCache(String key, Locale locale) throws IOException, CacheException;
    
    /**
     * Get the list for the given id and the default Locale.
     * 
     * @param listId
     *        id of the list
     * @return list for the given id and the default Locale
     * @throws CacheException
     *         as a wrapper for several mishaps
     */
    public V getList(String listId) throws CacheException
    {
        return getList(listId, null);
    }

    /**
     * Get the list for the given id and the given Locale.
     * 
     * @param listId
     *        id of the list
     * @param locale
     *        Locale, may be <code>null</code>
     * @return list for the given id and the given Locale
     * @throws CacheException
     *         as a wrapper for several mishaps
     */
    public V getList(String listId, Locale locale) throws CacheException
    {
        return super.getCachedObject(listId, locale);
    }
    

    /**
     * Use {@link ResourceLocator}-algorithm to find the resource with list of the given id and Locale.
     * 
     * @param listId
     *        id of the list
     * @param locale
     *        Locale, may be <code>null</code>
     * @return URL for given listId and Locale
     * @throws ResourceNotFoundException
     *         if a list for the given id does not exist
     */
    protected URL getURL(String listId, Locale locale) throws ResourceNotFoundException
    {
        String path = getBaseFolder() + listId.replaceAll("\\.", "/");
        URL url = ResourceLocator.getURL(path, locale, EXTENSION);
        if (url == null)
        {
            throw new ResourceNotFoundException("A resource with id '" + listId + "' was not found."
                    + "\nEither the file '" + path + "." + EXTENSION + "' is missing"
                    + "\nor the id '" + listId + "' is wrong.");
        }
        return url;
    }

    /**
     * Get an InputStream on the given listId for the given Locale. The caller is responsible for proper closing of the
     * InputStream.
     * 
     * @param listId
     *        id of the list
     * @param locale
     *        Locale, may be <code>null</code>
     * @return InputStream on the given listId for the given Locale.
     * @throws IOException
     *         if such mishap occurs
     * @throws ResourceNotFoundException
     *         if a list for the given id does not exist
     */
    public InputStream getInputStream(String listId, Locale locale) throws IOException, ResourceNotFoundException
    {
        URL url = getURL(listId, locale);
        return url.openStream();
    }

    /**
     * Get an InputStream on the given listId for the default Locale. The caller is responsible for proper closing of
     * the InputStream.
     * 
     * @param listId
     *        id of the list
     * @return InputStream on the given listId for the default Locale
     * @throws IOException
     *         if such mishap occurs
     * @throws ResourceNotFoundException
     *         if a list for the given id does not exist
     */
    public InputStream getInputStream(String listId) throws IOException, ResourceNotFoundException
    {
        return getInputStream(listId, null);
    }

    public byte[] getBytes(String listId, Locale locale) throws IOException, ResourceNotFoundException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream inStream = null;
        try
        {
            inStream = getInputStream(listId, locale);
            int b;
            while ((b = inStream.read()) != -1)
            {
                baos.write(b);
            }
        }
        finally
        {
            if (inStream != null)
            {
                inStream.close();
            }
        }
        
        return baos.toByteArray();
    }
    
    @Override
    protected V getObject(String key, Locale locale) throws CacheException
    {
        V list = null;
        try
        {
            list = getObjectForCache(key, locale);
        }
        catch (IOException e)
        {
            String msg = "Unable to close input stream on resource 'ChoiceList:" + key + "': ";
            logger.error(msg, e);
            throw new CacheException(msg, e);
        }
        return list;
    }

}
