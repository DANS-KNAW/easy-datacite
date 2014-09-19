package nl.knaw.dans.easy.business.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import nl.knaw.dans.common.lang.dataset.DatasetSB;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.search.Field;
import nl.knaw.dans.common.lang.search.FieldSet;
import nl.knaw.dans.common.lang.search.SearchRequest;
import nl.knaw.dans.common.lang.search.exceptions.SearchBeanFactoryException;
import nl.knaw.dans.common.lang.search.exceptions.SearchEngineException;
import nl.knaw.dans.common.lang.search.simple.SimpleField;
import nl.knaw.dans.common.lang.search.simple.SimpleFieldSet;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchRequest;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchResult;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.user.User.State;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.search.DatasetSearch;
import nl.knaw.dans.easy.data.search.EasyDatasetSB;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.security.CodedAuthz;
import nl.knaw.dans.easy.security.Security;
import nl.knaw.dans.easy.util.TestHelper;

import org.easymock.EasyMock;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class EasySearchServiceTest extends TestHelper {
    private static DatasetSearch datasetSearch;
    private static EasySearchService service;
    private static EasyUserImpl normalUser;
    private static EasyUserImpl superUser;

    @BeforeClass
    public static void beforeClass() {
        new Security(new CodedAuthz());

        datasetSearch = EasyMock.createMock(DatasetSearch.class);
        Data data = new Data();
        data.setDatasetSearch(datasetSearch);

        service = new EasySearchService();

        normalUser = new EasyUserImpl("jan");
        normalUser.setState(State.ACTIVE);

        superUser = new EasyUserImpl("piet");
        superUser.setState(State.ACTIVE);
        Set<Role> arch = new HashSet<Role>();
        arch.add(Role.USER);
        arch.add(Role.ARCHIVIST);
        superUser.setRoles(arch);
    }

    @AfterClass
    public static void afterClass() {
        // the next test class should not inherit from this one
        Data data = new Data();
        data.setDatasetSearch(null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void searchPublishedTest() throws ServiceException, SearchEngineException, SearchBeanFactoryException {
        SearchRequest request = new SimpleSearchRequest("test");
        FieldSet expected = new SimpleFieldSet();

        EasyMock.reset(datasetSearch);
        expected.add(new SimpleField(DatasetSB.DS_STATE_FIELD, "(" + DatasetState.PUBLISHED.toString() + " OR " + DatasetState.MAINTENANCE.toString() + ")"));
        EasyMock.expect(datasetSearch.search(request)).andReturn(new SimpleSearchResult());

        EasyMock.replay(datasetSearch);
        service.searchPublished(request, normalUser);
        EasyMock.verify(datasetSearch);

        compareFieldQueries(expected, request.getFilterQueries());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void searchWorkTest() throws ServiceException, SearchEngineException, SearchBeanFactoryException {
        FieldSet expected = new SimpleFieldSet();
        SimpleSearchRequest request = new SimpleSearchRequest("test");

        EasyMock.reset(datasetSearch);
        EasyMock.expect(datasetSearch.search(request)).andReturn(new SimpleSearchResult());
        expected.add(new SimpleField(DatasetSB.DS_STATE_FIELD, "(" + DatasetState.SUBMITTED.toString() + " OR " + DatasetState.MAINTENANCE.toString() + ")"));

        // our work
        EasyMock.replay(datasetSearch);
        service.searchOurWork(request, superUser);
        EasyMock.verify(datasetSearch);

        compareFieldQueries(expected, request.getFilterQueries());

        // all work
        request = new SimpleSearchRequest("test");

        EasyMock.reset(datasetSearch);
        EasyMock.expect(datasetSearch.search(request)).andReturn(new SimpleSearchResult());

        EasyMock.replay(datasetSearch);
        service.searchAllWork(request, superUser);
        EasyMock.verify(datasetSearch);

        compareFieldQueries(expected, request.getFilterQueries());

        // my work
        request = new SimpleSearchRequest("test");
        SimpleField assigneeId = new SimpleField(EasyDatasetSB.ASSIGNEE_ID_FIELD, superUser.getId());
        expected.add(assigneeId);

        EasyMock.reset(datasetSearch);
        EasyMock.expect(datasetSearch.search(request)).andReturn(new SimpleSearchResult());

        EasyMock.replay(datasetSearch);
        service.searchMyWork(request, superUser);
        EasyMock.verify(datasetSearch);

        compareFieldQueries(expected, request.getFilterQueries());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void searchTrashcanTest() throws ServiceException, SearchEngineException, SearchBeanFactoryException {
        SearchRequest request = new SimpleSearchRequest("test");
        FieldSet expected = new SimpleFieldSet();

        EasyMock.reset(datasetSearch);
        EasyMock.expect(datasetSearch.search(request)).andReturn(new SimpleSearchResult());
        expected.add(new SimpleField(DatasetSB.DS_STATE_FIELD, DatasetState.DELETED));

        EasyMock.replay(datasetSearch);
        service.searchTrashcan(request, superUser);
        EasyMock.verify(datasetSearch);

        compareFieldQueries(expected, request.getFilterQueries());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void searchMyDatasetsTest() throws ServiceException, SearchEngineException, SearchBeanFactoryException {
        SearchRequest request = new SimpleSearchRequest("test");
        FieldSet expected = new SimpleFieldSet();

        EasyMock.reset(datasetSearch);
        EasyMock.expect(datasetSearch.search(request)).andReturn(new SimpleSearchResult());
        expected.add(new SimpleField(DatasetSB.DS_STATE_FIELD, "(" + DatasetState.DRAFT.toString() + " OR " + DatasetState.SUBMITTED.toString() + " OR "
                + DatasetState.PUBLISHED.toString() + " OR " + DatasetState.MAINTENANCE.toString() + ")"));
        expected.add(new SimpleField(EasyDatasetSB.DEPOSITOR_ID_FIELD, superUser.getId()));

        EasyMock.replay(datasetSearch);
        service.searchMyDataset(request, superUser);
        EasyMock.verify(datasetSearch);

        compareFieldQueries(expected, request.getFilterQueries());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void searchMyDatasetsStateSubsetTest() throws ServiceException, SearchEngineException, SearchBeanFactoryException {
        SearchRequest request = new SimpleSearchRequest("test");
        FieldSet expected = new SimpleFieldSet();

        // try subset
        EasyMock.reset(datasetSearch);
        EasyMock.expect(datasetSearch.search(request)).andReturn(new SimpleSearchResult());
        SimpleField states = new SimpleField(DatasetSB.DS_STATE_FIELD, DatasetState.DRAFT);
        expected.add(states);
        expected.add(new SimpleField(EasyDatasetSB.DEPOSITOR_ID_FIELD, superUser.getId()));

        request.addFilterQuery(new SimpleField(states));

        EasyMock.replay(datasetSearch);
        service.searchMyDataset(request, superUser);
        EasyMock.verify(datasetSearch);

        compareFieldQueries(expected, request.getFilterQueries());

    }

    @Test
    @SuppressWarnings("unchecked")
    public void searchMyDatasetsStateInvalidSubsetTest() throws ServiceException, SearchEngineException, SearchBeanFactoryException {
        // prepare
        SearchRequest request = new SimpleSearchRequest("test");
        EasyMock.reset(datasetSearch);
        EasyMock.expect(datasetSearch.search(request)).andReturn(new SimpleSearchResult());

        SimpleField states = new SimpleField(DatasetSB.DS_STATE_FIELD, DatasetState.DELETED);

        request.addFilterQuery(new SimpleField(states));

        FieldSet expected = new SimpleFieldSet();
        expected.add(new SimpleField(DatasetSB.DS_STATE_FIELD, "(" + DatasetState.DRAFT.toString() + " OR " + DatasetState.SUBMITTED.toString() + " OR "
                + DatasetState.PUBLISHED.toString() + " OR " + DatasetState.MAINTENANCE.toString() + ")"));
        expected.add(new SimpleField<String>(EasyDatasetSB.DEPOSITOR_ID_FIELD, normalUser.getId()));

        // do test
        EasyMock.replay(datasetSearch);
        service.searchMyDataset(request, normalUser);
        EasyMock.verify(datasetSearch);

        // test results
        compareFieldQueries(expected, request.getFilterQueries());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void searchMyRequestsTest() throws ServiceException, SearchEngineException, SearchBeanFactoryException {
        SearchRequest request = new SimpleSearchRequest("test");
        FieldSet expected = new SimpleFieldSet();

        EasyMock.reset(datasetSearch);
        EasyMock.expect(datasetSearch.search(request)).andReturn(new SimpleSearchResult());
        expected.add(new SimpleField(DatasetSB.DS_STATE_FIELD, "(" + DatasetState.PUBLISHED.toString() + " OR " + DatasetState.MAINTENANCE.toString() + ")"));
        expected.add(new SimpleField(EasyDatasetSB.PERMISSION_STATUS_FIELD, superUser.getId()));

        EasyMock.replay(datasetSearch);
        service.searchMyRequests(request, superUser);
        EasyMock.verify(datasetSearch);

        compareFieldQueries(expected, request.getFilterQueries());
    }

    /**
     * compares if the expected fields are in filterQueries. filterQueries should have the expected fields but may have more filter queries.
     */
    private void compareFieldQueries(FieldSet<?> expected, FieldSet<?> filterQueries) {
        for (Field<?> exField : expected) {
            Field<?> inField = filterQueries.getByFieldName(exField.getName());
            assertTrue("Expected field " + exField.toString() + " was not found.", inField != null);
            assertEquals(exField.getValue().toString(), inField.getValue().toString());
        }
    }

}
