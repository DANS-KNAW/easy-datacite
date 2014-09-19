package nl.knaw.dans.common.lang.search;

public enum SortType {
    /**
     * Sorting by value is the default. How the values get sorted of course depends on the type of the value.
     */
    BY_VALUE,

    /**
     * Sorting by relevance score
     */
    BY_RELEVANCE_SCORE
}
