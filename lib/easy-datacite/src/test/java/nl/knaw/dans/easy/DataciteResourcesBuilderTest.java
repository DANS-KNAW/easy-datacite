package nl.knaw.dans.easy;

import static nl.knaw.dans.pf.language.emd.types.EmdConstants.DOI_RESOLVER;
import static nl.knaw.dans.pf.language.emd.types.EmdConstants.SCHEME_DOI;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeTrue;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EasyMetadataImpl;
import nl.knaw.dans.pf.language.emd.binding.EmdUnmarshaller;
import nl.knaw.dans.pf.language.emd.types.BasicIdentifier;

public class DataciteResourcesBuilderTest {
    private static final String XSL_EMD2DATACITE = new DataciteServiceConfiguration().getXslEmd2datacite();
    private static final String DATACITE_SCHEMA_LOCATION = "http://schema.datacite.org/meta/kernel-3/metadata.xsd";
    private static final String XP_GEOLOCATION = "//*[local-name()='geoLocations']/*[local-name()='geoLocation']";
    private static final String XP_GEOLOCATION_POINT = XP_GEOLOCATION + "/*[local-name()='geoLocationPoint']";
    private static final String XP_GEOLOCATION_BOX = XP_GEOLOCATION + "/*[local-name()='geoLocationBox']";
    private static final Logger logger = LoggerFactory.getLogger(DataciteResourcesBuilderTest.class);
    private Document document;
    private XPath xPath;

    @Before
    public void setup() throws Exception {

        EasyMetadata emd = new EmdBuilder().build();

        String dataciteXml = createDefaultBuilder().getEmd2DataciteXml(emd);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        document = db.parse(new InputSource(new StringReader(dataciteXml)));

        XPathFactory xpf = XPathFactory.newInstance();
        xPath = xpf.newXPath();
    }

    @Test
    public void invalidXsl() throws Exception {
        // covers just one of the exceptions thrown by the private method createDoiData
        EasyMetadata emd = new EmdBuilder().build();
        try {
            new DataciteResourcesBuilder("empty.xsl", getResolver()).create(emd);
        }
        catch (DataciteServiceException e) {
            assertThat(e.getMessage(), containsString(emd.getEmdIdentifier().getDansManagedDoi()));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void noEmdA() throws Exception {
        createDefaultBuilder().create();
    }

    @Test(expected = IllegalArgumentException.class)
    public void noEmdB() throws Exception {
        EasyMetadata[] emds = {};
        createDefaultBuilder().create(emds);
    }

    @Test(expected = IllegalArgumentException.class)
    public void noEmdC() throws Exception {
        EasyMetadata[] emds = null;
        createDefaultBuilder().create(emds);
    }

    @Test(expected = IllegalStateException.class)
    public void classpath() throws Exception {
        new DataciteResourcesBuilder("notFound.xsl", getResolver()).create();
    }

    @Test(expected = IllegalArgumentException.class)
    public void noDoi() throws Exception {
        EasyMetadata emd = new EmdBuilder().replaceAll("10.5072/dans-test-123", "\t").build();
        assertThat(emd.getEmdIdentifier().getDansManagedDoi(), equalTo("\t"));

        createDefaultBuilder().create(emd);
    }

    @Test
    public void once() throws Exception {

        EasyMetadata emd = new EmdBuilder().build();

        String out = createDefaultBuilder().create(emd);

        assertThat(out, containsString(emd.getEmdIdentifier().getDansManagedDoi()));
        assertThat(out, containsString(emd.getPreferredTitle()));
        logger.debug(out);
    }

    @Test
    public void twice() throws Exception {

        EasyMetadata emd1 = new EmdBuilder().build();
        EasyMetadata emd2 = new EmdBuilder().replaceAll("dans-test-123", "dans-test-456").build();

        String out = createDefaultBuilder().create(emd1, emd2);

        assertThat(out, containsString(emd1.getEmdIdentifier().getDansManagedDoi()));
        assertThat(out, containsString(emd2.getEmdIdentifier().getDansManagedDoi()));
        // further proof of the pudding is eating it: sending it to datacite
        logger.debug(out);
    }

    @Ignore(value = "TUDelft doesn't support Datacite v4.")
    @Test
    public void creatorAffiliation() throws Exception {

        String affiliation = xPath.evaluate("//*[local-name()='creators']/*[local-name()='creator']/*[local-name()='affiliation']/text()",
                document.getDocumentElement());

        assertEquals("International Atomic Energy Agency", affiliation);
    }

    @Ignore(value = "TUDelft doesn't support Datacite v4.")
    @Test
    public void contributorAffiliation() throws Exception {

        String affiliation = xPath.evaluate(
                "//*[local-name()='contributors']/*[local-name()='contributor'][@contributorType=\"Other\"]/*[local-name()='affiliation']/text()",
                document.getDocumentElement());

        assertEquals("CERN", affiliation);
    }

    @Ignore(value = "TUDelft doesn't support Datacite v4.")
    @Test
    public void geoLocationPoint() throws Exception {

        String pointLatitude = xPath.evaluate(XP_GEOLOCATION_POINT + "/*[local-name()='pointLatitude']/text()", document.getDocumentElement());
        String pointLongitude = xPath.evaluate(XP_GEOLOCATION_POINT + "/*[local-name()='pointLongitude']/text()", document.getDocumentElement());

        assertEquals("53.24478539", pointLatitude);
        assertEquals("5.63994851", pointLongitude);
    }

    @Ignore(value = "TUDelft doesn't support Datacite v4.")
    @Test
    public void geoLocationBox() throws Exception {

        String northBoundLatitude = xPath.evaluate(XP_GEOLOCATION_BOX + "/*[local-name()='northBoundLatitude']/text()", document.getDocumentElement());
        String eastBoundLongitude = xPath.evaluate(XP_GEOLOCATION_BOX + "/*[local-name()='eastBoundLongitude']/text()", document.getDocumentElement());
        String southBoundLatitude = xPath.evaluate(XP_GEOLOCATION_BOX + "/*[local-name()='southBoundLatitude']/text()", document.getDocumentElement());
        String westBoundLongitude = xPath.evaluate(XP_GEOLOCATION_BOX + "/*[local-name()='westBoundLongitude']/text()", document.getDocumentElement());

        assertEquals("51.22264603", northBoundLatitude);
        assertEquals("5.97521691", eastBoundLongitude);
        assertEquals("51.1741085", southBoundLatitude);
        assertEquals("5.90869255", westBoundLongitude);
    }

    @Test
    public void validateXmlOutputAgainstDataciteSchema() throws Exception {
        externalSchemaCheck();

        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(new URL(DATACITE_SCHEMA_LOCATION));
        Validator validator = schema.newValidator();

        try {
            validator.validate(new DOMSource(document));
        }
        catch (SAXException e) {
            Assert.fail("Not valid xml document, msg: " + e.getMessage());
        }
    }

    @Ignore(value = "used as a sample")
    @Test
    public void teasyEmds() throws Exception {
        int[] failing = {5, 48, 82};
        for (int i : failing) {
            // for (int i = 1000; i < 2000; i++) {
            try {
                EasyMetadata emd = readTeasyEmd(i);
                if (StringUtils.isNotBlank(emd.getEmdIdentifier().getPersistentIdentifier()))
                    try {
                        new DataciteResourcesBuilder(XSL_EMD2DATACITE, getResolver()).create(emd);
                    }
                    catch (DataciteServiceException e) {
                        System.err.println(emd.getEmdIdentifier().getDatasetId() + " " + emd.getEmdIdentifier().getPersistentIdentifier());
                        Assert.assertThat(e.getMessage(), containsString("transformation failed"));
                        Assert.assertThat(e.getMessage(), containsString("An empty sequence"));
                        Assert.assertThat(e.getMessage(), containsString("dates"));
                    }
            }
            catch (FileNotFoundException e) {
                // no dataset, skip
            }
        }
        verifyAll();
    }

    private EasyMetadata readTeasyEmd(int i) throws Exception {
        InputStream inputStream = new URL("http://teasy.dans.knaw.nl:8080/fedora/get/easy-dataset:" + i + "/EMD").openStream();
        try {
            String xml = IOUtils.toString(inputStream, "UTF-8");
            EasyMetadata emd = new EmdUnmarshaller<EasyMetadata>(EasyMetadataImpl.class).unmarshal(xml);
            if (StringUtils.isBlank(emd.getEmdIdentifier().getDansManagedDoi()))
                emd.getEmdIdentifier().add(createDOI());
            return emd;
        }
        finally {
            inputStream.close();
        }
    }

    private BasicIdentifier createDOI() {
        BasicIdentifier bi = new BasicIdentifier("10.5072/dans-test-123");
        bi.setIdentificationSystem(URI.create(DOI_RESOLVER));
        bi.setScheme(SCHEME_DOI);
        return bi;
    }

    private DataciteResourcesBuilder createDefaultBuilder() throws MalformedURLException {
        return new DataciteResourcesBuilder(XSL_EMD2DATACITE, getResolver());
    }

    private URL getResolver() throws MalformedURLException {
        return new URL("http://some.domain/and/path");
    }

    private void externalSchemaCheck() {
        // ignore test case if not available
        assumeTrue("can access " + DATACITE_SCHEMA_LOCATION, canConnect(DATACITE_SCHEMA_LOCATION));
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

    public String getStringFromDoc(Document doc) throws TransformerException {

        DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(domSource, result);
        writer.flush();
        return writer.toString();
    }
}
