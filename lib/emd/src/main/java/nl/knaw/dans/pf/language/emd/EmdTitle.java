package nl.knaw.dans.pf.language.emd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.knaw.dans.pf.language.emd.types.BasicString;

/**
 * Container for resource properties of category title.
 * 
 * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-title">dcmi-terms/#terms-title</a>
 * @author ecco
 */
public class EmdTitle extends AbstractEmdContainer {

    public static final String NO_TITLE = "[no title]";

    /**
     * Terms contained.
     */
    static final Term[] TERMS = {new Term(Term.Name.TITLE, Term.Namespace.DC, BasicString.class),
            new Term(Term.Name.ALTERNATIVE, Term.Namespace.DCTERMS, BasicString.class)};

    /**
     *
     */
    private static final long serialVersionUID = -6635380459648443255L;

    private List<BasicString> dcTitle = new ArrayList<BasicString>();
    private List<BasicString> termsAlternative = new ArrayList<BasicString>();

    /**
     * {@inheritDoc}
     */
    public List<Term> getTerms() {
        return Arrays.asList(TERMS);
    }

    /**
     * Get a list of resource properties known as 'title' in the "http://purl.org/dc/elements/1.1/" name space.
     * 
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-title">dcmi-terms/#terms-title</a>
     * @return a list of resource properties
     */
    public List<BasicString> getDcTitle() {
        return dcTitle;
    }

    /**
     * Set a list of resource properties known as 'title' in the "http://purl.org/dc/elements/1.1/" name space.
     * 
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-title">dcmi-terms/#terms-title</a>
     * @param dcTitle
     *        a list of resource properties
     */
    public void setDcTitle(final List<BasicString> dcTitle) {
        this.dcTitle = dcTitle;
    }

    /**
     * Get a list of resource properties known as 'alternative' in the "http://purl.org/dc/terms/" name space.
     * 
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-alternative">dcmi-terms/#terms-alternative</a>
     * @return a list of resource properties
     */
    public List<BasicString> getTermsAlternative() {
        return termsAlternative;
    }

    /**
     * Set a list of resource properties known as 'alternative' in the "http://purl.org/dc/terms/" name space.
     * 
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-alternative">dcmi-terms/#terms-alternative</a>
     * @param termsAlternative
     *        a list of resource properties
     */
    public void setTermsAlternative(final List<BasicString> termsAlternative) {
        this.termsAlternative = termsAlternative;
    }

    /**
     * Will return the first dcTitle or, if that is not available, the first termsAlternative or, if that is not available, the empty string.
     * 
     * @return see above
     */
    public String getPreferredTitle() {
        String preferredTitle = null;
        if (!dcTitle.isEmpty()) {
            preferredTitle = dcTitle.get(0).getValue();
        } else if (!termsAlternative.isEmpty()) {
            preferredTitle = termsAlternative.get(0).getValue();
        }
        if (preferredTitle == null) {
            preferredTitle = NO_TITLE;
        }
        return preferredTitle;
    }

}
