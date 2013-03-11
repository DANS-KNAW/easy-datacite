package nl.knaw.dans.pf.language.xml.validation;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import nl.knaw.dans.pf.language.xml.exc.SchemaCreationException;
import nl.knaw.dans.pf.language.xml.validation.XMLErrorHandler.Reporter;

import org.apache.commons.collections.map.LRUMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * Basic implementation of a cache facility for {@link Schema}s. Schemas that have not yet been cached
 * are created by parsing the schema at an {@link URL} and are than stored under the cache key that
 * equals the toString-representation of this URL.
 * <p/>
 * Special notice has to be taken to the fact that "parsers may choose to ignore all but the first
 * &lt;import&gt; for a given namespace, regardless of information provided in schemaLocation". (From
 * {@link SchemaFactory}.)
 * <p/>
 * Errors during the parsing process are logged by an {@link XMLErrorHandler} and thrown as
 * {@link SchemaCreationException} to the caller.
 * 
 * @author ecco
 * @see Schema
 */
public final class SchemaCache
{

    @SuppressWarnings("unchecked")
    private static final Map<String, Schema> CACHE = Collections.synchronizedMap(new LRUMap());

    /**
     * Logger for logging.
     */
    private static final Logger logger = LoggerFactory.getLogger(SchemaCache.class);

    private SchemaCache()
    {
        // never instantiate.
    }

    public static Schema putSchema(final String urlString, final Schema schema)
    {
        synchronized (CACHE)
        {
            return CACHE.put(urlString, schema);
        }
    }

    /**
     * Get the schema at the given location or <code>null</code> if not in cache and create is
     * <code>false</code>.
     * 
     * @param location
     *        schemaLocation
     * @param create
     *        create the schema if not in cache.
     * @return the schema at the given location or <code>null</code> if not in cache and create is
     *         <code>false</code>.
     * @throws SchemaCreationException
     */
    public static Schema getSchema(final String location, boolean create) throws SchemaCreationException
    {
        Schema schema;
        synchronized (CACHE)
        {
            schema = CACHE.get(location);
            if (schema == null && create)
            {
                schema = getSchema(location);
            }
        }
        return schema;
    }

    /**
     * Get or create the schema under the given urlString. Does not support multiple schemaLocations.
     * 
     * @param urlString
     *        string-representation of an URL pointing to the schema location
     * @return the schema parsed at said location
     * @throws SchemaCreationException
     *         for SAXParserExceptions and MalformedURLExceptions
     */
    public static Schema getSchema(final String urlString) throws SchemaCreationException
    {
        synchronized (CACHE)
        {
            Schema schema = CACHE.get(urlString);
            if (schema == null)
            {
                schema = createSchema(urlString);
            }
            return schema;
        }
    }

    /**
     * Get or create the schema at the given URL. If the schema was successfully created and cached, on
     * subsequent calls it may be gotten with the string-representation of the URL.
     * 
     * @param url
     *        the location of the schema
     * @return the schema parsed at said location
     * @throws SchemaCreationException
     *         for SAXParserExceptions and MalformedURLExceptions
     */
    public static Schema getSchema(final URL url) throws SchemaCreationException
    {
        if (url == null) // By exception check for null. Might be hard to trace.
        {
            final String msg = "Cannot get schema. url==null.";
            logger.error(msg);
            throw new SchemaCreationException(msg);
        }
        return getSchema(url.toString());
    }
    
    public static int size()
    {
        synchronized (CACHE)
        {
            return CACHE.size();
        }
    }
    
    public static void invalidate()
    {
        synchronized (CACHE)
        {
            CACHE.clear();
        }
    }

    private static Schema createSchema(final String urlString) throws SchemaCreationException
    {
        return createSchema(getURL(urlString));
    }

    private static synchronized Schema createSchema(final URL url) throws SchemaCreationException
    {
        logger.trace("Trying to parse schema at [{}].", url.toString());
        final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        final XMLErrorHandler errorHandler = new XMLErrorHandler(Reporter.error);
        schemaFactory.setErrorHandler(errorHandler);
        Schema schema = null;
        try
        {
            schema = schemaFactory.newSchema(url);
        }
        catch (final SAXException e)
        {
            final String msg = "Unable to parse schema at " + url.toString() + errorHandler.getMessages();
            logger.error(msg);
            throw new SchemaCreationException(msg, e);
        }
        // The behavior of parser families is different. We do not want invalid schemas cached.
        if (!errorHandler.passed())
        {
            final String msg = "Unable to parse schema: " + errorHandler.getMessages();
            logger.error(msg);
            throw new SchemaCreationException(msg);
        }
        CACHE.put(url.toString(), schema);
        logger.info("Cached schema at [{}].", url.toString());
        return schema;
    }

    private static URL getURL(final String urlString) throws SchemaCreationException
    {
        URL url = null;
        try
        {
            url = new URL(urlString);

        }
        catch (final MalformedURLException e)
        {
            final String msg = "Unable to create URL from '" + urlString + "'.";
            logger.error(msg);
            throw new SchemaCreationException(msg, e);
        }
        return url;
    }

}
