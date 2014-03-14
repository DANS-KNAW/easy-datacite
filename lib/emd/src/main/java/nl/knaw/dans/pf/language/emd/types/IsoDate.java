package nl.knaw.dans.pf.language.emd.types;

import nl.knaw.dans.pf.language.emd.util.Converter;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Wrapper for a {@link DateTime}.
 * 
 * @author ecco
 */
public class IsoDate extends SimpleElementImpl<DateTime>
{

    /**
     * The format to represent the wrapped DateTime as a string.
     * 
     * @author ecco
     */
    public enum Format
    {
        /**
         * represent the date in a pattern "yyyy-MM-dd'T'HH:mm:ss.SSSZ".
         */
        MILLISECOND("yyyy-MM-dd'T'HH:mm:ss.SSSZ"),
        /**
         * represent the date in a pattern "yyyy-MM-dd'T'HH:mm:ss".
         */
        SECOND("yyyy-MM-dd'T'HH:mm:ss"),
        /**
         * represent the date in a pattern "yyyy-MM-dd'T'HH:mm".
         */
        MINUTE("yyyy-MM-dd'T'HH:mm"),
        /**
         * represent the date in a pattern "yyyy-MM-dd'T'HH".
         */
        HOUR("yyyy-MM-dd'T'HH"),
        /**
         * represent the date in a pattern "yyyy-MM-dd".
         */
        DAY("yyyy-MM-dd"),
        /**
         * represent the string in a pattern "yyyy-MM".
         */
        MONTH("yyyy-MM"),
        /**
         * represent the string in a pattern "yyyy".
         */
        YEAR("yyyy");

        // ecco: CHECKSTYLE: OFF
        /**
         * The pattern of this format.
         */
        public final String pattern;

        // ecco: CHECKSTYLE: ON

        /**
         * Constructor.
         * 
         * @param pattern
         *        the pattern of this format
         */
        private Format(final String pattern)
        {
            this.pattern = pattern;
        }
    }

    /**
     * The default format.
     */
    public static final Format DEFAULT_FORMAT = Format.DAY;

    /**
     *
     */
    private static final long serialVersionUID = 6221080192319225316L;

    private final EmdConstants.DateScheme scheme = EmdConstants.DateScheme.W3CDTF;

    private Format format = DEFAULT_FORMAT;

    /**
     * Creates a {@link DateTime} from a string.
     * 
     * @param iso8601String
     *        string to create a date from
     * @return a new DateTime instance
     * @throws InvalidDateStringException
     *         if the given string, stripped of leading and trailing whitespace, was not in compliance
     *         with ISO8601
     */
    public static synchronized DateTime convert(final String iso8601String) throws InvalidDateStringException
    {
        DateTime dateTime;
        try
        {
            dateTime = new DateTime(iso8601String.trim(), Converter.LOCAL_TIME_ZONE);
        }
        // ecco: CHECKSTYLE: OFF
        catch (final RuntimeException e)
        // ecco: CHECKSTYLE: ON
        {
            final String msg = "Not a ISO8601 compliant date string: " + iso8601String;
            throw new InvalidDateStringException(msg, e);
        }
        return dateTime;
    }

    /**
     * Poor man's implementation to get the format of a given date string in ISO8601 notation.
     * 
     * @param dateString
     *        the string to determine the format of.
     * @return YEAR, MONTH, or DAY format for strings with lengths corresponding to said format's
     *         patterns, MILLISECCOND format for all other strings
     */
    private static synchronized Format forString(final String dateString)
    {
        Format format = null;
        switch (dateString.trim().length())
        {
        case 4:
            format = Format.YEAR;
            break;
        case 7:
            format = Format.MONTH;
            break;
        case 10:
            format = Format.DAY;
            break;
        case 13:
            format = Format.HOUR;
            break;
        case 16:
            format = Format.MINUTE;
            break;
        case 19:
            format = Format.SECOND;
        default:
            format = Format.MILLISECOND;
            break;
        }
        return format;
    }

    /**
     * Constructs a new ISODate with it's inner field <code>value</code> set to the current date and
     * time.
     */
    public IsoDate()
    {
        super();
        value = new DateTime();
    }

    /**
     * Constructs a new ISODate with it's inner field <code>value</code> set to the given object.
     * 
     * @param dateTime
     *        a DateTime
     */
    public IsoDate(final DateTime dateTime)
    {
        this.value = dateTime;
    }

    /**
     * Constructs a new ISODate with it's inner field <code>value</code> set to an instance of DateTime
     * constructed with the given string.
     * 
     * @param iso8601String
     *        string to create the date from
     * @throws InvalidDateStringException
     *         if the given string, stripped of leading and trailing whitespace, was not in compliance
     *         with ISO8601
     */
    public IsoDate(final String iso8601String) throws InvalidDateStringException
    {
        value = convert(iso8601String);
        format = forString(iso8601String);
    }

    /**
     * Set inner field <code>value</code> set to an instance of DateTime constructed with the given
     * string.
     * 
     * @param iso8601String
     *        string to create the date from
     * @throws InvalidDateStringException
     *         if the given string, stripped of leading and trailing whitespace, was not in compliance
     *         with ISO8601
     */
    public void setValueAsString(final String iso8601String) throws InvalidDateStringException
    {
        value = convert(iso8601String);
        format = forString(iso8601String);
    }

    /**
     * Returns a string representation of the inner field <code>value</code> in this ISODate's format.
     * 
     * @return a string representation of the inner field <code>value</code> in this ISODate's format
     */
    public String toString()
    {
        if (getValue() == null)
        {
            return "null";
        }
        else
        {
            return value.toString(DateTimeFormat.forPattern(format.pattern));// .withZone(Converter.EASY_TIME_ZONE));
        }
    }

    /**
     * Returns a string representation of the inner field <code>value</code> in
     * {@link Format#MILLISECOND}.
     * 
     * @return a string representation of the inner field <code>value</code> in
     *         {@link Format#MILLISECOND}
     */
    public String getValueAsString()
    {
        if (getValue() == null)
        {
            return "null";
        }
        else
        {
            return value.toString(DateTimeFormat.forPattern(Format.MILLISECOND.pattern).withZone(Converter.LOCAL_TIME_ZONE));
        }
    }

    /**
     * Get the pattern of this ISODate's format.
     * 
     * @return the pattern of the format
     */
    public String getPattern()
    {
        return format.pattern;
    }

    /**
     * Get the format of this ISODate.
     * 
     * @return the format of this ISODate
     */
    public Format getFormat()
    {
        return format;
    }

    /**
     * Set the format of this ISODate.
     * 
     * @param format
     *        the format for this ISODate
     */
    public void setFormat(final Format format)
    {
        this.format = format;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    // needed for JiBX when using generic classes
    public DateTime getValue()
    {
        return super.getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    // needed for JiBX when using generic classes
    public void setValue(final DateTime value)
    {
        super.setValue(value);
    }

    /**
     * Always returns {@link EmdConstants.DateScheme#W3CDTF}.
     * 
     * @return {@link EmdConstants.DateScheme#W3CDTF}
     */
    public EmdConstants.DateScheme getScheme()
    {
        return scheme;
    }

    public boolean isComplete()
    {
        return true;
    }

}
