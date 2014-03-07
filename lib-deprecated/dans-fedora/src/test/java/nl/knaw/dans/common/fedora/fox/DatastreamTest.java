package nl.knaw.dans.common.fedora.fox;

import static org.junit.Assert.assertEquals;
import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.common.lang.xml.XMLException;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatastreamTest
{
    private static final Logger logger = LoggerFactory.getLogger(DatastreamTest.class);

    private boolean printXml = Tester.isVerbose();

    @Test
    public void testNextVersionId() throws XMLSerializationException
    {
        Datastream ds = new Datastream("DC", ControlGroup.X);
        assertEquals("DC.0", ds.nextVersionId());
        ds.addDatastreamVersion("BlaBla", null);
        assertEquals("DC.0", ds.nextVersionId());
        ds.addDatastreamVersion(ds.nextVersionId(), null);
        assertEquals("DC.1", ds.nextVersionId());
        ds.addDatastreamVersion("DC.100", null);
        assertEquals("DC.101", ds.nextVersionId());

        if (printXml)
            logger.debug("\n" + ds.asXMLString(4) + '\n');
    }

    @Test
    public void serializeDeserializeEmpty() throws XMLException
    {
        Datastream ds = new Datastream("FOO", ControlGroup.B);
        if (printXml)
            logger.debug("\n" + ds.asXMLString(4) + '\n');

        Datastream ds2 = (Datastream) JiBXObjectFactory.unmarshal(Datastream.class, ds.asObjectXML());
        assertEquals(ds.asXMLString(), ds2.asXMLString());
    }

}
