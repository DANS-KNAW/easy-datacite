package nl.knaw.dans.common.fedora.fox;

import static org.junit.Assert.assertEquals;
import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.common.lang.xml.XMLException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectPropertiesTest
{

    private static final Logger logger = LoggerFactory.getLogger(ObjectPropertiesTest.class);

    private boolean printXml = Tester.isVerbose();

    @Test
    public void serializeDeserializeEmpty() throws XMLException
    {
        DigitalObjectProperties props = new DigitalObjectProperties();
        if (printXml)
            logger.debug("\n" + props.asXMLString(4) + "\n");

        DigitalObjectProperties props2 = (DigitalObjectProperties) JiBXObjectFactory.unmarshal(DigitalObjectProperties.class, props.asObjectXML());
        assertEquals(props.asXMLString(), props2.asXMLString());
    }

    @Test
    public void serializeDeserializeFull() throws XMLException
    {
        DigitalObjectProperties props = new DigitalObjectProperties();
        props.setProperty("foo", "bar");
        props.setProperty(DigitalObjectProperties.NAME_CREATED_DATE, "123");
        props.setProperty(DigitalObjectProperties.NAME_LABEL, "the label");
        props.setExtProperty("pipo", "koeien");
        if (printXml)
            logger.debug("\n" + props.asXMLString(4) + "\n");

        DigitalObjectProperties props2 = (DigitalObjectProperties) JiBXObjectFactory.unmarshal(DigitalObjectProperties.class, props.asObjectXML());
        assertEquals(props.asXMLString(), props2.asXMLString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setState()
    {
        DigitalObjectProperties props = new DigitalObjectProperties();
        props.setState("foo");
    }

}
