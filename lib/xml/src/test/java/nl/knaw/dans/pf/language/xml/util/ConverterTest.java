package nl.knaw.dans.pf.language.xml.util;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.Test;

public class ConverterTest
{

    @Test
    public void dateTime() throws Exception
    {
        DateTime dateTime = new DateTime();
        String date = Converter.serializeDateTime(dateTime);
        String utcDate = Converter.serializeToUTC(dateTime);
        // 2013-07-02T11:09:06.350+02:00
        assertThat(date, containsString("+"));
        // 2013-07-02T09:09:06.351Z
        assertThat(utcDate, containsString("Z"));

        DateTime cDate = Converter.deSerializeDateTime(date);
        DateTime cUtcDate = Converter.deSerializeDateTime(utcDate);

        assertThat(cDate, is(dateTime));
        assertThat(cUtcDate, is(dateTime));

        // System.err.println(date);
        // System.err.println(utcDate);
    }

}
