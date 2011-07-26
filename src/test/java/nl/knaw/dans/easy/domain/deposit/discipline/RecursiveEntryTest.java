package nl.knaw.dans.easy.domain.deposit.discipline;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.test.Tester;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecursiveEntryTest
{
    
    private static final Logger logger = LoggerFactory.getLogger(RecursiveEntryTest.class);
    
    private boolean verbose = Tester.isVerbose();
    
    @Test
    public void serializeDeserializeEmpty() throws Exception
    {
        RecursiveEntry entry = new RecursiveEntry();
        if (verbose)
            logger.debug("\n" + entry.asXMLString(4) + "\n");
        RecursiveEntry entry2 = (RecursiveEntry) JiBXObjectFactory.unmarshal(RecursiveEntry.class, entry.asObjectXML());
        assertEquals(entry.asXMLString(), entry2.asXMLString());
    }
    
    @Test
    public void serializeDeserializeFull() throws Exception
    {
        RecursiveEntry entry = new RecursiveEntry();
        entry.setKey("ABC");
        entry.setName("Name");
        entry.setShortname("shortname");
        entry.setOrdinal(1);
        
        RecursiveEntry a = new RecursiveEntry();
        a.setKey("cABC");
        a.setName("cName");
        a.setShortname("cshortname");
        a.setOrdinal(2);
        
        entry.add(a);
        
        RecursiveEntry b = new RecursiveEntry();
        b.setKey("bABC");
        b.setName("bName");
        b.setShortname("bshortname");
        b.setOrdinal(3);
        
        a.add(b);
        
        if (verbose)
            logger.debug("\n" + entry.asXMLString(4) + "\n");
        RecursiveEntry entry2 = (RecursiveEntry) JiBXObjectFactory.unmarshal(RecursiveEntry.class, entry.asObjectXML());
        assertEquals(entry.asXMLString(), entry2.asXMLString());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void addToSelf()
    {
        RecursiveEntry entry = new RecursiveEntry();
        entry.add(entry);
    }
    
    @Test(expected = NullPointerException.class)
    public void addNull() throws Exception
    {
        RecursiveEntry entry = new RecursiveEntry();
        entry.add(null);
        entry.asXMLString();
    }
    
    @Test
    public void comparable()
    {
        RecursiveEntry entry = new RecursiveEntry();
        entry.setKey("ABC");
        entry.setName("Name");
        entry.setShortname("shortname");
        entry.setOrdinal(1);
        
        RecursiveEntry a = new RecursiveEntry();
        a.setKey("cABC");
        a.setName("cName");
        a.setShortname("cshortname");
        a.setOrdinal(2);
        
        RecursiveEntry b = new RecursiveEntry();
        b.setKey("bABC");
        b.setName("bName");
        b.setShortname("bshortname");
        b.setOrdinal(3);
        
        List<RecursiveEntry> entries = new ArrayList<RecursiveEntry>();
        entries.add(a);
        entries.add(b);
        entries.add(entry);
        
        Collections.sort(entries);
        
        assertEquals(entry, entries.get(0));
        assertEquals(a, entries.get(1));
        assertEquals(b, entries.get(2));
    }

}
