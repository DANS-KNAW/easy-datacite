package nl.knaw.dans.pf.language.emd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.knaw.dans.pf.language.emd.types.Author;
import nl.knaw.dans.pf.language.emd.types.BasicString;

/**
 * Container for resource properties of category contributor.
 * 
 * @see <a
 *      href="http://dublincore.org/documents/dcmi-terms/#terms-contributor">dcmi-terms/#terms-contributor</a>
 * @author ecco
 */
public class EmdContributor extends AbstractEmdContainer
{

    /**
     * Terms contained.
     */
    static final Term[] TERMS = {new Term(Term.Name.CONTRIBUTOR, Term.Namespace.DC, BasicString.class),
            new Term(Term.Name.CONTRIBUTOR, Term.Namespace.EAS, Author.class)};

    /**
     *
     */
    private static final long serialVersionUID = 7285039294774059387L;

    private List<BasicString> dcContributor = new ArrayList<BasicString>();
    private List<Author> easContributor = new ArrayList<Author>();

    /**
     * {@inheritDoc}
     */
    public List<Term> getTerms()
    {
        return Arrays.asList(TERMS);
    }

    /**
     * Get a list of resource properties known as 'contributor' in the "http://purl.org/dc/elements/1.1/"
     * name space.
     * 
     * @see <a
     *      href="http://dublincore.org/documents/dcmi-terms/#terms-contributor">dcmi-terms/#terms-contributor</a>
     * @return a list of resource properties
     */
    public List<BasicString> getDcContributor()
    {
        return dcContributor;
    }

    /**
     * Set a list of resource properties known as 'contributor' in the "http://purl.org/dc/elements/1.1/"
     * name space.
     * 
     * @see <a
     *      href="http://dublincore.org/documents/dcmi-terms/#terms-contributor">dcmi-terms/#terms-contributor</a>
     * @param dcContributor
     *        a list of resource properties
     */
    public void setDcContributor(final List<BasicString> dcContributor)
    {
        this.dcContributor = dcContributor;
    }

    /**
     * Get a list of resource properties known as 'contributor' in the "http://purl.org/dc/elements/1.1/"
     * name space.
     * 
     * @see <a
     *      href="http://dublincore.org/documents/dcmi-terms/#terms-contributor">dcmi-terms/#terms-contributor</a>
     * @return a list of resource properties
     */
    public List<Author> getEasContributor()
    {
        return easContributor;
    }

    /**
     * Set a list of resource properties known as 'contributor' in the "http://purl.org/dc/elements/1.1/"
     * name space.
     * 
     * @see <a
     *      href="http://dublincore.org/documents/dcmi-terms/#terms-contributor">dcmi-terms/#terms-contributor</a>
     * @param easContributor
     *        a list of resource properties
     */
    public void setEasContributor(final List<Author> easContributor)
    {
        this.easContributor = easContributor;
    }

    public List<String> getDigitalAuthorIds()
    {
        List<String> dais = new ArrayList<String>();
        for (Author author : easContributor)
        {
            if (author.hasDigitalAuthorId())
            {
                dais.add(author.getEntityId());
            }
        }
        return dais;
    }

    public List<Author> getDAIAuthors()
    {
        List<Author> daiAuthors = new ArrayList<Author>();
        for (Author author : easContributor)
        {
            if (author.hasDigitalAuthorId())
            {
                daiAuthors.add(author);
            }
        }
        return daiAuthors;
    }

}
