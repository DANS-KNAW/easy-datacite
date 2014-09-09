package nl.knaw.dans.pf.language.ddm.api;

import java.io.File;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import nl.knaw.dans.pf.language.xml.validation.XMLErrorHandler;
import nl.knaw.dans.pf.language.xml.validation.XMLValidator;

import org.junit.Test;

public class ValidatorTest
{

    @Test
    public void testValidation() throws Exception
    {
        // XMLErrorHandler handler = DDMValidator.instance().validate(
        // new File("src/test/resources/input/spatial.xml"), "bla");
        // System.err.println(handler.getMessages());
        // Stupid Xerces is not loading all the schemas. Not same but related to
        // https://issues.apache.org/jira/browse/XERCESJ-1130

        // Explicitly loading both schemas is working...

        Source xmlSource = new StreamSource(new File("src/test/resources/input/spatial.xml"));
        // Note that online schemas are under http://easy.dans.knaw.nl/schemas
        Source s1 = new StreamSource(new File(OfflineDDMValidator.LOCAL_SCHEMA_DIR + OfflineDDMValidator.DDM_xsd));
        Source s2 = new StreamSource(new File(OfflineDDMValidator.LOCAL_SCHEMA_DIR + OfflineDDMValidator.DCX_GML_xsd));

        XMLErrorHandler handler = XMLValidator.validate(xmlSource, s1, s2);
        System.err.println(handler.getMessages());
        System.err.println(handler.passed());
    }
}
