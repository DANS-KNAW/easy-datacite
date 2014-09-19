package nl.knaw.dans.pf.language.xml.util;

import java.net.URI;
import java.net.URISyntaxException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Converter offering methods to serialize and deserialize Java types to and from strings.
 * 
 * @author ecco
 */
public final class Converter {

    /**
     * Time zone to use for the application.
     */
    private static final DateTimeZone DEFAULT_TIME_ZONE = DateTimeZone.getDefault();

    /**
     * Formatter for ISO8601 date format as used in the XML.
     */
    private static final DateTimeFormatter DATE_FORMATTER = ISODateTimeFormat.dateTime().withZone(DEFAULT_TIME_ZONE);

    private static final DateTimeFormatter UTC_DATE_FORMATTER = ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC);

    // utility class.
    private Converter() {

    }

    /**
     * Strip time zone and serialize the date to a string in UTC time zone.
     * 
     * @param dateTime
     *        dateTime to serialize, may be <code>null</code>
     * @return string in UTC time zone
     */
    public static synchronized String serializeToUTC(DateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return UTC_DATE_FORMATTER.print(dateTime);
    }

    /**
     * Serialize the date to a string.
     * 
     * @param dateTime
     *        Date to serialize
     * @return date as string
     */
    public static synchronized String serializeDateTime(final DateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        String serialized = DATE_FORMATTER.print(dateTime);
        if (serialized.length() > 29) // xs:date accepts no seconds in offset.
        {
            serialized = serialized.substring(0, 29);
        }
        return serialized;
    }

    /**
     * Deserialize date string to DateTime object.
     * 
     * @param dateString
     *        date string
     * @return dateTime object
     */
    public static synchronized DateTime deSerializeDateTime(final String dateString) {
        DateTime dateTimeZone = null;
        if (dateString != null) {
            dateTimeZone = DATE_FORMATTER.parseDateTime(dateString.trim());
        }
        return dateTimeZone;
    }

    public static synchronized String normalizeDateTime(String dateString) {
        return serializeDateTime(deSerializeDateTime(dateString));
    }

    /**
     * Serialize an URI to String.
     * 
     * @param uri
     *        URI to serialize
     * @return URI as string
     */
    public static synchronized String serializeURI(final URI uri) {
        return uri == null ? null : uri.toString();
    }

    /**
     * Deserialize a URI string to a URI object.
     * 
     * @param uriString
     *        URI string
     * @return URI object
     */
    public static synchronized URI deSerializeURI(final String uriString) {
        URI uri = null;
        if (uriString != null) {
            try {
                uri = new URI(uriString.trim());
            }
            catch (final URISyntaxException e) {
                throw new RuntimeException("Could not deserialize uri: ", e);
            }
        }
        return uri;
    }

    public static synchronized String serializeIntArray(final int[] ints) {
        StringBuilder sb = new StringBuilder();
        for (int i : ints) {
            sb.append(i);
            sb.append(",");
        }
        return sb.toString();
    }

    public static synchronized int[] deSerializeIntArray(final String intString) {
        String[] intStrings = intString.trim().split(",");
        int[] ints = new int[intStrings.length];
        for (int i = 0; i < intStrings.length; i++) {
            ints[i] = Integer.parseInt(intStrings[i]);
        }
        return ints;
    }

}
