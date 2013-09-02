package nl.knaw.dans.easy.web.template;

import java.util.Locale;

import org.apache.wicket.markup.html.link.Link;

/**
 * Link to switch locale.
 * 
 * @author Herman Suijs
 */
public final class LocaleLink extends Link
{
    /**
     * Dummy serial version uid.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Locale for this link.
     */
    private final Locale locale;

    /**
     * Constructor for a new LocaleLink.
     * 
     * @param wicketId
     *        Id of wicket component
     * @param localeString
     *        Locale to switch to
     */
    public LocaleLink(final String wicketId, final String localeString)
    {
        super(wicketId);
        this.locale = new Locale(localeString);
    }

    /**
     * Constructor with locale.
     * 
     * @param wicketId
     *        Id of wicket component
     * @param locale
     *        Locale to switch to
     */
    public LocaleLink(final String wicketId, final Locale locale)
    {
        super(wicketId);
        this.locale = locale;
    }

    /**
     * On click of a user.
     */
    @Override
    public void onClick()
    {
        this.getSession().setLocale(this.locale);
    }

    /**
     * Determine if enabled.
     * 
     * @return True if locale for component is different from locale in session.
     */
    @Override
    public boolean isEnabled()
    {
        return !this.getSession().getLocale().equals(this.locale);
    }

    /**
     * Make Localelink stateless.
     */
    @Override
    public boolean getStatelessHint() // NOPMD: wicket method.
    {
        return true;
    }

}
