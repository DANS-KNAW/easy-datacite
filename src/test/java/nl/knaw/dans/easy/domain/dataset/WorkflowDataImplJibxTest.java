package nl.knaw.dans.easy.domain.dataset;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import nl.knaw.dans.common.lang.test.ClassPathHacker;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;
import nl.knaw.dans.easy.domain.model.WorkflowData;
import nl.knaw.dans.easy.domain.workflow.Remark;
import nl.knaw.dans.easy.domain.workflow.WorkflowFactory;
import nl.knaw.dans.easy.domain.workflow.WorkflowStep;
import nl.knaw.dans.easy.util.AbstractJibxTest;

import org.jibx.runtime.JiBXException;
import org.junit.BeforeClass;
import org.junit.Test;

public class WorkflowDataImplJibxTest extends AbstractJibxTest<WorkflowDataImpl>
{

    @BeforeClass
    public static void testStartInformation()
    {
        before(WorkflowDataImplJibxTest.class);
    }

    public WorkflowDataImplJibxTest()
    {
        super(WorkflowDataImpl.class);
    }

    @Test
    public void testMarshalUnMarshalEmpty() throws JiBXException, IOException, XMLSerializationException
    {
        WorkflowDataImpl wfd = new WorkflowDataImpl();

        //log().debug("\n" + wfd.asXMLString(4));

        String filename = marshal(wfd, "_empty");
        WorkflowDataImpl wfd2 = unmarshal(filename);
        assertEquals(wfd.asXMLString(), wfd2.asXMLString());
    }

    @Test
    public void testMarshalUnMarshal() throws IOException, JiBXException, XMLSerializationException
    {
        WorkflowDataImpl wfd = new WorkflowDataImpl();
        wfd.setAssigneeId("willem");

        //log().debug("\n" + wfd.asXMLString(4));

        String filename = marshal(wfd, "_full");
        WorkflowDataImpl wfd2 = unmarshal(filename);
        assertEquals(wfd.asXMLString(), wfd2.asXMLString());
    }

    @Test
    public void testWorkflowStep() throws JiBXException, IOException, XMLSerializationException
    {
        WorkflowDataImpl wfd = new WorkflowDataImpl();
        wfd.getWorkflow();

        log().debug("\n" + wfd.asXMLString(4));

        String filename = marshal(wfd, "_withWorkflow");
        WorkflowDataImpl wfd2 = unmarshal(filename);
        assertEquals(wfd.asXMLString(), wfd2.asXMLString());
    }

    @Test
    public void testWorkflowDataFull() throws Exception
    {
        WorkflowDataImpl wfd = new WorkflowDataImpl();
        fillSteps(wfd.getWorkflow().getSteps());
        log().debug("\n" + wfd.asXMLString(4));

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
