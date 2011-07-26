package nl.knaw.dans.easy.web.view.dataset;

import java.io.Serializable;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.exceptions.DataIntegrityException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.WorkflowData;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.template.AbstractEasyPanel;
import nl.knaw.dans.easy.web.wicket.AssignToDropChoiceList;
import nl.knaw.dans.easy.web.wicket.WorkflowProgressPanel;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PublicationProgressPanel extends AbstractEasyPanel
{
    
    public static final String AJAX_EVENT_ADMIN_METADATA_SAVED = PublicationProgressPanel.class.getName() + " administrative metadata saved";
    
	private static final long serialVersionUID = 597926626305715195L;
    private static final Logger logger = LoggerFactory.getLogger(PublicationProgressPanel.class);
    
    private final DatasetModel datasetModel;
    
    private boolean initiated;
        

    public PublicationProgressPanel(String wicketId, DatasetModel datasetModel)
    {
        super(wicketId);
        this.datasetModel = datasetModel;
    }
    
    
    protected Dataset getDataset()
    {
    	return datasetModel.getObject();
    }
    
    @Override
    public boolean isVisible()
    {
        return DatasetState.SUBMITTED.equals(getDataset().getAdministrativeState())
                || DatasetState.MAINTENANCE.equals(getDataset().getAdministrativeState());
    }
    
    @Override
    protected void onBeforeRender()
    {
        if (!initiated)
        {
            init();
            initiated = true;
        }
        super.onBeforeRender();
    }

    private void init()
    {
        WorkflowProgressPanel pubProgress = new WorkflowProgressPanel("pubProgress", getDataset().getAdministrativeMetadata().getWorkflowData().getWorkflow())
        {
            private static final long serialVersionUID = -5454750325730819797L;
            
            @Override
            public boolean isVisible()
            {
            	// NOTE: GK: the publication progress has been disabled, remove if truly unnecessary
                //return DatasetState.SUBMITTED.equals(getDataset().getAdministrativeState())
                //    || DatasetState.MAINTENANCE.equals(getDataset().getAdministrativeState());
            	return false;
            }
            
        };
        add(pubProgress);

        try
        {
            IModel model = new PropertyModel(new AssignModel(getDataset().getAdministrativeMetadata().getWorkflowData()), "userId");
            Form assignToForm = new Form("assignToForm")
            {
            	/**
				 * 
				 */
				private static final long serialVersionUID = -7872676803301019518L;

				@Override
            	protected void onSubmit()
            	{
                    saveChanges();
            	}
            };
            
            DropDownChoice assignTo = AssignToDropChoiceList.getDropDownChoice("assignTo", model);
            assignTo.setNullValid(false);
            assignToForm.add(assignTo);
            assignToForm.add(new SubmitLink("submitLink"));
            add(assignToForm);
        }
        catch (ServiceException e)
        {
            final String message = errorMessage(EasyResources.ERROR_GETTING_ARCHIVISTS_LIST);
            logger.error(message, e);
        }
        
    }

    
    protected void saveChanges()
    {
        try
        {
            Services.getDatasetService().saveAdministrativeMetadata(getSessionUser(), getDataset());
            final String message = infoMessage(EasyResources.SUCCESFUL_UPDATE);
        	logger.info(message);           
        }
        catch (ServiceException e)
        {
            final String message = errorMessage(EasyResources.ERROR_SAVING_ADMINISTRATIVE_METADATA);
            logger.error(message, e);
        }
        catch (DataIntegrityException e)
        {
            final String message = errorMessage(EasyResources.ERROR_SAVING_ADMINISTRATIVE_METADATA);
            logger.error(message, e);
        }

    }

    protected static class AssignModel implements Serializable
    {

        private static final long serialVersionUID = -6033853618498949502L;
        
        private final WorkflowData workflowData;
        
        protected AssignModel(WorkflowData workflowData)
        {
            this.workflowData = workflowData;
        }

        public KeyValuePair getUserId()
        {
            return new KeyValuePair(workflowData.getAssigneeId(), null);
        }

        public void setUserId(KeyValuePair kvp)
        {
            workflowData.setAssigneeId(kvp.getKey());
        }
        
        
    }

}
