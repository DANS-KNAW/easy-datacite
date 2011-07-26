package nl.knaw.dans.easy.util;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;

// ecco: CHECKSTYLE: OFF

public class ConverterTest extends TestHelper
{

    @BeforeClass
    public static void testStartInformation()
    {
        before(ConverterTest.class);
    }

    /**
     * Test serialization and deserialization of a date.
     */
    @Test
    public void testSerializeDate()
    {
        DateTime date = new DateTime().withZone(Converter.EASY_TIME_ZONE);

        log().debug("Offset from local=" + Converter.EASY_TIME_ZONE.getOffsetFromLocal(new Date().getTime()) / 1000 / 60 + " minutes.");

        String sVal = Converter.serializeDateTime(date);
        log().debug(sVal);

        DateTime convertedDate = Converter.deSerializeDateTime(sVal);
        assertEquals("Date should be the same after serialization and deserialization", date, convertedDate);

        assertTrue(date.equals(convertedDate));

        String convertedSVal = Converter.serializeDateTime(convertedDate);
        log().debug(convertedSVal);
        assertEquals("Serialized value should be the same after deserialization and serialization", sVal, convertedSVal);
    }

    @Test
    public void testDeserializeDateTime()
    {        
        String dateString = "2008-08-05T13:17:24.898+02:00";
        DateTime dateTime = Converter.deSerializeDateTime(dateString);
        String returned = Converter.serializeDateTime(dateTime);
        assertEquals("2008-08-05T13:17:24.898+02:00", returned);
    }

    @Test
    public void testDeserializeDateTime2()
    {
        String dateString = "2008-08-05T01:17:24.898+02:00";
        DateTime dateTime = Converter.deSerializeDateTime(dateString);
        String returned = Converter.serializeDateTime(dateTime);
        assertFalse("2008-08-04T23:17:24.898Z".equals(returned));
        assertEquals("2008-08-05T01:17:24.898+02:00", returned);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeserializeDateTime3()
    {
        //2006-05-01T00:00:00+02:00  is a Jaxb contraption from old easy. It is not a legal date.
        String dateString = "2006-05-01T00:00:00+02:00";
        Converter.deSerializeDateTime(dateString);
    }
    
    
    @Test
    public void testNullDates()
    {
        assertNull(Converter.serializeDateTime(null));
        assertNull(Converter.deSerializeDateTime(null));
    }

    @Test
    public void testURI() throws URISyntaxException
    {
        String uriString = "info:bla/bla";
        URI uri = new URI(uriString);
        assertEquals(uriString, Converter.serializeURI(uri));
        assertEquals(uri, Converter.deSerializeURI(uriString));

        assertNull(Converter.serializeURI(null));
        assertNull(Converter.deSerializeURI(null));
    }

}


