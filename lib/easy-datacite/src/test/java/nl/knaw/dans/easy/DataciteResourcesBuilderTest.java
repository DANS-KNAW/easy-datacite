package nl.knaw.dans.easy;

import static nl.knaw.dans.pf.language.emd.types.EmdConstants.DOI_RESOLVER;
import static nl.knaw.dans.pf.language.emd.types.EmdConstants.SCHEME_DOI;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;
import static org.junit.Assume.assumeTrue;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
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
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EasyMetadataImpl;
import nl.knaw.dans.pf.language.emd.binding.EmdUnmarshaller;
import nl.knaw.dans.pf.language.emd.types.BasicIdentifier;

public class DataciteResourcesBuilderTest {

    private static final DataciteServiceConfiguration dsConfig = new DataciteServiceConfiguration();

    private static final String version = "3";

    private static final String XSL_EMD2DATACITE = String.format("xslt-files/EMD_doi_datacite_v%s.xsl", version);
    private static final String DATACITE_SCHEMA_LOCATION = String.format("http://schema.datacite.org/meta/kernel-%s/metadata.xsd", version);
    static {
        dsConfig.setXslEmd2datacite(dsConfig.getXslEmd2datacite());
    }
    private static final String CREATOR = "//*[local-name()='creators']/*[local-name()='creator']";
    private static final String CONTRIBUTOR = "//*[local-name()='contributors']/*[local-name()='contributor']";
    private static final String XP_GEOLOCATION = "//*[local-name()='geoLocations']/*[local-name()='geoLocation']";
    private static final String XP_GEOLOCATION_POINT = XP_GEOLOCATION + "/*[local-name()='geoLocationPoint']";
    private static final String XP_GEOLOCATION_BOX = XP_GEOLOCATION + "/*[local-name()='geoLocationBox']";

    private static final Logger logger = LoggerFactory.getLogger(DataciteResourcesBuilderTest.class);
    private Document document;
    private Element docElement;
    private Element maxiDocElement;
    private XPath xPath;

    private void ignoreIfNot(String version) {
        assumeThat(DATACITE_SCHEMA_LOCATION, containsString(version));
    }

    @Before
    public void setup() throws Exception {

        document = getDocument(new EmdBuilder("emd.xml").build());
        docElement = document.getDocumentElement();
        maxiDocElement = getDocument(new EmdBuilder("maxi-emd.xml").build()).getDocumentElement();
        xPath = XPathFactory.newInstance().newXPath();
    }

    private Document getDocument(EasyMetadata emd) throws DataciteServiceException, ParserConfigurationException, SAXException, IOException {
        String dataciteXml = createDefaultBuilder().getEmd2DataciteXml(emd);
        InputSource source = new InputSource(new StringReader(dataciteXml));

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        return dbf.newDocumentBuilder().parse(source);
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
        // noinspection ConstantConditions
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

    @Test
    public void creatorAffiliation() throws Exception {
        ignoreIfNot("kernel-4");

        String expression = CREATOR + "/*[local-name()='affiliation']/text()";

        assertEquals("International Atomic Energy Agency", xPath.evaluate(expression, docElement));
    }

    @Test
    public void simpleCreator() throws Exception {

        NodeList creators = (NodeList) xPath.evaluate(CREATOR, maxiDocElement, XPathConstants.NODESET);
        assertEquals(4, creators.getLength());
        assertEquals("creator 0", creators.item(2).getTextContent().trim());
        assertEquals("creator 1", creators.item(3).getTextContent().trim());
    }

    @Test
    public void simpleContributor() throws Exception {

        NodeList creators = (NodeList) xPath.evaluate(CONTRIBUTOR, maxiDocElement, XPathConstants.NODESET);
        assertEquals(6, creators.getLength());
        assertEquals("contributor 0", creators.item(2).getTextContent().trim());
        assertEquals("contributor 1", creators.item(3).getTextContent().trim());
        // FIXME
        assertEquals("rights 0", creators.item(4).getTextContent().trim());
        assertEquals("rights 1", creators.item(5).getTextContent().trim());
    }

    @Test
    public void contributorAffiliation() throws Exception {
        ignoreIfNot("kernel-4");

        String affiliation = CONTRIBUTOR + "[@contributorType=\"Other\"]/*[local-name()='affiliation']/text()";

        assertEquals("CERN", xPath.evaluate(affiliation, docElement));
    }

    @Test
    public void geoLocationPoint() throws Exception {
        ignoreIfNot("kernel-4");

        String latitude = XP_GEOLOCATION_POINT + "/*[local-name()='pointLatitude']/text()";
        String longitude = XP_GEOLOCATION_POINT + "/*[local-name()='pointLongitude']/text()";

        assertEquals("53.24478539", xPath.evaluate(latitude, docElement));
        assertEquals("5.63994851", xPath.evaluate(longitude, docElement));
    }

    @Test
    public void geoLocationBox() throws Exception {
        ignoreIfNot("kernel-4");

        String north = XP_GEOLOCATION_BOX + "/*[local-name()='northBoundLatitude']/text()";
        String east = XP_GEOLOCATION_BOX + "/*[local-name()='eastBoundLongitude']/text()";
        String south = XP_GEOLOCATION_BOX + "/*[local-name()='southBoundLatitude']/text()";
        String west = XP_GEOLOCATION_BOX + "/*[local-name()='westBoundLongitude']/text()";

        assertEquals("51.22264603", xPath.evaluate(north, docElement));
        assertEquals("5.97521691", xPath.evaluate(east, docElement));
        assertEquals("51.1741085", xPath.evaluate(south, docElement));
        assertEquals("5.90869255", xPath.evaluate(west, docElement));
    }

    @Test
    public void subject() throws Exception {
        ignoreIfNot("kernel-4");

        String subjectXPath = "//*[local-name()='subject'][@subjectScheme='NARCIS-classification']";
        Element subjectElement = (Element) xPath.evaluate(subjectXPath, docElement, XPathConstants.NODE);

        assertEquals(subjectElement.getTextContent(), "Archaeology");
        assertEquals(subjectElement.getAttribute("schemeURI"), "http://www.narcis.nl/classification");
        assertEquals(subjectElement.getAttribute("valueURI"), "http://www.narcis.nl/classfication/D37000");
        assertEquals(subjectElement.getAttribute("xml:lang"), "en");
    }

    @Test
    public void subjectAbrComplex() throws Exception {
        ignoreIfNot("kernel-4");

        String subjectXPath = "//*[local-name()='subject'][@subjectScheme='ABR-complex']";
        Element subjectElement = (Element) xPath.evaluate(subjectXPath, docElement, XPathConstants.NODE);

        assertEquals(subjectElement.getTextContent(), "Begraving, onbepaald (GX)");
        assertEquals(subjectElement.getAttribute("schemeURI"), "http://cultureelerfgoed.nl/");
        assertEquals(subjectElement.getAttribute("xml:lang"), "nl");
    }

    @Test
    public void subjectAbrPeriode() throws Exception {
        ignoreIfNot("kernel-4");

        String periodXPath = "//*[local-name()='subject'][@subjectScheme='ABR-periode']";
        Element periodElement = (Element) xPath.evaluate(periodXPath, docElement, XPathConstants.NODE);

        assertEquals(periodElement.getTextContent(), "Nieuwe tijd: 1500 - heden (NT)");
        assertEquals(periodElement.getAttribute("schemeURI"), "http://cultureelerfgoed.nl/");
        assertEquals(periodElement.getAttribute("xml:lang"), "nl");
    }

    @Test
    public void validateXmlOutputAgainstDataciteSchema() throws Exception {
        ignoreIfNot("kernel-3");
        ignoreIfSchemaNotAccessible();

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

    private void ignoreIfSchemaNotAccessible() {
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
}
