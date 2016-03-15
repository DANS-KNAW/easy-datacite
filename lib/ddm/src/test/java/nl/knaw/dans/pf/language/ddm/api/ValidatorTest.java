package nl.knaw.dans.pf.language.ddm.api;

import static nl.knaw.dans.pf.language.ddm.api.SpecialValidator.RECENT_SCHEMAS;
import static nl.knaw.dans.pf.language.ddm.handlermaps.NameSpace.DC;
import static nl.knaw.dans.pf.language.ddm.handlermaps.NameSpace.DDM;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import nl.knaw.dans.pf.language.ddm.handlermaps.NameSpace;
import nl.knaw.dans.pf.language.xml.validation.XMLErrorHandler;

import org.junit.BeforeClass;
import org.junit.Test;

public class ValidatorTest {

    @BeforeClass
    public static void externalSchemaCheck() {
        // ignore test case if not available
        assumeTrue("can access " + DC.xsd, canConnect(DC.xsd));
    }

    @Test
    public void testValidation() throws Exception {
        Source xmlSource = new StreamSource(new File("src/test/resources/input/spatial.xml"));
        XMLErrorHandler handler = new SpecialValidator().validate(xmlSource);
        System.err.println(handler.getMessages());
        System.err.println(handler.passed());
    }

    @Test
    public void testDDMValidation() throws Exception {
        assumeTrue("last DDM is published", DDM.equals(RECENT_SCHEMAS.get(new File(DDM.xsd).getName())));
        XMLErrorHandler handler = new DDMValidator().validate(new File("src/test/resources/input/ddm.xml"));
        assertTrue(handler.passed());
    }

    @Test
    public void testDDMValidationWithDOI() throws Exception {
        assumeTrue("last DDM is published", DDM.equals(RECENT_SCHEMAS.get(new File(DDM.xsd).getName())));
        XMLErrorHandler handler = new DDMValidator().validate(new File("src/test/resources/input/ddm-with-doi.xml"));
        assertTrue(handler.passed());
    }

    private static boolean canConnect(String url) {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.connect();
            urlConnection.disconnect();
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }
}
