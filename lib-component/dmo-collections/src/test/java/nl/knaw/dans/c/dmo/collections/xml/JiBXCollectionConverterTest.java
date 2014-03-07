package nl.knaw.dans.c.dmo.collections.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import nl.knaw.dans.c.dmo.collections.core.AbstractDmoCollectionsTest;
import nl.knaw.dans.c.dmo.collections.core.MockRootCreator;
import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;
import nl.knaw.dans.i.dmo.collections.DmoCollection;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiBXCollectionConverterTest extends AbstractDmoCollectionsTest
{
    private static final boolean verbose = false;
    private static final Logger logger = LoggerFactory.getLogger(JiBXCollectionConverterTest.class);
    
    @BeforeClass
    public static void beforeClass()
    {
        initializeWithNoSecurity();
    }
    
    @Test
    public void fromDmoCollectionToJiBXCollection() throws Exception
    {
        DmoCollection dmoRoot = MockRootCreator.createRoot("jib-col", 2, 2);
        
        JiBXCollection jibxRoot = JiBXCollectionConverter.convert(dmoRoot, true);
        
        assertEquals("jib-col", jibxRoot.getNamespace());
        assertEquals("root", jibxRoot.getId());
        assertEquals("Root of jib-col", jibxRoot.getLabel());
        assertEquals("Shortname of jib-col:root", jibxRoot.getShortName());
        assertTrue(jibxRoot.isPublishedAsOAISet());
        assertEquals(dmoRoot.getDcMetadata(), jibxRoot.getDcMetadata());
        
        DmoCollection dmoKid = dmoRoot.getChildren().get(0);
        JiBXCollection jibxKid = jibxRoot.getChildren().get(0);
        assertEquals("1", jibxKid.getId());
        assertEquals("Label of jib-col:1", jibxKid.getLabel());
        assertEquals("Shortname of jib-col:1", jibxKid.getShortName());
        assertTrue(jibxKid.isPublishedAsOAISet());
        assertEquals(dmoKid.getDcMetadata(), jibxKid.getDcMetadata());
        
        print(jibxRoot);
    }
    
    @Test
    public void fromJiBXCollectionToDmoCollection() throws Exception
    {
        URL url = this.getClass().getResource("class-resources/jibcol.xml");
        InputStream inStream = url.openStream();
        JiBXCollection jibRoot = (JiBXCollection) JiBXObjectFactory.unmarshal(JiBXCollection.class, inStream);
        inStream.close();
        
        DmoCollection dmoRoot = JiBXCollectionConverter.convert(jibRoot, false);
        assertEquals("jib-col:root", dmoRoot.getStoreId());
        assertEquals("root", dmoRoot.getDmoStoreId().getId());
        assertEquals("Root of jib-col", dmoRoot.getLabel());
        assertEquals("Shortname of jib-col:root", dmoRoot.getShortName());
        assertTrue(dmoRoot.isPublishedAsOAISet());
        assertEquals(dmoRoot.getDcMetadata(), dmoRoot.getDcMetadata());
        
        JiBXCollection jibKid = jibRoot.getChildren().get(0);
        DmoCollection dmoKid = dmoRoot.getChildren().get(0);
        assertEquals(dmoRoot.getDmoNamespace(), dmoKid.getDmoNamespace());
        assertEquals(jibKid.getId(), dmoKid.getDmoStoreId().getId());
        assertEquals(jibKid.getLabel(), dmoKid.getLabel());
        assertEquals(jibKid.getShortName(), dmoKid.getShortName());
        assertTrue(dmoKid.isPublishedAsOAISet());
        
        JiBXCollection jibRoot2 = JiBXCollectionConverter.convert(dmoRoot, true);
        print(jibRoot2);
    }
    
    @Test
    public void fromURL() throws Exception
    {
        URL url = this.getClass().getResource("class-resources/jibcol.xml");
        DmoCollection dmoRoot = JiBXCollectionConverter.convert(url, false);
        
        assertEquals("jib-col:root", dmoRoot.getStoreId());
        assertEquals("Label of jib-col:1", dmoRoot.getChildren().get(0).getDcMetadata().getTitle().get(0));
        
        JiBXCollection jibRoot2 = JiBXCollectionConverter.convert(dmoRoot, true);
        print(jibRoot2);
    }
    
    @Test(expected = IOException.class)
    public void fromNullUrl() throws Exception
    {
        URL url = this.getClass().getResource("class-resources/no.xml");
        JiBXCollectionConverter.convert(url, false);
    }
    
    @Test(expected = XMLDeserializationException.class)
    public void fromUrlWithInvalidXml() throws Exception
    {
        URL url = this.getClass().getResource("class-resources/jibcol-wrong1.xml");
        JiBXCollectionConverter.convert(url, false);
    }
    
    private void print(JiBXCollection jibxCol) throws XMLSerializationException
    {
        if (verbose)
        {
            logger.debug("Printing for " + this.getClass().getName());
            logger.debug("\n" + jibxCol.asXMLString(4) + "\n");
        }
    }

}
