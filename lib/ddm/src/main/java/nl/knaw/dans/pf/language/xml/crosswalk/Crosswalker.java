package nl.knaw.dans.pf.language.xml.crosswalk;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import nl.knaw.dans.pf.language.xml.exc.XMLException;
import nl.knaw.dans.pf.language.xml.validation.AbstractValidator2;
import nl.knaw.dans.pf.language.xml.validation.XMLErrorHandler;
import nl.knaw.dans.pf.language.xml.validation.XMLErrorHandler.Reporter;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class Crosswalker<T> {
    private static final String VALIDATE_ERROR_MESSAGE = "could not validate against XSD: ";
    private XMLReader reader;
    private XMLErrorHandler errorHandler = new XMLErrorHandler(Reporter.off);
    public CrosswalkHandlerMap<T> handlerMap;

    /**
     * * Creates an instance.
     * 
     * @param handlerMap
     * @throws IllegalArgumentException
     *         if handlerMap is null
     */
    public Crosswalker(CrosswalkHandlerMap<T> handlerMap) throws IllegalArgumentException {
        if (handlerMap == null)
            throw new IllegalArgumentException("hanlderMap can not be null");
        this.handlerMap = handlerMap;
    }

    /**
     * Creates an object after an optional validation against an XSD.
     * 
     * @param validator
     *        optional, omit if validation against XSD is already done, or the handlers passed on to the constructor are happy with invalid XML
     * @param file
     *        with XML content
     * @param target
     *        an instance that receives values from the XML
     * @return the target unless errors are reported by the {@link XMLErrorHandler}
     * @throws CrosswalkException
     */
    final protected T walk(final AbstractValidator2 validator, final File file, T target) throws CrosswalkException, IllegalStateException {
        try {
            if (validator != null)
                validateAgainstXsd(validator, new FileInputStream(file));
            return parse(new FileInputStream(file), target);
        }
        catch (final FileNotFoundException e) {
            throw new CrosswalkException(VALIDATE_ERROR_MESSAGE + e.getMessage(), e);
        }
    }

    /**
     * Fills the target after an optional validation against an XSD.
     * 
     * @param validator
     *        optional, omit if validation against XSD is already done, or the handlers passed on to the constructor are happy with invalid XML
     * @param xml
     *        the XML content
     * @param target
     *        an instance that receives values from the XML
     * @return the target unless errors are reported by the {@link XMLErrorHandler}
     * @throws CrosswalkException
     */
    final protected T walk(final AbstractValidator2 validator, final String xml, T target) throws CrosswalkException, IllegalStateException {
        final byte[] bytes = xml.getBytes();
        if (validator != null)
            validateAgainstXsd(validator, new ByteArrayInputStream(bytes));
        return parse(new ByteArrayInputStream(bytes), target);
    }

    /**
     * The handler of notifications. Initially the log level is off. The level can be changed with <code>setReporter(Reporter)</code>. The handler collects
     * notifications of subsequent calls to the walk methods unless reset is called in between.
     * 
     * @return The handler of notifications.
     */
    public XMLErrorHandler getXmlErrorHandler() {
        return errorHandler;
    }

    /**
     * Change the logging level of the error handler.
     * 
     * @param reporter
     */
    public void setReporter(Reporter reporter) {
        errorHandler.setReporter(reporter);
    }

    private void validateAgainstXsd(final AbstractValidator2 validator, final InputStream xml) throws CrosswalkException {
        try {
            validator.validate(errorHandler, xml);
        }
        catch (XMLException e) {
            throw new CrosswalkException(VALIDATE_ERROR_MESSAGE + e.getMessage(), e);
        }
    }

    private T parse(final InputStream source, T target) throws CrosswalkException {
        getReader().setErrorHandler(errorHandler);

        // sets itself as ContentHandler of the reader passed into it
        new CrosswalkHandler<T>(target, getReader(), handlerMap);

        final String msg = "could not parse: ";
        try {
            getReader().parse(new InputSource(source));
        }
        catch (final IOException e) {
            throw new CrosswalkException(msg + e.getMessage(), e);
        }
        catch (final SAXException e) {
            throw new CrosswalkException(msg + e.getMessage(), e);
        }
        if (errorHandler.getErrors().size() == 0 && errorHandler.getFatalErrors().size() == 0)
            return target;
        return null;
    }

    private XMLReader getReader() throws CrosswalkException {
        if (reader != null)
            return reader;
        try {
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            reader = factory.newSAXParser().getXMLReader();
            return reader;
        }
        catch (final SAXException e) {
            throw new CrosswalkException("could not get reader from parser: " + e.getMessage(), e);
        }
        catch (ParserConfigurationException e) {
            throw new CrosswalkException("could not create parser" + e.getMessage(), e);
        }
    }
}
