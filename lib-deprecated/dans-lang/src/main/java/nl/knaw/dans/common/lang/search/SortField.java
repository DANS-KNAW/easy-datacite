package nl.knaw.dans.common.lang.search;

/**
 * A sort field is a field that contains a sort order as a value. Additionally a sort type can be set.
 * The default sort type is BY_VALUE.
 * 
 * @author lobo
 */
public interface SortField extends Field<SortOrder>
{
    void setSortType(SortType sortType);

    SortType getSortType();
}
