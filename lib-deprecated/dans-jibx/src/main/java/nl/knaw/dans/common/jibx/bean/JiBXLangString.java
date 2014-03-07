package nl.knaw.dans.common.jibx.bean;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.regex.Pattern;

import nl.knaw.dans.common.lang.repo.bean.XMLLangString;

/**
 * Represents a string that can be localized with a language token. Methods in this class intermediate between the java
 * representation of a locale and the xml lang attribute.
 * 
 * @author ecco
 */
public class JiBXLangString implements XMLLangString
{

    /**
     * Serial version id.
     */
    private static final long serialVersionUID = -5033270791432895464L;

    /**
     * Holds the language token.
     */
    private String language;
    private String value;

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
        return token == null ? false : Pattern.matches(XMLLangString.LANGUAGE_TOKEN, token);
    }

    /**
     * Collects the 3-letter language code and, if appropriate, the 3-letter country code from the given locale.
     * 
     * @param locale
     *        a locale constructed with 2-letter codes
     * @return 3-letter [+ 3-letter] language token
     * @throws IllegalArgumentException
     *         if the 3-letter language token could not be collected
     */
    public static synchronized String getLanguageToken(final Locale locale) throws IllegalArgumentException
    {
        String lang = locale.getLanguage();
        String country = locale.getCountry();

        if (lang.length() != 2)
        {
            throw new IllegalArgumentException("Cannot look up 3-letter language code for " + lang);
        }

        if (country.length() != 2 && country.length() != 0)
        {
            throw new IllegalArgumentException("Cannot look up 3-letter country code for " + country);
        }

        try
        {
            lang = locale.getISO3Language();
        }
        catch (final MissingResourceException e)
        {
            throw new IllegalArgumentException(e);
        }

        if (country.length() == 2)
        {
            try
            {
                country = locale.getISO3Country();
            }
            catch (final MissingResourceException e)
            {
                throw new IllegalArgumentException(e);
            }
        }

        return lang + ("".equals(country) ? "" : "-" + country);
    }

    /**
     * Constructs a {@link JiBXLangString} with no value and no language token.
     */
    public JiBXLangString()
    {
        super();
    }

    /**
     * Constructs a {@link JiBXLangString} with the given value and no language token.
     * 
     * @param value
     *        the value of the string
     */
    public JiBXLangString(final String value)
    {
        this.value = value;
    }

    /**
     * Constructs a {@link JiBXLangString} with the given value and language token.
     * 
     * @param value
     *        the value of the string
     * @param language
     *        the language token
     * @throws IllegalArgumentException
     *         if it fails the regular expression test
     * @see #isValidLanguageToken(String)
     */
    public JiBXLangString(final String value, final String language) throws IllegalArgumentException
    {
        this.value = value;
        setLanguage(language);
    }

    /**
     * Constructs a {@link JiBXLangString} with the given value; constructs a language token from the given
     * locale.
     * 
     * @param value
     *        the value of the string
     * @param locale
     *        a valid Locale constructed with 2-letter parameters
     * @throws IllegalArgumentException
     *         if 3-letter language and/or country tokens could not be parsed
     * @see #setLanguage(Locale)
     */
    public JiBXLangString(final String value, final Locale locale) throws IllegalArgumentException
    {
        this.value = value;
        setLanguage(locale);
    }

    /* (non-Javadoc)
     * @see nl.knaw.dans.common.jibx.bean.XMLLangString#getLanguage()
     */
    public String getLanguage()
    {
        return language;
    }

    /* (non-Javadoc)
     * @see nl.knaw.dans.common.jibx.bean.XMLLangString#setLanguage(java.lang.String)
     */
    public final void setLanguage(final String language) throws IllegalArgumentException
    {
        if (language == null || "".equals(language) || isValidLanguageToken(language))
        {
            this.language = language;
        }
        else
        {
            throw new IllegalArgumentException("The token '" + language + "' is not a valid language token.");
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
     * @throws IllegalArgumentException
     *         if a language token could not be parsed from the given locale
     */
    public final void setLanguage(final Locale locale) throws IllegalArgumentException
    {
        this.language = getLanguageToken(locale);
    }

    /* (non-Javadoc)
     * @see nl.knaw.dans.common.jibx.bean.XMLLangString#getValue()
     */
    public String getValue()
    {
        return value;
    }

    /* (non-Javadoc)
     * @see nl.knaw.dans.common.jibx.bean.XMLLangString#setValue(java.lang.String)
     */
    public void setValue(final String value)
    {
        this.value = value;
    }

}
