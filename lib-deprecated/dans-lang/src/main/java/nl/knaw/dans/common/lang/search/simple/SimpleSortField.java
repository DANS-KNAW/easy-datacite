package nl.knaw.dans.common.lang.search.simple;

import nl.knaw.dans.common.lang.search.SortField;
import nl.knaw.dans.common.lang.search.SortOrder;
import nl.knaw.dans.common.lang.search.SortType;

public class SimpleSortField extends SimpleField<SortOrder> implements SortField
{
    private static final long serialVersionUID = -1932854592158278674L;

    /**
     *  default sort type is BY_VALUE
     */
    private SortType sortType = SortType.BY_VALUE;

    public SimpleSortField(String name, SortOrder value)
    {
        super(name, value);
    }

    public SimpleSortField(String name, SortOrder value, SortType type)
    {
        super(name, value);
        setSortType(type);
    }

    public SortType getSortType()
    {
        return sortType;
    }

    public void setSortType(SortType sortType)
    {
        this.sortType = sortType;
    }

    @Override
    public String toString()
    {
        return super.toString() + "[sortType = '" + sortType.toString() + "']";
    }
}
