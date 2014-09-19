package nl.knaw.dans.common.jibx.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Converter offering methods to serialize and deserialize Java types to and from strings. JiBX can be instructed to use methods from this converter.
 * 
 * @author ecco
 */
public final class Converter {

    /**
     * Time zone to use for the application.
     */
    public static final DateTimeZone DANS_TIME_ZONE = DateTimeZone.getDefault();

    /**
     * joda.time does not know how to parse a date like "2006-05-01T00:00:00+02:00" (it is missing millisecond precision), while it is a legal xml date format.
     * 
     * @see #deSerializeDateTime(String)
     */
    public static final Pattern INVALID_DATE_PATTERN = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\+[0-9]{2}:[0-9]{2}");

    /**
     * Formatter for ISO8601 date format as used in the XML.
     */
    public static final DateTimeFormatter DATE_FORMATTER = ISODateTimeFormat.dateTime().withZone(DANS_TIME_ZONE);

    private static final DateTimeFormatter XML_DATE_FORMATTER = ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC);

    // utility class.
    private Converter() {

    }

    public static synchronized String serializeToXml(DateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return XML_DATE_FORMATTER.print(dateTime);
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
     * <p/>
     * DateStrings missing millisecond precision but defining a time zone are not accepted by joda time. <strike>A dateString like 2006-05-01T00:00:00+02:00
     * will therefore be corrected to 2006-05-01T00:00:0.00+02:00 before parsing.</strike> (hb)
     * 
     * @param dateString
     *        date string
     * @return dateTime object
     */
    public static synchronized DateTime deSerializeDateTime(final String dateString) {
        DateTime dateTimeZone = null;
        if (dateString != null) {
            /*
             * if (INVALID_DATE_PATTERN.matcher(dateString).matches()) { // 2006-05-01T00:00:00+02:00 String corrected = dateString.substring(0, 19) + ".0" +
             * dateString.substring(19); dateTimeZone = DATE_FORMATTER.parseDateTime(corrected); } else {
             */
            dateTimeZone = DATE_FORMATTER.parseDateTime(dateString.trim());
            /* } */
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
