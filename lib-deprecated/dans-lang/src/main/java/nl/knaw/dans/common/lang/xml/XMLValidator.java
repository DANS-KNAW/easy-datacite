package nl.knaw.dans.common.lang.xml;

import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

/**
 * Utility class to validate xml against a schema. Validating will stop after a fatal parse error has been detected and
 * all validating methods will throw a {@link SAXException} on such occasions, though this behavior may depend on the
 * implementation of the javax.xml classes used by this implementation.
 *
 * @author ecco
 */
public final class XMLValidator
{

    private XMLValidator()
    {
    }

    /**
     * Validate xml against a schema.
     *
     * @param xmlSource
     *        the xml to validate
     * @param schemaSources
     *        the schema sources to validate against; should at least be one.
     * @return the result of the validation
     * @throws SAXException
     *         if the xml contains fatal errors
     * @throws IOException
     *         if an IOException occurs
     * @see XMLErrorHandler
     */
    public static XMLErrorHandler validate(final Source xmlSource, final Source... schemaSources) throws SAXException, IOException
    {
        final XMLErrorHandler result = new XMLErrorHandler();
        validate(result, xmlSource, schemaSources);
        return result;
    }

    /**
     * Validate xml against a schema and report to a log-facility.
     *
     * @param xmlSource
     *        the xml to validate
     * @param schemaGrammar
     *        the schema to validate against.
     * @return the result of the validation
     * @throws SAXException
     *         if the xml contains fatal errors
     * @throws IOException
     *         if an IOException occurs
     * @see XMLErrorHandler
     */
    public static XMLErrorHandler validate(final Source xmlSource, final Schema schemaGrammar) throws SAXException, IOException
    {
        final XMLErrorHandler result = new XMLErrorHandler();
        validate(result, xmlSource, schemaGrammar);
        return result;
    }

    /**
     * Validate xml against a schema and report to the given {@link ErrorHandler}.
     *
     * @param handler
     *        receives notification of errors and warnings
     * @param xmlSource
     *        the xml to validate
     * @param schemaSources
     *        the schema sources to validate against; should at least be one.
     * @throws SAXException
     *         if the xml contains fatal errors
     * @throws IOException
     *         if an IOException occurs
     */
    public static void validate(final ErrorHandler handler, final Source xmlSource, final Source... schemaSources) throws SAXException, IOException
    {
        final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        final Schema schemaGrammar = schemaFactory.newSchema(schemaSources);
        validate(handler, xmlSource, schemaGrammar);
    }

    /**
     * Validate xml against a schema and report to the given {@link ErrorHandler}.
     *
     * @param handler
     *        receives notification of errors and warnings
     * @param xmlSource
     *        the xml to validate
     * @param schemaGrammar
     *        the schema to validate against
     * @throws SAXException
     *         if the xml contains fatal errors
     * @throws IOException
     *         if an IOException occurs
     */
    public static void validate(final ErrorHandler handler, final Source xmlSource, final Schema schemaGrammar) throws SAXException, IOException
    {
        final Validator schemaValidator = schemaGrammar.newValidator();
        schemaValidator.setErrorHandler(handler);
        schemaValidator.validate(xmlSource);
    }

}
