package nl.knaw.dans.easy.business.item;

import java.util.List;

import nl.knaw.dans.common.lang.IdMutexProvider;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.download.DownloadHistory;
import nl.knaw.dans.easy.domain.download.DownloadList;
import nl.knaw.dans.easy.domain.download.DownloadList.Level;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadRegistration
{

    private static final IdMutexProvider MUTEX_PROVIDER = new IdMutexProvider();
    private static final Logger logger = LoggerFactory.getLogger(DownloadRegistration.class);

    private final EasyUser sessionUser;
    private final Dataset dataset;
    private final List<? extends ItemVO> downloadedItemVOs;
    private final DateTime downloadTime;

    public DownloadRegistration(EasyUser sessionUser, Dataset dataset, List<? extends ItemVO> downloadedItemVOs)
    {
        this.downloadTime = new DateTime();
        this.sessionUser = sessionUser;
        this.dataset = dataset;
        this.downloadedItemVOs = downloadedItemVOs;
    }

    public void registerDownloads()
    {
        new Thread(new LevelDatasetRegistrator()).start();
        // other levelRegistrators: level file, level store
    }

    private class LevelDatasetRegistrator implements Runnable
    {
        public void run()
        {
            synchronized (MUTEX_PROVIDER.getMutex(dataset.getStoreId()))
            {
                try
                {
                    String period = DownloadList.printPeriod(DownloadHistory.LIST_TYPE_DATASET, downloadTime);
                    DownloadHistory dlh = Data.getEasyStore().findDownloadHistoryFor(dataset, period);
                    if (dlh == null)
                    {
                        String storeId = Data.getEasyStore().nextSid(DownloadHistory.NAMESPACE);
                        dlh = new DownloadHistory(storeId, DownloadHistory.LIST_TYPE_DATASET, Level.DATASET, dataset.getStoreId());
                        dlh.getDownloadList().addDownload(downloadedItemVOs, sessionUser, downloadTime);
                        Data.getEasyStore().ingest(dlh, "First ingest with " + downloadedItemVOs.size() + " records.");
                        logger.debug("Ingested download history for " + dataset.getStoreId());
                    }
                    else
                    {
                        dlh.getDownloadList().addDownload(downloadedItemVOs, sessionUser, downloadTime);
                        DateTime updateTime = Data.getEasyStore().update(dlh, false, "Update with " + downloadedItemVOs.size() + " records.",
                                sessionUser.getId());
                        logger.debug("Updated download history for " + dataset.getStoreId() + " at " + updateTime);
                    }
                }
                catch (RepositoryException e)
                {
                    logger.error("Unable to register download history", e);
                }
            }

        }

    }

}
