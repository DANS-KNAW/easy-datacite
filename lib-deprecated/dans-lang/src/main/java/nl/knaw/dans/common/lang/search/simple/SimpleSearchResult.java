package nl.knaw.dans.common.lang.search.simple;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.search.FacetField;
import nl.knaw.dans.common.lang.search.SearchHit;
import nl.knaw.dans.common.lang.search.SearchResult;
import nl.knaw.dans.common.lang.search.exceptions.FieldNotFoundException;

public class SimpleSearchResult<T> implements SearchResult<T> {
    private static final long serialVersionUID = -2492083390296926152L;

    private List<SearchHit<T>> results;

    private int numFound = 0;

    private Map<String, FacetField> facets = null;

    private boolean useRelevanceScore;

    public SimpleSearchResult() {}

    @SuppressWarnings("unchecked")
    public List<SearchHit<T>> getHits() {
        return results != null ? results : Collections.EMPTY_LIST;
    }

    public void setHits(List<SearchHit<T>> results) {
        this.results = results;
    }

    public void setNumFound(int numFound) {
        this.numFound = numFound;
    }

    public int getTotalHits() {
        return this.numFound;
    }

    public Collection<FacetField> getFacets() {
        if (facets == null)
            return Collections.emptySet();
        else
            return facets.values();
    }

    public void setFacets(Collection<FacetField> facetList) {
        facets = new HashMap<String, FacetField>(facetList.size());
        for (FacetField field : facetList)
            facets.put(field.getName(), field);
    }

    @Override
    public String toString() {
        return super.toString() + "[numFound = '" + numFound + "' number of hits = '" + results.size() + "']";
    }

    public FacetField getFacetByName(String facetFieldName) throws FieldNotFoundException {
        if (facets == null)
            throw new FieldNotFoundException(facetFieldName);
        FacetField result = facets.get(facetFieldName);
        if (result == null)
            throw new FieldNotFoundException(facetFieldName);
        return result;
    }

    public void setUseRelevanceScore(boolean useRelevanceScore) {
        this.useRelevanceScore = useRelevanceScore;
    }

    public boolean useRelevanceScore() {
        return useRelevanceScore;
    }
}
