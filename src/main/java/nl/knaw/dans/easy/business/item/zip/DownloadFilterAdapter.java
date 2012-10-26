package nl.knaw.dans.easy.business.item.zip;

import java.util.List;

import nl.knaw.dans.common.lang.collect.Collector;
import nl.knaw.dans.common.lang.collect.CollectorException;
import nl.knaw.dans.common.lang.progress.ProgressSubject;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.DownloadFilter;

public class DownloadFilterAdapter extends ProgressSubject implements Collector<List<? extends ItemVO>>
{

    private final Collector<List<? extends ItemVO>> collector;
    private final DownloadFilter downloadFilter;

    public DownloadFilterAdapter(Collector<List<? extends ItemVO>> collector, EasyUser sessionUser, Dataset dataset)
    {
        this.collector = collector;
        downloadFilter = new DownloadFilter(sessionUser, dataset);
    }

    @Override
    public List<? extends ItemVO> collect() throws CollectorException
    {
        List<? extends ItemVO> originalList = collector.collect();

        onStartProcess();
        List<? extends ItemVO> filteredList;
        try
        {
            filteredList = downloadFilter.apply(originalList);
        }
        catch (DomainException e)
        {
            throw new CollectorException(e);
        }
        onEndProcess();
        return filteredList;
    }

}
