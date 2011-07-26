package nl.knaw.dans.easy.web.template;

import nl.knaw.dans.common.lang.service.exceptions.ServiceRuntimeException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.web.common.DatasetModel;

import org.apache.wicket.WicketRuntimeException;

public abstract class AbstractDatasetModelPanel extends AbstractEasyPanel
{

    private static final long serialVersionUID = -8845239111184791200L;
    
    private transient Dataset dataset;

    public AbstractDatasetModelPanel(String wicketId, DatasetModel model)
    {
        super(wicketId, model);
    }
    
    protected Dataset getDataset()
    {
        if (dataset == null)
        {
            try
            {
                dataset = (Dataset) getDefaultModelObject();
            }
            catch (ServiceRuntimeException e)
            {
                throw new WicketRuntimeException(e);
            }
        }
        return dataset;
    }
    
    protected DatasetModel getDatasetModel()
    {
    	return (DatasetModel) getDefaultModel();
    }

}
