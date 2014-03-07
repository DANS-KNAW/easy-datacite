package nl.knaw.dans.common.fedora.fox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.common.lang.xml.XMLException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatastreamVersionTest
{

    private static final Logger logger = LoggerFactory.getLogger(DatastreamVersionTest.class);

    private boolean printXml = Tester.isVerbose();

    @Test
    public void testContentDigestType() throws XMLException
    {
        DatastreamVersion dsv = new DatastreamVersion("bla", "bla");
        assertNull(dsv.getContentDigestType());
        assertNull(dsv.getContentDigest());

        dsv.setContentDigest(ContentDigestType.SHA_256, "xyz");
        assertEquals(ContentDigestType.SHA_256, dsv.getContentDigestType());
        assertEquals("xyz", dsv.getContentDigest());

        dsv.setContentDigest(null, null);
        assertNull(dsv.getContentDigestType());
        assertNull(dsv.getContentDigest());

        if (printXml)
            logger.debug("\n" + dsv.asXMLString(4) + "\n");

        DatastreamVersion dsv2 = (DatastreamVersion) JiBXObjectFactory.unmarshal(DatastreamVersion.class, dsv.asObjectXML());
        assertEquals(dsv.asXMLString(), dsv2.asXMLString());
    }

    @Test
    public void serializeDeserializeEmpty() throws XMLException
    {
        DatastreamVersion dsv = new DatastreamVersion("1", "foo/bar");
        if (printXml)
            logger.debug("\n" + dsv.asXMLString(4) + "\n");

        DatastreamVersion dsv2 = (DatastreamVersion) JiBXObjectFactory.unmarshal(DatastreamVersion.class, dsv.asObjectXML());
        assertEquals(dsv.asXMLString(), dsv2.asXMLString());
    }

    @Test
    public void getStreamId()
    {
        DatastreamVersion version = new DatastreamVersion(null, null);
        assertNull(version.getStreamId());
        version.setVersionId("FOO");
        assertEquals("FOO", version.getStreamId());
        version.setVersionId("FOO.1");
        assertEquals("FOO", version.getStreamId());
    }

    //    @Test
    //    public void testMarshalAndUnmarshal2() throws IOException, JiBXException, DocumentException
    //    {
    //        DatastreamVersion dsv = createDatastreamVersion();
    //        
    //        String filename = marshal(dsv, "_2");
    //        
    //        DatastreamVersion dsv2 = unmarshal(filename);
    //        assertEquals(dsv.asXMLString(), dsv2.asXMLString());
    //    }
    //
    //    public static DatastreamVersion createDatastreamVersion() throws DocumentException, JiBXException
    //    {
    //        DatastreamVersion dsv = new DatastreamVersion("2", "foo/bar");
    //        dsv.setLabel("test");
    //        dsv.setCreated(new DateTime());
    //        dsv.setFormatURI(URI.create("bla://bla"));
    //        dsv.setContentDigest(ContentDigestType.MD5, "spleen");
    //        
    //        DublinCoreMetadata dc = new DublinCoreMetadata();
    //        dc.addCreator("test");
    //        OAIDC oaidc = new OAIDC(dc);
    //        XMLContent xmlContent = new XMLContent(oaidc.asElement());
    //        dsv.setXmlContent(xmlContent);
    //        
    //        dsv.setContentLocation(Type.URL, URI.create("bar:foo"));
    //        dsv.setBinaryContent("binaryContent".getBytes());
    //        return dsv;
    //    }
    //

}
