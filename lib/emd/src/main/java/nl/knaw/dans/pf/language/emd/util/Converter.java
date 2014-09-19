package nl.knaw.dans.pf.language.emd.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Converter offering methods to serialize and deserialize Java types to and from strings. JiBX is instructed to use methods from this converter.
 * 
 * @author ecco
 */
public final class Converter {

    /**
     * Time zone to use for the application.
     */
    public static final DateTimeZone LOCAL_TIME_ZONE = DateTimeZone.getDefault();

    /**
     * joda.time does not know how to parse a date like "2006-05-01T00:00:00+02:00" (it is missing millisecond precision), while it is a legal xml date format.
     * 
     * @see #deSerializeDateTime(String)
     */
    public static final Pattern INVALID_DATE_PATTERN = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\+[0-9]{2}:[0-9]{2}");

    /**
     * Formatter for ISO8601 date format as used in the XML.
     */
    public static final DateTimeFormatter DATE_FORMATTER = ISODateTimeFormat.dateTime().withZone(LOCAL_TIME_ZONE);

    // utility class.
    private Converter() {

    }

    /**
     * Serialize the date to a string.
     * 
     * @param dateTime
     *        Date to serialize
     * @return date as string
     */
    public static synchronized String serializeDateTime(final DateTime dateTime) {
        return dateTime == null ? null : DATE_FORMATTER.print(dateTime);
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

            if (INVALID_DATE_PATTERN.matcher(dateString).matches()) { // 2006-05-01T00:00:00+02:00
                String corrected = dateString.substring(0, 19) + ".0" + dateString.substring(19);
                dateTimeZone = DATE_FORMATTER.parseDateTime(corrected);
            } else {
                dateTimeZone = DATE_FORMATTER.parseDateTime(dateString);
            }
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
                uri = new URI(uriString);
            }
            catch (final URISyntaxException e) {
                throw new RuntimeException("Could not deserialize uri: ", e);
            }
        }
        return uri;
    }

}
