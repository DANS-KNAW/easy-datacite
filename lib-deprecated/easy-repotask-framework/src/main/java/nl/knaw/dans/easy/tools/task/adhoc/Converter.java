package nl.knaw.dans.easy.tools.task.adhoc;

import java.util.regex.Pattern;

import nl.knaw.dans.common.lang.dataset.AccessCategory;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class Converter {

    /**
     * Time zone to use for the application.
     */
    public static final DateTimeZone TIME_ZONE = DateTimeZone.getDefault();

    /**
     * Formatter for ISO8601 date format as used in the XML.
     */
    public static final DateTimeFormatter DATE_FORMATTER = ISODateTimeFormat.dateTime().withZone(TIME_ZONE);

    /**
     * joda.time does not know how to parse a date like "2006-05-01T00:00:00+02:00" (it is missing millisecond precision), while it is a legal xml date format.
     * 
     * @see #deSerializeDateTime(String)
     */
    public static final Pattern INVALID_DATE_PATTERN = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\+[0-9]{2}:[0-9]{2}");

    public static DateTime Y1941 = new DateTime(1941, 1, 1, 0, 0, 0, 0);
    public static DateTime Y1938 = new DateTime(1938, 1, 1, 0, 0, 0, 0);

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
     * DateStrings missing millisecond precision but defining a time zone are not accepted by joda time. A dateString like 2006-05-01T00:00:00+02:00 will
     * therefore be corrected to 2006-05-01T00:00:00.000+02:00 before parsing. (hb)
     * 
     * @param dateString
     *        date string
     * @return dateTime object
     */
    public static synchronized DateTime deSerializeDateTime(final String dateString) {
        DateTime dateTimeZone = null;
        if (dateString != null) {
            if (INVALID_DATE_PATTERN.matcher(dateString).matches()) {
                // 2006-05-01T00:00:00+02:00 missing millisecond precision
                String corrected = dateString.substring(0, 19) + ".000" + dateString.substring(19);
                dateTimeZone = DATE_FORMATTER.parseDateTime(corrected);
            } else {
                dateTimeZone = DATE_FORMATTER.parseDateTime(dateString);
            }
        }
        return dateTimeZone;
    }

    public static synchronized String normalizeDateTime(String dateString) {
        DateTime nd = deSerializeDateTime(dateString);
        if (nd.isBefore(Y1941)) {
            nd = nd.plusHours(2);
        }
        if (nd.isBefore(Y1938)) {
            nd = nd.plusMinutes(19);
        }
        String normalized = serializeDateTime(nd);
        if (normalized.length() > 29) {
            normalized = normalized.substring(0, 29);
        }
        return normalized;
    }

    public static String getDatasetAccessCategory(String oldValue) {
        // dcTerms_accessRights.option.values=ar_everyone~ar_archeologists~ar_restricted~ar_noAccess
        if ("ar_everyone".equals(oldValue)) {
            return AccessCategory.OPEN_ACCESS_FOR_REGISTERED_USERS.toString();
        } else if ("ar_archeologists".equals(oldValue)) {
            return AccessCategory.GROUP_ACCESS.toString();
        } else if ("ar_restricted".equals(oldValue)) {
            return AccessCategory.REQUEST_PERMISSION.toString();
        } else {
            return AccessCategory.NO_ACCESS.toString();
        }
    }

}
