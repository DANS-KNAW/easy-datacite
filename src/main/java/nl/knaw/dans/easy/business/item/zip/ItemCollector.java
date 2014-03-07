package nl.knaw.dans.easy.business.item.zip;

import java.util.List;

import nl.knaw.dans.common.lang.collect.CollectorDecorator;
import nl.knaw.dans.common.lang.collect.CollectorException;
import nl.knaw.dans.common.lang.progress.ProgressSubject;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.dataset.item.RequestedItem;

public class ItemCollector extends ProgressSubject implements CollectorDecorator<List<? extends ItemVO>>
{

    private final List<RequestedItem> requestedItems;

    public ItemCollector(List<RequestedItem> requestedItems)
    {
        this.requestedItems = requestedItems;
    }

    @Override
    public List<? extends ItemVO> collect() throws CollectorException
    {

        return null;
    }

}
