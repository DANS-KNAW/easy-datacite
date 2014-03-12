package nl.knaw.dans.pf.language.emd;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.knaw.dans.common.jibx.bean.JiBXDublinCoreMetadata;
import nl.knaw.dans.common.lang.repo.AbstractTimestampedObject;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata.PropertyName;
import nl.knaw.dans.pf.language.emd.exceptions.NoSuchTermException;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.pf.language.emd.types.MetadataItem;

/**
 * Implementation of {@link EasyMetadata} with JiBX serialization.
 * 
 * @author ecco
 * @see <a href="package-summary.html#package_description">package description</a>
 */
public class EasyMetadataImpl extends AbstractTimestampedObject implements EasyMetadata
{

    /**
     * The version - when newly instantiated. The actual version of an instance as read from an xml-stream might be
     * obtained by {@link #getVersion()}.
     */
    public static final String EMD_VERSION = "0.1";

    // ecco: CHECKSTYLE: OFF
    private static Map<Term, MDContainer> TERMS_MAP;

    private static Map<Term, MDContainer> TERM_NAMES_MAP;
    // ecco: CHECKSTYLE: ON

    /**
     *
     */
    private static final long serialVersionUID = 6714642886170846806L;

    private boolean versionable;

    private String version;
    private EmdTitle emdTitle;
    private EmdCreator emdCreator;
    private EmdSubject emdSubject;
    private EmdDescription emdDescription;
    private EmdPublisher emdPublisher;
    private EmdContributor emdContributor;
    private EmdDate emdDate;
    private EmdType emdType;
    private EmdFormat emdFormat;
    private EmdIdentifier emdIdentifier;
    private EmdSource emdSource;
    private EmdLanguage emdLanguage;
    private EmdRelation emdRelation;
    private EmdCoverage emdCoverage;
    private EmdRights emdRights;
    private EmdAudience emdAudience;
    private EmdOther emdOther;

    /**
     * JiBX constructor.
     */
    protected EasyMetadataImpl()
    {

    }

    /**
     * Constructor.
     * @throws DomainException 
     */
    public EasyMetadataImpl(MetadataFormat metadataFormat)
    {
        super();
        getEmdOther().getEasApplicationSpecific().setMetadataFormat(metadataFormat);
    }

    /**
     * {@inheritDoc}
     */
    public String getVersion()
    {
        if (version == null)
        {
            version = EMD_VERSION;
        }
        return version;
    }

    public String getUnitFormat()
    {
        return UNIT_FORMAT;
    }

    public URI getUnitFormatURI()
    {
        return UNIT_FORMAT_URI;
    }

    public String getUnitLabel()
    {
        return UNIT_LABEL;
    }

    public String getUnitId()
    {
        return UNIT_ID;
    }

    public boolean isVersionable()
    {
        return versionable;
    }

    public void setVersionable(boolean versionable)
    {
        this.versionable = versionable;
    }

    /**
     * {@inheritDoc}
     */
    public Map<Term, MDContainer> getTermsMap()
    {
        return Collections.unmodifiableMap(getTermMDContainerMap());
    }

    /**
     * {@inheritDoc}
     */
    public Set<Term> getTerms()
    {
        return getTermsMap().keySet();
    }

    // Returns the TERMS_MAP. Instantiates and populates it if necessary.
    private static synchronized Map<Term, MDContainer> getTermMDContainerMap()
    {
        if (TERMS_MAP == null)
        {
            buidlMaps();
        }
        return TERMS_MAP;
    }

    // Returns the TERMS_NAMES_MAP. Instantiates and populates it if necessary.
    // The keys in this map only contain the term.name. So keep it private.
    private static synchronized Map<Term, MDContainer> getTermNameMDContainerMap()
    {
        if (TERM_NAMES_MAP == null)
        {
            buidlMaps();
        }
        return TERM_NAMES_MAP;
    }

    @SuppressWarnings("unchecked")
    private static void buidlMaps()
    {
        TERMS_MAP = Collections.synchronizedMap(new LinkedHashMap<Term, MDContainer>());
        TERM_NAMES_MAP = Collections.synchronizedMap(new LinkedHashMap<Term, MDContainer>());
        for (MDContainer mdContainer : MDContainer.values())
        {
            try
            {
                final Field classField = EasyMetadataImpl.class.getDeclaredField("emd" + mdContainer.name());
                final Class<? extends EmdContainer> containerType = (Class<? extends EmdContainer>) classField.getType();
                final Field termsField = containerType.getDeclaredField("TERMS");
                final Term[] terms = (Term[]) termsField.get(containerType.newInstance());
                for (Term term : terms)
                {
                    TERMS_MAP.put(term, mdContainer);
                    final Term termN = new Term(term.getName());
                    TERM_NAMES_MAP.put(termN, mdContainer);
                }
            }
            catch (final SecurityException e)
            {
                throw new RuntimeException(e);
            }
            catch (final NoSuchFieldException e)
            {
                throw new RuntimeException(e);
            }
            catch (final IllegalArgumentException e)
            {
                throw new RuntimeException(e);
            }
            catch (final IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
            catch (final InstantiationException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty()
    {
        boolean empty = true;
        for (MDContainer mdContainer : MDContainer.values())
        {
            final EmdContainer container = getContainerByField(mdContainer);
            if (container != null && !container.isEmpty())
            {
                empty = false;
                break;
            }
        }
        return empty;
    }

    /**
     * {@inheritDoc}
     */
    public Object visitChildren(boolean includeEmpty, EmdVisitor visitor)
    {
        Object object = null;
        if (includeEmpty)
        {
            for (MDContainer mdContainer : MDContainer.values())
            {
                final EmdContainer container = getContainerByMethod(mdContainer);
                object = visitor.container(container);
            }
        }
        else
        {
            for (MDContainer mdContainer : MDContainer.values())
            {
                final EmdContainer container = getContainerByField(mdContainer);
                if (container != null && !container.isEmpty())
                {
                    object = visitor.container(container);
                }
            }
        }
        return object;
    }

    /**
     * {@inheritDoc}
     */
    public String toString(final String separator)
    {
        final StringBuilder builder = new StringBuilder();
        for (MDContainer mdContainer : MDContainer.values())
        {
            final EmdContainer container = getContainerByField(mdContainer);
            if (container != null)
            {
                builder.append(container.toString(separator, true));
            }
        }
        builder.delete(0, 1);
        return builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    public String toString(final String separator, final MDContainer mdContainer)
    {
        return getContainerByMethod(mdContainer).toString(separator);
    }

    /**
     * {@inheritDoc}
     */
    public String toString(final String separator, final Term term) throws NoSuchTermException
    {
        return getContainerByMethod(term).toString(separator, term);
    }

    /**
     * {@inheritDoc}
     */
    public String toString(final String separator, final Term.Name termName) throws NoSuchTermException
    {
        return getContainerByMethod(new Term(termName)).toString(separator, termName);
    }

    /**
     * {@inheritDoc}
     */
    public String getPreferredTitle()
    {
        return getEmdTitle().getPreferredTitle();
    }

    /**
     * {@inheritDoc}
     */
    public EmdContainer getContainer(final MDContainer mdContainer, final boolean returnNull)
    {
        if (returnNull)
        {
            return getContainerByField(mdContainer);
        }
        else
        {
            return getContainerByMethod(mdContainer);
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<MetadataItem> getTerm(final Term term) throws NoSuchTermException
    {
        return getContainerByMethod(term).get(term);
    }

    /**
     * {@inheritDoc}
     */
    public DublinCoreMetadata getDublinCoreMetadata()
    {
        // TODO emd component should not know about Dublibn Core
        final JiBXDublinCoreMetadata jdc = new JiBXDublinCoreMetadata();
        for (PropertyName propertyName : PropertyName.values())
        {
            final EmdContainer emdContainer = getContainerByField(propertyName);
            if (emdContainer != null)
            {
                jdc.set(propertyName, emdContainer.getValues());
            }
        }
        return jdc;
    }

    // Get the container by field. May return null if container was not instantiated earlier.
    private EmdContainer getContainerByField(final MDContainer mdContainer)
    {
        EmdContainer container = null;
        try
        {
            final Field classField = this.getClass().getDeclaredField("emd" + mdContainer.name());
            container = (EmdContainer) classField.get(this);
        }
        catch (final SecurityException e)
        {
            throw new RuntimeException(e);
        }
        catch (final NoSuchFieldException e)
        {
            throw new RuntimeException(e);
        }
        catch (final IllegalArgumentException e)
        {
            throw new RuntimeException(e);
        }
        catch (final IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        return container;
    }

    // Get the container by field. May return null if container was not instantiated earlier.
    private EmdContainer getContainerByField(final PropertyName propertyName)
    {
        EmdContainer container = null;
        try
        {
            final Field classField = this.getClass().getDeclaredField("emd" + propertyName.name());
            container = (EmdContainer) classField.get(this);
        }
        catch (final SecurityException e)
        {
            throw new RuntimeException(e);
        }
        catch (final NoSuchFieldException e)
        {
            throw new RuntimeException(e);
        }
        catch (final IllegalArgumentException e)
        {
            throw new RuntimeException(e);
        }
        catch (final IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        return container;
    }

    // Get container containing given term.
    private EmdContainer getContainerByMethod(final Term term) throws NoSuchTermException
    {
        MDContainer mdContainer = getTermMDContainerMap().get(term); // first look for term.name/term.namesoace
        if (mdContainer == null)
        {
            mdContainer = getTermNameMDContainerMap().get(term); // then look for term.name
            if (mdContainer == null)
            {
                throw new NoSuchTermException("Requested term does not exist: " + (term == null ? "null" : term.toString()));
            }
        }
        return getContainerByMethod(mdContainer);
    }

    // Get the container by method. Never returns null.
    private EmdContainer getContainerByMethod(final MDContainer mdContainer)
    {
        EmdContainer container = null;
        try
        {
            final Method method = this.getClass().getDeclaredMethod("getEmd" + mdContainer.name());
            container = (EmdContainer) method.invoke(this);
        }
        catch (final SecurityException e)
        {
            throw new RuntimeException(e);
        }
        catch (final NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }
        catch (final IllegalArgumentException e)
        {
            throw new RuntimeException(e);
        }
        catch (final IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        catch (final InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
        return container;
    }

    /**
     * {@inheritDoc}
     */
    public EmdTitle getEmdTitle()
    {
        if (emdTitle == null)
        {
            emdTitle = new EmdTitle();
        }
        return emdTitle;
    }

    /**
     * {@inheritDoc}
     */
    public EmdCreator getEmdCreator()
    {
        if (emdCreator == null)
        {
            emdCreator = new EmdCreator();
        }
        return emdCreator;
    }

    /**
     * {@inheritDoc}
     */
    public EmdSubject getEmdSubject()
    {
        if (emdSubject == null)
        {
            emdSubject = new EmdSubject();
        }
        return emdSubject;
    }

    /**
     * {@inheritDoc}
     */
    public EmdDescription getEmdDescription()
    {
        if (emdDescription == null)
        {
            emdDescription = new EmdDescription();
        }
        return emdDescription;
    }

    /**
     * {@inheritDoc}
     */
    public EmdPublisher getEmdPublisher()
    {
        if (emdPublisher == null)
        {
            emdPublisher = new EmdPublisher();
        }
        return emdPublisher;
    }

    /**
     * {@inheritDoc}
     */
    public EmdContributor getEmdContributor()
    {
        if (emdContributor == null)
        {
            emdContributor = new EmdContributor();
        }
        return emdContributor;
    }

    /**
     * {@inheritDoc}
     */
    public EmdDate getEmdDate()
    {
        if (emdDate == null)
        {
            emdDate = new EmdDate();
        }
        return emdDate;
    }

    /**
     * {@inheritDoc}
     */
    public EmdType getEmdType()
    {
        if (emdType == null)
        {
            emdType = new EmdType();
        }
        return emdType;
    }

    /**
     * {@inheritDoc}
     */
    public EmdFormat getEmdFormat()
    {
        if (emdFormat == null)
        {
            emdFormat = new EmdFormat();
        }
        return emdFormat;
    }

    /**
     * {@inheritDoc}
     */
    public EmdIdentifier getEmdIdentifier()
    {
        if (emdIdentifier == null)
        {
            emdIdentifier = new EmdIdentifier();
        }
        return emdIdentifier;
    }

    /**
     * {@inheritDoc}
     */
    public EmdSource getEmdSource()
    {
        if (emdSource == null)
        {
            emdSource = new EmdSource();
        }
        return emdSource;
    }

    /**
     * {@inheritDoc}
     */
    public EmdLanguage getEmdLanguage()
    {
        if (emdLanguage == null)
        {
            emdLanguage = new EmdLanguage();
        }
        return emdLanguage;
    }

    /**
     * {@inheritDoc}
     */
    public EmdRelation getEmdRelation()
    {
        if (emdRelation == null)
        {
            emdRelation = new EmdRelation();
        }
        return emdRelation;
    }

    /**
     * {@inheritDoc}
     */
    public EmdCoverage getEmdCoverage()
    {
        if (emdCoverage == null)
        {
            emdCoverage = new EmdCoverage();
        }
        return emdCoverage;
    }

    /**
     * {@inheritDoc}
     */
    public EmdRights getEmdRights()
    {
        if (emdRights == null)
        {
            emdRights = new EmdRights();
        }
        return emdRights;
    }

    /**
     * {@inheritDoc}
     */
    public EmdAudience getEmdAudience()
    {
        if (emdAudience == null)
        {
            emdAudience = new EmdAudience();
        }
        return emdAudience;
    }

    /**
     * {@inheritDoc}
     */
    public EmdOther getEmdOther()
    {
        if (emdOther == null)
        {
            emdOther = new EmdOther();
        }
        return emdOther;
    }
}
