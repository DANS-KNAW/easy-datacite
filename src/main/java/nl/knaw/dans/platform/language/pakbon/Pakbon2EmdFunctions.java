package nl.knaw.dans.platform.language.pakbon;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class Pakbon2EmdFunctions
{
    
    private static final DateTimeFormatter UTC_DATE_FORMATTER = ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC);
    
    public static String currentDateTime()
    {
        return UTC_DATE_FORMATTER.print(new DateTime());
    }

}
