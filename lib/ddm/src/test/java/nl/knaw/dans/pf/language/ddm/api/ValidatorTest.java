package nl.knaw.dans.pf.language.ddm.api;

import java.io.File;
import java.net.URL;

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
        Source s1 = new StreamSource(new URL("http://easy.dans.knaw.nl/schemas/md/2012/11/ddm.xsd").openStream());
        Source s2 = new StreamSource(new URL("http://easy.dans.knaw.nl/schemas/dcx/2012/10/dcx-gml.xsd").openStream());

        XMLErrorHandler handler = XMLValidator.validate(xmlSource, s1, s2);
        System.err.println(handler.getMessages());
        System.err.println(handler.passed());
    }
}
