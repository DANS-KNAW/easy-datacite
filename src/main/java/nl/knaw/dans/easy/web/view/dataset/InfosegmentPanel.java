package nl.knaw.dans.easy.web.view.dataset;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.domain.exceptions.DataIntegrityException;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.common.DatasetModel;
import nl.knaw.dans.easy.web.template.AbstractDatasetModelPanel;
import nl.knaw.dans.easy.web.template.AbstractEasyPanel;
import nl.knaw.dans.easy.web.view.dataset.DatasetViewPage.Mode;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class InfosegmentPanel extends AbstractDatasetModelPanel
{

    private static final long serialVersionUID = -1476532905888130291L;

	private static final Logger logger = LoggerFactory.getLogger(InfosegmentPanel.class);

    private final DatasetViewPage.Mode mode;

    private boolean initiated;

    public InfosegmentPanel(String id, DatasetModel datasetModel, Mode mode)
    {
        super(id, datasetModel);
        this.mode = mode;
    }
    
    public Mode getMode()
    {
        return mode;
    }

    public boolean isInitiated()
    {
        return initiated;
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
        boolean showStatus = Mode.VIEW.equals(getMode());
        AbstractEasyPanel statusPanel = new StatusPanel("statusPanel", getDatasetModel(), getMode());
        statusPanel.setVisible(showStatus);
        add(statusPanel);

        Label depositorName = new Label("depositorName", new Model<String>()
        {
            private static final long serialVersionUID = -1964060298891182555L;

            @Override
            public String getObject()
            {
            	return getDataset().getAdministrativeMetadata().getDepositor().getDisplayName();
            }

        });
        add(depositorName);

        Link changeDepositorLink = new Link("changeDepositorLink")
        {

            private static final long serialVersionUID = -8501231770764062401L;

            @Override
            public void onClick()
            {
                logger.debug("changeDepositorLink clicked");
                setResponsePage(new DatasetIntermediatePage(getDatasetModel(), IntermediatePage.Mode.CHANGE_DEPOSITOR));
            }

        };
        changeDepositorLink.setVisible(showStatus);
        add(changeDepositorLink);

    }

    protected void handleRestoreDataset()
    {
        try
        {
            Services.getDatasetService().restoreDataset(getSessionUser(), getDataset());
        }
        catch (ServiceException e)
        {
            final String message = errorMessage(EasyResources.ERROR_RESTORING_DATASET, getDataset().getStoreId());
            logger.error(message, e);
            throw new InternalWebError();
        }
        catch (DataIntegrityException e)
        {
            final String message = errorMessage(EasyResources.ERROR_RESTORING_DATASET, getDataset().getStoreId());
            logger.error(message, e);
            throw new InternalWebError();
        }
    }


    private class DatasetIntermediatePage extends IntermediatePage 
    {
    	public DatasetIntermediatePage(DatasetModel datasetModel, IntermediatePage.Mode mode)
		{
    		super(datasetModel, mode);
		}
    	
    	@Override
    	Page getReturnToPage() 
    	{
    		return new DatasetViewPage(getDatasetModel(), DatasetViewPage.Mode.VIEW);
    	}
    }

}
