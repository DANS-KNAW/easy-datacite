package nl.knaw.dans.common.lang.search;

/**
 * The order in which sorting should occur: ascending or descending.
 * @author lobo
 *
 */
public enum SortOrder
{
    /**
     * sort in ascending order
     */
    ASC,

    /**
     * sort in descending order
     */
    DESC;

    /**
     * @return the reverse of the currrent sort order
     */
    public SortOrder getReverse()
    {
        if (this.equals(ASC))
            return SortOrder.DESC;
        else
            return SortOrder.ASC;
    }
}
