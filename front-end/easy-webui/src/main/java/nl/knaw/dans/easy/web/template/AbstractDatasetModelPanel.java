package nl.knaw.dans.easy.web.template;

import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.web.common.DatasetModel;

@SuppressWarnings("serial")
public abstract class AbstractDatasetModelPanel extends AbstractEasyPanel<Dataset>
{
    public AbstractDatasetModelPanel(String wicketId, DatasetModel model)
    {
        super(wicketId, model);
    }

    protected Dataset getDataset()
    {
        return (Dataset) getDefaultModelObject();
    }

    protected DatasetModel getDatasetModel()
    {
        return (DatasetModel) getDefaultModel();
    }

}
