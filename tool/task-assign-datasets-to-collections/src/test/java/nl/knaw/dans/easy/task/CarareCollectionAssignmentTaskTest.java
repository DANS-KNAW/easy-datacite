package nl.knaw.dans.easy.task;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;
import nl.knaw.dans.easy.domain.model.AdministrativeMetadata;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.DatasetRelations;
import nl.knaw.dans.easy.task.CarareCollectionAssignmentTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EmdCoverage;
import nl.knaw.dans.pf.language.emd.EmdOther;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.pf.language.emd.types.Spatial;
import nl.knaw.dans.pf.language.emd.types.Spatial.Box;
import nl.knaw.dans.pf.language.emd.types.Spatial.Point;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@Ignore
@RunWith(PowerMockRunner.class)
@PrepareForTest(CarareCollectionAssignmentTask.class)
public class CarareCollectionAssignmentTaskTest {
    private Point completePointMock;
    private Point incompletePointMock;
    private Spatial spatialWithCompletePointMock;
    private Spatial spatialWithIncompletePointMock;
    private Spatial spatialWithNoPointOrBoxMock;
    private Box completeBoxMock;
    private Spatial spatialWithCompleteBoxMock;

    private Dataset datasetMock;
    private EasyMetadata emdMock;
    private DublinCoreMetadata dcMetadataMock;
    private EmdOther emdOtherMock;
    private ApplicationSpecific appSpecificMock;
    private EmdCoverage emdCoverageMock;
    private DatasetRelations relationsMock;
    AdministrativeMetadata administrativeMetadataMock;

    private CarareCollectionAssignmentTask task;
    private JointMap jointMap;

    @Before
    public void setUp() {
        setUpSpatialMocks();

        setUpDatasetMocks();

        jointMap = new JointMap();
        jointMap.setDataset(datasetMock);

        task = new CarareCollectionAssignmentTask();
    }

    private void setUpSpatialMocks() {
        completePointMock = PowerMock.createMock(Point.class);
        expect(completePointMock.isComplete()).andReturn(true).anyTimes();

        incompletePointMock = PowerMock.createMock(Point.class);
        expect(incompletePointMock.isComplete()).andReturn(false).anyTimes();

        spatialWithCompletePointMock = PowerMock.createMock(Spatial.class);
        expect(spatialWithCompletePointMock.getPoint()).andStubReturn(completePointMock);
        expect(spatialWithCompletePointMock.getBox()).andStubReturn(null);

        spatialWithIncompletePointMock = PowerMock.createMock(Spatial.class);
        expect(spatialWithIncompletePointMock.getPoint()).andStubReturn(incompletePointMock);
        expect(spatialWithIncompletePointMock.getBox()).andStubReturn(null);

        spatialWithNoPointOrBoxMock = PowerMock.createMock(Spatial.class);
        expect(spatialWithNoPointOrBoxMock.getPoint()).andStubReturn(null);
        expect(spatialWithNoPointOrBoxMock.getBox()).andStubReturn(null);

        completeBoxMock = PowerMock.createMock(Box.class);
        expect(completeBoxMock.isComplete()).andReturn(true).anyTimes();

        spatialWithCompleteBoxMock = PowerMock.createMock(Spatial.class);
        expect(spatialWithCompleteBoxMock.getBox()).andStubReturn(completeBoxMock);
        expect(spatialWithCompleteBoxMock.getPoint()).andStubReturn(null);
    }

    private void setUpDatasetMocks() {
        datasetMock = PowerMock.createMock(Dataset.class);
        expect(datasetMock.getStoreId()).andStubReturn("easy-dataset:1");

        emdMock = PowerMock.createMock(EasyMetadata.class);
        expect(datasetMock.getEasyMetadata()).andStubReturn(emdMock);

        dcMetadataMock = PowerMock.createMock(DublinCoreMetadata.class);
        expect(emdMock.getDublinCoreMetadata()).andStubReturn(dcMetadataMock);

        emdOtherMock = PowerMock.createMock(EmdOther.class);
        expect(emdMock.getEmdOther()).andStubReturn(emdOtherMock);

        appSpecificMock = PowerMock.createMock(ApplicationSpecific.class);
        expect(emdOtherMock.getEasApplicationSpecific()).andStubReturn(appSpecificMock);

        emdCoverageMock = PowerMock.createMock(EmdCoverage.class);
        expect(emdMock.getEmdCoverage()).andStubReturn(emdCoverageMock);

        relationsMock = PowerMock.createMock(DatasetRelations.class);
        expect(datasetMock.getRelations()).andStubReturn(relationsMock);

        administrativeMetadataMock = PowerMock.createMock(AdministrativeMetadata.class);
        expect(datasetMock.getAdministrativeMetadata()).andStubReturn(administrativeMetadataMock);
        // not DatasetState.PUBLISHED, because if it is published some more mocks needed!
        expect(administrativeMetadataMock.getAdministrativeState()).andStubReturn(DatasetState.SUBMITTED);
    }

    @Test
    public void datasetIsAssignedWhenItHasAPoint() throws Exception {
        expect(relationsMock.getCollectionMemberships(isA(DmoNamespace.class))).andReturn((Set<DmoStoreId>) Collections.EMPTY_SET).anyTimes();

        expect(datasetMock.getAccessCategory()).andReturn(AccessCategory.OPEN_ACCESS_FOR_REGISTERED_USERS);
        expect(dcMetadataMock.getType()).andReturn(Arrays.asList("Text"));
        expect(appSpecificMock.getMetadataFormat()).andReturn(MetadataFormat.ARCHAEOLOGY);
        expect(emdCoverageMock.getEasSpatial()).andReturn(
                Arrays.asList(spatialWithNoPointOrBoxMock, spatialWithIncompletePointMock, spatialWithCompletePointMock, // this
                                                                                                                         // allows
                                                                                                                         // it
                        spatialWithIncompletePointMock)).anyTimes();

        relationsMock.addCollectionMembership(CarareCollectionAssignmentTask.COLLECTION_STORE_ID_FOR_CARARE);
        PowerMock.expectLastCall();

        PowerMock.replayAll();

        task.run(jointMap);

        assertThatDatasetIsAssigendToCollection(jointMap.getDataset());
    }

    @Test
    public void datasetIsAssignedWhenItHasABox() throws Exception {
        expect(relationsMock.getCollectionMemberships(isA(DmoNamespace.class))).andReturn((Set<DmoStoreId>) Collections.EMPTY_SET).anyTimes();

        expect(datasetMock.getAccessCategory()).andReturn(AccessCategory.OPEN_ACCESS_FOR_REGISTERED_USERS);
        expect(dcMetadataMock.getType()).andReturn(Arrays.asList("Text"));
        expect(appSpecificMock.getMetadataFormat()).andReturn(MetadataFormat.ARCHAEOLOGY);
        expect(emdCoverageMock.getEasSpatial()).andReturn(Arrays.asList(spatialWithNoPointOrBoxMock, spatialWithCompleteBoxMock)).anyTimes(); // this
                                                                                                                                              // allows
                                                                                                                                              // it!

        relationsMock.addCollectionMembership(CarareCollectionAssignmentTask.COLLECTION_STORE_ID_FOR_CARARE);
        PowerMock.expectLastCall();

        PowerMock.replayAll();

        task.run(jointMap);

        assertThatDatasetIsAssigendToCollection(jointMap.getDataset());
    }

    @Test
    public void datasetIsNotAssignedWhenItHasNonText() throws Exception {
        expect(relationsMock.getCollectionMemberships(isA(DmoNamespace.class))).andReturn((Set<DmoStoreId>) Collections.EMPTY_SET).anyTimes();

        expect(datasetMock.getAccessCategory()).andReturn(AccessCategory.OPEN_ACCESS_FOR_REGISTERED_USERS);
        expect(appSpecificMock.getMetadataFormat()).andReturn(MetadataFormat.ARCHAEOLOGY);
        expect(emdCoverageMock.getEasSpatial()).andReturn(
                Arrays.asList(spatialWithNoPointOrBoxMock, spatialWithIncompletePointMock, spatialWithCompletePointMock, // this
                                                                                                                         // allows
                                                                                                                         // it
                        spatialWithIncompletePointMock)).anyTimes();

        // and it also has non text
        expect(dcMetadataMock.getType()).andReturn(Arrays.asList("Dataset", "Text", "Image"));

        PowerMock.replayAll();

        task.run(jointMap);

        assertThatDatasetIsNotAssigendToCollection(jointMap.getDataset());
    }

    @Test
    public void datasetIsNotAssignedWhenAlreadyAssigned() throws Exception {
        expect(datasetMock.getAccessCategory()).andReturn(AccessCategory.OPEN_ACCESS_FOR_REGISTERED_USERS);
        expect(dcMetadataMock.getType()).andReturn(Arrays.asList("Text"));
        expect(appSpecificMock.getMetadataFormat()).andReturn(MetadataFormat.ARCHAEOLOGY);
        expect(emdCoverageMock.getEasSpatial()).andReturn(Arrays.asList(spatialWithNoPointOrBoxMock, spatialWithCompleteBoxMock)).anyTimes(); // this
                                                                                                                                              // allows
                                                                                                                                              // it!

        // and already assigned
        expect(relationsMock.getCollectionMemberships(isA(DmoNamespace.class))).andReturn(
                new HashSet<DmoStoreId>(Arrays.asList(CarareCollectionAssignmentTask.COLLECTION_STORE_ID_FOR_CARARE))).anyTimes();

        PowerMock.replayAll();

        task.run(jointMap);

        assertThatDatasetIsNotAssigendToCollection(jointMap.getDataset());
    }

    @Test
    public void datasetIsNotAssignedWhenNotOpen() throws Exception {
        expect(relationsMock.getCollectionMemberships(isA(DmoNamespace.class))).andReturn((Set<DmoStoreId>) Collections.EMPTY_SET).anyTimes();

        expect(dcMetadataMock.getType()).andReturn(Arrays.asList("Text"));
        expect(appSpecificMock.getMetadataFormat()).andReturn(MetadataFormat.ARCHAEOLOGY);
        expect(emdCoverageMock.getEasSpatial()).andReturn(
                Arrays.asList(spatialWithNoPointOrBoxMock, spatialWithIncompletePointMock, spatialWithCompletePointMock, // this
                                                                                                                         // allows
                                                                                                                         // it
                        spatialWithIncompletePointMock)).anyTimes();

        // and not an open acces
        expect(datasetMock.getAccessCategory()).andReturn(AccessCategory.REQUEST_PERMISSION);

        PowerMock.replayAll();

        task.run(jointMap);

        assertThatDatasetIsNotAssigendToCollection(jointMap.getDataset());
    }

    @Test
    public void datasetIsNotAssignedWithoutArchaeologyFormat() throws Exception {
        expect(relationsMock.getCollectionMemberships(isA(DmoNamespace.class))).andReturn((Set<DmoStoreId>) Collections.EMPTY_SET).anyTimes();

        expect(dcMetadataMock.getType()).andReturn(Arrays.asList("Text"));
        expect(datasetMock.getAccessCategory()).andReturn(AccessCategory.OPEN_ACCESS_FOR_REGISTERED_USERS);
        expect(emdCoverageMock.getEasSpatial()).andReturn(
                Arrays.asList(spatialWithNoPointOrBoxMock, spatialWithIncompletePointMock, spatialWithCompletePointMock, // this
                                                                                                                         // allows
                                                                                                                         // it
                        spatialWithIncompletePointMock)).anyTimes();

        // and not the right format
        expect(appSpecificMock.getMetadataFormat()).andReturn(MetadataFormat.SOCIOLOGY);

        PowerMock.replayAll();

        task.run(jointMap);

        assertThatDatasetIsNotAssigendToCollection(jointMap.getDataset());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void datasetIsNotAssignedWithNoPointOrBoxSpatial() throws Exception {
        expect(relationsMock.getCollectionMemberships(isA(DmoNamespace.class))).andReturn((Set<DmoStoreId>) Collections.EMPTY_SET).anyTimes();

        expect(datasetMock.getAccessCategory()).andReturn(AccessCategory.OPEN_ACCESS_FOR_REGISTERED_USERS);
        expect(dcMetadataMock.getType()).andReturn(Arrays.asList("Text"));
        expect(appSpecificMock.getMetadataFormat()).andReturn(MetadataFormat.ARCHAEOLOGY);

        // and not the right spatial
        expect(emdCoverageMock.getEasSpatial()).andReturn(Arrays.asList(spatialWithNoPointOrBoxMock)).anyTimes();

        PowerMock.replayAll();

        task.run(jointMap);

        assertThatDatasetIsNotAssigendToCollection(jointMap.getDataset());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void datasetIsNotAssignedWithNoSpatial() throws Exception {
        expect(relationsMock.getCollectionMemberships(isA(DmoNamespace.class))).andReturn((Set<DmoStoreId>) Collections.EMPTY_SET).anyTimes();

        expect(datasetMock.getAccessCategory()).andReturn(AccessCategory.OPEN_ACCESS_FOR_REGISTERED_USERS);
        expect(dcMetadataMock.getType()).andReturn(Arrays.asList("Text"));
        expect(appSpecificMock.getMetadataFormat()).andReturn(MetadataFormat.ARCHAEOLOGY);

        // and not the right spatial
        expect(emdCoverageMock.getEasSpatial()).andReturn((List<Spatial>) Collections.EMPTY_LIST).anyTimes();

        PowerMock.replayAll();

        task.run(jointMap);

        assertThatDatasetIsNotAssigendToCollection(jointMap.getDataset());
    }

    @Test
    public void datasetIsNotAssignedWithIncompletePointSpatial() throws Exception {
        expect(relationsMock.getCollectionMemberships(isA(DmoNamespace.class))).andReturn((Set<DmoStoreId>) Collections.EMPTY_SET).anyTimes();

        expect(datasetMock.getAccessCategory()).andReturn(AccessCategory.OPEN_ACCESS_FOR_REGISTERED_USERS);
        expect(dcMetadataMock.getType()).andReturn(Arrays.asList("Text"));
        expect(appSpecificMock.getMetadataFormat()).andReturn(MetadataFormat.ARCHAEOLOGY);

        // and not the right spatial
        expect(emdCoverageMock.getEasSpatial()).andReturn(Arrays.asList(spatialWithIncompletePointMock)).anyTimes();

        PowerMock.replayAll();

        task.run(jointMap);

        assertThatDatasetIsNotAssigendToCollection(jointMap.getDataset());
    }

    private void assertThatDatasetIsNotAssigendToCollection(Dataset dataset) {
        // Note: no inspection of the dataset needed, because we have mocked the relations
        PowerMock.verify(relationsMock);
    }

    private void assertThatDatasetIsAssigendToCollection(Dataset dataset) {
        // Note: no inspection of the dataset needed, because we have mocked the relations
        PowerMock.verify(relationsMock);
    }

}
