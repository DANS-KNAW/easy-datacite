package nl.knaw.dans.common.lang.ldap;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Test;

public class DateTimeTranslatorTest {

    @Test
    public void fromAndToLdap() {
        DateTimeTranslator translator = new DateTimeTranslator();

        String object = "20100222154032Z";
        DateTime dateTime = translator.fromLdap(object);
        assertEquals("2010-02-22T15:40:32.000+01:00", dateTime.toString());

        Object ldapValue = translator.toLdap(dateTime);
        assertEquals(object, ldapValue);
    }

}
