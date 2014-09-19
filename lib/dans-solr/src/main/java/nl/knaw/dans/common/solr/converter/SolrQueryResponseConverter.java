package nl.knaw.dans.common.solr.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nl.knaw.dans.common.lang.search.Document;
import nl.knaw.dans.common.lang.search.FacetField;
import nl.knaw.dans.common.lang.search.FacetValue;
import nl.knaw.dans.common.lang.search.Index;
import nl.knaw.dans.common.lang.search.SearchHit;
import nl.knaw.dans.common.lang.search.SnippetField;
import nl.knaw.dans.common.lang.search.simple.SimpleDocument;
import nl.knaw.dans.common.lang.search.simple.SimpleFacetField;
import nl.knaw.dans.common.lang.search.simple.SimpleFacetValue;
import nl.knaw.dans.common.lang.search.simple.SimpleField;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchHit;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchResult;
import nl.knaw.dans.common.lang.search.simple.SimpleSnippetField;

import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class SolrQueryResponseConverter {
    public static SimpleSearchResult<Document> convert(QueryResponse queryResponse, Index index) {
        SimpleSearchResult<Document> result = new SimpleSearchResult<Document>();

        Map<String, Map<String, List<String>>> hl = queryResponse.getHighlighting();
        SolrDocumentList sdl = queryResponse.getResults();

        // paging info
        result.setNumFound((int) sdl.getNumFound());

        // Relevance scores in Solr are calculated from the base
        // of 1.0f. If any document is scored any different then
        // the maximum relevance score is not 1.0f anymore. The
        // chances of a maximum relevance score of 1.0f with actual
        // meaning is pretty slim. This therefore assumes that if
        // a maximum relevance score of 1.0f is returned that it
        // is then better for the receiver of the search results
        // to ignore the relevancy score completely.
        result.setUseRelevanceScore(sdl.getMaxScore() != 1.0f);

        // add the documents
        List<SearchHit<Document>> hits = new ArrayList<SearchHit<Document>>(sdl.size());
        String primaryKeyValue = null;
        for (SolrDocument solrDoc : sdl) {
            // Don't change class type! SimpleSearchHit is assumed in SolrSearchEngine!
            SimpleDocument resultDoc = new SimpleDocument();
            float score = 0;
            List<SnippetField> snippetFields = null;

            // copy all fields
            for (Entry<String, Object> fieldEntry : solrDoc.entrySet()) {
                if (index != null) {
                    if (fieldEntry.getKey().equals(index.getPrimaryKey())) {
                        primaryKeyValue = fieldEntry.getValue().toString();
                    }
                }

                if (fieldEntry.getKey().equals("score")) {
                    score = ((Float) fieldEntry.getValue()).floatValue() / sdl.getMaxScore();
                } else {
                    SimpleField<Object> field = new SimpleField<Object>(fieldEntry.getKey(), fieldEntry.getValue());
                    resultDoc.addField(field);
                }
            }

            // add highlight info to SearchHit
            if (hl != null && primaryKeyValue != null) {
                Map<String, List<String>> hlMap = hl.get(primaryKeyValue);
                if (hlMap != null && hlMap.size() > 0) {
                    snippetFields = new ArrayList<SnippetField>(hlMap.size());
                    for (Entry<String, List<String>> hlEntry : hlMap.entrySet()) {
                        SimpleSnippetField snippetField = new SimpleSnippetField(hlEntry.getKey(), hlEntry.getValue());
                        snippetFields.add(snippetField);
                    }
                }
            }

            SimpleSearchHit<Document> hit = new SimpleSearchHit<Document>(resultDoc);
            hit.setRelevanceScore(score);
            if (snippetFields != null)
                hit.setSnippets(snippetFields);
            hits.add(hit);
        }
        result.setHits(hits);

        // add facet fields to response
        List<org.apache.solr.client.solrj.response.FacetField> solrFacets = queryResponse.getFacetFields();
        if (solrFacets != null) {
            List<FacetField> facetFields = new ArrayList<FacetField>(solrFacets.size());
            for (org.apache.solr.client.solrj.response.FacetField solrFacet : solrFacets) {
                List<Count> solrFacetValues = solrFacet.getValues();
                if (solrFacetValues == null)
                    continue;
                List<FacetValue<?>> facetValues = new ArrayList<FacetValue<?>>(solrFacetValues.size());
                for (Count solrFacetValue : solrFacetValues) {
                    SimpleFacetValue<String> facetValue = new SimpleFacetValue<String>();
                    facetValue.setCount((int) solrFacetValue.getCount());
                    facetValue.setValue(solrFacetValue.getName());
                    facetValues.add(facetValue);
                }

                facetFields.add(new SimpleFacetField(solrFacet.getName(), facetValues));
            }
            result.setFacets(facetFields);
        }

        return result;
    }
}
