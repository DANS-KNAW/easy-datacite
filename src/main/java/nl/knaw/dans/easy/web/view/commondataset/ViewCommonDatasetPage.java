package nl.knaw.dans.easy.web.view.commondataset;

import java.util.List;

import nl.knaw.dans.common.lang.dataset.CommonDataset;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.exceptions.InternalWebError;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasyResources;
import nl.knaw.dans.easy.web.main.AbstractEasyNavPage;
import nl.knaw.dans.easy.web.template.AbstractEasyPage;
import nl.knaw.dans.easy.web.view.dataset.DatasetViewPage;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewCommonDatasetPage extends AbstractEasyNavPage
{
	private static final Logger	LOGGER = LoggerFactory.getLogger(ViewCommonDatasetPage.class);

	public ViewCommonDatasetPage(PageParameters parameters)
	{
        String datasetId = parameters.getString(DatasetViewPage.PM_DATASET_ID);
        CommonDataset dataset = null;
        try
		{
			dataset = Services.getDatasetService().getCommonDataset(new DmoStoreId(datasetId));
		}
		catch (ServiceException e)
		{
            final String message = errorMessage(EasyResources.LOAD_DATASET,datasetId);
            LOGGER.error(message, e);
			throw new InternalWebError();
		}
		
		add(new Label("title", new Model<String>(formatStrList(dataset.getDublinCoreMetadata().getTitle()))));
		add(new Label("dcTitle", new Model<String>(formatStrList(dataset.getDublinCoreMetadata().getTitle()))));
		add(new Label("dcSubject", new Model<String>(formatStrList(dataset.getDublinCoreMetadata().getSubject()))));
		add(new Label("dcCreator", new Model<String>(formatStrList(dataset.getDublinCoreMetadata().getCreator()))));
		add(new Label("dcDescription", new Model<String>(formatStrList(dataset.getDublinCoreMetadata().getDescription()))));
		add(new Label("dcFormat", new Model<String>(formatStrList(dataset.getDublinCoreMetadata().getFormat()))));
		String href = dataset.getDublinCoreMetadata().getIdentifier().get(0);
		add(new ExternalLink("link", href, href));

        Link backToListLink = new Link("backToList") 
        {
            private static final long serialVersionUID = 2282643032675018321L;

            @Override
            public void onClick()
            {
                Page page = ViewCommonDatasetPage.this.getEasySession().getRedirectPage(ViewCommonDatasetPage.class);
                if (page != null && page instanceof AbstractEasyPage)
                {
                    ((AbstractEasyPage) page).refresh();
                }
                if (page != null)
                {
                    setResponsePage(page);
                }
            }
            
            @Override
            public boolean isVisible()
            {
            	return ViewCommonDatasetPage.this.getEasySession().hasRedirectPage(ViewCommonDatasetPage.class);
            }
        };
        add(backToListLink);
	}
	
    private String formatStrList(List<String> c)
    {
		String result = "";
    	if (c != null && c.size() > 0)
    	{
    		for(int i = 0; i < c.size(); i++)
    		{
    			result += c.get(i);
    			if (i+1 < c.size()) result += ", ";
    		}
    	}
    	        	
    	return result;
    }	
}
