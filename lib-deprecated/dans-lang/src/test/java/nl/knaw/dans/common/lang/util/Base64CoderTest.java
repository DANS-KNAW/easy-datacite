package nl.knaw.dans.common.lang.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Base64CoderTest
{

    @Test
    public void encodeDecode()
    {
        String encoded = Base64Coder.encodeString("foo");
        assertEquals("Zm9v", encoded);

        String decoded = Base64Coder.decodeString(encoded);
        assertEquals("foo", decoded);

    }

}
