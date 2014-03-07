package nl.knaw.dans.common.lang.ldap;

import nl.knaw.dans.common.lang.annotations.ldap.LdapAttributeValueTranslator;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateTimeTranslator implements LdapAttributeValueTranslator<DateTime>
{

    private static DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyyMMddHHmmss");

    public DateTime fromLdap(Object value)
    {
        if (value != null)
        {
            String s = (String) value;
            return FORMATTER.parseDateTime(s.substring(0, 14));
        }
        else
        {
            return null;
        }
    }

    public Object toLdap(DateTime value)
    {
        if (value != null)
        {
            return FORMATTER.print(value) + "Z";
        }
        else
        {
            return null;
        }
    }

}
