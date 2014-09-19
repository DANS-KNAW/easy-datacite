package nl.knaw.dans.pf.language.emd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.knaw.dans.pf.language.emd.types.BasicString;

/**
 * Container for resource properties of category language.
 * 
 * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-language">dcmi-terms/#terms-language</a>
 * @author ecco
 */
public class EmdLanguage extends AbstractEmdContainer {

    /**
     * Terms contained.
     */
    static final Term[] TERMS = {new Term(Term.Name.LANGUAGE, Term.Namespace.DC, BasicString.class)};

    /**
     *
     */
    private static final long serialVersionUID = 3744331175394016441L;

    private List<BasicString> dcLanguage = new ArrayList<BasicString>();

    /**
     * {@inheritDoc}
     */
    public List<Term> getTerms() {
        return Arrays.asList(TERMS);
    }

    /**
     * Get a list of resource properties known as 'language' in the "http://purl.org/dc/elements/1.1/" name space.
     * 
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-language">dcmi-terms/#terms-language</a>
     * @return a list of resource properties
     */
    public List<BasicString> getDcLanguage() {
        return dcLanguage;
    }

    /**
     * Set a list of resource properties known as 'language' in the "http://purl.org/dc/elements/1.1/" name space.
     * 
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-language">dcmi-terms/#terms-language</a>
     * @param dcLanguage
     *        a list of resource properties
     */
    public void setDcLanguage(final List<BasicString> dcLanguage) {
        this.dcLanguage = dcLanguage;
    }

}
