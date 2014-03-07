package nl.knaw.dans.common.jibx.bean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import nl.knaw.dans.common.lang.ResourceNotFoundException;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.common.lang.xml.DublinCoreMetadataValidator;
import nl.knaw.dans.common.lang.xml.SchemaCreationException;
import nl.knaw.dans.common.lang.xml.ValidatorException;
import nl.knaw.dans.common.lang.xml.XMLErrorHandler;
import nl.knaw.dans.common.lang.xml.XMLException;

import org.junit.Test;
import org.xml.sax.SAXException;

public class DublinCoreMetadataValidatorOnlineTest
{

    @Test
    public void validateBeanEmpty() throws XMLException, SAXException, SchemaCreationException
    {
        DublinCoreMetadata dc = new JiBXDublinCoreMetadata();
        XMLErrorHandler handler = DublinCoreMetadataValidator.instance().validate(dc);
        assertTrue(handler.passed());
    }

    @Test
    public void validateBeanFull() throws XMLException, SAXException, SchemaCreationException
    {
        DublinCoreMetadata dc = JiBXDublinCoreMetadataTest.createFull();
        XMLErrorHandler handler = DublinCoreMetadataValidator.instance().validate(dc);
        assertTrue(handler.passed());
    }

    @Test
    public void validateString() throws ValidatorException, SAXException, SchemaCreationException
    {
        String dc = "<oai_dc:dc xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\"/>";
        XMLErrorHandler handler = DublinCoreMetadataValidator.instance().validate(dc, null);
        assertTrue(handler.passed());
    }

    @Test
    public void validateFile() throws ValidatorException, SAXException, SchemaCreationException, ResourceNotFoundException
    {
        File file = Tester.getFile("test-files/bean/oai_dc.xml");
        XMLErrorHandler handler = DublinCoreMetadataValidator.instance().validate(file, null);
        assertTrue(handler.passed());
    }

    @Test
    public void validateInvalidFile() throws ValidatorException, SAXException, SchemaCreationException, ResourceNotFoundException
    {
        File file = Tester.getFile("test-files/bean/invalid_oai_dc.xml");
        XMLErrorHandler handler = DublinCoreMetadataValidator.instance().validate(file, null);
        assertFalse(handler.passed());
    }

    @Test
    public void validateValidFile() throws ValidatorException, SAXException, SchemaCreationException, ResourceNotFoundException
    {
        File file = Tester.getFile("test-files/bean/valid_oai_dc.xml");
        XMLErrorHandler handler = DublinCoreMetadataValidator.instance().validate(file, null);
        assertTrue(handler.passed());
    }

}
