package nl.knaw.dans.easy.domain.dataset;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;
import nl.knaw.dans.easy.domain.workflow.Remark;
import nl.knaw.dans.easy.domain.workflow.WorkflowStep;
import nl.knaw.dans.easy.util.AbstractJibxTest;

import org.jibx.runtime.JiBXException;
import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdministrativeMetadataImplJibxTest extends AbstractJibxTest<AdministrativeMetadataImpl>
{

    private static final Logger logger = LoggerFactory.getLogger(AdministrativeMetadataImplJibxTest.class);

    private boolean verbose = Tester.isVerbose();

    @BeforeClass
    public static void testStartInformation()
    {
        before(AdministrativeMetadataImplJibxTest.class);
    }

    public AdministrativeMetadataImplJibxTest()
    {
        super(AdministrativeMetadataImpl.class);
    }

    @Test
    public void testMarshalUnMarshalEmpty() throws IOException, JiBXException, XMLSerializationException
    {
        AdministrativeMetadataImpl amd = new AdministrativeMetadataImpl();

        if (verbose)
            logger.debug("\n" + amd.asXMLString(4));

        String filename = marshal(amd, "_empty");
        AdministrativeMetadataImpl amd2 = unmarshal(filename);
        assertEquals(amd.asXMLString(), amd2.asXMLString());
    }

    @Test
    public void testMarshalUnMarshal() throws JiBXException, IOException, XMLSerializationException
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

        logger.debug("\n" + amd.asXMLString(4));

        String filename = marshal(amd, "_full");
        logger.debug("filename=" + filename);
        AdministrativeMetadataImpl amd2 = unmarshal(filename);
        assertEquals(amd.asXMLString(), amd2.asXMLString());
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
