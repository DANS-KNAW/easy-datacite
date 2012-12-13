package nl.knaw.dans.pf.language.emd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.pf.language.emd.types.BasicDate;
import nl.knaw.dans.pf.language.emd.types.IsoDate;

import org.joda.time.DateTime;

/**
 * Container for resource properties of category date.
 *
 * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-date">dcmi-terms/#terms-date</a>
 * @author ecco
 */
public class EmdDate extends AbstractEmdContainer
{

    private static final long serialVersionUID = -3009080247290017998L;

    private static List<String> QUALIFIER_LIST;

    private List<BasicDate> dcDate;
    private List<BasicDate> termsCreated;
    private List<BasicDate> termsValid;
    private List<BasicDate> termsAvailable;
    private List<BasicDate> termsIssued;
    private List<BasicDate> termsModified;
    private List<BasicDate> termsDateAccepted;
    private List<BasicDate> termsDateCopyrighted;
    private List<BasicDate> termsDateSubmitted;

    private List<IsoDate> easDate;
    private List<IsoDate> easCreated;
    private List<IsoDate> easValid;
    private List<IsoDate> easAvailable;
    private List<IsoDate> easIssued;
    private List<IsoDate> easModified;
    private List<IsoDate> easDateAccepted;
    private List<IsoDate> easDateCopyrighted;
    private List<IsoDate> easDateSubmitted;

    /**
     * Terms contained.
     */
    static final Term[] TERMS = {new Term(Term.Name.DATE, Term.Namespace.DC, BasicDate.class),

    new Term(Term.Name.CREATED, Term.Namespace.DCTERMS, BasicDate.class), new Term(Term.Name.VALID, Term.Namespace.DCTERMS, BasicDate.class),
            new Term(Term.Name.AVAILABLE, Term.Namespace.DCTERMS, BasicDate.class), new Term(Term.Name.ISSUED, Term.Namespace.DCTERMS, BasicDate.class),
            new Term(Term.Name.MODIFIED, Term.Namespace.DCTERMS, BasicDate.class), new Term(Term.Name.DATE_ACCEPTED, Term.Namespace.DCTERMS, BasicDate.class),
            new Term(Term.Name.DATE_COPYRIGHTED, Term.Namespace.DCTERMS, BasicDate.class),
            new Term(Term.Name.DATE_SUBMITTED, Term.Namespace.DCTERMS, BasicDate.class),

            new Term(Term.Name.DATE, Term.Namespace.EAS, IsoDate.class), new Term(Term.Name.CREATED, Term.Namespace.EAS, IsoDate.class),
            new Term(Term.Name.VALID, Term.Namespace.EAS, IsoDate.class), new Term(Term.Name.AVAILABLE, Term.Namespace.EAS, IsoDate.class),
            new Term(Term.Name.ISSUED, Term.Namespace.EAS, IsoDate.class), new Term(Term.Name.MODIFIED, Term.Namespace.EAS, IsoDate.class),
            new Term(Term.Name.DATE_ACCEPTED, Term.Namespace.EAS, IsoDate.class), new Term(Term.Name.DATE_COPYRIGHTED, Term.Namespace.EAS, IsoDate.class),
            new Term(Term.Name.DATE_SUBMITTED, Term.Namespace.EAS, IsoDate.class)};

    public static final String DATE = "";
    public static final String CREATED = "created";
    public static final String VALID = "valid";
    public static final String AVAILABLE = "available";
    public static final String ISSUED = "issued";
    public static final String MODIFIED = "modified";
    public static final String DATE_ACCEPTED = "dateAccepted";
    public static final String DATE_COPYRIGHTED = "dateCopyrighted";
    public static final String DATE_SUBMITTED = "dateSubmitted";

    public static List<String> getQualifierList()
    {
        if (QUALIFIER_LIST == null)
        {
            QUALIFIER_LIST = new ArrayList<String>();
            QUALIFIER_LIST.add(DATE);
            QUALIFIER_LIST.add(CREATED);
            QUALIFIER_LIST.add(VALID);
            QUALIFIER_LIST.add(AVAILABLE);
            QUALIFIER_LIST.add(ISSUED);
            QUALIFIER_LIST.add(MODIFIED);
            QUALIFIER_LIST.add(DATE_ACCEPTED);
            QUALIFIER_LIST.add(DATE_COPYRIGHTED);
            QUALIFIER_LIST.add(DATE_SUBMITTED);
        }
        return Collections.unmodifiableList(QUALIFIER_LIST);
    }

    public Map<String, List<IsoDate>> getIsoDateMap()
    {
        // application critic date types (created, available, submitted) are set separately
        // and cannot be in drop down gui widgets.
        Map<String, List<IsoDate>> map = new HashMap<String, List<IsoDate>>();
        map.put(DATE, this.getEasDate());
        map.put(VALID, this.getEasValid());
        map.put(ISSUED, this.getEasIssued());
        map.put(MODIFIED, this.getEasModified());
        map.put(DATE_ACCEPTED, this.getEasDateAccepted());
        map.put(DATE_COPYRIGHTED, this.getEasDateCopyrighted());

        return map;
    }

    public Map<String, List<IsoDate>> getAllIsoDates()
    {
        Map<String, List<IsoDate>> map = new HashMap<String, List<IsoDate>>();
        map.put(DATE, this.getEasDate());
        map.put(CREATED, getEasCreated());
        map.put(VALID, this.getEasValid());
        map.put(AVAILABLE, getEasAvailable());
        map.put(ISSUED, this.getEasIssued());
        map.put(MODIFIED, this.getEasModified());
        map.put(DATE_ACCEPTED, this.getEasDateAccepted());
        map.put(DATE_COPYRIGHTED, this.getEasDateCopyrighted());
        map.put(DATE_SUBMITTED, getEasDateSubmitted());

        return map;
    }

    public Map<String, List<BasicDate>> getBasicDateMap()
    {
        // application critic date types (created, available, submitted) are set separately
        // and cannot be in drop down gui widgets.
        // above all: Basic Dates are Strings and cannot be used in date calculations.
        Map<String, List<BasicDate>> map = new HashMap<String, List<BasicDate>>();
        map.put(DATE, this.getDcDate());
        map.put(VALID, this.getTermsValid());
        map.put(ISSUED, this.getTermsIssued());
        map.put(MODIFIED, this.getTermsModified());
        map.put(DATE_ACCEPTED, this.getTermsDateAccepted());
        map.put(DATE_COPYRIGHTED, this.getTermsDateCopyrighted());

        return map;
    }

    public Map<String, List<BasicDate>> getAllBasicDates()
    {
        Map<String, List<BasicDate>> map = new HashMap<String, List<BasicDate>>();
        map.put(DATE, this.getDcDate());
        map.put(CREATED, this.getTermsCreated());
        map.put(VALID, this.getTermsValid());
        map.put(AVAILABLE, this.getTermsAvailable());
        map.put(ISSUED, this.getTermsIssued());
        map.put(MODIFIED, this.getTermsModified());
        map.put(DATE_ACCEPTED, this.getTermsDateAccepted());
        map.put(DATE_COPYRIGHTED, this.getTermsDateCopyrighted());
        map.put(DATE_SUBMITTED, this.getTermsDateSubmitted());

        return map;
    }

    //    public static final String[] LIST_KEYS = {DATE, CREATED, VALID, AVAILABLE, ISSUED, MODIFIED, DATE_ACCEPTED
    //		, DATE_COPYRIGHTED, DATE_SUBMITTED };

    /**
     * {@inheritDoc}
     */
    public List<Term> getTerms()
    {
        return Arrays.asList(TERMS);
    }

    public DateTime getDateCreated()
    {
        DateTime dt = null;
        if (easCreated != null && easCreated.size() > 0)
        {
            dt = easCreated.get(0).getValue();
        }
        return dt;
    }

    public String getFormattedDateCreated()
    {
        String fdt = null;
        if (easCreated != null && easCreated.size() > 0)
        {
            fdt = easCreated.get(0).toString();
        }
        return fdt;
    }

    public DateTime getDateAvailable()
    {
        DateTime dt = null;
        if (easAvailable != null && easAvailable.size() > 0)
        {
            dt = easAvailable.get(0).getValue();
        }
        return dt;
    }

    public String getFormattedDateAvailable()
    {
        String fdt = null;
        if (easAvailable != null && easAvailable.size() > 0)
        {
            fdt = easAvailable.get(0).toString();
        }
        return fdt;
    }

    /**
     * Get a list of resource properties known as 'date' in the "http://purl.org/dc/elements/1.1/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-date">dcmi-terms/#terms-date</a>
     * @return a list of resource properties
     */
    public List<BasicDate> getDcDate()
    {
        if (dcDate == null)
        {
            dcDate = new ArrayList<BasicDate>();
        }
        return dcDate;
    }

    /**
     * Set a list of resource properties known as 'date' in the "http://purl.org/dc/elements/1.1/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-date">dcmi-terms/#terms-date</a>
     * @param dcDate
     *        a list of resource properties
     */
    public void setDcDate(final List<BasicDate> dcDate)
    {
        this.dcDate = dcDate;
    }

    /**
     * Get a list of resource properties known as 'created' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-created">dcmi-terms/#terms-created</a>
     * @return a list of resource properties
     */
    public List<BasicDate> getTermsCreated()
    {
        if (termsCreated == null)
        {
            termsCreated = new ArrayList<BasicDate>();
        }
        return termsCreated;
    }

    /**
     * Set a list of resource properties known as 'created' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-created">dcmi-terms/#terms-created</a>
     * @param termsCreated
     *        a list of resource properties
     */
    public void setTermsCreated(final List<BasicDate> termsCreated)
    {
        this.termsCreated = termsCreated;
    }

    /**
     * Get a list of resource properties known as 'valid' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-valid">dcmi-terms/#terms-valid</a>
     * @return a list of resource properties
     */
    public List<BasicDate> getTermsValid()
    {
        if (termsValid == null)
        {
            termsValid = new ArrayList<BasicDate>();
        }
        return termsValid;
    }

    /**
     * Set a list of resource properties known as 'valid' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-valid">dcmi-terms/#terms-valid</a>
     * @param termsValid
     *        a list of resource properties
     */
    public void setTermsValid(final List<BasicDate> termsValid)
    {
        this.termsValid = termsValid;
    }

    /**
     * Get a list of resource properties known as 'available' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-available">dcmi-terms/#terms-available</a>
     * @return a list of resource properties
     */
    public List<BasicDate> getTermsAvailable()
    {
        if (termsAvailable == null)
        {
            termsAvailable = new ArrayList<BasicDate>();
        }
        return termsAvailable;
    }

    /**
     * Set a list of resource properties known as 'available' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-available">dcmi-terms/#terms-available</a>
     * @param termsAvailable
     *        a list of resource properties
     */
    public void setTermsAvailable(final List<BasicDate> termsAvailable)
    {
        this.termsAvailable = termsAvailable;
    }

    /**
     * Get a list of resource properties known as 'issued' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-issued">dcmi-terms/#terms-issued</a>
     * @return a list of resource properties
     */
    public List<BasicDate> getTermsIssued()
    {
        if (termsIssued == null)
        {
            termsIssued = new ArrayList<BasicDate>();
        }
        return termsIssued;
    }

    /**
     * Set a list of resource properties known as 'issued' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-issued">dcmi-terms/#terms-issued</a>
     * @param termsIssued
     *        a list of resource properties
     */
    public void setTermsIssued(final List<BasicDate> termsIssued)
    {
        this.termsIssued = termsIssued;
    }

    /**
     * Get a list of resource properties known as 'modified' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-modified">dcmi-terms/#terms-modified</a>
     * @return a list of resource properties
     */
    public List<BasicDate> getTermsModified()
    {
        if (termsModified == null)
        {
            termsModified = new ArrayList<BasicDate>();
        }
        return termsModified;
    }

    /**
     * Set a list of resource properties known as 'modified' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-modified">dcmi-terms/#terms-modified</a>
     * @param termsModified
     *        a list of resource properties
     */
    public void setTermsModified(final List<BasicDate> termsModified)
    {
        this.termsModified = termsModified;
    }

    /**
     * Get a list of resource properties known as 'dateAccepted' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-dateAccepted">dcmi-terms/#terms-dateAccepted</a>
     * @return a list of resource properties
     */
    public List<BasicDate> getTermsDateAccepted()
    {
        if (termsDateAccepted == null)
        {
            termsDateAccepted = new ArrayList<BasicDate>();
        }
        return termsDateAccepted;
    }

    /**
     * Set a list of resource properties known as 'dateAccepted' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-dateAccepted">dcmi-terms/#terms-dateAccepted</a>
     * @param termsDateAccepted
     *        a list of resource properties
     */
    public void setTermsDateAccepted(final List<BasicDate> termsDateAccepted)
    {
        this.termsDateAccepted = termsDateAccepted;
    }

    /**
     * Get a list of resource properties known as 'dateCopyrighted' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a
     *      href="http://dublincore.org/documents/dcmi-terms/#terms-dateCopyrighted">dcmi-terms/#terms-dateCopyrighted</a>
     * @return a list of resource properties
     */
    public List<BasicDate> getTermsDateCopyrighted()
    {
        if (termsDateCopyrighted == null)
        {
            termsDateCopyrighted = new ArrayList<BasicDate>();
        }
        return termsDateCopyrighted;
    }

    /**
     * Set a list of resource properties known as 'dateCopyrighted' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a
     *      href="http://dublincore.org/documents/dcmi-terms/#terms-dateCopyrighted">dcmi-terms/#terms-dateCopyrighted</a>
     * @param termsDateCopyrighted
     *        a list of resource properties
     */
    public void setTermsDateCopyrighted(final List<BasicDate> termsDateCopyrighted)
    {
        this.termsDateCopyrighted = termsDateCopyrighted;
    }

    /**
     * Get a list of resource properties known as 'dateSubmitted' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-dateSubmitted">dcmi-terms/#terms-dateSubmitted</a>
     * @return a list of resource properties
     */
    public List<BasicDate> getTermsDateSubmitted()
    {
        if (termsDateSubmitted == null)
        {
            termsDateSubmitted = new ArrayList<BasicDate>();
        }
        return termsDateSubmitted;
    }

    /**
     * Set a list of resource properties known as 'dateSubmitted' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-dateSubmitted">dcmi-terms/#terms-dateSubmitted</a>
     * @param termsDateSubmitted
     *        a list of resource properties
     */
    public void setTermsDateSubmitted(final List<BasicDate> termsDateSubmitted)
    {
        this.termsDateSubmitted = termsDateSubmitted;
    }

    /**
     * Get a list of resource properties known as 'date' in the "http://purl.org/dc/elements/1.1/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-date">dcmi-terms/#terms-date</a>
     * @return a list of resource properties
     */
    public List<IsoDate> getEasDate()
    {
        if (easDate == null)
        {
            easDate = new ArrayList<IsoDate>();
        }
        return easDate;
    }

    /**
     * Set a list of resource properties known as 'date' in the "http://purl.org/dc/elements/1.1/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-date">dcmi-terms/#terms-date</a>
     * @param easDate
     *        a list of resource properties
     */
    public void setEasDate(final List<IsoDate> easDate)
    {
        this.easDate = easDate;
    }

    /**
     * Get a list of resource properties known as 'created' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-created">dcmi-terms/#terms-created</a>
     * @return a list of resource properties
     */
    public List<IsoDate> getEasCreated()
    {
        if (easCreated == null)
        {
            easCreated = new ArrayList<IsoDate>();
        }
        return easCreated;
    }

    /**
     * Set a list of resource properties known as 'created' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-created">dcmi-terms/#terms-created</a>
     * @param easCreated
     *        a list of resource properties
     */
    public void setEasCreated(final List<IsoDate> easCreated)
    {
        this.easCreated = easCreated;
    }

    /**
     * Get a list of resource properties known as 'valid' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-valid">dcmi-terms/#terms-valid</a>
     * @return a list of resource properties
     */
    public List<IsoDate> getEasValid()
    {
        if (easValid == null)
        {
            easValid = new ArrayList<IsoDate>();
        }
        return easValid;
    }

    /**
     * Set a list of resource properties known as 'valid' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-valid">dcmi-terms/#terms-valid</a>
     * @param easValid
     *        a list of resource properties
     */
    public void setEasValid(final List<IsoDate> easValid)
    {
        this.easValid = easValid;
    }

    /**
     * Get a list of resource properties known as 'available' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-available">dcmi-terms/#terms-available</a>
     * @return a list of resource properties
     */
    public List<IsoDate> getEasAvailable()
    {
        if (easAvailable == null)
        {
            easAvailable = new ArrayList<IsoDate>();
        }
        return easAvailable;
    }

    /**
     * Set a list of resource properties known as 'available' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-available">dcmi-terms/#terms-available</a>
     * @param easAvailable
     *        a list of resource properties
     */
    public void setEasAvailable(final List<IsoDate> easAvailable)
    {
        this.easAvailable = easAvailable;
    }

    /**
     * Get a list of resource properties known as 'issued' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-issued">dcmi-terms/#terms-issued</a>
     * @return a list of resource properties
     */
    public List<IsoDate> getEasIssued()
    {
        if (easIssued == null)
        {
            easIssued = new ArrayList<IsoDate>();
        }
        return easIssued;
    }

    /**
     * Set a list of resource properties known as 'issued' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-issued">dcmi-terms/#terms-issued</a>
     * @param easIssued
     *        a list of resource properties
     */
    public void setEasIssued(final List<IsoDate> easIssued)
    {
        this.easIssued = easIssued;
    }

    /**
     * Get a list of resource properties known as 'modified' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-modified">dcmi-terms/#terms-modified</a>
     * @return a list of resource properties
     */
    public List<IsoDate> getEasModified()
    {
        if (easModified == null)
        {
            easModified = new ArrayList<IsoDate>();
        }
        return easModified;
    }

    /**
     * Set a list of resource properties known as 'modified' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-modified">dcmi-terms/#terms-modified</a>
     * @param easModified
     *        a list of resource properties
     */
    public void setEasModified(final List<IsoDate> easModified)
    {
        this.easModified = easModified;
    }

    /**
     * Get a list of resource properties known as 'dateAccepted' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-dateAccepted">dcmi-terms/#terms-dateAccepted</a>
     * @return a list of resource properties
     */
    public List<IsoDate> getEasDateAccepted()
    {
        if (easDateAccepted == null)
        {
            easDateAccepted = new ArrayList<IsoDate>();
        }
        return easDateAccepted;
    }

    /**
     * Set a list of resource properties known as 'dateAccepted' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-dateAccepted">dcmi-terms/#terms-dateAccepted</a>
     * @param easDateAccepted
     *        a list of resource properties
     */
    public void setEasDateAccepted(final List<IsoDate> easDateAccepted)
    {
        this.easDateAccepted = easDateAccepted;
    }

    /**
     * Get a list of resource properties known as 'dateCopyrighted' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a
     *      href="http://dublincore.org/documents/dcmi-terms/#terms-dateCopyrighted">dcmi-terms/#terms-dateCopyrighted</a>
     * @return a list of resource properties
     */
    public List<IsoDate> getEasDateCopyrighted()
    {
        if (easDateCopyrighted == null)
        {
            easDateCopyrighted = new ArrayList<IsoDate>();
        }
        return easDateCopyrighted;
    }

    /**
     * Set a list of resource properties known as 'dateCopyrighted' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a
     *      href="http://dublincore.org/documents/dcmi-terms/#terms-dateCopyrighted">dcmi-terms/#terms-dateCopyrighted</a>
     * @param easDateCopyrighted
     *        a list of resource properties
     */
    public void setEasDateCopyrighted(final List<IsoDate> easDateCopyrighted)
    {
        this.easDateCopyrighted = easDateCopyrighted;
    }

    /**
     * Get a list of resource properties known as 'dateSubmitted' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-dateSubmitted">dcmi-terms/#terms-dateSubmitted</a>
     * @return a list of resource properties
     */
    public List<IsoDate> getEasDateSubmitted()
    {
        if (easDateSubmitted == null)
        {
            easDateSubmitted = new ArrayList<IsoDate>();
        }
        return easDateSubmitted;
    }

    /**
     * Set a list of resource properties known as 'dateSubmitted' in the "http://purl.org/dc/terms/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-dateSubmitted">dcmi-terms/#terms-dateSubmitted</a>
     * @param easDateSubmitted
     *        a list of resource properties
     */
    public void setEasDateSubmitted(final List<IsoDate> easDateSubmitted)
    {
        this.easDateSubmitted = easDateSubmitted;
    }

}
