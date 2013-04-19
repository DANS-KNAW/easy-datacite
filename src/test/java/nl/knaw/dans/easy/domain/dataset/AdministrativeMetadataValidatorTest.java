package nl.knaw.dans.easy.domain.dataset;

import static org.junit.Assert.assertTrue;

import java.util.List;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.test.ClassPathHacker;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.common.lang.xml.XMLErrorHandler;
import nl.knaw.dans.easy.domain.workflow.Remark;
import nl.knaw.dans.easy.domain.workflow.WorkflowStep;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdministrativeMetadataValidatorTest
{

    private static final Logger logger = LoggerFactory.getLogger(AdministrativeMetadataValidatorTest.class);

    private static final boolean verbose = Tester.isVerbose();

    @BeforeClass
    public static void beforeClass()
    {
        ClassPathHacker.addFile("../../app/easy-webui/src/main/resources");
    }

    @Test
    public void testMarshalUnMarshal() throws Exception
    {
        AdministrativeMetadataImpl amd = new AdministrativeMetadataImpl();
        amd.setAdministrativeState(DatasetState.MAINTENANCE);
        amd.setAdministrativeState(DatasetState.DRAFT);
        amd.setAdministrativeState(DatasetState.SUBMITTED);
        amd.setAdministrativeState(DatasetState.SUBMITTED);
        amd.setAdministrativeState(DatasetState.DELETED);
        amd.setAdministrativeState(DatasetState.SUBMITTED);

        amd.setDepositorId("did");
        amd.addGroupId("xyz");
        amd.addGroupId("abc");
        amd.addGroupId("abc");

        amd.setTimestamp(new DateTime());
        amd.setVersionable(true);

        amd.getWorkflowData().setAssigneeId("elsa");
        Remark remark = new Remark("This is the text of the remark", "idOfRemarker");
        amd.getWorkflowData().getWorkflow().addRemark(remark);

        List<WorkflowStep> steps = amd.getWorkflowData().getWorkflow().getSteps();
        fillSteps(steps);

        if (verbose)
            logger.debug("\n" + amd.asXMLString(4));

        XMLErrorHandler handler = AdministrativeMetadataValidator.instance().validate(amd);
        assertTrue(handler.passed());
    }

    private void fillSteps(List<WorkflowStep> steps)
    {
        for (WorkflowStep step : steps)
        {
            step.addRemark(new Remark("remark text", "remarkerId"));
            step.addRemark(new Remark("2e remark", "2eremarkerId"));
            step.setCompleted(true, "completerId");
            if (step.isTimeSpentWritable())
                step.setTimeSpent(2.54D);
            fillSteps(step.getSteps());
        }
    }

}
