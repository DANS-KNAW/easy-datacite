package nl.knaw.dans.common.lang.xml;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import nl.knaw.dans.common.lang.xml.XMLErrorHandler.Reporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * Basic implementation of a cache facility for {@link Schema}s. Schemas that have not yet been cached are created by
 * parsing the schema at an {@link URL} and are than stored under the cache key that equals the toString-representation
 * of this URL. <p/> Special notice has to be taken to the fact that "parsers may choose to ignore all but the first
 * &lt;import&gt; for a given namespace, regardless of information provided in schemaLocation". (From
 * {@link SchemaFactory}.) <p/> Errors during the parsing process are logged by an {@link XMLErrorHandler} and thrown
 * as {@link SchemaCreationException} to the caller.
 *
 * @author ecco
 * @see Schema
 */
public final class SchemaCache
{

    private static final Map<String, Schema> CACHE = Collections.synchronizedMap(new HashMap<String, Schema>());

    /**
     * Logger for logging.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaCache.class);

    private SchemaCache()
    {
        // never instantiate.
    }

    /**
     * Get or create the schema under the given urlString.
     *
     * @param urlString
     *        string-representation of an URL pointing to the schema location
     * @return the schema parsed at said location
     * @throws SchemaCreationException
     *         for SAXParserExceptions and MalformedURLExceptions
     */
    public static Schema getSchema(final String urlString) throws SchemaCreationException
    {
        Schema schema = CACHE.get(urlString);
        if (schema == null)
        {
            schema = createSchema(urlString);
        }
        return schema;
    }

    /**
     * Get or create the schema at the given URL. If the schema was successfully created and cached, on subsequent calls
     * it may be gotten with the string-representation of the URL.
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
            LOGGER.error(msg);
            throw new SchemaCreationException(msg);
        }
        return getSchema(url.toString());
    }

    private static Schema createSchema(final String urlString) throws SchemaCreationException
    {
        return createSchema(getURL(urlString));
    }

    private static synchronized Schema createSchema(final URL url) throws SchemaCreationException
    {
        LOGGER.info("Trying to parse schema at " + url.toString());
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
            LOGGER.error(msg);
            throw new SchemaCreationException(msg, e);
        }
        // The behavior of parser families is different. We do not want invalid schemas cached.
        if (!errorHandler.passed())
        {
            final String msg = "Unable to parse schema at " + url.toString() + ". See debug messages for reason(s)." + errorHandler.getMessages();
            LOGGER.error(msg);
            throw new SchemaCreationException(msg);
        }
        CACHE.put(url.toString(), schema);
        LOGGER.info("Cached schema at " + url.toString());
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
            LOGGER.error(msg);
            throw new SchemaCreationException(msg, e);
        }
        return url;
    }

}
