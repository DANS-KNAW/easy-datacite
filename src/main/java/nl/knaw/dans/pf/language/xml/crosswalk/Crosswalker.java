package nl.knaw.dans.pf.language.xml.crosswalk;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import nl.knaw.dans.pf.language.xml.exc.SchemaCreationException;
import nl.knaw.dans.pf.language.xml.exc.ValidatorException;
import nl.knaw.dans.pf.language.xml.validation.AbstractValidator;
import nl.knaw.dans.pf.language.xml.validation.XMLErrorHandler;
import nl.knaw.dans.pf.language.xml.validation.XMLErrorHandler.Reporter;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.XMLReader;

public class Crosswalker<T, V extends AbstractValidator>
{
    private static final String VALIDATE_ERROR_MESSAGE = "could not validate against XSD: ";

    /** lazy singleton instance */
    protected static SAXParser parser;

    /** The log level configured for the error handler of new instances. */
    protected static Reporter reporter = Reporter.off;

    /** The collector of validating errors and warnings of this instance. */
    private final XMLErrorHandler errorHandler;

    private V validator;

    private CrosswalkHandlerMap<T> handlerMap;

    /** Creates an instance. */
    public Crosswalker(V validator, CrosswalkHandlerMap<T> handlerMap)
    {
        this.validator = validator;
        this.handlerMap = handlerMap;
        errorHandler = new XMLErrorHandler(reporter);
    }

    /**
     * Creates an object after validation against an XSD.
     * 
     * @param file
     *        with XML content
     * @return null in case of errors reported by {@link #getXmlErrorHandler()}
     * @throws CrosswalkException
     */
    final protected T walk(final File file, T target) throws CrosswalkException
    {
        try
        {
            validateAgainstXsd(new FileInputStream(file));
            return parse(new FileInputStream(file), target);
        }
        catch (final FileNotFoundException e)
        {
            throw new CrosswalkException(VALIDATE_ERROR_MESSAGE + e.getMessage(), e);
        }
    }

    /**
     * Fills the target after validation against an XSD.
     * 
     * @param xml
     *        the XML content
     * @param target
     *        receives values from the XML
     * @return null in case of errors reported by {@link #getXmlErrorHandler()}
     * @throws CrosswalkException
     */
    final protected T walk(final String xml, T target) throws CrosswalkException
    {
        final byte[] bytes = xml.getBytes();
        validateAgainstXsd(new ByteArrayInputStream(bytes));
        return parse(new ByteArrayInputStream(bytes), target);
    }

    /**
     * Configures the logging level of the {@link XMLErrorHandler} for new instances. The method is not
     * static to allow configuration by spring. The default level is off.
     */
    public void setReporter(final Reporter myReporter)
    {
        reporter = myReporter;
    }

    /**
     * @return The handler of notifications. The log level depends on the reporter configured at
     *         instantiation of the {@link Crosswalker}. The default level is off.
     */
    public XMLErrorHandler getXmlErrorHandler()
    {
        return errorHandler;
    }

    private void validateAgainstXsd(final InputStream xml) throws CrosswalkException
    {
        try
        {
            validator.validate(errorHandler, xml, null);
        }
        catch (final ValidatorException e)
        {
            throw new CrosswalkException(VALIDATE_ERROR_MESSAGE + e.getMessage(), e);
        }
        catch (final SchemaCreationException e)
        {
            throw new CrosswalkException(VALIDATE_ERROR_MESSAGE + e.getMessage(), e);
        }
        catch (final SAXException e)
        {
            throw new CrosswalkException(VALIDATE_ERROR_MESSAGE + e.getMessage(), e);
        }
    }

    private T parse(final InputStream source, T target) throws CrosswalkException
    {
        final XMLReader reader = getReader();
        reader.setErrorHandler(errorHandler);

        // sets itself as ContentHandler of the reader passed into it
        new CrosswalkHandler<T>(target, reader, handlerMap);

        final String msg = "could not parse: ";
        try
        {
            reader.parse(new InputSource(source));
        }
        catch (final IOException e)
        {
            throw new CrosswalkException(msg + e.getMessage(), e);
        }
        catch (final SAXException e)
        {
            throw new CrosswalkException(msg + e.getMessage(), e);
        }
        if (errorHandler.getErrors().size() == 0 && errorHandler.getFatalErrors().size() == 0)
            return target;
        return null;
    }

    private XMLReader getReader() throws CrosswalkException
    {
        final SAXParser parser = getParser();
        try
        {
            return parser.getXMLReader();
        }
        catch (final SAXException e)
        {
            throw new CrosswalkException("could not get reader from parser: " + e.getMessage(), e);
        }
    }

    private SAXParser getParser() throws CrosswalkException
    {
        if (parser == null)
            parser = createParser();
        return parser;
    }

    private SAXParser createParser() throws CrosswalkException
    {
        final String msg = "could not create parser: ";
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        try
        {
            return factory.newSAXParser();
        }
        catch (final SAXNotRecognizedException e)
        {
            throw new CrosswalkException(msg + e.getMessage(), e);
        }
        catch (final ParserConfigurationException e)
        {
            throw new CrosswalkException(msg + e.getMessage(), e);
        }
        catch (final SAXException e)
        {
            throw new CrosswalkException(msg + e.getMessage(), e);
        }
    }
}
