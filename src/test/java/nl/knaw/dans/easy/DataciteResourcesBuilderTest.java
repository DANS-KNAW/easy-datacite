/*
 * Copyright (C) 2014 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    private static final String version = dsConfig.getXslVersion();

    private static final String XSL_EMD2DATACITE = String.format("xslt-files/EMD_doi_datacite_v%s.xsl", version);
    private static final String DATACITE_SCHEMA_LOCATION = String.format("http://schema.datacite.org/meta/kernel-%s/metadata.xsd", dsConfig.getXslVersion());
    private static final String CREATOR = "//*[local-name()='creators']/*[local-name()='creator']";
    private static final String CONTRIBUTOR = "//*[local-name()='contributors']/*[local-name()='contributor']";
    private static final String XP_GEOLOCATION = "//*[local-name()='geoLocations']/*[local-name()='geoLocation']";
    private static final String XP_GEOLOCATION_POINT = XP_GEOLOCATION + "/*[local-name()='geoLocationPoint']";
    private static final String XP_GEOLOCATION_BOX = XP_GEOLOCATION + "/*[local-name()='geoLocationBox']";
    private static final String XP_TYPE = "//*[local-name()='resourceType']";

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

    @Test(expected = IllegalStateException.class)
    public void classpath() throws Exception {
        new DataciteResourcesBuilder("notFound.xsl", getResolver()).create(new EmdBuilder().build());
    }

    @Test(expected = IllegalArgumentException.class)
    public void noDoi() throws Exception {
        EasyMetadata emd = new EmdBuilder().replaceAll("10.17026/dans-test-123", "\t").build();
        assertThat(emd.getEmdIdentifier().getDansManagedDoi(), equalTo("\t"));

        createDefaultBuilder().create(emd);
    }

    @Test
    public void once() throws Exception {
        EasyMetadata emd = new EmdBuilder().build();

        DataciteResourcesBuilder.Resources out = createDefaultBuilder().create(emd);
        String doiOut = out.doiResource;
        String metadataOut = out.metadataResource;

        assertThat(doiOut, containsString(emd.getEmdIdentifier().getDansManagedDoi()));
        assertThat(metadataOut, containsString(emd.getPreferredTitle()));
        logger.debug(doiOut);
        logger.debug(metadataOut);
    }

    @Test
    public void noAccessWithDansDoi() throws Exception {
        EasyMetadata emd = new EmdBuilder("no-access-dans-doi-emd.xml").build();

        DataciteResourcesBuilder.Resources out = createDefaultBuilder().create(emd);
        String metadataOut = out.metadataResource;

        assertThat(metadataOut, containsString("<rights>info:eu-repo/semantics/closedAccess</rights>"));
        logger.debug(metadataOut);
    }

    @Test
    public void noAccessWithOtherDoi() throws Exception {
        EasyMetadata emd = new EmdBuilder("no-access-other-doi-emd.xml").build();

        DataciteResourcesBuilder.Resources out = createDefaultBuilder().create(emd);
        String metadataOut = out.metadataResource;

        assertThat(metadataOut, containsString("<rights>info:eu-repo/semantics/openAccess</rights>"));
        logger.debug(metadataOut);
    }

    @Test
    public void creatorAffiliation() throws Exception {
        ignoreIfNot("kernel-" + version);

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
        assertEquals(7, creators.getLength());
        assertEquals("Hello Internet", creators.item(2).getChildNodes().item(1).getTextContent().trim());
        assertEquals("contributor 0", creators.item(3).getTextContent().trim());
        assertEquals("contributor 1", creators.item(4).getTextContent().trim());
        assertEquals("rights 0", creators.item(5).getTextContent().trim());
        assertEquals("rights 1", creators.item(6).getTextContent().trim());
    }

    @Test
    public void contributorAffiliation() throws Exception {
        ignoreIfNot("kernel-" + version);

        String affiliation = CONTRIBUTOR + "[@contributorType=\"Other\"]/*[local-name()='affiliation']/text()";

        assertEquals("CERN", xPath.evaluate(affiliation, docElement));
    }

    @Test
    public void geoLocationPoint() throws Exception {
        ignoreIfNot("kernel-" + version);

        String latitude = XP_GEOLOCATION_POINT + "/*[local-name()='pointLatitude']/text()";
        String longitude = XP_GEOLOCATION_POINT + "/*[local-name()='pointLongitude']/text()";

        assertEquals("53.24478539", xPath.evaluate(latitude, docElement));
        assertEquals("5.63994851", xPath.evaluate(longitude, docElement));
    }

    @Test
    public void geoLocationBox() throws Exception {
        ignoreIfNot("kernel-" + version);

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
        ignoreIfNot("kernel-" + version);

        String subjectXPath = "//*[local-name()='subject'][@subjectScheme='NARCIS-classification']";
        Element subjectElement = (Element) xPath.evaluate(subjectXPath, docElement, XPathConstants.NODE);

        assertEquals(subjectElement.getTextContent(), "Archaeology");
        assertEquals(subjectElement.getAttribute("schemeURI"), "http://www.narcis.nl/classification");
        assertEquals(subjectElement.getAttribute("valueURI"), "http://www.narcis.nl/classfication/D37000");
        assertEquals(subjectElement.getAttribute("xml:lang"), "en");
    }

    @Test
    public void subjectAbrComplex() throws Exception {
        ignoreIfNot("kernel-" + version);

        String subjectXPath = "//*[local-name()='subject'][@subjectScheme='ABR-complex']";
        Element subjectElement = (Element) xPath.evaluate(subjectXPath, docElement, XPathConstants.NODE);

        assertEquals(subjectElement.getTextContent(), "Begraving, onbepaald (GX)");
        assertEquals(subjectElement.getAttribute("schemeURI"), "http://cultureelerfgoed.nl/");
        assertEquals(subjectElement.getAttribute("xml:lang"), "nl");
    }

    @Test
    public void subjectAbrPeriode() throws Exception {
        ignoreIfNot("kernel-" + version);

        String periodXPath = "//*[local-name()='subject'][@subjectScheme='ABR-periode']";
        Element periodElement = (Element) xPath.evaluate(periodXPath, docElement, XPathConstants.NODE);

        assertEquals(periodElement.getTextContent(), "Nieuwe tijd: 1500 - heden (NT)");
        assertEquals(periodElement.getAttribute("schemeURI"), "http://cultureelerfgoed.nl/");
        assertEquals(periodElement.getAttribute("xml:lang"), "nl");
    }

    @Test
    public void otherAccessDoi() throws Exception {
        ignoreIfNot("kernel-" + version);

        String periodXPath = "//*[local-name()='alternateIdentifier'][@alternateIdentifierType='DOI']";
        Element identifierElement = (Element) xPath.evaluate(periodXPath, docElement, XPathConstants.NODE);

        assertEquals(identifierElement.getTextContent(), "10.17026/other-test-123");
        assertEquals(identifierElement.getAttribute("alternateIdentifierType"), "DOI");
    }

    @Test
    public void dcmiTypeForEMD() throws Exception {
        ignoreIfNot("kernel-" + version);

        Element dcmiTypeElement = (Element) xPath.evaluate(XP_TYPE, docElement, XPathConstants.NODE);

        assertEquals("Text", dcmiTypeElement.getTextContent());
        assertEquals("Text", dcmiTypeElement.getAttribute("resourceTypeGeneral"));
    }

    @Test
    public void dcmiTypeForMaxiEMD() throws Exception {
        ignoreIfNot("kernel-" + version);

        Element dcmiTypeElement = (Element) xPath.evaluate(XP_TYPE, maxiDocElement, XPathConstants.NODE);

        assertEquals("Dataset", dcmiTypeElement.getTextContent());
        assertEquals("Dataset", dcmiTypeElement.getAttribute("resourceTypeGeneral"));
    }

    @Test
    public void validateXmlOutputAgainstDataciteSchema() throws Exception {
        ignoreIfNot("kernel-" + version);
        ignoreIfSchemaNotAccessible();

        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(new URL(DATACITE_SCHEMA_LOCATION));
        Validator validator = schema.newValidator();

        try {
            validator.validate(new DOMSource(document));
            validator.validate(new DOMSource(maxiDocElement));
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
        BasicIdentifier bi = new BasicIdentifier("10.17026/dans-test-123");
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
