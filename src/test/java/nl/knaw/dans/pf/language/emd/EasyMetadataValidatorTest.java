package nl.knaw.dans.pf.language.emd;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.validation.Schema;

import nl.knaw.dans.common.lang.xml.SchemaCreationException;
import nl.knaw.dans.common.lang.xml.ValidatorException;
import nl.knaw.dans.common.lang.xml.XMLErrorHandler;
import nl.knaw.dans.common.lang.xml.XMLException;
import nl.knaw.dans.pf.language.emd.bean.EasyMetadataImpl;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

// ecco: CHECKSTYLE: OFF

public class EasyMetadataValidatorTest
{

    private static final String VALID_XML = "src/test/resources/xml-validator/valid-emd.xml";
    private static final String INVALID_0_XML = "src/test/resources/xml-validator/invalid-emd0.xml";


    @Test
    public void testValidate() throws XMLException, SAXException, SchemaCreationException
    {
        EasyMetadata emd = new EasyMetadataImpl(MetadataFormat.UNSPECIFIED);
        XMLErrorHandler result = EasyMetadataValidator.instance().validate(emd);
        Assert.assertTrue(result.passed());
        //
        XMLErrorHandler result2 = EasyMetadataValidator.instance().validate(emd);
        Assert.assertTrue(result2.passed());
        Assert.assertNotSame(result, result2);
    }

    @Test
    public void testValidateValidXML() throws IOException, ValidatorException, SAXException, SchemaCreationException
    {
        InputStream fis = null;
        try
        {
            fis = new FileInputStream(VALID_XML);
            XMLErrorHandler result = EasyMetadataValidator.instance().validate(fis, EasyMetadataValidator.VERSION_0_1);
            Assert.assertTrue(result.passed());
        }
        finally
        {
            if (fis != null)
            {
                fis.close();
            }
        }
    }

    @Test
    public void testValidateInvalidXML0() throws IOException, ValidatorException, SAXException, SchemaCreationException
    {

        InputStream fis = null;
        try
        {
            fis = new FileInputStream(INVALID_0_XML);

            XMLErrorHandler result = EasyMetadataValidator.instance().validate(fis, EasyMetadataValidator.VERSION_0_1);

            Assert.assertFalse(result.passed());
            Assert.assertEquals(9, result.getNotificationCount());
            Assert.assertEquals(0, result.getWarnings().size());
            Assert.assertEquals(9, result.getErrors().size());
            Assert.assertEquals(0, result.getFatalErrors().size());
        }
        finally
        {
            if (fis != null)
            {
                fis.close();
            }
        }
    }

    @Test
    public void testValidateString() throws ValidatorException, SAXException, SchemaCreationException
    {
        String xmlString = "<emd:easymetadata xmlns:emd=\"http://easy.dans.knaw.nl/easy/easymetadata/\"/>";
        XMLErrorHandler result = EasyMetadataValidator.instance().validate(xmlString, EasyMetadataValidator.VERSION_0_1);
        Assert.assertFalse(result.passed());
        //        Assert.assertEquals("cvc-complex-type.4: Attribute 'version' must appear on element 'emd:easymetadata'.",
        //                result.getErrors().get(0).getMessage());

        xmlString = "<emd:easymetadata xmlns:emd=\"http://easy.dans.knaw.nl/easy/easymetadata/\" emd:version=\"0.1\"/>";
        result = EasyMetadataValidator.instance().validate(xmlString, EasyMetadataValidator.VERSION_0_1);
        Assert.assertTrue(result.passed());
    }

    @Test
    public void testSchema() throws ValidatorException, SchemaCreationException
    {
        Schema schemaGrammer = EasyMetadataValidator.instance().getSchema(EasyMetadataValidator.VERSION_0_1);
        Schema schemaGrammer2 = EasyMetadataValidator.instance().getSchema(EasyMetadataValidator.VERSION_0_1);
        Assert.assertSame(schemaGrammer, schemaGrammer2);
        Assert.assertNotSame(schemaGrammer.newValidator(), schemaGrammer2.newValidator());
    }

}
