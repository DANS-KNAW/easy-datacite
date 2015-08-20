package nl.knaw.dans.easy.task;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.domain.model.AdministrativeMetadata;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.DatasetRelations;
import nl.knaw.dans.easy.task.WO2CollectionAssignmentTask;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EmdAudience;
import nl.knaw.dans.pf.language.emd.EmdOther;
import nl.knaw.dans.pf.language.emd.EmdSubject;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.pf.language.emd.types.BasicString;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@Ignore
@RunWith(PowerMockRunner.class)
@PrepareForTest(WO2CollectionAssignmentTask.class)
public class WO2CollectionAssignmentTaskTest {
    private EmdSubject subjectMock;
    private EmdAudience emdAudienceMock;

    private Dataset datasetMock;
    private EasyMetadata emdMock;
    private EmdOther emdOtherMock;
    private ApplicationSpecific appSpecificMock;
    private DatasetRelations relationsMock;
    AdministrativeMetadata administrativeMetadataMock;

    private WO2CollectionAssignmentTask task;
    private JointMap jointMap;

    @Before
    public void setUp() {
        setUpDatasetMocks();

        jointMap = new JointMap();
        jointMap.setDataset(datasetMock);

        task = new WO2CollectionAssignmentTask();
    }

    private void setUpDatasetMocks() {
        datasetMock = PowerMock.createMock(Dataset.class);
        expect(datasetMock.getStoreId()).andStubReturn("easy-dataset:1");

        emdMock = PowerMock.createMock(EasyMetadata.class);
        expect(datasetMock.getEasyMetadata()).andStubReturn(emdMock);

        emdOtherMock = PowerMock.createMock(EmdOther.class);
        expect(emdMock.getEmdOther()).andStubReturn(emdOtherMock);

        appSpecificMock = PowerMock.createMock(ApplicationSpecific.class);
        expect(emdOtherMock.getEasApplicationSpecific()).andStubReturn(appSpecificMock);

        subjectMock = PowerMock.createMock(EmdSubject.class);
        expect(emdMock.getEmdSubject()).andStubReturn(subjectMock);

        emdAudienceMock = PowerMock.createMock(EmdAudience.class);
        expect(emdMock.getEmdAudience()).andStubReturn(emdAudienceMock);

        relationsMock = PowerMock.createMock(DatasetRelations.class);
        expect(datasetMock.getRelations()).andStubReturn(relationsMock);

        administrativeMetadataMock = PowerMock.createMock(AdministrativeMetadata.class);
        expect(datasetMock.getAdministrativeMetadata()).andStubReturn(administrativeMetadataMock);
        // not DatasetState.PUBLISHED, because if it is published some more mocks needed!
        expect(administrativeMetadataMock.getAdministrativeState()).andStubReturn(DatasetState.SUBMITTED);
    }

    @Test
    public void datasetIsAssignedWhenItShould() throws Exception {
        expect(relationsMock.getCollectionMemberships(isA(DmoNamespace.class))).andReturn((Set<DmoStoreId>) Collections.EMPTY_SET).anyTimes();

        expect(subjectMock.getDcSubject()).andReturn(Arrays.asList(new BasicString("WO2"))).anyTimes();
        expect(appSpecificMock.getMetadataFormat()).andReturn(MetadataFormat.SOCIOLOGY); // not
                                                                                         // ARCHAEOLOGY
        expect(emdAudienceMock.getDisciplines()).andReturn(Arrays.asList(new BasicString("easy-discipline:1"))).anyTimes(); // not
                                                                                                                            // "easy-discipline:2"

        relationsMock.addCollectionMembership(WO2CollectionAssignmentTask.COLLECTION_STORE_ID_FOR_WO2);
        PowerMock.expectLastCall();

        PowerMock.replayAll();

        task.run(jointMap);

        assertThatDatasetIsAssigendToCollection(jointMap.getDataset());
    }

    @Test
    public void datasetIsNotAssignedWhenItHasWrongTitle() throws Exception {
        expect(relationsMock.getCollectionMemberships(isA(DmoNamespace.class))).andReturn((Set<DmoStoreId>) Collections.EMPTY_SET).anyTimes();

        expect(appSpecificMock.getMetadataFormat()).andReturn(MetadataFormat.SOCIOLOGY); // not
                                                                                         // ARCHAEOLOGY
        expect(emdAudienceMock.getDisciplines()).andReturn(Arrays.asList(new BasicString("easy-discipline:1"))).anyTimes(); // not
                                                                                                                            // "easy-discipline:2"

        // wrong subject
        expect(subjectMock.getDcSubject()).andReturn(Arrays.asList(new BasicString("a subject"))).anyTimes();

        PowerMock.replayAll();

        task.run(jointMap);

        assertThatDatasetIsNotAssigendToCollection(jointMap.getDataset());
    }

    @Test
    public void datasetIsNotAssignedWithArchaeologyFormat() throws Exception {
        expect(relationsMock.getCollectionMemberships(isA(DmoNamespace.class))).andReturn((Set<DmoStoreId>) Collections.EMPTY_SET).anyTimes();

        expect(subjectMock.getDcSubject()).andReturn(Arrays.asList(new BasicString("WO2"))).anyTimes();
        expect(emdAudienceMock.getDisciplines()).andReturn(Arrays.asList(new BasicString("easy-discipline:1"))).anyTimes(); // not
                                                                                                                            // "easy-discipline:2"

        // wrong format
        expect(appSpecificMock.getMetadataFormat()).andReturn(MetadataFormat.ARCHAEOLOGY);

        PowerMock.replayAll();

        task.run(jointMap);

        assertThatDatasetIsNotAssigendToCollection(jointMap.getDataset());
    }

    @Test
    public void datasetIsNotAssignedWithArchaeologyDiscipline() throws Exception {
        expect(relationsMock.getCollectionMemberships(isA(DmoNamespace.class))).andReturn((Set<DmoStoreId>) Collections.EMPTY_SET).anyTimes();

        expect(subjectMock.getDcSubject()).andReturn(Arrays.asList(new BasicString("WO2"))).anyTimes();
        expect(appSpecificMock.getMetadataFormat()).andReturn(MetadataFormat.SOCIOLOGY); // not
                                                                                         // ARCHAEOLOGY

        // wrong discipline
        expect(emdAudienceMock.getDisciplines()).andReturn(Arrays.asList(new BasicString("easy-discipline:2"))).anyTimes();

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
