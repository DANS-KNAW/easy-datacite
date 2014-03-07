package nl.knaw.dans.common.fedora.fox;

import static org.junit.Assert.assertEquals;
import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.jibx.bean.JiBXDublinCoreMetadata;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata.PropertyName;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.common.lang.xml.XMLException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DublinCoreMetadataTest
{

    private static final Logger logger = LoggerFactory.getLogger(DublinCoreMetadataTest.class);

    private boolean printXml = !Tester.isVerbose();

    @Test
    public void serializeDeserializeEmpty() throws XMLException
    {
        DublinCoreMetadata dc = new JiBXDublinCoreMetadata();
        if (printXml)
            logger.debug("\n" + dc.asXMLString(4) + "\n");

        DublinCoreMetadata dc2 = (DublinCoreMetadata) JiBXObjectFactory.unmarshal(JiBXDublinCoreMetadata.class, dc.asObjectXML());
        assertEquals(dc.asXMLString(), dc2.asXMLString());
    }

    @Test
    public void serializeDeserializeFull() throws XMLException
    {
        DublinCoreMetadata dc = createFull();
        if (printXml)
            logger.debug("\n" + dc.asXMLString(4) + "\n");

        DublinCoreMetadata dc2 = (DublinCoreMetadata) JiBXObjectFactory.unmarshal(JiBXDublinCoreMetadata.class, dc.asObjectXML());
        assertEquals(dc.asXMLString(), dc2.asXMLString());
    }

    public static DublinCoreMetadata createFull()
    {
        DublinCoreMetadata dc = new JiBXDublinCoreMetadata();
        for (PropertyName name : PropertyName.values())
        {
            dc.add(name, name.toString() + " 1");
            dc.add(name, name.toString() + " 2");
        }
        return dc;
    }

}
