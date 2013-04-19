package nl.knaw.dans.easy.domain.workflow;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.test.ClassPathHacker;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowFactoryTest
{

    private static final Logger logger = LoggerFactory.getLogger(WorkflowFactoryTest.class);

    private boolean verbose = Tester.isVerbose();

    @BeforeClass
    public static void beforeClass() throws ServiceException
    {
        ClassPathHacker.addFile("../../app/easy-webui/src/main/resources");
    }

    @Test
    public void newDatasetWorkflow() throws Exception
    {
        WorkflowStep root = WorkflowFactory.newDatasetWorkflow();
        if (verbose)
            logger.debug("number of steps: " + root.countSteps());
        if (verbose)
            logger.debug("number of required steps: " + root.countRequiredSteps());
        if (verbose)
            logger.debug("\n" + root.printStructure(4) + "\n");
        if (verbose)
            logger.debug("\n" + root.asXMLString(4) + "\n");
    }

    @Test
    public void printLeafIds()
    {
        WorkflowStep root = WorkflowFactory.newDatasetWorkflow();
        for (WorkflowStep step : root.getSteps())
        {
            printLeafId(step);
        }
    }

    private void printLeafId(WorkflowStep step)
    {
        if (step.isLeaf() && verbose)
        {
            logger.debug("Leaf WorkflowStep encountered: " + step.getId());
        }
        else
        {
            for (WorkflowStep kid : step.getSteps())
            {
                printLeafId(kid);
            }
        }

    }

}
