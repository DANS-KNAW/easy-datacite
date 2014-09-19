package nl.knaw.dans.common.wicket.components.search.results;

import java.io.Serializable;

import nl.knaw.dans.common.lang.search.SortOrder;
import nl.knaw.dans.common.lang.search.SortType;
import nl.knaw.dans.common.wicket.components.search.model.SearchData;
import nl.knaw.dans.common.wicket.components.search.model.SearchModel;

/**
 * Configuration of a sort link. Sort links are presented by the SearchResultPanel.
 * 
 * @author lobo
 */
public class SortLinkConfig implements Serializable {
    private static final long serialVersionUID = 4339062684284092473L;

    /**
     * the name of the search field to sort on. Must be an existing, known and sortable field in the search engine otherwise errors are bound to occur.
     */
    private String fieldName;

    private SortType sortType;

    private final SortOrder initialSortOrder;

    /**
     * Either set this property or use override the isVisible method for conditional visibility.
     */
    private boolean visible = true;

    public SortLinkConfig() {
        initialSortOrder = SortOrder.ASC;
    }

    public SortLinkConfig(String fieldName, SortType sortType) {
        this.fieldName = fieldName;
        this.sortType = sortType;
        initialSortOrder = SortOrder.ASC;
    }

    public SortLinkConfig(String fieldName, SortType sortType, SortOrder initialSortOrder) {
        this.fieldName = fieldName;
        this.sortType = sortType;
        this.initialSortOrder = initialSortOrder;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setSortType(SortType sortType) {
        this.sortType = sortType;
    }

    public SortType getSortType() {
        return sortType;
    }

    public SortOrder getInitialSortOrder() {
        return initialSortOrder;
    }

    public boolean isVisible(SearchModel model) {
        return visible;
    }

    public boolean isVisible(SearchData sdata) {
        return visible;
    }
}
