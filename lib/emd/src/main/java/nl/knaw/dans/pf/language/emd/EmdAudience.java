package nl.knaw.dans.pf.language.emd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.knaw.dans.pf.language.emd.types.BasicString;
import nl.knaw.dans.pf.language.emd.types.EmdConstants;

/**
 * Container for resource properties of category audience.
 * 
 * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-audience">dcmi-terms/#terms-audience</a>
 * @author ecco
 */
public class EmdAudience extends AbstractEmdContainer {

    /**
     * Terms contained.
     */
    static final Term[] TERMS = {new Term(Term.Name.AUDIENCE, Term.Namespace.DCTERMS, BasicString.class)};
    /**
     *
     */
    private static final long serialVersionUID = -7542030124818102201L;

    private List<BasicString> termsAudience = new ArrayList<BasicString>();

    /**
     * {@inheritDoc}
     */
    public List<Term> getTerms() {
        return Arrays.asList(TERMS);
    }

    /**
     * Get a list of resource properties known as 'audience' in the "http://purl.org/dc/terms/" name space.
     * 
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-audience">dcmi-terms/#terms-audience</a>
     * @return a list of resource properties
     */
    public List<BasicString> getTermsAudience() {
        return termsAudience;
    }

    /**
     * Set a list of resource properties known as 'audience' in the "http://purl.org/dc/terms/" name space.
     * 
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-audience">dcmi-terms/#terms-audience</a>
     * @param termsAudience
     *        a list of resource properties
     */
    public void setTermsAudience(final List<BasicString> termsAudience) {
        this.termsAudience = termsAudience;
    }

    public List<BasicString> getDisciplines() {
        List<BasicString> disciplines = new ArrayList<BasicString>();
        for (BasicString bs : termsAudience) {
            if (EmdConstants.SCHEME_ID_DISCIPLINES.equals(bs.getSchemeId())) {
                disciplines.add(bs);
            }
        }
        return disciplines;
    }

    public List<BasicString> removeAllDisciplines() {
        List<BasicString> disciplines = getDisciplines();
        for (BasicString bs : disciplines) {
            termsAudience.remove(bs);
        }
        return disciplines;
    }

    public boolean containsDiscipline(String disciplineId) {
        boolean contains = false;
        for (BasicString bs : termsAudience) {
            if (EmdConstants.SCHEME_ID_DISCIPLINES.equals(bs.getSchemeId()) && bs.getValue() != null && bs.getValue().equals(disciplineId)) {
                contains = true;
                break;
            }
        }
        return contains;
    }

}
