package nl.knaw.dans.easy.web.search.custom;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.wicket.components.search.FieldNameResourceTranslator;
import nl.knaw.dans.common.wicket.components.search.criteria.FacetCriterium;
import nl.knaw.dans.common.wicket.components.search.facets.FacetConfig;
import nl.knaw.dans.common.wicket.components.search.model.CriteriumListener;
import nl.knaw.dans.common.wicket.components.search.model.SearchCriterium;
import nl.knaw.dans.common.wicket.components.search.model.SearchRequestBuilder;
import nl.knaw.dans.easy.data.search.EasyDatasetSB;
import nl.knaw.dans.easy.search.RecursiveListCache;

public class ArchaeologyCriteriumListener implements CriteriumListener {

    public static final String TRIGGER_NAME = EasyDatasetSB.AUDIENCE_FIELD;
    public static final String TRIGGER_VALUE = "easy-discipline:2"; // i.e. archaeology

    private static final long serialVersionUID = 2884656653601564370L;

    private static ArchaeologyCriteriumListener INSTANCE;

    public static ArchaeologyCriteriumListener instance() {
        if (INSTANCE == null) {
            INSTANCE = new ArchaeologyCriteriumListener();
        }
        return INSTANCE;
    }

    private ArchaeologyCriteriumListener() {

    }

    @Override
    public void addFacets(List<FacetConfig> refineFacets, SearchRequestBuilder builder) {
        for (SearchCriterium searchCriterium : builder.getCriteria()) {
            if (isTrigger(searchCriterium)) {
                refineFacets.addAll(0, getArchaeologyFacets());
            }
        }
    }

    @Override
    public void onCriteriumAdded(SearchCriterium searchCriterium, SearchRequestBuilder searchRequestBuilder) {
        if (isTrigger(searchCriterium)) {
            searchRequestBuilder.getFacets().addAll(0, getArchaeologyFacets());
        }
    }

    @Override
    public void onCriteriumRemoved(SearchCriterium searchCriterium, SearchRequestBuilder searchRequestBuilder) {
        if (isTrigger(searchCriterium)) {
            removeArchaeologyFacets(searchRequestBuilder);
        }
    }

    private List<FacetConfig> getArchaeologyFacets() {
        FacetConfig facetConfig;
        ArrayList<FacetConfig> refineFacets = new ArrayList<FacetConfig>();

        facetConfig = new FacetConfig(EasyDatasetSB.ARCHAEOLOGY_DCTERMS_TEMPORAL);
        facetConfig.setFacetNameTranslator(new FieldNameResourceTranslator());
        facetConfig.setFacetValueTranslator(new RecursiveListTranslator(RecursiveListCache.LID_ARCHAEOLOGY_DCTERMS_TEMPORAL));
        facetConfig.setFacetValueCollapser(new RecursiveListValueCollapser(RecursiveListCache.LID_ARCHAEOLOGY_DCTERMS_TEMPORAL, true));
        refineFacets.add(facetConfig);

        facetConfig = new FacetConfig(EasyDatasetSB.ARCHAEOLOGY_DC_SUBJECT);
        facetConfig.setFacetNameTranslator(new FieldNameResourceTranslator());
        facetConfig.setFacetValueTranslator(new RecursiveListTranslator(RecursiveListCache.LID_ARCHAEOLOGY_DC_SUBJECT));
        facetConfig.setFacetValueCollapser(new RecursiveListValueCollapser(RecursiveListCache.LID_ARCHAEOLOGY_DC_SUBJECT, true));
        refineFacets.add(facetConfig);

        return refineFacets;
    }

    private void removeArchaeologyFacets(SearchRequestBuilder searchRequestBuilder) {
        List<FacetConfig> facetsToRemove = new ArrayList<FacetConfig>();
        for (FacetConfig facetConfig : searchRequestBuilder.getFacets()) {
            if (isArchaeologyFacet(facetConfig.getFacetName())) {
                facetsToRemove.add(facetConfig);
            }
        }
        searchRequestBuilder.getFacets().removeAll(facetsToRemove);
    }

    private boolean isArchaeologyFacet(String facetName) {
        return EasyDatasetSB.ARCHAEOLOGY_DC_SUBJECT.equals(facetName) || EasyDatasetSB.ARCHAEOLOGY_DCTERMS_TEMPORAL.equals(facetName);
    }

    private boolean isTrigger(SearchCriterium searchCriterium) {
        boolean isTrigger = false;
        if (searchCriterium instanceof FacetCriterium) {
            FacetCriterium facetCriterium = (FacetCriterium) searchCriterium;
            String facetName = facetCriterium.getFacetName();
            Object value = facetCriterium.getFacetValue().getValue();
            isTrigger = TRIGGER_NAME.equals(facetName) && TRIGGER_VALUE.equals(value);
        }
        return isTrigger;
    }

}
