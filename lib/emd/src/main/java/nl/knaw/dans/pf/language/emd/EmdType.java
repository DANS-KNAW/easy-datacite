package nl.knaw.dans.pf.language.emd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import nl.knaw.dans.pf.language.emd.types.BasicString;

/**
 * Container for resource properties of category type.
 * 
 * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-type">dcmi-terms/#terms-type</a>
 * @author ecco
 */
public class EmdType extends AbstractEmdContainer
{
    /**
     * Terms contained.
     */
    static final Term[] TERMS = {new Term(Term.Name.TYPE, Term.Namespace.DC, BasicString.class)};

    private static final long serialVersionUID = -9078955732564236681L;

    private List<BasicString> dcType = new ArrayList<BasicString>();

    /**
     * {@inheritDoc}
     */
    public List<Term> getTerms()
    {
        return Arrays.asList(TERMS);
    }

    /**
     * Get a list of resource properties known as 'type' in the "http://purl.org/dc/elements/1.1/" name
     * space.
     * 
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-type">dcmi-terms/#terms-type</a>
     * @return a list of resource properties
     */
    public List<BasicString> getDcType()
    {
        return dcType;
    }

    /**
     * Set a list of resource properties known as 'type' in the "http://purl.org/dc/elements/1.1/" name
     * space.
     * 
     * @see <a href="http://dublincore.org/documents/dcmi-terms/#terms-type">dcmi-terms/#terms-type</a>
     * @param dcType
     *        a list of resource properties
     */
    public void setDcType(final List<BasicString> dcType)
    {
        this.dcType = dcType;
    }

    public boolean contains(BasicString bs)
    {
        boolean found = false;
        Iterator<BasicString> iter = dcType.iterator();
        while (iter.hasNext() && !found)
        {
            found = iter.next().shallowEquals(bs);
        }
        return found;
    }

}
