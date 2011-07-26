package nl.knaw.dans.easy.web.rest.dataset;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.wicket.rest.PageDescription;
import nl.knaw.dans.common.wicket.rest.RESTcascadePage;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.view.dataset.DatasetViewPage;

import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.protocol.http.servlet.AbortWithWebErrorCodeException;

public class RESTdatasetsPage extends RESTcascadePage
{
    
    public static final String NAME = "datasets";
    public static final String RESOURCE_KEY = "rest.datasets";
    
    public static final String PM_DATASET = "dataset";
    
    private static final int LEVEL = 2;
    
    private static Map<String, PageDescription> CHILDREN;

    public RESTdatasetsPage(PageParameters parameters)
    {
        super(parameters);
    }

    @Override
    public Map<String, PageDescription> getChildren()
    {
        if (CHILDREN == null)
        {
            CHILDREN = new LinkedHashMap<String, PageDescription>();
            
            String name = RESTviewPage.NAME;
            String resourceKey = RESTviewPage.RESOURCE_KEY;
            PageDescription description = new PageDescription(name, resourceKey, RESTviewPage.class);
            CHILDREN.put(name, description);
            
            name = RESTfoxmlPage.NAME;
            resourceKey = RESTfoxmlPage.RESOURCE_KEY;
            description = new PageDescription(name, resourceKey, RESTfoxmlPage.class);
            CHILDREN.put(name, description);
            
            name = RESTfilesPage.NAME;
            resourceKey = RESTfilesPage.RESOURCE_KEY;
            description = new PageDescription(name, resourceKey, RESTfilesPage.class);
            CHILDREN.put(name, description);
        }
        return CHILDREN;
    }

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public String getResourceKey()
    {
        return RESOURCE_KEY;
    }
    
    @Override
    public int getLevel()
    {
        return LEVEL;
    }
    
    @Override
    protected void doDefaultDissemination()
    {
        String targetUrl = DatasetViewPage.urlFor(getDatasetId(), 0, false, this);
        throw new RestartResponseException(new RedirectPage(targetUrl));
    }
    
    @Override
    protected void contributeParameters(PageParameters parameters)
    {
        parameters.put(PM_DATASET, getDataset());
    }
    
    protected Dataset getDataset()
    {
        Dataset dataset;
        try
        {                                                     
            dataset = Services.getDatasetService().getDataset(EasySession.getSessionUser(), getDatasetId());
        }
        catch (ObjectNotAvailableException e)
        {
            throw new AbortWithWebErrorCodeException(HttpServletResponse.SC_NOT_FOUND);
        }
        catch (CommonSecurityException e)
        {
            throw new AbortWithWebErrorCodeException(HttpServletResponse.SC_UNAUTHORIZED);
        }
        catch (ServiceException e)
        {
            throw new AbortWithWebErrorCodeException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return dataset;
    }
    
    protected String getDatasetId()
    {
        String storeId;
        String itemId = getUrlFragments()[LEVEL];
        if (itemId.startsWith(Dataset.NAMESPACE))
        {
            storeId = itemId;
        }
        else
        {
            storeId = Dataset.NAMESPACE + ":" + itemId;
        }
        return storeId;
    }

}
