package nl.knaw.dans.pf.language.emd.types;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.regex.Pattern;

/**
 * Represents a string that can be localized with a language token. Methods in this class intermediate between the java
 * representation of a locale and the xml lang attribute.
 *
 * @author ecco
 */
public abstract class LanguageTokenizedString extends SimpleElementImpl<String>
{

    /**
     * Pattern to check the validity of the language token. {@value}
     */
    public static final String LANGUAGE_TOKEN = "([a-zA-Z]{2,3}|[il]-[a-zA-Z]+|[xX]-[a-zA-Z]{1,8})(-[a-zA-Z]{2,8})*";

    /**
     * Serial version id.
     */
    private static final long serialVersionUID = -5033270791432895464L;

    // ecco: CHECKSTYLE: OFF
    /**
     * Holds the language token.
     */
    protected String language; // keep modifier protected, JiBX must see it from sub classes

    // ecco: CHECKSTYLE: ON

    /**
     * Test if the given string is a valid language token. Does not say the given string belongs to any standard. See
     * RFC 3066 at http://www.ietf.org/rfc/rfc3066.txt and the IANA registry at
     * http://www.iana.org/assignments/lang-tag-apps.htm for further information, as well as
     * http://www.ietf.org/rfc/rfc4646.txt
     * <p>
     * Examples of valid language tokens as conceived by this method are:
     *
     * <pre>
     *     nl
     *     en-US
     *     nld
     *     nld-NLD
     * </pre>
     *
     * </p>
     *
     * @param token
     *        two or three letter language code, optionally followed by a hyphen and a two or three letter country code
     * @return <code>true</code> if valid, <code>false</code> otherwise
     */
    public static synchronized boolean isValidLanguageToken(final String token)
    {
        return token == null ? false : Pattern.matches(LANGUAGE_TOKEN, token);
    }

    /**
     * Collects the 3-letter language code and, if appropriate, the 3-letter country code from the given locale.
     * @param locale a locale constructed with 2-letter codes
     * @return 3-letter [+ 3-letter] language token
     * @throws InvalidLanguageTokenException if the 3-letter language token could not be collected
     */
    public static synchronized String getLanguageToken(final Locale locale) throws InvalidLanguageTokenException
    {
        String lang = locale.getLanguage();
        String country = locale.getCountry();

        if (lang.length() != 2)
        {
            throw new InvalidLanguageTokenException("Cannot look up 3-letter language code for " + lang);
        }

        if (country.length() != 2 && country.length() != 0)
        {
            throw new InvalidLanguageTokenException("Cannot look up 3-letter country code for " + country);
        }

        try
        {
            lang = locale.getISO3Language();
        }
        catch (final MissingResourceException e)
        {
            throw new InvalidLanguageTokenException(e);
        }

        if (country.length() == 2)
        {
            try
            {
                country = locale.getISO3Country();
            }
            catch (final MissingResourceException e)
            {
                throw new InvalidLanguageTokenException(e);
            }
        }

        return lang + ("".equals(country) ? "" : "-" + country);
    }

    /**
     * Constructs a {@link LanguageTokenizedString} with no value and no language token.
     */
    public LanguageTokenizedString()
    {
        super();
    }

    /**
     * Constructs a {@link LanguageTokenizedString} with the given value and no language token.
     *
     * @param value
     *        the value of the string
     */
    public LanguageTokenizedString(final String value)
    {
        setValue(value);
    }

    /**
     * Constructs a {@link LanguageTokenizedString} with the given value and language token.
     *
     * @param value
     *        the value of the string
     * @param language
     *        the language token
     * @throws InvalidLanguageTokenException
     *         if it fails the regular expression test
     * @see #isValidLanguageToken(String)
     */
    public LanguageTokenizedString(final String value, final String language) throws InvalidLanguageTokenException
    {
        setValue(value);
        setLanguage(language);
    }

    /**
     * Constructs a {@link LanguageTokenizedString} with the given value; constructs a language token from the given
     * locale.
     *
     * @param value
     *        the value of the string
     * @param locale
     *        a valid Locale constructed with 2-letter parameters
     * @throws InvalidLanguageTokenException
     *         if 3-letter language and/or country tokens could not be parsed
     * @see #setLanguage(Locale)
     */
    public LanguageTokenizedString(final String value, final Locale locale) throws InvalidLanguageTokenException
    {
        setValue(value);
        setLanguage(locale);
    }

    /**
     * Get the language token.
     *
     * @return the language token, or <code>null</code>
     */
    public String getLanguage()
    {
        return language;
    }

    /**
     * Set the language token. Acceptable values are <code>null</code>, the empty string and a string conforming to
     * the regular expression of {@link #LANGUAGE_TOKEN}.
     *
     * @param language
     *        the language token
     * @throws InvalidLanguageTokenException
     *         if the token is not valid
     */
    public final void setLanguage(final String language) throws InvalidLanguageTokenException
    {
        if (language == null || "".equals(language) || isValidLanguageToken(language))
        {
            this.language = language;
        }
        else
        {
            throw new InvalidLanguageTokenException("The token '" + language + "' is not a valid language token.");
        }
    }

    /**
     * Save method to set the language token; it looks up 3-letter language and country codes.
     * <p>
     * The Locale argument should be constructed according to the Locale API. Quoted from {@link Locale}: <blockquote>
     * <p>
     * The language argument is a valid ISO Language Code. These codes are the lower-case, two-letter codes as defined
     * by ISO-639.
     * </p>
     * <p>
     * The country argument is a valid ISO Country Code. These codes are the upper-case, two-letter codes as defined by
     * ISO-3166.
     * </p>
     * </blockquote>
     *
     * @param locale
     *        the java representation of a language token
     * @throws InvalidLanguageTokenException
     *         if a language token could not be parsed from the given locale
     */
    public final void setLanguage(final Locale locale) throws InvalidLanguageTokenException
    {
        this.language = getLanguageToken(locale);
    }

    /**
     * Get the value.
     *
     * @return the value, or <code>null</code>
     */
    @Override
    // needed for JiBX when using generic classes
    public String getValue()
    {
        return super.getValue();
    }

    /**
     * Set the value.
     *
     * @param value
     *        the value, or <code>null</code>
     */
    @Override
    // needed for JiBX when using generic classes
    public void setValue(final String value)
    {
        super.setValue(value);
    }

}
