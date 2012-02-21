package nl.knaw.dans.easy.business.item;

import java.util.Collection;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.dataset.item.RequestedItem;
import nl.knaw.dans.easy.domain.download.FileContentWrapper;
import nl.knaw.dans.easy.domain.download.ZipFileContentWrapper;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

public class DownloadWorkDispatcher
{
    
    public FileContentWrapper prepareFileContent(EasyUser sessionUser, Dataset dataset, DmoStoreId fileItemId)
        throws CommonSecurityException, ServiceException
    {
        DownloadWorker worker = new DownloadWorker();
        return worker.getFileContent(sessionUser, dataset, fileItemId);
    }
    
    public ZipFileContentWrapper prepareZippedContent(EasyUser sessionUser, Dataset dataset,
            Collection<RequestedItem> requestedItems) throws CommonSecurityException, ServiceException
    {
        DownloadWorker worker = new DownloadWorker();
        return worker.getZippedContent(sessionUser, dataset, requestedItems);
    }
            

}
