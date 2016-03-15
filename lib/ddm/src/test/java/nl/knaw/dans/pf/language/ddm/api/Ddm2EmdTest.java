/**
 * 
 */
package nl.knaw.dans.pf.language.ddm.api;

import nl.knaw.dans.pf.language.ddm.handlermaps.NameSpace;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.binding.EmdMarshaller;
import nl.knaw.dans.pf.language.xml.validation.XMLErrorHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static nl.knaw.dans.pf.language.ddm.api.SpecialValidator.LOCAL_SCHEMA_DIR;
import static nl.knaw.dans.pf.language.ddm.handlermaps.NameSpace.DC;
import static org.apache.commons.io.FileUtils.copyFile;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeTrue;

public class Ddm2EmdTest {

    private Ddm2EmdCrosswalk crosswalk = new Ddm2EmdCrosswalk(new SpecialValidator());
    private File[] publicExamples = new File(LOCAL_SCHEMA_DIR, "docs/examples/ddm/").listFiles();
    private static final Logger logger = LoggerFactory.getLogger(Ddm2EmdTest.class);

    private void externalSchemaCheck() {
        // ignore test case if not available
        assumeTrue("can access " + DC.xsd, canConnect(DC.xsd));
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

    @Test
    /** Proofs validity of the public examples.
     *
     * This might fail during upgrade of XSDs
     * @see SpecialValidator
     *
     * NB: also eat each sword pudding (after build and deploy of easy-app/front-end/easy-sword) with:
     * 'zip -r d.zip *;curl -i --data-binary @d.zip -u easyadmin:easyadmin deasy.dans.knaw.nl/sword/deposit'
     **/
    public void publicExamplesAreValid() throws Exception {
        externalSchemaCheck();
        for (File file : publicExamples) {

            EasyMetadata emd = crosswalk.createFrom(file);

            // write result
            String dir = "target/swordPuddings/" + file.getName().replace(".xml", "");
            String emdString = new EmdMarshaller(emd).getXmlString();
            XMLErrorHandler errorHandler = crosswalk.getXmlErrorHandler();
            copyFile(file, new File(dir + "/DansDatasetMetadata.xml"));
            writeStringToFile(new File(dir + "/data/emd.xml"), emdString);
            writeStringToFile(new File(dir + "/data/messages.txt/"), errorHandler.getMessages());

            // we have warnings so skip check on: errorHandler.passed()
            assertThat("Ddm2EmdCrosswalk errors for " + file, errorHandler.getErrors().size(), is(0));
            assertThat("Ddm2EmdCrosswalk fatalErrors for " + file, errorHandler.getFatalErrors().size(), is(0));

            // just a limited check of the output
            assertThat("EMD content from " + file, emdString, containsString("easy-discipline:"));

        }
    }

    @Test
    public void publicExamplesUseLastXsdVersions() throws Exception {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Map<String, String> xsdUri2Url = new HashMap<String, String>();
        for (NameSpace ns : NameSpace.values())
            xsdUri2Url.put(ns.uri, ns.xsd);
        for (File file : publicExamples) {
            String[] locations = documentBuilder.parse(file).getElementsByTagName("ddm:DDM").item(0).getAttributes().getNamedItem("xsi:schemaLocation")
                    .getNodeValue().trim().split("\\s+");
            for (int i = 1; i < locations.length; i += 2) {
                String uri = locations[i - 1];
                String url = locations[i];
                logger.debug(uri + " - " + url);
                if (xsdUri2Url.containsKey(uri))
                    assertThat(" namspace location " + file, url, is(xsdUri2Url.get(uri)));
            }
        }
    }

    @Test
    public void creatorTest() throws Exception {
        externalSchemaCheck();
        String emdXmlExpected = "src/test/resources/input/emd-actual-expected.xml";
        String ddmXml = "src/test/resources/input/ddm-creators-organization-mixed.xml";
        String emdXmlActual = "target/emd-actual.xml";
        InputSource inputSourceEmdXmlExpected = new InputSource(emdXmlExpected);
        XPath xpath = createXpathInstance();
        // Conversion
        EasyMetadata emd = crosswalk.createFrom(new File(ddmXml));
        String emdString = new EmdMarshaller(emd).getXmlString();
        // Write the conversion result
        Files.write(Paths.get(emdXmlActual), emdString.getBytes(StandardCharsets.UTF_8));
        // Compare only the <emd:creator> element
        InputSource inputSourceEmdXmlActual = new InputSource(emdXmlActual);
        XPathExpression expr = xpath.compile(".//emd:creator");
        Object emdCreatorExpectedObject = expr.evaluate(inputSourceEmdXmlExpected, XPathConstants.NODESET);
        NodeList emdCreatorExptectedNodeList = (NodeList) emdCreatorExpectedObject;
        Object emdCreatorActualObject = expr.evaluate(inputSourceEmdXmlActual, XPathConstants.NODESET);
        NodeList emdCreatorActualNodeList = (NodeList) emdCreatorActualObject;

        for (int i = 0; i < emdCreatorExptectedNodeList.getLength(); i++) {
            Node emdCreatorExptectedNode = emdCreatorExptectedNodeList.item(i);
            Node emdCreatorActualNode = emdCreatorActualNodeList.item(i);
            compareNodes(emdCreatorExptectedNode, emdCreatorActualNode);
        }
    }

    private XPath createXpathInstance() {
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new NamespaceContext() {

            HashMap<String, String> prefMap = new HashMap<String, String>() {
                private static final long serialVersionUID = 4130661524161291370L;
                {
                    put("emd", "http://easy.dans.knaw.nl/easy/easymetadata/");
                    put("dc", DC.uri);
                    put("dct", "http://purl.org/dc/terms/n");
                    put("eas", "http://easy.dans.knaw.nl/easy/easymetadata/eas/");
                }
            };

            public String getNamespaceURI(String prefix) {
                return prefMap.get(prefix);
            }

            public String getPrefix(String uri) {
                throw new UnsupportedOperationException();
            }

            public Iterator<?> getPrefixes(String uri) {
                throw new UnsupportedOperationException();
            }
        });
        return xpath;
    }

    private static void compareNodes(Node expected, Node actual) throws Exception {
        if (expected.getNodeType() != actual.getNodeType()) {
            throw new Exception("Different types of nodes: " + expected + " " + actual);
        }
        if (expected instanceof Document) {
            Document expectedDoc = (Document) expected;
            Document actualDoc = (Document) actual;
            compareNodes(expectedDoc.getDocumentElement(), actualDoc.getDocumentElement());
        } else if (expected instanceof Element) {
            Element expectedElement = (Element) expected;
            Element actualElement = (Element) actual;

            // compare element names
            if (!expectedElement.getLocalName().equals(actualElement.getLocalName())) {
                throw new Exception("Element names do not match: " + expectedElement.getLocalName() + " " + actualElement.getLocalName());
            }
            // compare element ns
            String expectedNS = expectedElement.getNamespaceURI();
            String actualNS = actualElement.getNamespaceURI();
            if ((expectedNS == null && actualNS != null) || (expectedNS != null && !expectedNS.equals(actualNS))) {
                throw new Exception("Element namespaces names do not match: " + expectedNS + " " + actualNS);
            }

            String elementName = "{" + expectedElement.getNamespaceURI() + "}" + actualElement.getLocalName();

            // compare attributes
            NamedNodeMap expectedAttrs = expectedElement.getAttributes();
            NamedNodeMap actualAttrs = actualElement.getAttributes();
            if (countNonNamespaceAttribures(expectedAttrs) != countNonNamespaceAttribures(actualAttrs)) {
                throw new Exception(elementName + ": Number of attributes do not match up: " + countNonNamespaceAttribures(expectedAttrs) + " "
                        + countNonNamespaceAttribures(actualAttrs));
            }
            for (int i = 0; i < expectedAttrs.getLength(); i++) {
                Attr expectedAttr = (Attr) expectedAttrs.item(i);
                if (expectedAttr.getName().startsWith("xmlns")) {
                    continue;
                }
                Attr actualAttr;
                if (expectedAttr.getNamespaceURI() == null) {
                    actualAttr = (Attr) actualAttrs.getNamedItem(expectedAttr.getName());
                } else {
                    actualAttr = (Attr) actualAttrs.getNamedItemNS(expectedAttr.getNamespaceURI(), expectedAttr.getLocalName());
                }
                if (actualAttr == null) {
                    throw new Exception(elementName + ": No attribute found:" + expectedAttr);
                } else if (!expectedAttr.getValue().equals(actualAttr.getValue())) {
                    throw new Exception(elementName + ": Attribute values do not match: " + expectedAttr.getValue() + " " + actualAttr.getValue());
                }
            }

            // compare children
            NodeList expectedChildren = expectedElement.getChildNodes();
            NodeList actualChildren = actualElement.getChildNodes();
            if (expectedChildren.getLength() != actualChildren.getLength()) {
                throw new Exception(elementName + ": Number of children do not match up: " + expectedChildren.getLength() + " " + actualChildren.getLength());
            }
            for (int i = 0; i < expectedChildren.getLength(); i++) {
                Node expectedChild = expectedChildren.item(i);
                Node actualChild = actualChildren.item(i);
                compareNodes(expectedChild, actualChild);
            }
        } else if (expected instanceof Text) {
            String expectedData = ((Text) expected).getData().trim();
            String actualData = ((Text) actual).getData().trim();

            if (!expectedData.equals(actualData)) {
                throw new Exception("Text does not match: " + expectedData + " " + actualData);
            }
        }
    }

    private static int countNonNamespaceAttribures(NamedNodeMap attrs) {
        int n = 0;
        for (int i = 0; i < attrs.getLength(); i++) {
            Attr attr = (Attr) attrs.item(i);
            if (!attr.getName().startsWith("xmlns")) {
                n++;
            }
        }
        return n;
    }
}
