package nl.knaw.dans.easy.web.permission;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.PermissionSequence;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.security.ContextParameters;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.PageBookmark;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;

import org.apache.wicket.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This page acts as a staging for the {@link PermissionReplyPage.java}. It is created when a user clicks on a
 * bookmarkable link (set in {@link PageBookmark.java}). This link is mostly clicked from an email send to the user.
 * 
 * Before the PermissionReplyPage is instantiated the authorization is checked using the authorization rule 
 * {@link isDepositorOfDataset.java}. For this authorization-rule to work the dataset needs to be added to the 
 * ContextParameters. Which isn't the case when a user tries to access the page directly.
 * 
 * This page sets thing up, so it adds the dataset to the ContextParameters and redirects to the 
 * PermissionReplyPage properly checking the authorization.
 * 
 */
public class PermissionReplyPrePage extends AbstractEasyNavPage
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionReplyPrePage.class);
    
    private final EasyUser sessionUser;
    
    public PermissionReplyPrePage(PageParameters parameters)
    {
        sessionUser = EasySession.getSessionUser();
        String datasetId = parameters.getString(PermissionReplyPage.PM_DATASET_ID);
        String requesterId = parameters.getString(PermissionReplyPage.PM_REQUESTER_ID);
        try
        {         
            // Get the dataset using the id in the url
            Dataset dataset = Services.getDatasetService().getDataset(sessionUser, datasetId);
            
            // The dataset is not in the ContextParameters, add it because the authorization needs it.
            getEasySession().setContextParameters(new ContextParameters(sessionUser, dataset));
            
            DatasetModel datasetModel = new DatasetModel(dataset);
            PermissionSequence request = dataset.getPermissionSequenceList().getSequenceFor(requesterId);
            
            // Finished setting up, go to the PermissionReplyPage
            setResponsePage(new PermissionReplyPage(datasetModel, null, request));
            //setResponsePage(new PermissionReplyPage(parameters));
        }
        catch (ServiceException e)
        {
            errorMessage(EasyResources.DATASET_LOAD, datasetId);
            LOGGER.error("Unable to load model object: ", e);
            throw new InternalWebError();
        }
    }
}
