package nl.knaw.dans.easy.tools.task.am.dataset;

import static org.junit.Assert.assertEquals;
import nl.knaw.dans.common.lang.log.RL;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.model.AdministrativeMetadata;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.WorkflowData;
import nl.knaw.dans.easy.domain.workflow.WorkflowStep;
import nl.knaw.dans.easy.tools.JointMap;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RL.class)
public class CompleteRequiredAdministrativeStepsTaskTest {
    private CompleteRequiredAdministrativeStepsTask taskUnderTest;

    private JointMap jointMap;

    @Before
    public void setUp() throws Exception {
        setupJointMapWithDataset();

        PowerMock.mockStatic(RL.class);
        taskUnderTest = new CompleteRequiredAdministrativeStepsTask();
    }

    private void setupJointMapWithDataset() {
        jointMap = new JointMap();
        Dataset dataset = new DatasetImpl("dummy-dataset:1", MetadataFormat.ARCHAEOLOGY);
        jointMap.setDataset(dataset);
    }

    @Test
    public void testRequiredAdministrativeStepsAreComplete() throws Exception {
        taskUnderTest.run(jointMap);

        assertEquals(true, getAdministrativeSteps().areRequiredStepsCompleted());
    }

    private WorkflowStep getAdministrativeSteps() {
        Dataset dataset = jointMap.getDataset();
        AdministrativeMetadata adm = dataset.getAdministrativeMetadata();
        WorkflowData wfd = adm.getWorkflowData();
        return wfd.getWorkflow();
    }
}
