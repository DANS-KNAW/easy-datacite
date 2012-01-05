package nl.knaw.dans.easy.domain.download;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.AbstractDmoFactory;

public class DownloadHistoryFactory extends AbstractDmoFactory<DownloadHistory>
{

    @Override
    public String getNamespace()
    {
        return DownloadHistory.NAMESPACE;
    }

    @Override
    public DownloadHistory newDmo() throws RepositoryException
    {
        return createDmo(nextSid());
    }

    @Override
    public DownloadHistory createDmo(String storeId)
    {
        return new DownloadHistory(storeId);
    }

}
