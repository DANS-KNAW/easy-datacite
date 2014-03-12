package nl.knaw.dans.pf.language.emd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.knaw.dans.pf.language.emd.types.BasicString;
import nl.knaw.dans.pf.language.emd.types.EmdScheme;
import nl.knaw.dans.pf.language.emd.types.Spatial;

/**
 * Container for resource properties of category coverage.
 * 
 * @see <a
 *      href="http://dublincore.org/documents/dcmi-terms/#terms-coverage">dcmi-terms/#terms-coverage</a>
 * @author ecco
 */
public class EmdCoverage extends AbstractEmdContainer
{

    /**
     * Terms contained.
     */
    static final Term[] TERMS = {new Term(Term.Name.COVERAGE, Term.Namespace.DC, BasicString.class),
            new Term(Term.Name.SPATIAL, Term.Namespace.DCTERMS, BasicString.class), new Term(Term.Name.TEMPORAL, Term.Namespace.DCTERMS, BasicString.class),
            new Term(Term.Name.SPATIAL, Term.Namespace.EAS, Spatial.class)};

    /**
     *
     */
    private static final long serialVersionUID = 2217945012322650233L;

    private List<BasicString> dcCoverage = new ArrayList<BasicString>();

    private List<BasicString> termsSpatial = new ArrayList<BasicString>();

    private List<BasicString> termsTemporal = new ArrayList<BasicString>();

    private List<Spatial> easSpatial = new ArrayList<Spatial>();

    /**
     * {@inheritDoc}
     */
    public List<Term> getTerms()
    {
        return Arrays.asList(TERMS);
    }

    /**
     * Get a list of resource properties known as 'coverage' in the "http://purl.org/dc/elements/1.1/"
     * name space.
     * 
     * @see <a
     *      href="http://dublincore.org/documents/dcmi-terms/#terms-coverage">dcmi-terms/#terms-coverage</a>
     * @return a list of resource properties
     */
    public List<BasicString> getDcCoverage()
    {
        return dcCoverage;
    }

    /**
     * Set a list of resource properties known as 'coverage' in the "http://purl.org/dc/elements/1.1/"
     * name space.
     * 
     * @see <a
     *      href="http://dublincore.org/documents/dcmi-terms/#terms-coverage">dcmi-terms/#terms-coverage</a>
     * @param dcCoverage
     *        a list of resource properties
     */
    public void setDcCoverage(final List<BasicString> dcCoverage)
    {
        this.dcCoverage = dcCoverage;
    }

    /**
     * Get a list of resource properties known as 'spatial' in the "http://purl.org/dc/terms/" name
     * space.
     * 
     * @see <a
     *      href="http://dublincore.org/documents/dcmi-terms/#terms-spatial">dcmi-terms/#terms-spatial</a>
     * @return a list of resource properties
     */
    public List<BasicString> getTermsSpatial()
    {
        return termsSpatial;
    }

    /**
     * Set a list of resource properties known as 'spatial' in the "http://purl.org/dc/terms/" name
     * space.
     * 
     * @see <a
     *      href="http://dublincore.org/documents/dcmi-terms/#terms-spatial">dcmi-terms/#terms-spatial</a>
     * @param termsSpatial
     *        a list of resource properties
     */
    public void setTermsSpatial(final List<BasicString> termsSpatial)
    {
        this.termsSpatial = termsSpatial;
    }

    /**
     * Get a list of resource properties known as 'temporal' in the "http://purl.org/dc/terms/" name
     * space.
     * 
     * @see <a
     *      href="http://dublincore.org/documents/dcmi-terms/#terms-temporal">dcmi-terms/#terms-temporal</a>
     * @return a list of resource properties
     */
    public List<BasicString> getTermsTemporal()
    {
        return termsTemporal;
    }

    /**
     * Set a list of resource properties known as 'temporal' in the "http://purl.org/dc/terms/" name
     * space.
     * 
     * @see <a
     *      href="http://dublincore.org/documents/dcmi-terms/#terms-temporal">dcmi-terms/#terms-temporal</a>
     * @param termsTemporal
     *        a list of resource properties
     */
    public void setTermsTemporal(final List<BasicString> termsTemporal)
    {
        this.termsTemporal = termsTemporal;
    }

    public List<String> getTermsTemporalValues(String schemeId)
    {
        List<String> termsTemporalValues = new ArrayList<String>();
        for (BasicString bs : termsTemporal)
        {
            if (schemeId.equals(bs.getSchemeId()))
            {
                termsTemporalValues.add(bs.getValue());
            }
        }
        return termsTemporalValues;
    }

    public List<String> getArchaeologyTermsTemporalValues()
    {
        return getTermsTemporalValues(EmdScheme.ARCHAEOLOGY_DCTERMS_TEMPORAL.getId());
    }

    /**
     * Get a list of resource properties known as 'spatial' in the "http://purl.org/dc/terms/" name
     * space.
     * 
     * @see <a
     *      href="http://dublincore.org/documents/dcmi-terms/#terms-spatial">dcmi-terms/#terms-spatial</a>
     * @return a list of resource properties
     */
    public List<Spatial> getEasSpatial()
    {
        return easSpatial;
    }

    /**
     * Set a list of resource properties known as 'spatial' in the "http://purl.org/dc/terms/" name
     * space.
     * 
     * @see <a
     *      href="http://dublincore.org/documents/dcmi-terms/#terms-spatial">dcmi-terms/#terms-spatial</a>
     * @param easSpatial
     *        a list of resource properties
     */
    public void setEasSpatial(final List<Spatial> easSpatial)
    {
        this.easSpatial = easSpatial;
    }

}
