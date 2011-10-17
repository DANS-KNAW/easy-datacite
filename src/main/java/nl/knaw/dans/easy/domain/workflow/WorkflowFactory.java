package nl.knaw.dans.easy.domain.workflow;

import java.io.IOException;
import java.io.InputStream;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.ResourceLocator;
import nl.knaw.dans.common.lang.ResourceNotFoundException;
import nl.knaw.dans.common.lang.xml.XMLDeserializationException;
import nl.knaw.dans.easy.domain.exceptions.DeserializationException;


public final class WorkflowFactory
{
    
    public static final String DATASET_WORKFLOW_PATH = "xml-files/datasetWorkflow.xml";
    
    private WorkflowFactory()
    {
        // static class
    }
    
    public static WorkflowStep newDatasetWorkflow()
    {
        WorkflowStep datasetWorkflow = null;
        try
        {
            datasetWorkflow = getWorkflowStep(DATASET_WORKFLOW_PATH);
        }
        catch (IOException e)
        {
            throw new DeserializationException("Exception while closing inputstream: ", e);
        }
        return datasetWorkflow;
    }
    
    public static WorkflowStep getWorkflowStep(final String path) throws IOException
    {
        WorkflowStep wfStep = null;
        InputStream inStream = null;
        try
        {
            inStream = WorkflowFactory.class.getResourceAsStream(DATASET_WORKFLOW_PATH);
            wfStep = (WorkflowStep) JiBXObjectFactory.unmarshal(WorkflowStep.class, inStream);
        }
        catch (IOException e)
        {
            throw new DeserializationException(e);
        }
        catch (XMLDeserializationException e)
        {
            throw new DeserializationException(e);
        }
        finally
        {
            if (inStream != null)
            {
                inStream.close();
            }
        }
        return wfStep;
    }
    
    

}
