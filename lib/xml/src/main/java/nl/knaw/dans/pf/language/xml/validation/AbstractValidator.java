package nl.knaw.dans.pf.language.xml.validation;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;

import nl.knaw.dans.pf.language.xml.exc.SchemaCreationException;
import nl.knaw.dans.pf.language.xml.exc.ValidatorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Convenience class for creating validators. Due to the fact that
 * com.sun.org.apache.xerces.internal.jaxp.validation.ValidatorImpl is not loading schemas
 * that have a schema location declared in the xml instance to be validated, this validator
 * cannot handle validation under these circumstances. Alternatively use {@link AbstractValidator2}.
 * 
 * @see AbstractValidator2
 * 
 * @author ecco
 */
public abstract class AbstractValidator
{

    private static final Logger logger = LoggerFactory.getLogger(AbstractValidator.class);

    /**
     * Map containing version strings as key and the string-representation of their schema-URL as value.
     */
    private final Map<String, String> versionMap = Collections.synchronizedMap(new HashMap<String, String>());

    /**
     * Get the URL of the schema definition for the given version.
     * 
     * @param version
     *        a version indicator of the schema, may be ignored.
     * @return the URL of the schema definition for T, optionally for the version of schema definition
     *         for T
     * @throws SchemaCreationException
     *         if the schema could not be created
     */
    public abstract URL getSchemaURL(final String version) throws SchemaCreationException;

    /**
     * Validate the given string against the schema of the given version.
     * 
     * @param xmlString
     *        string xml
     * @param version
     *        the XMLBean version to validate against
     * @return the result of the validation
     * @throws ValidatorException
     *         if something goes wrong in the validating process
     * @throws SAXException
     *         if we encounter a fatal {@link org.xml.sax.SAXParseException}
     * @throws SchemaCreationException
     *         if the schema could not be created
     */
    public XMLErrorHandler validate(final String xmlString, final String version) throws ValidatorException, SAXException, SchemaCreationException
    {
        final InputStream xmlStream = new ByteArrayInputStream(xmlString.getBytes());
        return validate(xmlStream, version);
    }

    /**
     * Validate the given file against the schema of the given version.
     * 
     * @param file
     *        file containing XMLBean
     * @param version
     *        the XMLBean version to validate against
     * @return the result of the validation
     * @throws ValidatorException
     *         if something goes wrong in the validating process
     * @throws SAXException
     *         if we encounter a fatal {@link org.xml.sax.SAXParseException}
     * @throws SchemaCreationException
     *         if the schema could not be created
     */
    public XMLErrorHandler validate(final File file, final String version) throws ValidatorException, SAXException, SchemaCreationException
    {
        final Schema schemaGrammar = getSchema(version);
        final StreamSource xmlSource = new StreamSource(file);
        return validate(schemaGrammar, xmlSource);
    }

    /**
     * Validate the given xmlStream against the schema of the given version. The InputStream is closed
     * after usage.
     * 
     * @param xmlStream
     *        stream of xml
     * @param version
     *        the XMLBean version to validate against
     * @return the result of the validation
     * @throws ValidatorException
     *         if something goes wrong in the validating process
     * @throws SAXException
     *         if we encounter a fatal {@link org.xml.sax.SAXParseException}
     * @throws SchemaCreationException
     *         if the schema could not be created
     */
    public XMLErrorHandler validate(final InputStream xmlStream, final String version) throws ValidatorException, SAXException, SchemaCreationException
    {
        XMLErrorHandler handler = null;
        try
        {
            final Schema schemaGrammar = getSchema(version);
            final SAXSource xmlSource = new SAXSource(new InputSource(xmlStream));
            handler = validate(schemaGrammar, xmlSource);
        }
        finally
        {
            closeStream(xmlStream);
        }
        return handler;
    }

    /**
     * Validate the given xmlStream against the schema of the given version.
     * 
     * @param xmlSource
     *        xml source
     * @param version
     *        the XMLBean version to validate against
     * @return the result of the validation
     * @throws SAXException
     *         if we encounter a fatal {@link org.xml.sax.SAXParseException}
     * @throws ValidatorException
     *         if something goes wrong in the validating process
     * @throws SchemaCreationException
     *         if the schema could not be created
     */
    public XMLErrorHandler validate(final Source xmlSource, final String version) throws SAXException, ValidatorException, SchemaCreationException
    {
        final Schema schemaGrammar = getSchema(version);
        return validate(schemaGrammar, xmlSource);
    }

    /**
     * Validate the given string against the schema of the given version and report to the given handler.
     * 
     * @param handler
     *        receives notification of {@link org.xml.sax.SAXParseException}s
     * @param xmlString
     *        serialized XMLBean
     * @param version
     *        the version of XMLBean schema to validate against
     * @throws ValidatorException
     *         if something goes wrong in the validating process
     * @throws SAXException
     *         if we encounter a fatal {@link org.xml.sax.SAXParseException}
     * @throws SchemaCreationException
     *         if the schema could not be created
     */
    public void validate(final ErrorHandler handler, final String xmlString, final String version) throws ValidatorException, SAXException,
            SchemaCreationException
    {
        final InputStream xmlStream = new ByteArrayInputStream(xmlString.getBytes());
        validate(handler, xmlStream, version);
    }

    /**
     * Validate the given file against the schema of the given version and report to the given handler.
     * 
     * @param handler
     *        receives notification of {@link org.xml.sax.SAXParseException}s
     * @param file
     *        serialized XMLBean
     * @param version
     *        the version of XMLBean schema to validate against
     * @throws ValidatorException
     *         if something goes wrong in the validating process
     * @throws SAXException
     *         if we encounter a fatal {@link org.xml.sax.SAXParseException}
     * @throws SchemaCreationException
     *         if the schema could not be created
     */
    public void validate(final ErrorHandler handler, final File file, final String version) throws ValidatorException, SAXException, SchemaCreationException
    {
        final Schema schemaGrammar = getSchema(version);
        final StreamSource xmlSource = new StreamSource(file);
        try
        {
            XMLValidator.validate(handler, xmlSource, schemaGrammar);
        }
        catch (final IOException e)
        {
            throw new ValidatorException(e);
        }

    }

    /**
     * Validate the given xmlStream against the schema of the given version and report to the given
     * handler. The InputStream is closed after usage.
     * 
     * @param handler
     *        receives notification of {@link org.xml.sax.SAXParseException}s
     * @param xmlStream
     *        stream of xml
     * @param version
     *        the version of the schema to validate against
     * @throws ValidatorException
     *         if something goes wrong in the validating process
     * @throws SAXException
     *         if we encounter a fatal {@link org.xml.sax.SAXParseException}
     * @throws SchemaCreationException
     *         if the schema could not be created
     */
    public void validate(final ErrorHandler handler, final InputStream xmlStream, final String version) throws ValidatorException, SAXException,
            SchemaCreationException
    {
        try
        {
            final Schema schema = getSchema(version);
            final StreamSource xmlSource = new StreamSource(xmlStream);
            XMLValidator.validate(handler, xmlSource, schema);
        }
        catch (final IOException e)
        {
            throw new ValidatorException(e);
        }
        finally
        {
            closeStream(xmlStream);
        }
    }

    /**
     * Get the schema for the given version. A {@link Schema} is an immutable in-memory representation of
     * grammar. A Schema object is thread safe and applications are encouraged to share it across many
     * parsers in many threads.
     * 
     * @param version
     *        the version of schema grammar to get
     * @return the schema for the given version
     * @throws SchemaCreationException
     *         if something goes wrong in the creation of schema, but most notably if the given schema
     *         version is unknown
     */
    public Schema getSchema(final String version) throws SchemaCreationException
    {
        String urlString;
        synchronized (versionMap)
        {
            urlString = versionMap.get(version);
        }

        if (urlString == null)
        {
            return getSchemaWithURL(version);
        }
        else
        {
            final Schema schema = SchemaCache.getSchema(urlString);
            return schema;
        }
    }

    private Schema getSchemaWithURL(final String version) throws SchemaCreationException
    {
        final URL url = getSchemaURL(version);
        Schema schema = null;
        schema = SchemaCache.getSchema(url);
        final String urlString = url.toString();
        synchronized (versionMap)
        {
            versionMap.put(version, urlString);
            logger.debug("Created reference to version of xsd. version=" + version + " url=" + urlString);
        }
        return schema;
    }

    private static XMLErrorHandler validate(final Schema schemaGrammar, final Source xmlSource) throws SAXException, ValidatorException
    {
        XMLErrorHandler result = null;
        try
        {
            result = XMLValidator.validate(xmlSource, schemaGrammar);
        }
        catch (final IOException e)
        {
            throw new ValidatorException(e);
        }
        return result;
    }

    private void closeStream(final InputStream xmlStream) throws ValidatorException
    {
        try
        {
            xmlStream.close();
        }
        catch (final IOException e)
        {
            throw new ValidatorException(e);
        }
    }

}
