package nl.knaw.dans.easy.web.statistics;

import java.util.HashMap;

import nl.knaw.dans.easy.domain.model.Dataset;

public class DatasetStatistics extends StatisticsModel<Dataset>
{
    public DatasetStatistics(Dataset dataset)
    {
        super(dataset);
    }

    @Override
    public HashMap<String, String> getLogValues()
    {
        HashMap<String, String> res = new HashMap<String, String>();
        res.put("DATASET_ID", getObject().getStoreId());
        return res;
    }

    @Override
    public String getName()
    {
        return "dataset";
    }
}
