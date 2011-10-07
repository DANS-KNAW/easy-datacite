package nl.knaw.dans.easy.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.common.lang.xml.Dom4jReader;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.common.lang.xml.XMLErrorHandler;
import nl.knaw.dans.easy.domain.dataset.FileItemMetadataImpl;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FileItemMetadata;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceMetadataListTest
{
    private static final Logger logger = LoggerFactory.getLogger(ResourceMetadataListTest.class);
    
    private boolean verbose = Tester.isVerbose();
    
    @Test
    public void serializeDeserializeEmpty() throws Exception
    {
        ResourceMetadataList rml = new ResourceMetadataList();
        
        if (verbose)
            logger.debug("\n" + rml.asXMLString(4) + "\n");
        
        ResourceMetadataList rml2 
            = (ResourceMetadataList) JiBXObjectFactory.unmarshal(ResourceMetadataList.class, rml.asObjectXML());
        assertEquals(rml.asXMLString(), rml2.asXMLString());
        
        XMLErrorHandler handler = ResourceMetadataListValidator.instance().validate(rml);
        assertTrue(handler.passed());
    }
    
    @Test
    public void serializeDeserializeFull() throws Exception
    {
        ResourceMetadataList rml = new ResourceMetadataList();

        ResourceMetadata rm = new ResourceMetadata("foo/bar/test.txt");
        rm.setCategoryDiscover(AccessCategory.OPEN_ACCESS);
        rm.setCategoryRead(AccessCategory.ANONYMOUS_ACCESS);
        
        AdditionalMetadata addmd = rm.getAdditionalMetadata();
        Element content = getContent("src/test/resources/test-files/add-content.xml");
        AdditionalContent addContent = new AdditionalContent("id1", content);
        addmd.addAdditionalContent(addContent);
        addmd.getPropertryList().addProperty("foo", "bar");
        addmd.getPropertryList().addProperty("bla bla", "rabarberplanten en -struiken");
        rml.addResourceMetadata(rm);
        
        rm = new ResourceMetadata(FileItem.NAMESPACE + ":123");
        rm.setCategoryWrite(AccessCategory.GROUP_ACCESS);
        rm.setCategoryDelete(AccessCategory.GROUP_ACCESS);
        rml.addResourceMetadata(rm);
        
        if (verbose)
            logger.debug("\n" + rml.asXMLString(4) + "\n");
        
        ResourceMetadataList rml2 
            = (ResourceMetadataList) JiBXObjectFactory.unmarshal(ResourceMetadataList.class, rml.asObjectXML());
        assertEquals(rml.asXMLString(), rml2.asXMLString());
        
        XMLErrorHandler handler = ResourceMetadataListValidator.instance().validate(rml);
        assertTrue(handler.passed());
    }
    
    private Element getContent(String filename) throws DocumentException
    {
        Dom4jReader reader = new Dom4jReader(filename);
        return (Element) reader.getNode("content");
    }
    
    @Test
    public void deserializeFromFile() throws Exception
    {
        ResourceMetadataList rml = (ResourceMetadataList) 
            JiBXObjectFactory.unmarshal(ResourceMetadataList.class, 
                    new File("src/test/resources/test-files/resource-metadata-list.xml"));
        if (verbose)
            logger.debug("\n" + rml.asXMLString(4) + "\n");
    }
    
    @Test(expected = XMLDeserializationException.class)
    public void deserializeFromWrongFile() throws Exception
    {
        JiBXObjectFactory.unmarshal(ResourceMetadataList.class, 
                    new File("src/test/resources/test-files/wrong-resource-metadata-list.xml"));
    }
    
    @Test
    public void integrateWithFileItemMetadata() throws Exception
    {
        ResourceMetadataList rml = (ResourceMetadataList) 
        JiBXObjectFactory.unmarshal(ResourceMetadataList.class, 
                new File("src/test/resources/test-files/resource-metadata-list.xml"));
        
        for (ResourceMetadata rmd : rml.getResourceMetadataAsList())
        {
            FileItemMetadata fimd = new FileItemMetadataImpl("test");
            fimd.setAdditionalMetadata(rmd.getAdditionalMetadata());
            if (verbose)
                logger.debug("\n" + fimd.asXMLString(4) + "\n");
        }
        
        assertEquals(2, rml.getResourceMetadataAsList().size());
    }


}
