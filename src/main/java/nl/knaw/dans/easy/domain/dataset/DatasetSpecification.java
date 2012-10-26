package nl.knaw.dans.easy.domain.dataset;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.exceptions.DataIntegrityException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.WorkflowData;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;
import nl.knaw.dans.easy.domain.model.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.easy.domain.model.emd.types.BasicString;
import nl.knaw.dans.easy.domain.workflow.Remark;
import nl.knaw.dans.easy.domain.workflow.WorkflowStep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Minimum check on validity of a dataset.
 * @author ecco Aug 11, 2009
 *
 */
public class DatasetSpecification
{

    private static final Logger logger = LoggerFactory.getLogger(DatasetSpecification.class);

    public static void evaluate(Dataset dataset) throws ServiceException, DataIntegrityException
    {
        List<String> errorMessages = new ArrayList<String>();

        String part = " (easyMetadata)";
        MetadataFormat mdFormat = dataset.getMetadataFormat();
        if (mdFormat == null)
        {
            String msg = "No metadata format specified." + part;
            logger.warn(msg);
            errorMessages.add(msg);
        }

        part = " (administrative metadata)";
        String depositorId = dataset.getAdministrativeMetadata().getDepositorId();
        if (depositorId == null)
        {
            String msg = "no depositorId." + part;
            logger.warn(msg);
            errorMessages.add(msg);
        }

        if (!userExists(depositorId))
        {
            String msg = "depositorId '" + depositorId + "' does not exist." + part;
            logger.warn(msg);
            errorMessages.add(msg);
        }

        Set<String> groupIds = dataset.getAdministrativeMetadata().getGroupIds();
        for (String groupId : groupIds)
        {
            if (!groupExists(groupId))
            {
                String msg = "groupId '" + groupId + "' does not exist." + part;
                logger.warn(msg);
                errorMessages.add(msg);
            }
        }

        part = " (workflow data)";
        WorkflowData workflowData = dataset.getAdministrativeMetadata().getWorkflowData();
        String assigneeId = workflowData.getAssigneeId();
        if (assigneeId != null && !WorkflowData.NOT_ASSIGNED.equals(assigneeId) && !userExists(assigneeId))
        {
            String msg = "assigneeId '" + assigneeId + "' does not exist." + part;
            logger.warn(msg);
            errorMessages.add(msg);
        }

        checkWorkflowSteps(errorMessages, workflowData.getWorkflow());

        EasyMetadata easyMetadata = dataset.getEasyMetadata();
        easyMetadata.getEmdIdentifier().setDatasetId(dataset.getStoreId());

        if (!errorMessages.isEmpty())
        {
            String msg0 = "Invalid dataset. datasetId=" + dataset.getStoreId();
            errorMessages.add(0, msg0);
            throw new DataIntegrityException(msg0, errorMessages);
        }
    }

    public static void completeEasyMetadata(EasyMetadata easyMetadata)
    {
        completeDcType(easyMetadata);
    }

    private static void completeDcType(EasyMetadata easyMetadata)
    {
        BasicString type = new BasicString("Dataset");
        type.setScheme("DCMI");
        type.setSchemeId("common.dc.type");
        if (!easyMetadata.getEmdType().contains(type))
        {
            easyMetadata.getEmdType().getDcType().add(type);
        }
    }

    private static void checkWorkflowSteps(List<String> errorMessages, WorkflowStep step) throws ServiceException, DataIntegrityException
    {
        String doneById = step.getDoneById();
        if (doneById != null && !userExists(doneById))
        {
            String msg = "doneById '" + doneById + "' does not exist. WorkflowStep(" + step.getId() + ")";
            logger.warn(msg);
            errorMessages.add(msg);
        }
        for (Remark remark : step.getRemarks())
        {
            String remarkerId = remark.getRemarkerId();
            if (remarkerId != null && !userExists(remarkerId))
            {
                String msg = "remarkerId '" + remarkerId + "' does not exist. WorkflowStep(" + step.getId() + ")";
                logger.warn(msg);
                errorMessages.add(msg);
            }
        }

        for (WorkflowStep kidStep : step.getSteps())
        {
            checkWorkflowSteps(errorMessages, kidStep);
        }
    }

    private static boolean userExists(String username) throws ServiceException
    {
        boolean exists = false;
        try
        {
            exists = Data.getUserRepo().exists(username);
        }
        catch (RepositoryException e)
        {
            throw new ServiceException(e);
        }
        return exists;
    }

    private static boolean groupExists(String groupId) throws ServiceException
    {
        boolean exists = false;
        try
        {
            exists = Data.getGroupRepo().exists(groupId);
        }
        catch (RepositoryException e)
        {
            throw new ServiceException(e);
        }
        return exists;
    }

}
