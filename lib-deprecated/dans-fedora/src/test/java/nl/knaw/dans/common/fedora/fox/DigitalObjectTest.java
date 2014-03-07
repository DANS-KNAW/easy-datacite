package nl.knaw.dans.common.fedora.fox;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.repo.exception.ObjectSerializationException;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.common.lang.xml.XMLException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.BaseElement;
import org.dom4j.tree.FlyweightText;
import org.joda.time.DateTime;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DigitalObjectTest
{
    private static final Logger logger = LoggerFactory.getLogger(DigitalObjectTest.class);

    private boolean printXml = Tester.isVerbose();

    @Test
    public void staticVariables()
    {
        assertEquals("info:fedora/fedora-system:FOXML-1.1", DigitalObject.FORMAT_FOXML_1_1);
    }

    @Test
    public void serializeDeserializeEmpty() throws XMLException
    {
        DigitalObject dob = new DigitalObject();
        if (printXml)
            logger.debug("\n" + dob.asXMLString(4) + "\n");

        DigitalObject dob2 = (DigitalObject) JiBXObjectFactory.unmarshal(DigitalObject.class, dob.asObjectXML());
        assertEquals(dob.asXMLString(), dob2.asXMLString());
    }

    @Test
    public void serializeDeserializeFull() throws URISyntaxException, DocumentException, XMLException, ObjectSerializationException
    {
        DigitalObject dob = new DigitalObject(DobState.Active, "foo");
        dob.setSid("easy:123");
        URI uri = new URI("info:foo/bar");
        dob.setFedoraURI(uri);
        dob.setLabel("Digital Object Test");
        dob.setOwnerId("ecco");
        // set variable for timestamp.
        DateTime timestamp = new DateTime();
        dob.getObjectProperties().setProperty(DigitalObjectProperties.NAME_CREATED_DATE, timestamp.toString());
        dob.getObjectProperties().setProperty(DigitalObjectProperties.NAME_LASTMODIFIED_DATE, timestamp.toString());

        dob.setExternalProperty("foo", "bar");

        dob.putDatastream(new Datastream("BAR", ControlGroup.E));
        Datastream datastream = new Datastream("FOO", ControlGroup.R);
        dob.putDatastream(datastream);
        datastream.setState(Datastream.State.A);
        datastream.setFedoraUri(new URI("info:easy/debug"));

        DatastreamVersion dsv = new DatastreamVersion("CHOCOLADE", "kwatta");
        datastream.putDatastreamVersion(dsv);
        dsv.setLabel("An Easy Version");

        dsv.setMimeType("text/xml");
        dsv.getAltIds().add(new URI("info:easy/dit"));
        dsv.getAltIds().add(new URI("info:easy/dat"));
        dsv.setFormatURI(new URI("bla:foo/bar"));

        dsv.setContentDigest(ContentDigestType.SHA_512, "bladiebladiebla");
        Element element = new BaseElement("foo");
        element.add(new FlyweightText("Why 1 < 2, and not 2 < 1"));
        dsv.setXmlContent(element);

        DatastreamVersion dsvLoc = new DatastreamVersion("LOCATION", "kwatta");
        datastream.putDatastreamVersion(dsvLoc);
        dsvLoc.setContentLocation(ContentLocation.Type.URL, new URI("http://foo.bar.com"));

        Datastream dsBin = new Datastream("DS_BIN", ControlGroup.R);
        dob.putDatastream(dsBin);
        DatastreamVersion dsvBin = new DatastreamVersion("BIN", "kwatta");
        dsBin.putDatastreamVersion(dsvBin);
        String binContent = "<div align=\"center\" id=\"thePlayer\"></div>\n"
                + "<!--\\\\\n"
                + "   This is comment\n"
                + "\\\\-->\n"
                + "<script src=\"/mediaplayer/swfobject.js\" type=\"text/javascript\"></script><script type=\"text/javascript\">var swf = new SWFObject('/mediaplayer/player.swf','player','512','288','9'); swf.addParam('allowfullscreen','true'); swf.addParam('allowscriptaccess','always'); swf.addParam('wmode','opaque'); swf.addVariable('config','/mediaplayer/player-config.xml'); swf.addVariable('file','GV_GAR_bombardement_01.mp4'); swf.write('thePlayer');</script>";
        dsvBin.setBinaryContent(binContent.getBytes());

        dob.addDatastreamVersion(DublinCoreMetadataTest.createFull());
        dob.addDatastreamVersion(DublinCoreMetadataTest.createFull());

        URL url = Tester.getResource("test-files/ds.xml");
        Document doc = createDocument(url);
        Element root = doc.getRootElement();

        DatastreamVersion versionEle = dob.addDatastreamVersion("ELE", root);
        versionEle.setFormatURI(URI.create("http://easy.dans.knaw.nl/easy/dataset-administrative-metadata/"));
        versionEle.setLabel("Foo for this object from an element");

        DatastreamVersion versionDoc = dob.addDatastreamVersion("DOC", doc);
        versionDoc.setLabel("Foo for this object from a document");

        Map<String, String> map = new HashMap<String, String>();
        map.put("wfs", "http://easy.dans.knaw.nl/easy/workflow/");
        map.put("damd", "http://easy.dans.knaw.nl/easy/dataset-administrative-metadata/");
        XPath xpath = DocumentHelper
                .createXPath("/damd:administrative-md/damd:workflowData/wfs:workflow/steps/wfs:workflow[1]/steps/wfs:workflow[1]/steps/wfs:workflow[1]/id");
        xpath.setNamespaceURIs(map);

        Element el = (Element) xpath.selectSingleNode(doc);
        el.addNamespace("wfs", "http://easy.dans.knaw.nl/easy/workflow/");
        DatastreamVersion version = dob.addDatastreamVersion("WF", el);
        version.setLabel("workflow for this object");

        if (printXml)
            logger.debug("\n" + dob.asXMLString(4) + "\n");

        DigitalObject dob2 = (DigitalObject) JiBXObjectFactory.unmarshal(DigitalObject.class, dob.asObjectXML());
        //assertEquals(dob.asXMLString(), dob2.asXMLString());
        System.err.println(dob2.asXMLString(4));

        assertEquals(timestamp, dob2.getTimestamp());
        assertEquals(timestamp, dob2.getObjectProperties().getTimestamp());

        Datastream rdsBin = dob2.getDatastream("DS_BIN");
        DatastreamVersion rdsvBin = rdsBin.getDatastreamVersion("BIN");
        System.err.println(new String(rdsvBin.getBinaryContent()));
    }

    private Document createDocument(URL url) throws DocumentException
    {
        SAXReader reader = new SAXReader();
        Document document = reader.read(url);
        return document;

    }

}
