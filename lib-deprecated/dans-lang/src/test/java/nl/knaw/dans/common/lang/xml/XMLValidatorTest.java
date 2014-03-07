package nl.knaw.dans.common.lang.xml;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import nl.knaw.dans.common.lang.ResourceNotFoundException;
import nl.knaw.dans.common.lang.test.Tester;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLValidatorTest
{

    public static final String SCHEMA_FILE = "test-files/xml/validatorTest.xsd";
    public static final String VALID_XML = "test-files/xml/validatorTest0.xml";
    public static final String INVALID_XML = "test-files/xml/validatorTest1.xml";

    @Test
    public void testValidXML() throws SAXException, IOException, ResourceNotFoundException
    {
        Source schemaSource = new StreamSource(Tester.getFile(SCHEMA_FILE));
        Source xmlSource = new StreamSource(Tester.getFile(VALID_XML));
        XMLErrorHandler result = XMLValidator.validate(xmlSource, schemaSource);
        Assert.assertTrue(result.passed());
        Assert.assertEquals(0, result.getNotificationCount());
        Assert.assertEquals(0, result.getNotifications().size());
    }

    @Test
    public void testInvalidXML() throws IOException, ResourceNotFoundException
    {
        int saxEx = 0;
        Source schemaSource = new StreamSource(Tester.getFile(SCHEMA_FILE));
        Source xmlSource = new StreamSource(Tester.getFile(INVALID_XML));
        XMLErrorHandler result = null;
        try
        {
            result = XMLValidator.validate(xmlSource, schemaSource);
            Assert.fail("expected SAXException");
        }
        catch (SAXException e)
        {
            saxEx++;
        }
        Assert.assertEquals(1, saxEx);
        Assert.assertNull(result);

    }

    @Test
    public void testValidateAndReportToHandler() throws XMLStreamException, IOException, ResourceNotFoundException
    {
        int saxEx = 0;

        Handler handler = new Handler();

        Source schemaSource = new StreamSource(Tester.getFile(SCHEMA_FILE));
        Source xmlSource = new StreamSource(Tester.getFile(INVALID_XML));
        try
        {
            XMLValidator.validate(handler, xmlSource, schemaSource);
        }
        catch (SAXException e)
        {
            saxEx++;
        }
        Assert.assertEquals(1, saxEx);
        Assert.assertEquals(1, handler.fatalErrors);
        Assert.assertEquals(5, handler.errors);
        Assert.assertEquals(0, handler.warnings);
    }

    static class Handler implements ErrorHandler
    {

        int errors;
        int fatalErrors;
        int warnings;

        public void error(SAXParseException exception) throws SAXException
        {
            errors++;
        }

        public void fatalError(SAXParseException exception) throws SAXException
        {
            fatalErrors++;
        }

        public void warning(SAXParseException exception) throws SAXException
        {
            warnings++;
        }

    }

}
