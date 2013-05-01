package nl.knaw.dans.easy.domain.workflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import nl.knaw.dans.common.lang.xml.XMLSerializationException;
import nl.knaw.dans.easy.util.AbstractJibxTest;

import org.jibx.runtime.JiBXException;
import org.junit.BeforeClass;
import org.junit.Test;

public class WorkflowStepTest extends AbstractJibxTest<WorkflowStep>
{
    @BeforeClass
    public static void beforeClass()
    {
        before(WorkflowStepTest.class);
    }

    public WorkflowStepTest()
    {
        super(WorkflowStep.class);
    }

    @Test
    public void getRequiredSteps()
    {
        WorkflowStep root = WorkflowFactory.newDatasetWorkflow();
        for (WorkflowStep step : root.getRequiredSteps())
        {
            assertTrue(step.isRequired());
        }
    }

    @Test
    public void testCompleted()
    {
        WorkflowStep root = WorkflowFactory.newDatasetWorkflow();
        assertFalse(root.areRequiredStepsCompleted());
        assertFalse(root.isCompleted());

        for (WorkflowStep requiredStep : root.getRequiredSteps())
        {
            requiredStep.setCompleted(true);
        }
        assertTrue(root.areRequiredStepsCompleted());
        assertFalse(root.isCompleted());

        root.setCompleted(true);
        assertTrue(root.isCompleted());

        root.setCompleted(false);
        assertFalse(root.areRequiredStepsCompleted());
        assertFalse(root.isCompleted());
    }

    @Test
    public void testCompletionTime() throws IOException, JiBXException, XMLSerializationException
    {
        WorkflowStep root = WorkflowFactory.newDatasetWorkflow();
        assertNull(root.getCompletionTimeRequiredSteps());
        assertNull(root.getCompletionTimeAllSteps());

        root = unmarshal(getFile("someRequiredCompleted.xml").getPath());
        assertNull(root.getCompletionTimeRequiredSteps());
        assertNull(root.getCompletionTimeAllSteps());

        root = unmarshal(getFile("allRequiredCompleted.xml").getPath());
        assertNotNull(root.getCompletionTimeRequiredSteps());
        assertEquals("2010-08-26T11:55:06.676+02:00", root.getCompletionTimeRequiredSteps().toString());
        assertNull(root.getCompletionTimeAllSteps());

        root = unmarshal(getFile("allCompleted.xml").getPath());
        assertNotNull(root.getCompletionTimeRequiredSteps());
        assertEquals("2009-10-26T12:15:34.287+01:00", root.getCompletionTimeRequiredSteps().toString());
        assertNotNull(root.getCompletionTimeAllSteps());
        assertEquals("2009-10-26T12:15:34.287+01:00", root.getCompletionTimeAllSteps().toString());
    }

}
