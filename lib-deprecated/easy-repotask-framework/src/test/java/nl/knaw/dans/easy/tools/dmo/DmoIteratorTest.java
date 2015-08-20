package nl.knaw.dans.easy.tools.dmo;

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.fedora.Fedora;
import nl.knaw.dans.common.fedora.ObjectAccessor;
import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.EasyStore;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.tools.Application;
import nl.knaw.dans.easy.tools.task.am.dataset.DatasetDepositorIdFilter;
import nl.knaw.dans.easy.tools.task.am.dataset.DatasetStateFilter;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.pf.language.emd.types.BasicString;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import fedora.server.types.gen.FieldSearchQuery;
import fedora.server.types.gen.FieldSearchResult;
import fedora.server.types.gen.ListSession;
import fedora.server.types.gen.ObjectFields;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Data.class, Application.class})
public class DmoIteratorTest {
    private static DmoNamespace NAMESPACE = new DmoNamespace("dummy-dataset");
    private static String DEPOSITOR_ID_1 = "depo";
    private static String DEPOSITOR_ID_2 = "dino";

    Fedora fedoraMock;
    ObjectAccessor oaMock;
    EasyStore easyStoreMock;

    Dataset dataset1;
    Dataset dataset2;
    Dataset dataset3;
    Dataset dataset4;
    Dataset dataset5;
    Dataset dataset6;

    @Before
    public void setUp() throws Exception {
        // The datasets in our repository
        dataset1 = createDataset(1, MetadataFormat.SOCIOLOGY, DatasetState.PUBLISHED, AccessCategory.OPEN_ACCESS_FOR_REGISTERED_USERS, DEPOSITOR_ID_1);
        dataset2 = createDataset(2, MetadataFormat.SOCIOLOGY, DatasetState.SUBMITTED, AccessCategory.OPEN_ACCESS_FOR_REGISTERED_USERS, DEPOSITOR_ID_1);
        dataset3 = createDataset(3, MetadataFormat.HISTORY, DatasetState.PUBLISHED, AccessCategory.REQUEST_PERMISSION, DEPOSITOR_ID_1);
        dataset4 = createDataset(4, MetadataFormat.HISTORY, DatasetState.SUBMITTED, AccessCategory.OPEN_ACCESS_FOR_REGISTERED_USERS, DEPOSITOR_ID_2);
        dataset5 = createDataset(5, MetadataFormat.ARCHAEOLOGY, DatasetState.PUBLISHED, AccessCategory.REQUEST_PERMISSION, DEPOSITOR_ID_2);
        dataset6 = createDataset(6, MetadataFormat.ARCHAEOLOGY, DatasetState.DRAFT, AccessCategory.GROUP_ACCESS, DEPOSITOR_ID_2);
        // The datasets in our search results
        ObjectFields of1 = createObjectFields(dataset1);
        ObjectFields of2 = createObjectFields(dataset2);
        ObjectFields of3 = createObjectFields(dataset3);
        ObjectFields of4 = createObjectFields(dataset4);
        ObjectFields of5 = createObjectFields(dataset5);
        ObjectFields of6 = createObjectFields(dataset6);

        ListSession listSession = new ListSession();
        listSession.setToken("token");

        mockStatic(Application.class);
        fedoraMock = PowerMock.createMock(Fedora.class);
        expect(Application.getFedora()).andReturn(fedoraMock).anyTimes();

        oaMock = PowerMock.createMock(ObjectAccessor.class);
        expect(fedoraMock.getObjectAccessor()).andReturn(oaMock).anyTimes();

        FieldSearchResult result = new FieldSearchResult(listSession, new ObjectFields[] {of1, of2, of3, of4, of5, of6});
        FieldSearchResult empty = new FieldSearchResult(listSession, new ObjectFields[] {});

        expect(oaMock.findObjects(isA(String[].class), anyInt(), isA(FieldSearchQuery.class))).andReturn(result).anyTimes();
        expect(oaMock.resumeFindObjects(isA(String.class))).andReturn(empty).anyTimes();

        mockStatic(Data.class);
        easyStoreMock = PowerMock.createMock(EasyStore.class);
        expect(Data.getEasyStore()).andReturn(easyStoreMock).anyTimes();
        expect(easyStoreMock.retrieve(new DmoStoreId(of1.getPid()))).andReturn(dataset1).anyTimes();
        expect(easyStoreMock.retrieve(new DmoStoreId(of2.getPid()))).andReturn(dataset2).anyTimes();
        expect(easyStoreMock.retrieve(new DmoStoreId(of3.getPid()))).andReturn(dataset3).anyTimes();
        expect(easyStoreMock.retrieve(new DmoStoreId(of4.getPid()))).andReturn(dataset4).anyTimes();
        expect(easyStoreMock.retrieve(new DmoStoreId(of5.getPid()))).andReturn(dataset5).anyTimes();
        expect(easyStoreMock.retrieve(new DmoStoreId(of6.getPid()))).andReturn(dataset6).anyTimes();
    }

    @Test
    public void testDefaultIterator() throws Exception {
        replayAll();
        List<Dataset> datasets = new ArrayList<Dataset>(6);
        DmoIterator<Dataset> diter = new DmoIterator<Dataset>(NAMESPACE);
        while (diter.hasNext()) {
            datasets.add(diter.next());
        }

        assertEquals(6, datasets.size());
        assertEquals(true, datasets.contains(dataset1));
        assertEquals(true, datasets.contains(dataset2));
        assertEquals(true, datasets.contains(dataset3));
        assertEquals(true, datasets.contains(dataset4));
        assertEquals(true, datasets.contains(dataset5));
        assertEquals(true, datasets.contains(dataset6));
    }

    @Test
    public void testIteratorWithDatasetStateFilter() throws Exception {
        replayAll();
        List<Dataset> datasets = new ArrayList<Dataset>(3);
        String filterPublished = DatasetState.PUBLISHED.toString();

        DmoIterator<Dataset> diter = new DmoIterator<Dataset>(NAMESPACE, new DatasetStateFilter(filterPublished));
        while (diter.hasNext()) {
            datasets.add(diter.next());
        }

        assertEquals(3, datasets.size());
        assertEquals(true, datasets.contains(dataset1));
        assertEquals(false, datasets.contains(dataset2));
        assertEquals(true, datasets.contains(dataset3));
        assertEquals(false, datasets.contains(dataset4));
        assertEquals(true, datasets.contains(dataset5));
        assertEquals(false, datasets.contains(dataset6));
    }

    @Test
    public void testIteratorWith2DatasetStateFilters() throws Exception {
        replayAll();
        List<Dataset> datasets = new ArrayList<Dataset>(5);
        String filterPublished = DatasetState.PUBLISHED.toString();
        String filterSubmitted = DatasetState.SUBMITTED.toString();

        DmoIterator<Dataset> diter = new DmoIterator<Dataset>(NAMESPACE, new DatasetStateFilter(filterPublished, filterSubmitted));
        while (diter.hasNext()) {
            datasets.add(diter.next());
        }

        assertEquals(5, datasets.size());
        assertEquals(true, datasets.contains(dataset1));
        assertEquals(true, datasets.contains(dataset2));
        assertEquals(true, datasets.contains(dataset3));
        assertEquals(true, datasets.contains(dataset4));
        assertEquals(true, datasets.contains(dataset5));
        assertEquals(false, datasets.contains(dataset6));
    }

    @Test
    public void testIteratorWithDepositorIdFilter() throws Exception {
        replayAll();
        List<Dataset> datasets = new ArrayList<Dataset>(3);
        DmoIterator<Dataset> diter = new DmoIterator<Dataset>(NAMESPACE, new DatasetDepositorIdFilter(DEPOSITOR_ID_1));
        while (diter.hasNext()) {
            datasets.add(diter.next());
        }

        assertEquals(3, datasets.size());
        assertEquals(true, datasets.contains(dataset1));
        assertEquals(true, datasets.contains(dataset2));
        assertEquals(true, datasets.contains(dataset3));
        assertEquals(false, datasets.contains(dataset4));
        assertEquals(false, datasets.contains(dataset5));
        assertEquals(false, datasets.contains(dataset6));
    }

    @Test
    public void testIteratorWith2DepositorIds() throws Exception {
        replayAll();
        List<Dataset> datasets = new ArrayList<Dataset>(6);
        DmoIterator<Dataset> diter = new DmoIterator<Dataset>(NAMESPACE, new DatasetDepositorIdFilter(DEPOSITOR_ID_1, DEPOSITOR_ID_2));
        while (diter.hasNext()) {
            datasets.add(diter.next());
        }

        assertEquals(6, datasets.size());
        assertEquals(true, datasets.contains(dataset1));
        assertEquals(true, datasets.contains(dataset2));
        assertEquals(true, datasets.contains(dataset3));
        assertEquals(true, datasets.contains(dataset4));
        assertEquals(true, datasets.contains(dataset5));
        assertEquals(true, datasets.contains(dataset6));
    }

    @Test
    public void testIteratorWithUnknownDepositorId() throws Exception {
        replayAll();
        List<Dataset> datasets = new ArrayList<Dataset>(0);
        DmoIterator<Dataset> diter = new DmoIterator<Dataset>(NAMESPACE, new DatasetDepositorIdFilter("unknown"));
        while (diter.hasNext()) {
            datasets.add(diter.next());
        }

        assertEquals(0, datasets.size());
        assertEquals(false, datasets.contains(dataset1));
        assertEquals(false, datasets.contains(dataset2));
        assertEquals(false, datasets.contains(dataset3));
        assertEquals(false, datasets.contains(dataset4));
        assertEquals(false, datasets.contains(dataset5));
        assertEquals(false, datasets.contains(dataset6));
    }

    @Test
    public void testIteratorWith2Filters() throws Exception {
        replayAll();
        List<Dataset> datasets = new ArrayList<Dataset>(1);
        DmoIterator<Dataset> diter = new DmoIterator<Dataset>(NAMESPACE, new DatasetDepositorIdFilter(DEPOSITOR_ID_2), new DatasetStateFilter(
                DatasetState.PUBLISHED.toString()));
        while (diter.hasNext()) {
            datasets.add(diter.next());
        }

        assertEquals(1, datasets.size());
        assertEquals(false, datasets.contains(dataset1));
        assertEquals(false, datasets.contains(dataset2));
        assertEquals(false, datasets.contains(dataset3));
        assertEquals(false, datasets.contains(dataset4));
        assertEquals(true, datasets.contains(dataset5));
        assertEquals(false, datasets.contains(dataset6));
    }

    @Test
    public void testIteratorWith2Filters2Depositors() throws Exception {
        replayAll();
        List<Dataset> datasets = new ArrayList<Dataset>(1);
        DmoIterator<Dataset> diter = new DmoIterator<Dataset>(NAMESPACE, new DatasetDepositorIdFilter(DEPOSITOR_ID_1, DEPOSITOR_ID_2), new DatasetStateFilter(
                DatasetState.SUBMITTED.toString()));
        while (diter.hasNext()) {
            datasets.add(diter.next());
        }

        assertEquals(2, datasets.size());
        assertEquals(false, datasets.contains(dataset1));
        assertEquals(true, datasets.contains(dataset2));
        assertEquals(false, datasets.contains(dataset3));
        assertEquals(true, datasets.contains(dataset4));
        assertEquals(false, datasets.contains(dataset5));
        assertEquals(false, datasets.contains(dataset6));
    }

    private Dataset createDataset(int pid, MetadataFormat format, DatasetState state, AccessCategory rights, String depositorId) {
        Dataset dataset = new DatasetImpl(NAMESPACE + ":" + pid, format);
        dataset.getAdministrativeMetadata().setAdministrativeState(state);
        BasicString dcRights = new BasicString(rights.toString());
        dcRights.setSchemeId("common.dcterms.accessrights");
        dataset.getEasyMetadata().getEmdRights().setAccessCategory(rights, "common.dcterms.accessrights");
        dataset.getEasyMetadata().getEmdTitle().getDcTitle().add(new BasicString("dummy dataset " + pid));
        dataset.setOwnerId(depositorId);

        return dataset;
    }

    private ObjectFields createObjectFields(Dataset dataset) {
        ObjectFields of = new ObjectFields();
        of.setPid(dataset.getStoreId());
        of.setTitle(new String[] {dataset.getEasyMetadata().getEmdTitle().getDcTitle().get(0).toString()});
        of.setState(dataset.getAdministrativeState().toString());
        of.setOwnerId(dataset.getOwnerId());

        return of;
    }
}
