package nl.knaw.dans.c.dmo.collections.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiBXCollectionTest
{
    
    private static final boolean verbose = false;
    private static final Logger logger = LoggerFactory.getLogger(JiBXCollectionTest.class);
    
    
    @Test
    public void marshalAndUnmarshalEmpty() throws Exception
    {
        JiBXCollection simcol = new JiBXCollection();
        print(simcol);
        
        assertTrue(CollectionTreeValidator.instance().validate(simcol).passed());
        
        JiBXCollection simcol2 = (JiBXCollection) JiBXObjectFactory.unmarshal(JiBXCollection.class, simcol.asObjectXML());
        assertEquals(simcol.asXMLString(), simcol2.asXMLString());
    }
    
    @Test
    public void marshalAndUnmarshalFull() throws Exception
    {
        JiBXCollection simcol = new JiBXCollection();
        simcol.setNamespace("easy-simple");
        simcol.setId("root");
        simcol.setLabel("Root of easy-simple dmoCollection");
        List<JiBXCollection> children = new ArrayList<JiBXCollection>();
        for (int i = 0; i < 5; i++)
        {
            JiBXCollection kid = new JiBXCollection();
            kid.setId("" + (i + 1));
            kid.setLabel("kid " + kid.getId());
            kid.getDcMetadata().addDescription("This is the description of this collection\nIt can be a long story");
            children.add(kid);
        }
        simcol.setChildren(children);
        simcol.getDcMetadata().addCreator("jibx");
        print(simcol);
        
        assertTrue(CollectionTreeValidator.instance().validate(simcol).passed());
        
        JiBXCollection simcol2 = (JiBXCollection) JiBXObjectFactory.unmarshal(JiBXCollection.class, simcol.asObjectXML());
        print(simcol2);
        assertEquals(simcol.asXMLString(), simcol2.asXMLString());
        
    }
    
    @Test
    public void unmarshalFromFile() throws Exception
    {
        String filename = "src/test/java/nl/knaw/dans/c/dmo/collections/xml/class-resources/jibcol.xml";
        JiBXCollection simcol = (JiBXCollection) JiBXObjectFactory.unmarshalFile(JiBXCollection.class, filename);
        
        assertTrue(CollectionTreeValidator.instance().validate(simcol).passed());
        
        print(simcol);
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
