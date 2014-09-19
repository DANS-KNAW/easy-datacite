package nl.knaw.dans.pf.language.xml.validation;

import java.io.File;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import nl.knaw.dans.pf.language.xml.validation.XMLErrorHandler;
import nl.knaw.dans.pf.language.xml.validation.XMLValidator;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLValidatorTest {

    public static final String TEST_DIR = "src/test/resources/test-files/validation/";
    public static final File SCHEMA_FILE = new File(TEST_DIR, "schema.xsd");
    public static final File VALID_XML = new File(TEST_DIR, "schema-valid.xml");
    public static final File INVALID_XML = new File(TEST_DIR, "schema-invalid.xml");

    @Test
    public void testValidXML() throws Exception {
        Source schemaSource = new StreamSource(SCHEMA_FILE);
        Source xmlSource = new StreamSource(VALID_XML);
        XMLErrorHandler result = XMLValidator.validate(xmlSource, schemaSource);
        Assert.assertTrue(result.passed());
        Assert.assertEquals(0, result.getNotificationCount());
        Assert.assertEquals(0, result.getNotifications().size());
    }

    @Test
    public void testInvalidXML() throws Exception {
        int saxEx = 0;
        Source schemaSource = new StreamSource(SCHEMA_FILE);
        Source xmlSource = new StreamSource(INVALID_XML);
        XMLErrorHandler result = null;
        try {
            result = XMLValidator.validate(xmlSource, schemaSource);
            Assert.fail("expected SAXException");
        }
        catch (SAXException e) {
            saxEx++;
        }
        Assert.assertEquals(1, saxEx);
        Assert.assertNull(result);

    }

    @Test
    public void testValidateAndReportToHandler() throws Exception {
        int saxEx = 0;

        Handler handler = new Handler();

        Source schemaSource = new StreamSource(SCHEMA_FILE);
        Source xmlSource = new StreamSource(INVALID_XML);
        try {
            XMLValidator.validate(handler, xmlSource, schemaSource);
        }
        catch (SAXException e) {
            saxEx++;
        }
        Assert.assertEquals(1, saxEx);
        Assert.assertEquals(1, handler.fatalErrors);
        Assert.assertEquals(5, handler.errors);
        Assert.assertEquals(0, handler.warnings);
    }

    static class Handler implements ErrorHandler {

        int errors;
        int fatalErrors;
        int warnings;

        public void error(SAXParseException exception) throws SAXException {
            errors++;
        }

        public void fatalError(SAXParseException exception) throws SAXException {
            fatalErrors++;
        }

        public void warning(SAXParseException exception) throws SAXException {
            warnings++;
        }

    }

}
