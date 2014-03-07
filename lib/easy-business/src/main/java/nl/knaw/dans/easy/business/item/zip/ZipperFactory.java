package nl.knaw.dans.easy.business.item.zip;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import nl.knaw.dans.common.lang.progress.ProgressSubject;
import nl.knaw.dans.easy.domain.dataset.item.RequestedItem;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

public class ZipperFactory
{
    private Map<String, Integer> weightMap;

    public Zipper createZipper(EasyUser sessionUser, Dataset dataset, List<RequestedItem> requestedItems, ZipOutputStream zipOut)
    {
        int totalWeight = 0;
        ItemCollector itemCollector = new ItemCollector(requestedItems);
        itemCollector.setWeight(getWeight(ItemCollector.class.getName()));
        totalWeight += itemCollector.getWeight();

        DownloadFilterAdapter downloadFilter = new DownloadFilterAdapter(itemCollector, sessionUser, dataset);
        downloadFilter.setWeight(getWeight(DownloadFilterAdapter.class.getName()));
        totalWeight += downloadFilter.getWeight();

        ContentCollector contentCollector = new ContentCollector(downloadFilter, zipOut);
        contentCollector.setWeight(getWeight(ContentCollector.class.getName()));
        totalWeight += contentCollector.getWeight();

        return null;
    }

    private int getWeight(String subjectId)
    {
        Integer weight = getWeightMap().get(subjectId);
        if (weight == null)
        {
            return ProgressSubject.DEFAULT_WEIGHT;
        }
        else
        {
            return weight.intValue();
        }
    }

    public void setWeightMap(Map<String, Integer> weightMap)
    {
        this.weightMap = weightMap;
    }

    private Map<String, Integer> getWeightMap()
    {
        if (weightMap == null)
        {
            weightMap = new HashMap<String, Integer>();
            weightMap.put(ItemCollector.class.getName(), 17000);
            weightMap.put(DownloadFilterAdapter.class.getName(), 20);
        }
        return weightMap;
    }

}
