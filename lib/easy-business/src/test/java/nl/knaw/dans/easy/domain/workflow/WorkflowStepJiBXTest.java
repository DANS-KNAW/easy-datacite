package nl.knaw.dans.easy.domain.workflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import nl.knaw.dans.common.lang.xml.XMLSerializationException;
import nl.knaw.dans.easy.util.AbstractJibxTest;

import org.jibx.runtime.JiBXException;
import org.junit.BeforeClass;
import org.junit.Test;

public class WorkflowStepJiBXTest extends AbstractJibxTest<WorkflowStep>
{

    private boolean verbose = false;

    @BeforeClass
    public static void testStartInformation()
    {
        before(WorkflowStepJiBXTest.class);
    }

    public WorkflowStepJiBXTest()
    {
        super(WorkflowStep.class);
    }

    @Test
    public void testEmpty() throws IOException, JiBXException, XMLSerializationException
    {
        WorkflowStep wfs = new WorkflowStep("x");
        String filename = marshal(wfs, "_empty");

        WorkflowStep wfs2 = unmarshal(filename);
        assertEquals(wfs.asXMLString(), wfs2.asXMLString());
    }

    @Test
    public void testFull() throws JiBXException, IOException, XMLSerializationException
    {
        WorkflowStep wfs = new WorkflowStep("dasa_10000");
        wfs.setDoneById("piet");

        wfs.addRemark(new Remark("Wat een trash!", "jan"));
        wfs.addRemark(new Remark("Let op je woorden, Jan!", "marietje"));

        WorkflowStep subA = new WorkflowStep("dasa_11000");
        wfs.addStep(subA);

        WorkflowStep subB = new WorkflowStep("dasa_11100");
        subA.addStep(subB);

        subA.setCompleted(true);
        String filename = marshal(wfs, "_full");

        WorkflowStep wfs2 = unmarshal(filename);
        assertEquals(wfs.asXMLString(), wfs2.asXMLString());

        subA = wfs2.getSteps().get(0);
        assertNotNull(subA.getParent());
    }

    @Test
    public void read() throws IOException, JiBXException
    {
        WorkflowStep wfs = unmarshal(getFile("read.xml").getPath());
        assertEquals("dasa_10000", wfs.getId());
        assertTrue(wfs.isRequired());
    }

    @Test
    public void testFull2() throws Exception
    {
        WorkflowStep wfs = WorkflowFactory.newDatasetWorkflow();
        wfs.addRemark(new Remark("text of remark", "henkb"));
        wfs.addRemark(new Remark("text of remark2", "henkb"));
        wfs.setCompleted(true, "henkb");
        if (wfs.isTimeSpentWritable())
            wfs.setTimeSpent(3.54D);
        fillSteps(wfs.getSteps());
        if (verbose)
            System.err.println("\n" + wfs.asXMLString(4));
    }

    private void fillSteps(List<WorkflowStep> steps)
    {
        for (WorkflowStep step : steps)
        {
            step.addRemark(new Remark("remark text", "henkb"));
            step.addRemark(new Remark("2e remark", "henkb"));
            step.setCompleted(true, "completerId");
            if (step.isTimeSpentWritable())
                step.setTimeSpent(2.54D);
            fillSteps(step.getSteps());
        }
    }
}
