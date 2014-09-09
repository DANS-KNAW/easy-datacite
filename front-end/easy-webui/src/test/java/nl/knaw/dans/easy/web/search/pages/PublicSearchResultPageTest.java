package nl.knaw.dans.easy.web.search.pages;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.dataset.DatasetSB;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.search.FacetValue;
import nl.knaw.dans.common.lang.search.SearchHit;
import nl.knaw.dans.common.lang.search.SearchRequest;
import nl.knaw.dans.common.lang.search.SearchResult;
import nl.knaw.dans.common.lang.search.exceptions.FieldNotFoundException;
import nl.knaw.dans.common.lang.search.simple.SimpleFacetField;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchHit;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.components.search.SearchBar;
import nl.knaw.dans.common.wicket.components.search.facets.CollapsedFacetValue;
import nl.knaw.dans.easy.EasyApplicationContextMock;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.data.search.EasyDatasetSB;
import nl.knaw.dans.easy.domain.exceptions.ObjectNotFoundException;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.DisciplineCollectionService;

import org.apache.wicket.PageParameters;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

public class PublicSearchResultPageTest
{
    private static final String FACET_VALUE_LINK = "searchResultPanel:refineFacetsEnclosure:refineFacets:2:facet:facetValuesUL:facetValuesLI:0:facetValueLink";
    private EasyApplicationContextMock applicationContext;

    @Before
    public void mockApplicationContext() throws Exception
    {
        applicationContext = new EasyApplicationContextMock();
        applicationContext.expectStandardSecurity(false);
        applicationContext.expectDefaultResources();
        applicationContext.expectNoDatasets();
    }

    @After
    public void reset()
    {
        PowerMock.resetAll();
    }

    @Test
    public void noDatasets() throws Exception
    {
        expect(applicationContext.getSearchService().searchPublished(isA(SearchRequest.class), isA(EasyUser.class)))//
                .andStubReturn(null);
        final EasyWicketTester tester = EasyWicketTester.startPage(applicationContext, PublicSearchResultPage.class);

        tester.dumpPage();
        tester.assertRenderedPage(PublicSearchResultPage.class);
        tester.assertLabel("searchResultPanel:resultMessage", "No results found");
    }

    @Test
    public void noMatch() throws Exception
    {
        expect(applicationContext.getSearchService().searchPublished(isA(SearchRequest.class), isA(EasyUser.class)))//
                .andStubReturn(null);
        final PageParameters parameters = new PageParameters();
        String value = "rabarbera";
        parameters.add(SearchBar.QUERY_PARAM, value);
        final EasyWicketTester tester = EasyWicketTester.startPage(applicationContext, PublicSearchResultPage.class, parameters);

        tester.assertRenderedPage(PublicSearchResultPage.class);
        tester.assertLabel("searchResultPanel:resultMessage", "No results found matching your criteria.");
    }

    @Test
    public void oneDataset() throws Exception
    {
        mockDisciplineService();
        final SearchResultMock searchResult = mockSearchResult(createHits(mockDatasetSB("mocked title 1")));
        mockGetFacetByName(searchResult, "emd_audience", new ArrayList<FacetValue<?>>());
        mockGetFacetByName(searchResult, "easy_collections", new ArrayList<FacetValue<?>>());
        mockGetFacetByName(searchResult, "ds_accesscategory", new ArrayList<FacetValue<?>>());

        final EasyWicketTester tester = EasyWicketTester.startPage(applicationContext, BrowsePage.class);
        tester.dumpPage();

        tester.assertRenderedPage(PublicSearchResultPage.class);
        tester.assertLabelContains("searchResultPanel:resultMessage", "<b>1</b>");
    }

    @Test
    public void datasets() throws Exception
    {
        mockDisciplineService();
        final SearchResultMock searchResult = mockSearchResult(createHits(mockDatasetSB("mocked title 1"), mockDatasetSB("mocked title 2")));
        mockGetFacetByName(searchResult, "emd_audience", new ArrayList<FacetValue<?>>());
        mockGetFacetByName(searchResult, "easy_collections", new ArrayList<FacetValue<?>>());
        mockGetFacetByName(searchResult, "ds_accesscategory", mockFacetValues(2));
        mockGetFacetByName(searchResult, "ds_state", new ArrayList<FacetValue<?>>());
        EasyMock.expect(searchResult.useRelevanceScore()).andStubReturn(false);

        final EasyWicketTester tester = EasyWicketTester.startPage(applicationContext, PublicSearchResultPage.class);
        tester.dumpPage();

        assertFacet(tester);
        tester.clickLink(FACET_VALUE_LINK);
        tester.dumpPage("FacetSelected");
        tester.clickLink("searchResultPanel:searchCriteria:criteriumLinks:1:removeLink");
        tester.dumpPage("FacetUnselected");
        assertFacet(tester);
    }

    private void mockDisciplineService() throws ServiceException, ObjectNotFoundException
    {
        final DisciplineCollectionService disciplineCollectionService = PowerMock.createMock(DisciplineCollectionService.class);
        EasyMock.expect(disciplineCollectionService.getDisciplineName(isA(DmoStoreId.class))).andStubReturn("mocked Discipline");
        applicationContext.putBean("disciplineService", disciplineCollectionService);
    }

    private EasyDatasetSB mockDatasetSB(final String string)
    {
        final EasyDatasetSB datasetSB = new EasyDatasetSB();
        datasetSB.setState(DatasetState.PUBLISHED);
        datasetSB.setDcTitle(createList(string));
        datasetSB.setDcCreator(createList("mocked creator"));
        datasetSB.setAccessCategory(AccessCategory.OPEN_ACCESS_FOR_REGISTERED_USERS);
        datasetSB.setAudience(createList(DisciplineContainer.NAMESPACE + ":1"));
        datasetSB.setDateSubmitted(new DateTime("2014-05-01"));
        return datasetSB;
    }

    private ArrayList<SearchHit<DatasetSB>> createHits(final DatasetSB... datasetSBs)
    {
        final ArrayList<SearchHit<DatasetSB>> hits = new ArrayList<SearchHit<DatasetSB>>();
        for (final DatasetSB datasetSB : datasetSBs)
            hits.add(new SimpleSearchHit<DatasetSB>(datasetSB));
        return hits;
    }

    public static interface SearchResultMock extends SearchResult<DatasetSB>
    {
        // PowerMock.createMock needs a simple class, not a generic.
    };

    private SearchResultMock mockSearchResult(final ArrayList<SearchHit<DatasetSB>> hits) throws ServiceException
    {
        final SearchResultMock searchResult = PowerMock.createMock(SearchResultMock.class);
        EasyMock.expect(searchResult.getTotalHits()).andStubReturn(hits.size());
        EasyMock.expect(searchResult.getHits()).andStubReturn(hits);
        @SuppressWarnings("unchecked")
        final SearchResult<DatasetSB> searchPublished = (SearchResult<DatasetSB>) applicationContext.getSearchService()//
                .searchPublished(isA(SearchRequest.class), isA(EasyUser.class));
        expect(searchPublished).andStubReturn(searchResult);
        return searchResult;
    }

    private List<String> createList(final String... strings)
    {
        return Arrays.asList(strings);
    }

    private ArrayList<FacetValue<?>> mockFacetValues(final int count) throws Exception
    {
        final ArrayList<FacetValue<?>> audienceFacets = new ArrayList<FacetValue<?>>();
        final CollapsedFacetValue<AccessCategory> facetValue = new CollapsedFacetValue<AccessCategory>();
        facetValue.setCount(count);
        facetValue.setValue(AccessCategory.OPEN_ACCESS_FOR_REGISTERED_USERS);
        audienceFacets.add(facetValue);
        return audienceFacets;
    }

    private void mockGetFacetByName(final SearchResultMock searchResult, final String facetFieldName, final List<FacetValue<?>> value)
            throws FieldNotFoundException
    {
        EasyMock.expect(searchResult.getFacetByName(facetFieldName)).andStubReturn(new SimpleFacetField(facetFieldName, value));
    }

    private void assertFacet(final EasyWicketTester tester)
    {
        tester.assertRenderedPage(PublicSearchResultPage.class);
        tester.assertLabelContains("searchResultPanel:resultMessage", "<b>2</b>");
        final String hitsPath = "searchResultPanel:searchHits:";
        tester.assertLabel(FACET_VALUE_LINK + ":facetValue", "Open for registered users");
        tester.assertLabel(FACET_VALUE_LINK + ":facetCount", "2");
        tester.assertLabel(hitsPath + "0:searchHit:showDataset:disciplines:0:disciplineName", "mocked Discipline");
        tester.assertLabel(hitsPath + "0:searchHit:showDataset:title", "mocked title 1");
        tester.assertLabel(hitsPath + "1:searchHit:showDataset:title", "mocked title 2");
    }
}
