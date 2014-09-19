package nl.knaw.dans.common.lang.search.simple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.search.Field;
import nl.knaw.dans.common.lang.search.FieldSet;
import nl.knaw.dans.common.lang.search.Index;
import nl.knaw.dans.common.lang.search.SearchQuery;
import nl.knaw.dans.common.lang.search.SearchRequest;
import nl.knaw.dans.common.lang.search.SortField;

public class SimpleSearchRequest implements SearchRequest {
    private static final long serialVersionUID = -3624908538629551131L;

    @SuppressWarnings("unchecked")
    private FieldSet fieldQueries = new SimpleFieldSet();

    @SuppressWarnings("unchecked")
    private FieldSet filterQueries = new SimpleFieldSet();

    private SearchQuery query;

    private int limit = 10;

    private int offset;

    private List<SortField> sortFields;

    private Set<Class<?>> filterBeans;

    private Index index;

    private boolean highlightEnabled;

    private Set<String> facetFieldList;

    public SimpleSearchRequest() {}

    public SimpleSearchRequest(SearchQuery query) {
        this.query = query;
    }

    public SimpleSearchRequest(SearchQuery query, Index index) {
        this.index = index;
        this.query = query;
    }

    public SimpleSearchRequest(String queryString) {
        this.query = new SimpleSearchQuery(queryString);
    }

    public FieldSet<?> getFieldQueries() {
        return fieldQueries;
    }

    public void setFieldQueries(FieldSet<?> fieldQueries) {
        this.fieldQueries = fieldQueries;
    }

    public void clearFieldQueries() {
        fieldQueries.clear();
    }

    @SuppressWarnings("unchecked")
    public void addFieldQuery(Field<?> query) {
        fieldQueries.add(query);
    }

    public FieldSet<?> getFilterQueries() {
        return filterQueries;
    }

    @SuppressWarnings("unchecked")
    public void addFilterQuery(Field<?> query) {
        if (filterQueries == null)
            filterQueries = new SimpleFieldSet();
        filterQueries.add(query);
    }

    public void setFilterQueries(FieldSet<?> filterQueries) {
        this.filterQueries = filterQueries;
    }

    public void clearFilterQueries() {
        if (filterQueries != null)
            filterQueries.clear();
    }

    public Index getIndex() {
        return index;
    }

    public SearchQuery getQuery() {
        return query != null ? query : EmptySearchQuery.getInstance();
    }

    public void setQuery(SearchQuery query) {
        this.query = query;
    }

    public int getLimit() {
        return limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void addSortField(SortField orderField) {
        if (sortFields == null)
            sortFields = new ArrayList<SortField>();
        sortFields.add(orderField);
    }

    public void clearSortFields() {
        if (sortFields != null)
            sortFields.clear();
    }

    @SuppressWarnings("unchecked")
    public List<SortField> getSortFields() {
        return (List<SortField>) (sortFields != null ? sortFields : Collections.emptyList());
    }

    public void setSortFields(List<SortField> orderFields) {
        this.sortFields = orderFields;
    }

    public void addFilterBean(Class<?> filterBean) {
        if (filterBeans == null)
            filterBeans = new HashSet<Class<?>>();

        filterBeans.add(filterBean);
    }

    public void clearFilterBeans() {
        if (filterBeans != null)
            filterBeans.clear();
    }

    @SuppressWarnings("unchecked")
    public Set<Class<?>> getFilterBeans() {
        return filterBeans != null ? filterBeans : Collections.EMPTY_SET;
    }

    public void setFilterBeans(Set<Class<?>> filterBeans) {
        this.filterBeans = filterBeans;
    }

    public void setIndex(Index index) {
        this.index = index;
    }

    public boolean isHighlightingEnabled() {
        return highlightEnabled;
    }

    public void setHighlightingEnabled(boolean enabled) {
        highlightEnabled = enabled;
    }

    @SuppressWarnings("unchecked")
    public Set<String> getFacetFields() {
        return facetFieldList != null ? facetFieldList : Collections.EMPTY_SET;
    }

    public void setFacetFields(Set<String> facetField) {
        this.facetFieldList = facetField;
    }

}
