package nl.knaw.dans.pf.language.emd.types;

import java.util.Locale;

import org.joda.time.DateTime;

/**
 * A date holder that is essentially a string. So the date can be anything from "march 28" to "foo to bar". However, if
 * the scheme of this BasicDate is said to be {@link EmdConstants.DateScheme#W3CDTF}, the validity of the value in respect
 * to the scheme is checked.
 *
 * @see IsoDate
 * @author ecco
 */
public final class BasicDate extends LanguageTokenizedString
{

    /**
     * Serial version id.
     */
    private static final long serialVersionUID = -5096088175356626732L;

    /**
     * Holds the scheme.
     */
    private EmdConstants.DateScheme scheme;

    /**
     * Holds the DateTime.
     */
    private transient DateTime dateTime;

    /**
     * Constructs a BasicDate.
     */
    public BasicDate()
    {
        super();
    }

    /**
     * Constructs a BasicDate.
     *
     * @param value
     *        the value of this BasicDate
     * @see #setValue(String)
     */
    public BasicDate(final String value)
    {
        super(value);
    }

    /**
     * Constructs a BasicDate.
     *
     * @param value
     *        the value of this BasicDate
     * @param language
     *        the language of this BasicDate
     * @throws InvalidLanguageTokenException
     *         if the language does not conform to the regular expression in {@link #LANGUAGE_TOKEN}
     * @see #setValue(String)
     * @see #setLanguage(String)
     */
    public BasicDate(final String value, final String language) throws InvalidLanguageTokenException
    {
        super(value, language);
    }

    /**
     * Save constructor of a BasicDate.
     *
     * @param value
     *        the value of this BasicDate
     * @param locale
     *        the java representation of a language token
     * @throws InvalidLanguageTokenException
     *         if a language token could not be parsed from the given locale
     * @see #setValue(String)
     * @see #setLanguage(Locale)
     */
    public BasicDate(final String value, final Locale locale) throws InvalidLanguageTokenException
    {
        super(value, locale);
    }

    /**
     * Constructs a BasicDate.
     *
     * @param value
     *        the value of this BasicDate
     * @param language
     *        the language of this BasicDate
     * @param scheme
     *        the scheme for this BasicDate
     * @throws InvalidLanguageTokenException
     *         if the language does not conform to the regular expression in {@link #LANGUAGE_TOKEN}
     * @throws IllegalStateException
     *         if the scheme is {@link EmdConstants.DateScheme#W3CDTF} and the value of this BasicDate is not in compliance
     *         with ISO8601
     * @see #setValue(String)
     * @see #setLanguage(String)
     */
    public BasicDate(final String value, final String language, final EmdConstants.DateScheme scheme) throws InvalidLanguageTokenException,
            IllegalStateException
    {
        super(value, language);
        setScheme(scheme);
    }

    /**
     * Save constructor of a BasicDate.
     *
     * @param value
     *        the value of this BasicDate
     * @param locale
     *        the java representation of a language token
     * @param scheme
     *        the scheme for this BasicDate
     * @throws InvalidLanguageTokenException
     *         if a language token could not be parsed from the given locale
     * @throws IllegalStateException
     *         if the scheme is {@link EmdConstants.DateScheme#W3CDTF} and the value of this BasicDate is not in compliance
     *         with ISO8601
     * @see #setValue(String)
     * @see #setLanguage(Locale)
     */
    public BasicDate(final String value, final Locale locale, final EmdConstants.DateScheme scheme) throws InvalidLanguageTokenException, IllegalStateException
    {
        super(value, locale);
        setScheme(scheme);
    }

    /**
     * Sets the inner value of this BasicDate to the given value. If the value is in compliance with ISO8601, the inner
     * date field will be set. If the value is in compliance with ISO8601 and the scheme of this BasicDate is
     * <code>null</code>, the scheme will be set to {@link EmdConstants.DateScheme#W3CDTF}.
     *
     * @param value
     *        the new value for this BasicDate
     */
    @Override
    public void setValue(final String value)
    {
        super.setValue(value == null ? null : value.trim());
        setDateTimeAndScheme();
    }

    private void setDateTimeAndScheme()
    {
        dateTime = null;
        if (getValue() != null)
        {
            try
            {
                dateTime = new DateTime(getValue());
                if (scheme == null)
                {
                    scheme = EmdConstants.DateScheme.W3CDTF;
                }
            }
            // ecco: CHECKSTYLE: OFF
            catch (RuntimeException e)
            {
                if (EmdConstants.DateScheme.W3CDTF.equals(scheme))
                {
                    scheme = null;
                }
            }
            // ecco: CHECKSTYLE: ON
        }
    }

    /**
     * Get the value of this BasicDate as a DateTime, or <code>null</code> if the value of this BasicDate is not in
     * compliance with ISO8601.
     *
     * @return the DateTime or <code>null</code>
     */
    public DateTime getDateTime()
    {
        return dateTime;
    }

    /**
     * Get the scheme this {@link BasicDate} is said to be in compliance with.
     *
     * @return the scheme
     */
    public EmdConstants.DateScheme getScheme()
    {
        return scheme;
    }

    /**
     * Set the scheme on this {@link BasicDate}.
     *
     * @param scheme
     *        the scheme for this BasicDate
     * @throws IllegalStateException
     *         if the scheme is {@link EmdConstants.DateScheme#W3CDTF} and the value of this BasicDate is not in compliance
     *         with ISO8601
     */
    public void setScheme(final EmdConstants.DateScheme scheme) throws IllegalStateException
    {
        if (isValidScheme(scheme, getValue()))
        {
            this.scheme = scheme;
        }
        else
        {
            throw new IllegalStateException("The value '" + getValue() + "' is not in compliance with the scheme '" + scheme + "'");
        }
    }

    /**
     * Test the validity of a scheme with respect to the given value.
     *
     * @param schemeToTest
     *        the scheme to test
     * @param withValue
     *        the string to test
     * @return <code>true</code> if valid, <code>false </code> otherwise
     */
    public static synchronized boolean isValidScheme(final EmdConstants.DateScheme schemeToTest, final String withValue)
    {
        boolean isValid = true;
        if (EmdConstants.DateScheme.W3CDTF.equals(schemeToTest))
        {
            isValid = isISODateString(withValue);
        }
        return isValid;
    }

    /**
     * Test if the given string is a valid ISO8601 date string.
     *
     * @param toTest
     *        the string to test
     * @return <code>true</code> if valid, <code>false </code> otherwise
     */
    public static synchronized boolean isISODateString(final String toTest)
    {
        boolean isIso = false;
        if (toTest != null)
        {
            try
            {
                new DateTime(toTest);
                isIso = true;
            }
            // ecco: CHECKSTYLE: OFF
            catch (final RuntimeException e)
            {
                isIso = false;
            }
            // ecco: CHECKSTYLE: ON
        }
        return isIso;
    }

    public boolean isComplete()
    {
        return true;
    }

}
