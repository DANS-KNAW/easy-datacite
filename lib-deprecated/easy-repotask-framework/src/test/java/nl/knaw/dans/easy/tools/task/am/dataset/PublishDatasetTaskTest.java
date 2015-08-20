package nl.knaw.dans.easy.tools.task.am.dataset;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.anyBoolean;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verify;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.log.Event;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.userrepo.EasyUserRepo;
import nl.knaw.dans.easy.domain.model.AdministrativeMetadata;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.WorkflowData;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.domain.workflow.WorkflowStep;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.tools.JointMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RL.class, Data.class, Services.class})
public class PublishDatasetTaskTest {

    private static String ARCHIVIST_ID = "dummy-archivist";
    private PublishDatasetTask taskUnderTest;

    private JointMap jointMap;

    private EasyUserRepo mockedUserRepo;
    private DatasetService mockedDatasetService;

    @Before
    public void setUp() throws Exception {
        jointMap = new JointMap();

        mockUserRepo();

        mockStatic(RL.class);

        taskUnderTest = new PublishDatasetTask();
        taskUnderTest.setArchivistId(ARCHIVIST_ID);
    }

    private void mockUserRepo() throws Exception {
        mockStatic(Data.class);
        mockedUserRepo = createMock(EasyUserRepo.class);
        expect(Data.getUserRepo()).andReturn(mockedUserRepo).once();
        expect(mockedUserRepo.findById(ARCHIVIST_ID)).andReturn(new EasyUserImpl(ARCHIVIST_ID)).once();
    }

    @Test
    public void publishSubmittedDataset() throws Exception {
        RL.info(isA(Event.class));
        PowerMock.expectLastCall().times(3);

        mockDatasetService();

        runTaskWith(createDataset(DatasetState.SUBMITTED, true));

        verify(mockedDatasetService);
        verify(RL.class);
    }

    private void mockDatasetService() throws Exception {
        mockStatic(Services.class);
        mockedDatasetService = createMock(DatasetService.class);
        expect(Services.getDatasetService()).andReturn(mockedDatasetService).once();
        mockedDatasetService.publishDataset(isA(EasyUser.class), isA(Dataset.class), anyBoolean(), anyBoolean());
        PowerMock.expectLastCall().once();
    }

    @Test
    public void dontPublishWhenAdminstrationIsNotCompleted() throws Exception {
        mockInfoLoggers(2);
        mockWarnLoggers(1);

        runTaskWith(createDataset(DatasetState.SUBMITTED, false));

        verify(RL.class);
    }

    private void mockInfoLoggers(int infoCounter) {
        if (infoCounter > 0) {
            RL.info(isA(Event.class));
            PowerMock.expectLastCall().times(infoCounter);
        }
    }

    private void mockWarnLoggers(int warnCounter) {
        if (warnCounter > 0) {
            RL.warn(isA(Event.class));
            PowerMock.expectLastCall().times(warnCounter);
        }
    }

    @Test
    public void dontPublishAllreadyPublishedDataset() throws Exception {
        mockInfoLoggers(1);
        mockWarnLoggers(1);

        runTaskWith(createDataset(DatasetState.PUBLISHED, true));

        verify(RL.class);
    }

    @Test
    public void publishMaintenanceDataset() throws Exception {
        mockInfoLoggers(3);
        mockDatasetService();

        taskUnderTest.setPublishDatasetInMaintenance(true);
        runTaskWith(createDataset(DatasetState.MAINTENANCE, true));

        verify(mockedDatasetService);
        verify(RL.class);
    }

    @Test
    public void defaultPublishMaintenanceDataset() throws Exception {
        mockInfoLoggers(1);
        mockWarnLoggers(1);

        taskUnderTest.setPublishDatasetInMaintenance(false);
        runTaskWith(createDataset(DatasetState.MAINTENANCE, true));

        verify(RL.class);
    }

    private Dataset createDataset(DatasetState state, boolean administrationComplete) {
        Dataset mockedDataset = createMock(Dataset.class);
        DmoStoreId dmoStoreId = new DmoStoreId("dummy-dataset:1");

        expect(mockedDataset.getDmoStoreId()).andReturn(dmoStoreId).anyTimes();
        expect(mockedDataset.getAdministrativeState()).andReturn(state).anyTimes();

        mockAdminstrativeSteps(administrationComplete, mockedDataset);

        return mockedDataset;
    }

    private void mockAdminstrativeSteps(boolean administrationComplete, Dataset mockedDataset) {
        AdministrativeMetadata mockedAdminMetadata = createMock(AdministrativeMetadata.class);
        WorkflowData mockedWorkflowData = createMock(WorkflowData.class);
        WorkflowStep mockedWorkflow = createMock(WorkflowStep.class);

        expect(mockedDataset.getAdministrativeMetadata()).andReturn(mockedAdminMetadata).anyTimes();
        expect(mockedAdminMetadata.getWorkflowData()).andReturn(mockedWorkflowData).anyTimes();
        expect(mockedWorkflowData.getWorkflow()).andReturn(mockedWorkflow).anyTimes();
        expect(mockedWorkflow.areRequiredStepsCompleted()).andReturn(administrationComplete);
    }

    private void runTaskWith(Dataset dataset) throws Exception {
        replayAll();
        jointMap.setDataset(dataset);
        taskUnderTest.run(jointMap);
    }
}
