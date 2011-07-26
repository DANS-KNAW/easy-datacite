package nl.knaw.dans.easy.domain.model.emd.types;

import nl.knaw.dans.easy.domain.model.emd.types.BasicString;

import org.junit.Test;
import static org.junit.Assert.*;

public class BasicStringTest
{
    
    @Test
    public void testEqualsAndHash()
    {
        BasicString a = new BasicString();
        BasicString b = new BasicString();
        assertTrue(a.equals(b));
        assertEquals(a.hashCode(), b.hashCode());
        
        a.setValue("foo");
        assertFalse(a.equals(b));
        
        b.setValue("foo");
        assertTrue(b.equals(a));
        assertEquals(a.hashCode(), b.hashCode());
        
        a.setLanguage("nld");
        assertFalse(a.equals(b));
        
        b.setLanguage("nld");
        assertTrue(b.equals(a));
        assertEquals(a.hashCode(), b.hashCode());
        
        a.setScheme("bar");
        assertFalse(a.equals(b));
        
        b.setScheme("bar");
        assertTrue(b.equals(a));
        assertEquals(a.hashCode(), b.hashCode());
    }

}
