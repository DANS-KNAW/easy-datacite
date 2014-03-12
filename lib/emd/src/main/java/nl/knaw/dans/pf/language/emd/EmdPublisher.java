package nl.knaw.dans.pf.language.emd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.knaw.dans.pf.language.emd.types.BasicString;

/**
 * Container for resource properties of category publisher.
 * 
 * @see <a
 *      href="http://dublincore.org/documents/dcmi-terms/#terms-publisher">dcmi-terms/#terms-publisher</a>
 * @author ecco
 */
public class EmdPublisher extends AbstractEmdContainer
{

    /**
     * Terms contained.
     */
    static final Term[] TERMS = {new Term(Term.Name.PUBLISHER, Term.Namespace.DC, BasicString.class)};

    /**
     *
     */
    private static final long serialVersionUID = 9127949937115669653L;

    private List<BasicString> dcPublisher = new ArrayList<BasicString>();

    /**
     * {@inheritDoc}
     */
    public List<Term> getTerms()
    {
        return Arrays.asList(TERMS);
    }

    /**
     * Get a list of resource properties known as 'publisher' in the "http://purl.org/dc/elements/1.1/"
     * name space.
     * 
     * @see <a
     *      href="http://dublincore.org/documents/dcmi-terms/#terms-publisher">dcmi-terms/#terms-publisher</a>
     * @return a list of resource properties
     */
    public List<BasicString> getDcPublisher()
    {
        return dcPublisher;
    }

    /**
     * Set a list of resource properties known as 'publisher' in the "http://purl.org/dc/elements/1.1/"
     * name space.
     * 
     * @see <a
     *      href="http://dublincore.org/documents/dcmi-terms/#terms-publisher">dcmi-terms/#terms-publisher</a>
     * @param dcPublisher
     *        a list of resource properties
     */
    public void setDcPublisher(final List<BasicString> dcPublisher)
    {
        this.dcPublisher = dcPublisher;
    }

}
