package nl.knaw.dans.easy.domain.deposit.discipline;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.test.Tester;

public class RecursiveListTest
{
    
    private static final Logger logger = LoggerFactory.getLogger(RecursiveListTest.class);
    
    private boolean verbose = Tester.isVerbose();
    
    @Test
    public void serializeDeserializeEmpty() throws Exception
    {
        RecursiveList rl = new RecursiveList("foo");
        if (verbose)
            logger.debug("\n" + rl.asXMLString(4) + "\n");
        RecursiveList rl2 = (RecursiveList) JiBXObjectFactory.unmarshal(RecursiveList.class, rl.asObjectXML());
        assertEquals(rl.asXMLString(), rl2.asXMLString());
    }
    
    @Test
    public void serializeDeserializeFull() throws Exception
    {
        RecursiveList rl = new RecursiveList("foo");
        
        RecursiveEntry entry = new RecursiveEntry();
        entry.setKey("ABC");
        entry.setName("Name");
        entry.setShortname("shortname");
        entry.setOrdinal(1);
        
        RecursiveEntry a = new RecursiveEntry();
        a.setKey("a");
        a.setName("aName");
        a.setShortname("aShortname");
        a.setOrdinal(2);
        
        entry.add(a);
        
        rl.add(entry);
        
        RecursiveEntry b = new RecursiveEntry();
        b.setKey("bABC");
        b.setName("bName");
        b.setShortname("bshortname");
        b.setOrdinal(3);
        
        rl.add(b);
        
        //if (verbose)
            logger.debug("\n" + rl.asXMLString(4) + "\n");
        RecursiveList rl2 = (RecursiveList) JiBXObjectFactory.unmarshal(RecursiveList.class, rl.asObjectXML());
        assertEquals(rl.asXMLString(), rl2.asXMLString());
        
        RecursiveEntry af = rl.getEntry("a");
        assertEquals(a, af);
    }

}
