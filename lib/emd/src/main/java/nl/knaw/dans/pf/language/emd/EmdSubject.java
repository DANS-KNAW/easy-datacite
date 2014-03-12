package nl.knaw.dans.pf.language.emd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.knaw.dans.pf.language.emd.types.BasicString;
import nl.knaw.dans.pf.language.emd.types.EmdScheme;

/**
 * Container for resource properties of category subject.
 * 
 * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-subject">dcmi-terms/#terms-subject</a>
 * @author ecco
 */
public class EmdSubject extends AbstractEmdContainer
{
    /**
     * Terms contained.
     */
    static final Term[] TERMS = {new Term(Term.Name.SUBJECT, Term.Namespace.DC, BasicString.class)};

    /**
     *
     */
    private static final long serialVersionUID = -282675459550767423L;

    private List<BasicString> dcSubject = new ArrayList<BasicString>();

    /**
     * {@inheritDoc}
     */
    public List<Term> getTerms()
    {
        return Arrays.asList(TERMS);
    }

    /**
     * Get a list of resource properties known as 'subject' in the "http://purl.org/dc/elements/1.1/"
     * name space.
     * 
     * @see <a
     *      href="http://dublincore.org/documents/dcmi-terms/#terms-subject">dcmi-terms/#terms-subject</a>
     * @return a list of resource properties
     */
    public List<BasicString> getDcSubject()
    {
        return dcSubject;
    }

    /**
     * Set a list of resource properties known as 'subject' in the "http://purl.org/dc/elements/1.1/"
     * name space.
     * 
     * @see <a
     *      href="http://dublincore.org/documents/dcmi-terms/#terms-subject">dcmi-terms/#terms-subject</a>
     * @param dcSubject
     *        a list of resource properties
     */
    public void setDcSubject(final List<BasicString> dcSubject)
    {
        this.dcSubject = dcSubject;
    }

    public List<String> getDcSubjectValues(String schemeId)
    {
        List<String> dcSubjectValues = new ArrayList<String>();
        for (BasicString bs : dcSubject)
        {
            if (schemeId.equals(bs.getSchemeId()))
            {
                dcSubjectValues.add(bs.getValue());
            }
        }
        return dcSubjectValues;
    }

    public List<String> getArchaeologyDcSubjectValues()
    {
        return getDcSubjectValues(EmdScheme.ARCHAEOLOGY_DC_SUBJECT.getId());
    }

}
