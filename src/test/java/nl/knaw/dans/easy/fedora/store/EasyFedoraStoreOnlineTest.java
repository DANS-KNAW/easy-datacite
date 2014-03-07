package nl.knaw.dans.easy.fedora.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.dataset.DatasetSB;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.dataset.DatasetsIndex;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.common.lang.search.SearchResult;
import nl.knaw.dans.common.lang.search.simple.SimpleField;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchRequest;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.search.EasyDatasetSB;
import nl.knaw.dans.easy.data.store.EasyStore;
import nl.knaw.dans.easy.domain.model.AdministrativeMetadata;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.pf.language.emd.EasyMetadata;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EasyFedoraStoreOnlineTest extends AbstractOnlineTest
{

    private static final Logger logger = LoggerFactory.getLogger(EasyFedoraStoreOnlineTest.class);

    private static EasyStore store;

    private boolean verbose = Tester.isVerbose();

    @BeforeClass
    public static void beforeClass() throws RepositoryException, MalformedURLException
    {
        setUpData();
        store = Data.getEasyStore();

    }

    @Test
    public void ingestRetrievePurge() throws Exception
    {
        Dataset dataset = getDummyDataset(store.nextSid(Dataset.NAMESPACE));

        String datasetId = store.ingest(dataset, "ingest dataset for test");
        assertTrue(datasetId.startsWith("easy-dataset"));

        byte[] objectXML = store.getObjectXML(new DmoStoreId(datasetId));
        if (verbose)
            logger.debug("\n" + new String(objectXML) + "\n");

        // Retrieved dataset
        Dataset dataset2 = (Dataset) store.retrieve(new DmoStoreId(datasetId));
        assertNotNull(dataset2.getTimestamp());

        AdministrativeMetadata amd = dataset2.getAdministrativeMetadata();
        assertNotNull(amd.getTimestamp());

        logger.debug("\n" + amd.asXMLString(4) + "\n");

        EasyMetadata emd = dataset2.getEasyMetadata();
        assertNotNull(emd.getTimestamp());

        // check if the new dataset was inserted into the search index
        SimpleSearchRequest searchRequest = new SimpleSearchRequest();
        searchRequest.addFilterQuery(new SimpleField<String>(EasyDatasetSB.SID_FIELD, dataset.getStoreId()));
        searchRequest.setIndex(new DatasetsIndex());
        searchRequest.addFilterBean(DatasetSB.class);
        SearchResult<? extends Object> result = getSearchEngine().searchBeans(searchRequest);
        assertEquals(1, result.getTotalHits());

        purge(dataset);

        // check that the dataset has been removed from the search index
        result = getSearchEngine().searchBeans(searchRequest);
        assertEquals(0, result.getTotalHits());
    }

    private void purge(Dataset dataset) throws RepositoryException
    {
        dataset.getAdministrativeMetadata().setAdministrativeState(DatasetState.DELETED);
        dataset.registerDeleted();
        store.purge(dataset, false, "cleaning up");
    }

    @Ignore("Due to latency of tripple store a newly ingested object cannot be found immediately there after")
    @Test
    public void findJumpoffDmo() throws Exception
    {
        String datasetId = "easy-dataset:181"; // store.ingest(dataset, "ingest dataset for test");

        JumpoffDmo foundJod = store.findJumpoffDmoFor(new DmoStoreId(datasetId));
        assertNotNull(foundJod);
        assertEquals("easy-jumpoff:31", foundJod.getStoreId());
        assertEquals(datasetId, foundJod.getObjectId());
    }

    @Test
    public void createDownloadHistoryQuery()
    {
        String query = EasyFedoraStore.createDownloadHistoryQuery("info:fedora/easy-dataset:457", "year=2010 week=11");
        String expected = "select ?s from <#ri> where {?s <http://dans.knaw.nl/ontologies/relations#hasDownloadHistoryOf> <info:fedora/easy-dataset:457> . ?s <http://dans.knaw.nl/ontologies/relations#hasPeriod> \"year=2010 week=11\"^^<http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral> . }";
        assertEquals(expected, query);
    }

}
