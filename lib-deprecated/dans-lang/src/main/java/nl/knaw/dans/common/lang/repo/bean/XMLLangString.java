package nl.knaw.dans.common.lang.repo.bean;

import java.io.Serializable;
import java.util.Locale;

/**
 * String that can have a language token.
 * 
 * @author ecco Sep 27, 2009
 */
public interface XMLLangString extends Serializable
{

    /**
     * Pattern to check the validity of the language token. {@value}
     */
    String LANGUAGE_TOKEN = "([a-zA-Z]{2,3}|[il]-[a-zA-Z]+|[xX]-[a-zA-Z]{1,8})(-[a-zA-Z]{2,8})*";

    /**
     * Get the language token.
     * 
     * @return the language token, or <code>null</code>
     */
    String getLanguage();

    /**
     * Set the language token. Acceptable values are <code>null</code>, the empty string and a string conforming to the
     * regular expression of {@link #LANGUAGE_TOKEN}.
     * 
     * @param language
     *        the language token
     * @throws IllegalArgumentException
     *         if the token is not valid
     */
    void setLanguage(final String language) throws IllegalArgumentException;

    /**
     * Get the value.
     * 
     * @return the value, or <code>null</code>
     */
    String getValue();

    /**
     * Set the value.
     * 
     * @param value
     *        the value, or <code>null</code>
     */
    void setValue(final String value);

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
    void setLanguage(final Locale locale) throws IllegalArgumentException;

}
