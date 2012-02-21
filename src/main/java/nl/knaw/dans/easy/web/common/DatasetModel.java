package nl.knaw.dans.easy.web.common;

import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceRuntimeException;
import nl.knaw.dans.common.wicket.model.DMOModel;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasySession;

public class DatasetModel extends DMOModel<Dataset>
{
    private static final long serialVersionUID = -8416446576640508341L;
    
    public DatasetModel(String storeId) throws ObjectNotAvailableException, CommonSecurityException, ServiceException
    {
    	super(storeId);
    	init();
    	Dataset dataset = Services.getDatasetService().getDataset(EasySession.get().getUser(), getDmoStoreId());
    	setObject(dataset);
    }

    public DatasetModel(Dataset dataset)
    {
    	super(dataset);
    	init();
    }

    public DatasetModel(DatasetModel datasetModel)
    {
    	super(datasetModel);
    	init();
    }

    private void init()
	{
    	setDynamicReload(true);
	}

	protected Dataset loadDmo() throws ServiceRuntimeException
    {
		try
        {
            return Services.getDatasetService().getDataset(EasySession.get().getUser(), getDmoStoreId());
        }
        catch (ServiceException e)
        {
            throw new ServiceRuntimeException(e);
        }
    }
}
