package nl.knaw.dans.pf.language.emd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.knaw.dans.pf.language.emd.types.BasicIdentifier;

/**
 * Container for resource properties of category source.
 * 
 * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-source">dcmi-terms/#terms-source</a>
 * @author ecco
 */
public class EmdSource extends AbstractEmdContainer
{

    /**
     * Terms contained.
     */
    static final Term[] TERMS = {new Term(Term.Name.SOURCE, Term.Namespace.DC, BasicIdentifier.class)};

    /**
     *
     */
    private static final long serialVersionUID = -8406539731481024935L;

    private List<BasicIdentifier> dcSource = new ArrayList<BasicIdentifier>();

    /**
     * {@inheritDoc}
     */
    public List<Term> getTerms()
    {
        return Arrays.asList(TERMS);
    }

    /**
     * Get a list of resource properties known as 'source' in the "http://purl.org/dc/elements/1.1/" name
     * space.
     * 
     * @see <a
     *      href="http://dublincore.org/documents/dcmi-terms/#terms-source">dcmi-terms/#terms-source</a>
     * @return a list of resource properties
     */
    public List<BasicIdentifier> getDcSource()
    {
        return dcSource;
    }

    /**
     * Set a list of resource properties known as 'source' in the "http://purl.org/dc/elements/1.1/" name
     * space.
     * 
     * @see <a
     *      href="http://dublincore.org/documents/dcmi-terms/#terms-source">dcmi-terms/#terms-source</a>
     * @param dcSource
     *        a list of resource properties
     */
    public void setDcSource(final List<BasicIdentifier> dcSource)
    {
        this.dcSource = dcSource;
    }

}
