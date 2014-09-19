package nl.knaw.dans.common.solr.converter;

import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.search.Field;
import nl.knaw.dans.common.lang.search.FieldSet;
import nl.knaw.dans.common.lang.search.SearchQuery;
import nl.knaw.dans.common.lang.search.SearchRequest;
import nl.knaw.dans.common.lang.search.SortField;
import nl.knaw.dans.common.lang.search.SortOrder;
import nl.knaw.dans.common.lang.search.SortType;
import nl.knaw.dans.common.lang.search.simple.CombinedOptionalField;
import nl.knaw.dans.common.solr.SolrUtil;
import nl.knaw.dans.common.solr.exceptions.NullPointerFieldException;
import nl.knaw.dans.common.solr.exceptions.SolrSearchEngineException;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;

public class SolrQueryRequestConverter {

    public static SolrQuery convert(SearchRequest request) throws SolrSearchEngineException {
        SolrQuery queryObj = new SolrQuery();

        // set query
        String query = "";
        String qstr = request.getQuery().getQueryString();
        if (qstr != null)
            query += qstr;
        FieldSet<?> fieldQueries = request.getFieldQueries();
        if (fieldQueries != null) {
            for (Field<?> fieldQuery : fieldQueries) {
                query += fieldQueryToString(fieldQuery) + " ";
            }
        }
        queryObj.setQuery(query);

        // set filter queries
        FieldSet<?> filterQueries = request.getFilterQueries();
        if (filterQueries != null) {
            int i = 0;
            String[] fq = new String[filterQueries.size()];
            for (Field<?> field : filterQueries) {
                fq[i] = fieldQueryToString(field);
                i++;
            }
            queryObj.setFilterQueries(fq);
        }

        // set sort fields
        List<SortField> sortFields = request.getSortFields();
        if (sortFields != null) {
            for (SortField sortField : sortFields) {
                ORDER order;
                if (sortField.getValue().equals(SortOrder.DESC))
                    order = ORDER.desc;
                else
                    order = ORDER.asc;
                String sortFieldName = sortField.getName();
                if (sortField.getSortType().equals(SortType.BY_RELEVANCE_SCORE))
                    sortFieldName = "score";
                queryObj.addSortField(sortFieldName, order);
            }
        }

        // faceting enabled
        Set<String> facetFields = request.getFacetFields();
        boolean enableFaceting = facetFields != null && facetFields.size() > 0;
        queryObj.setFacet(enableFaceting);
        if (enableFaceting) {
            for (String facetField : facetFields) {
                queryObj.addFacetField(facetField);
            }
        }

        // hit highlighting
        queryObj.setHighlight(request.isHighlightingEnabled());

        // paging
        queryObj.setRows(request.getLimit());
        queryObj.setStart(request.getOffset());

        // misc settings
        queryObj.setIncludeScore(true);

        return queryObj;
    }

    private static String fieldQueryToString(Field<?> fieldQuery) throws NullPointerFieldException {
        Object fieldValue = fieldQuery.getValue();
        if (fieldValue == null)
            throw new NullPointerFieldException("got null pointer for field " + fieldQuery.getName());

        String queryString = null;

        String queryStringValuePart = (fieldValue instanceof SearchQuery ? SolrUtil.escapeColon(((SearchQuery) fieldValue).getQueryString()) : SolrUtil
                .escapeColon(SolrUtil.toString(fieldValue)));

        if (fieldQuery instanceof CombinedOptionalField) {
            // assume that the values must be forced into phrases using double quotes around them
            // note that it would be better if we could ask the field if it needs forcing to a phrase
            queryStringValuePart = "(\"" + queryStringValuePart + "\")";

            List<String> names = ((CombinedOptionalField<?>) fieldQuery).getNames();
            StringBuilder sbQuery = new StringBuilder();
            // name1:value OR name2: value OR name3:value, etc..
            for (String name : names) {
                if (sbQuery.length() > 0)
                    sbQuery.append(" OR ");
                sbQuery.append(name);
                sbQuery.append(":");
                sbQuery.append(queryStringValuePart);
            }
            queryString = sbQuery.toString();
        } else {
            queryStringValuePart = "(" + queryStringValuePart + ")";
            queryString = fieldQuery.getName() + ":" + queryStringValuePart;
        }

        return queryString;
    }

}
