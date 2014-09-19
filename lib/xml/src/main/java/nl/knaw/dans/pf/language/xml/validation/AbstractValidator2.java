package nl.knaw.dans.pf.language.xml.validation;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.io.IOUtils;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import nl.knaw.dans.pf.language.xml.exc.SchemaCreationException;
import nl.knaw.dans.pf.language.xml.exc.XMLException;

/**
 * Abstract validator capable of handling multiple schema sources.
 * 
 * @author henk van den berg
 */
public abstract class AbstractValidator2 {

    private final String mainLocation;
    private final String[] schemaLocations;

    protected AbstractValidator2(String... schemaLocations) {
        this.mainLocation = schemaLocations[0];
        this.schemaLocations = schemaLocations;
    }

    public XMLErrorHandler validate(final String xmlString) throws XMLException {
        Source xmlSource = new StreamSource(new ByteArrayInputStream(xmlString.getBytes()));
        return validate(xmlSource);
    }

    public void validate(ErrorHandler handler, String xmlString) throws XMLException {
        validate(handler, new StreamSource(new ByteArrayInputStream(xmlString.getBytes())));
    }

    public XMLErrorHandler validate(final File file) throws XMLException {
        Source xmlSource = new StreamSource(file);
        return validate(xmlSource);
    }

    public void validate(ErrorHandler handler, File file) throws XMLException {
        validate(handler, new StreamSource(file));
    }

    public XMLErrorHandler validate(final InputStream xmlStream) throws XMLException {
        try {
            Source xmlSource = new StreamSource(xmlStream);
            return validate(xmlSource);
        }
        finally {
            IOUtils.closeQuietly(xmlStream);
        }
    }

    public void validate(ErrorHandler handler, InputStream xmlStream) throws XMLException {
        try {
            Source xmlSource = new StreamSource(xmlStream);
            validate(handler, xmlSource);
        }
        finally {
            IOUtils.closeQuietly(xmlStream);
        }
    }

    public XMLErrorHandler validate(Source xmlSource) throws XMLException {
        XMLErrorHandler handler = new XMLErrorHandler();
        validate(handler, xmlSource);
        return handler;
    }

    public void validate(final ErrorHandler handler, final Source xmlSource) throws XMLException {
        final Validator schemaValidator = getSchema().newValidator();
        schemaValidator.setErrorHandler(handler);
        try {
            schemaValidator.validate(xmlSource);
        }
        catch (SAXException e) {
            throw new XMLException(e);
        }
        catch (IOException e) {
            throw new XMLException(e);
        }
    }

    private Schema getSchema() throws XMLException {
        Schema schema;
        try {
            schema = SchemaCache.getSchema(mainLocation, false);
            if (schema == null) {
                schema = initializeCache();
            }
        }
        catch (SchemaCreationException e) {
            throw new XMLException(e);
        }
        catch (IOException e) {
            throw new XMLException(e);
        }
        return schema;
    }

    private Schema initializeCache() throws XMLException, IOException {
        Schema schema;
        Source[] sources = new Source[schemaLocations.length];
        InputStream[] inputStreams = new InputStream[schemaLocations.length];
        try {
            for (int i = 0; i < schemaLocations.length; i++) {
                inputStreams[i] = new URL(schemaLocations[i]).openStream();
                sources[i] = new StreamSource(inputStreams[i]);
            }
        }
        catch (MalformedURLException e) {
            throw new XMLException(e);
        }
        catch (IOException e) {
            throw new XMLException(e);
        }
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            schema = schemaFactory.newSchema(sources);
            SchemaCache.putSchema(mainLocation, schema);
        }
        catch (SAXException e) {
            throw new XMLException(e);
        }
        finally {
            for (InputStream in : inputStreams) {
                IOUtils.closeQuietly(in);
            }
        }
        return schema;
    }

}
