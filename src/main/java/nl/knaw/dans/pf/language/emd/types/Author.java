package nl.knaw.dans.pf.language.emd.types;

import java.net.URI;

import nl.knaw.dans.common.lang.id.DAI;

import org.apache.commons.lang.StringUtils;

/**
 * Author of a resource.
 * <p/>
 * An optional entityId can relate an author to a common entity through an identification system. The
 * default identification system is the Digital Author Identification (DAI).
 * 
 * @see <a
 *      href="http://www.rug.nl/bibliotheek/informatie/digitaleBibliotheek/daikort">digitaleBibliotheek/daikort</a>
 * @author ecco
 */
public class Author implements MetadataItem
{

    /**
     * The default identification system. {@value}
     */
    public static final String DEFAULT_SCHEME = EmdConstants.SCHEME_DAI;

    /**
     *
     */
    private static final long serialVersionUID = -8429016201723749485L;

    private String title;
    private String initials;
    private String prefix;
    private String surname;
    private String organization;
    private URI identificationSystem;
    private String entityId;
    private String scheme;

    /**
     * Constructs an Author.
     */
    public Author()
    {
        super();
    }

    /**
     * Constructs an Author.
     * 
     * @param title
     *        title(s) of the author, may be <code>null</code>
     * @param initials
     *        initials, may be <code>null</code>
     * @param prefix
     *        prefix(es), may be <code>null</code>
     * @param surname
     *        surname, may be <code>null</code>
     */
    public Author(final String title, final String initials, final String prefix, final String surname)
    {
        setTitle(title);
        setInitials(initials);
        setPrefix(prefix);
        this.surname = surname;
    }

    /**
     * A string-representation of this author.
     * 
     * @return string-representation of this author
     */
    public String toString()
    {
        return (surname == null || "".equals(surname) ? "" : surname + ", ") + (title == null || "".equals(title) ? "" : title + " ")
                + (initials == null || "".equals(initials) ? "" : initials) + (prefix == null || "".equals(prefix) ? "" : " " + prefix)
                + (organization == null ? "" : hasPersonalEntries() ? " (" + organization + ")" : organization);
    }

    /**
     * Get this author's titles, may be <code>null</code>.
     * 
     * @return the authors titles
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Set this author's title(s).
     * 
     * @param title
     *        title(s) of the author, may be <code>null</code>
     */
    public final void setTitle(final String title)
    {
        this.title = title == null ? null : title.trim();
    }

    /**
     * Get this author's initials.
     * 
     * @return this author's initials, may be <code>null</code>
     */
    public String getInitials()
    {
        return initials;
    }

    /**
     * Set this author's initials.
     * 
     * @param initials
     *        this author's initials, may be <code>null</code>
     */
    public final void setInitials(final String initials)
    {
        this.initials = initials == null ? null : initials.trim().toUpperCase();
    }

    /**
     * Get this author's prefix.
     * 
     * @return this author's prefix, may be <code>null</code>
     */
    public String getPrefix()
    {
        return prefix == null ? null : prefix.trim();
    }

    /**
     * Set this author's prefix.
     * 
     * @param prefix
     *        this author's prefix, may be <code>null</code>
     */
    public final void setPrefix(final String prefix)
    {
        this.prefix = prefix;
    }

    /**
     * Get this author's surname.
     * 
     * @return this author's surname, may be <code>null</code>
     */
    public String getSurname()
    {
        return surname;
    }

    /**
     * Set this author's surname.
     * 
     * @param surname
     *        this author's surname, may be <code>null</code>
     */
    public final void setSurname(final String surname)
    {
        this.surname = surname == null ? null : surname.trim();
    }

    public String getOrganization()
    {
        return organization;
    }

    public void setOrganization(String organization)
    {
        this.organization = organization;
    }

    /**
     * Get the scheme by which this author is identified.
     * 
     * @return the scheme by which this author is identified
     */
    public String getScheme()
    {
        return scheme;
    }

    /**
     * Set the scheme by which this author is identified.
     * 
     * @param scheme
     *        the scheme by which this author is identified
     */
    public void setScheme(final String scheme)
    {
        this.scheme = scheme;
    }

    /**
     * Get this author's identification system.
     * 
     * @return this author's identification system, may be <code>null</code>
     */
    public URI getIdentificationSystem()
    {
        return identificationSystem;
    }

    /**
     * Set this author's identification system.
     * 
     * @param identificationSystem
     *        this author's identification system, may be <code>null</code>
     */
    public void setIdentificationSystem(final URI identificationSystem)
    {
        this.identificationSystem = identificationSystem;
    }

    /**
     * Get the entity id of this author.
     * 
     * @return the entity id of this author, may be <code>null</code>
     */
    public String getEntityId()
    {
        return entityId;
    }

    /**
     * Set the entity id of this author. If the scheme was not yet set, it is set to
     * "DAI".
     * 
     * @param entityId
     *        the entity id of this author
     */
    public void setEntityId(final String entityId)
    {
        this.entityId = entityId;
        if (scheme == null)
        {
            scheme = DEFAULT_SCHEME;
        }
    }

    /**
     * Set entity id and identification system of this author.
     * 
     * @param entityId
     *        the entity id of this author
     * @param scheme
     *        formal name of the identification system
     */
    public void setEntityId(final String entityId, final String scheme)
    {
        this.entityId = entityId;
        this.scheme = scheme;
    }

    public boolean isComplete()
    {
        return (hasPersonalEntries() && StringUtils.isNotBlank(surname) && StringUtils.isNotBlank(initials)) || StringUtils.isNotBlank(organization);
    }

    @Override
    public String getSchemeId()
    {
        // we have no schemeId
        return null;
    }

    private boolean hasPersonalEntries()
    {
        return StringUtils.isNotBlank(entityId) || StringUtils.isNotBlank(initials) || StringUtils.isNotBlank(prefix) || StringUtils.isNotBlank(surname)
                || StringUtils.isNotBlank(title);
    }

    public boolean hasDigitalAuthorId()
    {
        return EmdConstants.SCHEME_DAI.equals(scheme) && DAI.isValid(entityId);
    }

    public DAI getDigitalAuthorId()
    {
        if (!hasDigitalAuthorId())
        {
            return null;
        }
        else
        {
            return new DAI(entityId);
        }
    }

}
