/**
 * 
 */
package nl.knaw.dans.pf.language.ddm.api;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.binding.EmdMarshaller;
import nl.knaw.dans.pf.language.xml.crosswalk.CrosswalkException;
import nl.knaw.dans.pf.language.xml.exc.XMLSerializationException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

/**
 * @author Eko Indarto
 */
public class Ddm2EmdTest {
    Exception ex;
    Ddm2EmdCrosswalk crosswalk;
    String emdXmlExpected = "src/test/resources/input/emd-actual-expected.xml";
    String ddmXml = "src/test/resources/input/ddm-creators-organization-mixed.xml";
    String emdXmlActual = "target/emd-actual.xml";
    InputSource inputSourceEmdXmlExpected;
    InputSource inputSourceEmdXmlActual;
    EasyMetadata emd;

    /**
     * Set up for tests.
     */
    @Before
    public void setUp() {
        crosswalk = new Ddm2EmdCrosswalk();
        inputSourceEmdXmlExpected = new InputSource(emdXmlExpected);
    }

    @Test
    public void test() {

        XPath xpath = createNewIntance();
        try {
            // Conversion
            emd = crosswalk.createFrom(new File(ddmXml));
            String emdString = new EmdMarshaller(emd).getXmlString();
            // Write the conversion result
            Files.write(Paths.get(emdXmlActual), emdString.getBytes(StandardCharsets.UTF_8));
            // Compare only the <emd:creator> element
            inputSourceEmdXmlActual = new InputSource(emdXmlActual);
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
        catch (CrosswalkException e) {
            ex = e;
        }
        catch (IOException e) {
            ex = e;
        }
        catch (XMLSerializationException e) {
            ex = e;
        }
        catch (XPathExpressionException e) {
            ex = e;
        }
        catch (Exception e) {
            ex = e;
        }

        assertEquals(null, ex);
    }

    private XPath createNewIntance() {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        HashMap<String, String> prefMap = new HashMap<String, String>() {
            private static final long serialVersionUID = 4130661524161291370L;
            {
                put("emd", "http://easy.dans.knaw.nl/easy/easymetadata/");
                put("dc", "http://purl.org/dc/elements/1.1/");
                put("dct", "http://purl.org/dc/terms/n");
                put("eas", "http://easy.dans.knaw.nl/easy/easymetadata/eas/");
            }
        };

        SimpleNamespaceContext namespaces = new SimpleNamespaceContext(prefMap);
        xpath.setNamespaceContext(namespaces);
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
                Attr actualAttr = null;
                if (expectedAttr.getNamespaceURI() == null) {
                    actualAttr = (Attr) actualAttrs.getNamedItem(expectedAttr.getName());
                } else {
                    actualAttr = (Attr) actualAttrs.getNamedItemNS(expectedAttr.getNamespaceURI(), expectedAttr.getLocalName());
                }
                if (actualAttr == null) {
                    throw new Exception(elementName + ": No attribute found:" + expectedAttr);
                }
                if (!expectedAttr.getValue().equals(actualAttr.getValue())) {
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

class SimpleNamespaceContext implements NamespaceContext {

    private final Map<String, String> PREF_MAP = new HashMap<String, String>();

    public SimpleNamespaceContext(final Map<String, String> prefMap) {
        PREF_MAP.putAll(prefMap);
    }

    public String getNamespaceURI(String prefix) {
        return PREF_MAP.get(prefix);
    }

    public String getPrefix(String uri) {
        throw new UnsupportedOperationException();
    }

    public Iterator<?> getPrefixes(String uri) {
        throw new UnsupportedOperationException();
    }

}
