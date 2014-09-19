package nl.knaw.dans.pf.language.emd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.knaw.dans.pf.language.emd.types.BasicString;

/**
 * Container for resource properties of category description.
 * 
 * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-description">dcmi-terms/#terms-description</a>
 * @author ecco
 */
public class EmdDescription extends AbstractEmdContainer {

    /**
     * Terms contained.
     */
    static final Term[] TERMS = {new Term(Term.Name.DESCRIPTION, Term.Namespace.DC, BasicString.class),
            new Term(Term.Name.TABLEOFCONTENTS, Term.Namespace.DCTERMS, BasicString.class),
            new Term(Term.Name.ABSTRACT, Term.Namespace.DCTERMS, BasicString.class)};

    /**
     *
     */
    private static final long serialVersionUID = -31861178057564841L;

    private List<BasicString> dcDescription = new ArrayList<BasicString>();

    private List<BasicString> termsTableOfContents = new ArrayList<BasicString>();

    private List<BasicString> termsAbstract = new ArrayList<BasicString>();

    /**
     * {@inheritDoc}
     */
    public List<Term> getTerms() {
        return Arrays.asList(TERMS);
    }

    /**
     * Get a list of resource properties known as 'description' in the "http://purl.org/dc/elements/1.1/" name space.
     * 
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-description">dcmi-terms/#terms-description</a>
     * @return a list of resource properties
     */
    public List<BasicString> getDcDescription() {
        return dcDescription;
    }

    /**
     * Set a list of resource properties known as 'description' in the "http://purl.org/dc/elements/1.1/" name space.
     * 
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-description">dcmi-terms/#terms-description</a>
     * @param dcDescription
     *        a list of resource properties
     */
    public void setDcDescription(final List<BasicString> dcDescription) {
        this.dcDescription = dcDescription;
    }

    /**
     * Get a list of resource properties known as 'tableOfContents' in the "http://purl.org/dc/terms/" name space.
     * 
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-tableOfContents">dcmi-terms/#terms-tableOfContents</a>
     * @return a list of resource properties
     */
    public List<BasicString> getTermsTableOfContents() {
        return termsTableOfContents;
    }

    /**
     * Set a list of resource properties known as 'tableOfContents' in the "http://purl.org/dc/terms/" name space.
     * 
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-tableOfContents">dcmi-terms/#terms-tableOfContents</a>
     * @param termsTableOfContents
     *        a list of resource properties
     */
    public void setTermsTableOfContents(final List<BasicString> termsTableOfContents) {
        this.termsTableOfContents = termsTableOfContents;
    }

    /**
     * Get a list of resource properties known as 'abstract' in the "http://purl.org/dc/terms/" name space.
     * 
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-abstract">dcmi-terms/#terms-abstract</a>
     * @return a list of resource properties
     */
    public List<BasicString> getTermsAbstract() {
        return termsAbstract;
    }

    /**
     * Set a list of resource properties known as 'abstract' in the "http://purl.org/dc/terms/" name space.
     * 
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-abstract">dcmi-terms/#terms-abstract</a>
     * @param termsAbstract
     *        a list of resource properties
     */
    public void setTermsAbstract(final List<BasicString> termsAbstract) {
        this.termsAbstract = termsAbstract;
    }
}
