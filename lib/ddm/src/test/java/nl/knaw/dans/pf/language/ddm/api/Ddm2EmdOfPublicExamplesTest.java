package nl.knaw.dans.pf.language.ddm.api;

import nl.knaw.dans.pf.language.xml.crosswalk.CrosswalkException;
import nl.knaw.dans.pf.language.xml.exc.XMLException;
import nl.knaw.dans.pf.language.xml.validation.XMLErrorHandler;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.xml.sax.SAXParseException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeTrue;

/**
 * Tests that increases the chance of maintaining the example(s) in easy-schema without making the GitHub easy-schema dependent on the legacy easy-app. Passing
 * the test does not guarantee Sword-V1 will swallow the resulting EMD, the ingest of the business layer may still reject it.
 */
public class Ddm2EmdOfPublicExamplesTest {

    // TODO parse the folder for more examples, sadly it is an HTML table

    @Test
    public void example1() throws CrosswalkException, XMLException {

        checkDDM(getUrlContent("https://easy.dans.knaw.nl/schemas/docs/examples/ddm/example1.xml")
        // future proof work around for an obsolete value
        // TODO drop work around once a correct example is released
                .replace("<ddm:audience>C20000", "<ddm:audience>D20000"));
    }

    private void checkDDM(String ddm) throws CrosswalkException, XMLException {
        DDMValidator ddmValidator = new DDMValidator();
        Ddm2EmdCrosswalk crosswalk = new Ddm2EmdCrosswalk(ddmValidator);

        crosswalk.createFrom(ddm);
        assertThat(crosswalk.getXmlErrorHandler().getErrors().size(), is(0));
        assertThat(crosswalk.getXmlErrorHandler().getFatalErrors().size(), is(0));
        List<SAXParseException> warnings = crosswalk.getXmlErrorHandler().getWarnings();
        assertThat(warnings.size(), is(5));
        assertThat(warnings.get(0).getMessage(), is("skipped dc:coverage at level:3"));
        assertThat(warnings.get(1).getMessage(), is("skipped mods:recordInfo at level:3"));
        assertThat(warnings.get(2).getMessage(), is("skipped mods:recordContentSource at level:4"));
        assertThat(warnings.get(3).getMessage(), is("skipped mods:recordCreationDate at level:4"));
        assertThat(warnings.get(4).getMessage(), is("skipped mods:recordOrigin at level:4"));

        XMLErrorHandler handler = ddmValidator.validate(ddm);
        assertThat(handler.getErrors().size(), is(0));
        assertThat(handler.getFatalErrors().size(), is(0));
        assertThat(handler.getWarnings().size(), is(0));
    }

    private String getUrlContent(String url) {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.connect();
            String ddm = IOUtils.toString(urlConnection.getInputStream());
            urlConnection.disconnect();
            return ddm;
        }
        catch (Exception e) {
            // ignore if no web access
            assumeTrue(String.format("can access %s [%s]", url, e.getClass().getSimpleName()), false);
        }
        return "";// never reached because of return and assume
    }
}
