package nl.knaw.dans.easy.domain.model.emd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.knaw.dans.easy.domain.model.emd.types.Author;
import nl.knaw.dans.easy.domain.model.emd.types.BasicString;

/**
 * Container for resource properties of category creator.
 *
 * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-creator">dcmi-terms/#terms-creator</a>
 * @author ecco
 */
public class EmdCreator extends AbstractEmdContainer
{

    /**
     * Terms contained.
     */
    static final Term[] TERMS            = {
        new Term(Term.Name.CREATOR, Term.Namespace.DC, BasicString.class),
        new Term(Term.Name.CREATOR, Term.Namespace.EAS, Author.class)};

    /**
     *
     */
    private static final long  serialVersionUID = 709621311103768603L;

    private List<BasicString>  dcCreator        = new ArrayList<BasicString>();
    private List<Author>       easCreator       = new ArrayList<Author>();

    /**
     * {@inheritDoc}
     */
    public List<Term> getTerms()
    {
        return Arrays.asList(TERMS);
    }

    /**
     * Get a list of resource properties known as 'creator' in the "http://purl.org/dc/elements/1.1/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-creator">dcmi-terms/#terms-creator</a>
     * @return a list of resource properties
     */
    public List<BasicString> getDcCreator()
    {
        return dcCreator;
    }

    /**
     * Set a list of resource properties known as 'creator' in the "http://purl.org/dc/elements/1.1/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-creator">dcmi-terms/#terms-creator</a>
     * @param dcCreator
     *        a list of resource properties
     */
    public void setDcCreator(final List<BasicString> dcCreator)
    {
        this.dcCreator = dcCreator;
    }

    /**
     * Get a list of resource properties known as 'creator' in the "http://purl.org/dc/elements/1.1/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-creator">dcmi-terms/#terms-creator</a>
     * @return a list of resource properties
     */
    public List<Author> getEasCreator()
    {
        return easCreator;
    }

    /**
     * Set a list of resource properties known as 'creator' in the "http://purl.org/dc/elements/1.1/" name space.
     *
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-creator">dcmi-terms/#terms-creator</a>
     * @param easCreator
     *        a list of resource properties
     */
    public void setEasCreator(final List<Author> easCreator)
    {
        this.easCreator = easCreator;
    }

}
