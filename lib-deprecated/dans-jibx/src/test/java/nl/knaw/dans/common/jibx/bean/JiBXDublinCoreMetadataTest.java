package nl.knaw.dans.common.jibx.bean;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Observer;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.ResourceNotFoundException;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata.PropertyName;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.common.lang.xml.SchemaCreationException;
import nl.knaw.dans.common.lang.xml.XMLException;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class JiBXDublinCoreMetadataTest
{
    private static final Logger logger = LoggerFactory.getLogger(JiBXDublinCoreMetadataTest.class);

    private boolean verbose = Tester.isVerbose();

    @Test
    public void serializeDeserializeEmpty() throws XMLException
    {
        DublinCoreMetadata dc = new JiBXDublinCoreMetadata();
        if (verbose)
            logger.debug("\n" + dc.asXMLString(4) + "\n");

        DublinCoreMetadata dc2 = (DublinCoreMetadata) JiBXObjectFactory.unmarshal(JiBXDublinCoreMetadata.class, dc.asObjectXML());
        assertEquals(dc.asXMLString(), dc2.asXMLString());
    }

    @Test
    public void serializeDeserializeFull() throws XMLException
    {
        DublinCoreMetadata dc = createFull();
        // if (verbose)
        logger.debug("\n" + dc.asXMLString(4) + "\n");

        DublinCoreMetadata dc2 = (DublinCoreMetadata) JiBXObjectFactory.unmarshal(JiBXDublinCoreMetadata.class, dc.asObjectXML());
        assertEquals(dc.asXMLString(), dc2.asXMLString());
    }

    @Test
    public void serializeToFile() throws IOException, XMLException
    {
        DublinCoreMetadata dc = createFull();
        File tempFile = null;
        try
        {
            tempFile = File.createTempFile("jibx-test-", null);
            dc.serializeTo(tempFile, 4);
            if (verbose)
                logger.debug("Serialized to " + tempFile.getPath());

            DublinCoreMetadata dc2 = (DublinCoreMetadata) JiBXObjectFactory.unmarshal(JiBXDublinCoreMetadata.class, tempFile);
            assertEquals(dc.asXMLString(), dc2.asXMLString());
        }
        finally
        {
            if (tempFile != null && !tempFile.delete())
            {
                logger.warn("Could not remove temporary file: " + tempFile.getName());
                tempFile.deleteOnExit();
            }
        }
    }

    @Test
    public void serializeToInputStream() throws XMLException, IOException
    {
        DublinCoreMetadata dc = createFull();
        InputStream inStream = dc.asXMLInputStream();

        DublinCoreMetadata dc2 = (DublinCoreMetadata) JiBXObjectFactory.unmarshal(JiBXDublinCoreMetadata.class, inStream);

        assertEquals(dc.asXMLString(), dc2.asXMLString());
    }

    @Test
    public void serializeToOutputStream() throws XMLSerializationException
    {
        DublinCoreMetadata dc = createFull();
        if (verbose)
            dc.serializeTo(System.out, 4);
    }

    @Test
    public void serializeToWriter() throws XMLSerializationException
    {
        DublinCoreMetadata dc = createFull();
        dc.add(PropertyName.Type, "K\u2016jihu\u1234");
        PrintWriter out = new PrintWriter(System.out, true);
        if (verbose)
            dc.serializeTo(null, out);
    }

    @Test
    public void deserializeFromFile() throws XMLException, SAXException, SchemaCreationException, ResourceNotFoundException
    {
        DublinCoreMetadata dc = (DublinCoreMetadata) JiBXObjectFactory.unmarshal(JiBXDublinCoreMetadata.class, Tester.getFile("test-files/bean/oai_dc.xml"));
        // assertTrue(DublinCoreMetadataValidator.instance().validate(dc).passed());
        if (verbose)
            logger.debug("\n" + dc.asXMLString(4) + "\n");
    }

    @Test
    public void deserializeFromValidFile() throws XMLException, SAXException, SchemaCreationException, ResourceNotFoundException
    {
        DublinCoreMetadata dc = (DublinCoreMetadata) JiBXObjectFactory.unmarshal(JiBXDublinCoreMetadata.class,
                Tester.getFile("test-files/bean/valid_oai_dc.xml"));
        // assertTrue(DublinCoreMetadataValidator.instance().validate(dc).passed());
        if (verbose)
            logger.debug("\n" + dc.asXMLString(4) + "\n");

        // there are 4 source elements scattered around in the document.
        assertEquals(4, dc.getXl(PropertyName.Source).size());
    }

    public static DublinCoreMetadata createFull()
    {
        DublinCoreMetadata dc = new JiBXDublinCoreMetadata();
        int i = 0;
        for (PropertyName name : PropertyName.values())
        {
            if (i % 2 == 0)
            {
                dc.add(name, name.toString() + " 1", "nl");
                dc.add(name, name.toString() + " 2");
            }
            else
            {
                dc.add(name, name.toString() + " 1");
                dc.add(name, name.toString() + " 2", "nld-NLD");
            }
            i++;
        }
        return dc;
    }

    @Test
    public void Observable() throws Exception
    {
        JiBXDublinCoreMetadata jdc = new JiBXDublinCoreMetadata();
        JDCObserver dcObserver = new JDCObserver();
        jdc.addObserver(dcObserver);

        for (PropertyName name : PropertyName.values())
        {
            jdc.set(name, new ArrayList<String>());
            assertEquals(1, dcObserver.count);
            assertEquals(name, dcObserver.getLatestArgument());
        }

        for (PropertyName name : PropertyName.values())
        {
            jdc.set(name, "foo");
            assertEquals(1, dcObserver.count);
            assertEquals(name, dcObserver.getLatestArgument());
        }

        for (PropertyName name : PropertyName.values())
        {
            jdc.add(name, "bar");
            assertEquals(1, dcObserver.count);
            assertEquals(name, dcObserver.getLatestArgument());
        }

        for (PropertyName name : PropertyName.values())
        {
            jdc.add(name, new JiBXLangString("jazz"));
            assertEquals(1, dcObserver.count);
            assertEquals(name, dcObserver.getLatestArgument());
        }

        for (PropertyName name : PropertyName.values())
        {
            jdc.add(name, "beer", "nl");
            assertEquals(1, dcObserver.count);
            assertEquals(name, dcObserver.getLatestArgument());
        }

        for (PropertyName name : PropertyName.values())
        {
            jdc.add(name, "drunk", new Locale("nl"));
            assertEquals(1, dcObserver.count);
            assertEquals(name, dcObserver.getLatestArgument());
        }

        if (verbose)
            logger.debug("\n" + jdc.asXMLString(4) + "\n");
    }

    class JDCObserver implements Observer
    {

        private Object latestArgument;
        private int count;

        @Override
        public void update(java.util.Observable arg0, Object arg1)
        {
            count++;
            latestArgument = arg1;
        }

        public Object getLatestArgument()
        {
            Object la = latestArgument;
            latestArgument = null;
            count = 0;
            return la;
        }

    }

}
