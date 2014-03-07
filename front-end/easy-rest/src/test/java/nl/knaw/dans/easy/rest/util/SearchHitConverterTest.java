package nl.knaw.dans.easy.rest.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.dataset.DatasetSB;
import nl.knaw.dans.common.lang.search.simple.SimpleSearchHit;
import nl.knaw.dans.easy.data.search.EasyDatasetSB;

import org.junit.Before;
import org.junit.Test;

public class SearchHitConverterTest
{

    private List<SimpleSearchHit<?>> hits;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp()
    {
        hits = new ArrayList<SimpleSearchHit<?>>();
        EasyDatasetSB hit = setUpHit();
        EasyDatasetSB hit2 = setUpHit();
        EasyDatasetSB hit3 = setUpNullHit();
        SimpleSearchHit<DatasetSB> ssh = mock(SimpleSearchHit.class);
        SimpleSearchHit<DatasetSB> ssh2 = mock(SimpleSearchHit.class);
        SimpleSearchHit<DatasetSB> ssh3 = mock(SimpleSearchHit.class);
        when(ssh.getData()).thenReturn(hit);
        when(ssh2.getData()).thenReturn(hit2);
        when(ssh3.getData()).thenReturn(hit3);
        hits.add(ssh);
        hits.add(ssh2);
        hits.add(ssh3);
    }

    private EasyDatasetSB setUpHit()
    {
        EasyDatasetSB hit = mock(EasyDatasetSB.class);
        when(hit.getDcTitleSortable()).thenReturn("title");
        when(hit.getStoreId()).thenReturn("easy-dataset:1");
        when(hit.getDcCreatorSortable()).thenReturn("creator");
        when(hit.getDateCreatedFormatted()).thenReturn("13-13-13");
        ArrayList<String> description = new ArrayList<String>();
        description.add("description");
        when(hit.getDcDescription()).thenReturn(description);
        ArrayList<String> identifier = new ArrayList<String>();
        identifier.add("identifier");
        when(hit.getDcIdentifier()).thenReturn(identifier);
        ArrayList<String> coverage = new ArrayList<String>();
        coverage.add("coverage");
        when(hit.getDcCoverage()).thenReturn(coverage);
        when(hit.getAccessCategory()).thenReturn(AccessCategory.OPEN_ACCESS);
        return hit;
    }

    private EasyDatasetSB setUpNullHit()
    {
        EasyDatasetSB hit = mock(EasyDatasetSB.class);
        when(hit.getDcTitleSortable()).thenReturn("title");
        when(hit.getStoreId()).thenReturn("easy-dataset:1");
        when(hit.getDcCreatorSortable()).thenReturn("creator");
        when(hit.getDateCreatedFormatted()).thenReturn("13-13-13");
        when(hit.getDcDescription()).thenReturn(null);
        when(hit.getDcIdentifier()).thenReturn(null);
        when(hit.getDcCoverage()).thenReturn(null);
        when(hit.getAccessCategory()).thenReturn(AccessCategory.OPEN_ACCESS);
        return hit;
    }

    @Test(expected = AssertionError.class)
    public void notInstantiable()
    {
        new SearchHitConverter();
    }

    @Test
    public void emptyConversion()
    {
        String xml = SearchHitConverter.convert(new ArrayList<SimpleSearchHit<?>>());
        String expectedXml = "<hits></hits>";
        assertEquals(expectedXml, xml);
    }

    @Test
    public void normalConversion()
    {
        String xml = SearchHitConverter.convert(hits);
        String normalHit = "<hit>" + "<title>title</title>" + "<storeId>easy-dataset:1</storeId>" + "<creator>creator</creator>"
                + "<dateCreated>13-13-13</dateCreated>" + "<description>description</description>" + "<identifier>identifier</identifier>"
                + "<coverage>coverage</coverage>" + "<accessCategory>OPEN_ACCESS</accessCategory>" + "</hit>";
        String nullHit = "<hit>" + "<title>title</title>" + "<storeId>easy-dataset:1</storeId>" + "<creator>creator</creator>"
                + "<dateCreated>13-13-13</dateCreated>" + "<accessCategory>OPEN_ACCESS</accessCategory>" + "</hit>";
        String expectedXml = "<hits>" + normalHit + normalHit + nullHit + "</hits>";
        assertEquals(expectedXml, xml);
    }

}
