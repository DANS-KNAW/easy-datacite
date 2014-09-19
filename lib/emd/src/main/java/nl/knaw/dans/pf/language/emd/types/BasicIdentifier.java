package nl.knaw.dans.pf.language.emd.types;

import java.net.URI;
import java.util.Locale;

/**
 * A {@link BasicString} who's value is most likely an encoding in compliance with a certain scheme that has a formal identification system expressed as a URI.
 * 
 * @author ecco
 */
public class BasicIdentifier extends BasicString {

    private static final long serialVersionUID = 3064530380288101430L;

    private URI identificationSystem;

    /**
     * Constructs a BasicIdentifier.
     */
    public BasicIdentifier() {
        super();
    }

    /**
     * Save constructor of a BasicIdentifier.
     * 
     * @param value
     *        the value of this BasicString
     * @param locale
     *        the java representation of a language token
     * @param scheme
     *        any string that codes for a scheme
     * @throws InvalidLanguageTokenException
     *         if a language token could not be parsed from the given locale
     * @see #setLanguage(Locale)
     */
    public BasicIdentifier(final String value, final Locale locale, final String scheme) throws InvalidLanguageTokenException {
        super(value, locale, scheme);
    }

    /**
     * Save constructor of a BasicIdentifier.
     * 
     * @param value
     *        the value of this BasicIdentifier
     * @param locale
     *        the java representation of a language token
     * @throws InvalidLanguageTokenException
     *         if a language token could not be parsed from the given locale
     * @see #setLanguage(Locale)
     */
    public BasicIdentifier(final String value, final Locale locale) throws InvalidLanguageTokenException {
        super(value, locale);
    }

    /**
     * Constructs a BasicIdentifier.
     * 
     * @param value
     *        the value of this BasicIdentifier
     * @param language
     *        the language of this BasicIdentifier
     * @param scheme
     *        any string that codes for a scheme
     * @throws InvalidLanguageTokenException
     *         if the language does not conform to the regular expression in {@link #LANGUAGE_TOKEN}
     */
    public BasicIdentifier(final String value, final String language, final String scheme) throws InvalidLanguageTokenException {
        super(value, language, scheme);
    }

    /**
     * Constructs a BasicIdentifier.
     * 
     * @param value
     *        the value of this BasicIdentifier
     * @param language
     *        the language of this BasicIdentifier
     * @throws InvalidLanguageTokenException
     *         if the language does not conform to the regex in {@link #LANGUAGE_TOKEN}
     */
    public BasicIdentifier(final String value, final String language) throws InvalidLanguageTokenException {
        super(value, language);
    }

    /**
     * Constructs a BasicIdentifier.
     * 
     * @param value
     *        the value of this BasicIdentifier
     */
    public BasicIdentifier(final String value) {
        super(value);
    }

    /**
     * Get the formal identification system.
     * 
     * @return the formal identification system
     */
    public URI getIdentificationSystem() {
        return identificationSystem;
    }

    /**
     * Set the formal identification system.
     * 
     * @param identificationSystem
     *        the formal identification system
     */
    public void setIdentificationSystem(final URI identificationSystem) {
        this.identificationSystem = identificationSystem;
    }

}
