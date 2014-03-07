package nl.knaw.dans.common.search.simple;

import static org.junit.Assert.assertEquals;
import nl.knaw.dans.common.lang.search.simple.SimpleField;
import nl.knaw.dans.common.lang.search.simple.SimpleFieldSet;

import org.junit.Test;

public class SimpleFieldSetTest
{

    @Test
    public void testSet()
    {
        SimpleFieldSet<String> t = new SimpleFieldSet<String>();

        assertEquals(0, t.size());

        t.add(new SimpleField<String>("hello", "world"));
        t.add(new SimpleField<String>("foo", "bar"));

        assertEquals(2, t.size());

        assertEquals("world", t.getByFieldName("hello").getValue());
        assertEquals("bar", t.getByFieldName("foo").getValue());

        // this should overwrite the other field named hello
        t.add(new SimpleField<String>("hello", "world2"));

        // therefore this should still be 2
        assertEquals(2, t.size());

        assertEquals("world2", t.getByFieldName("hello").getValue());
        assertEquals("bar", t.getByFieldName("foo").getValue());

        t.clear();

        assertEquals(0, t.size());
    }
}
