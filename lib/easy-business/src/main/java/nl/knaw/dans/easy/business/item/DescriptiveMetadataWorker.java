package nl.knaw.dans.easy.business.item;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.UnitOfWork;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.worker.AbstractWorker;

public class DescriptiveMetadataWorker extends AbstractWorker
{

    public DescriptiveMetadataWorker(final UnitOfWork uow)
    {
        super(uow);
    }

    protected void saveDescriptiveMetadata(final Dataset dataset, final Map<String, Element> descriptiveMetadataMap) throws ServiceException
    {
        final Set<String> filesToChange = new HashSet<String>(descriptiveMetadataMap.keySet());
        final Map<String, String> files = getAllFiles(dataset.getDmoStoreId());
        for (final String fileStoreId : files.keySet())
        {
            final String fileName = files.get(fileStoreId);
            if (filesToChange.contains(fileName))
            {
                try
                {
                    final FileItem fileItem = (FileItem) getUnitOfWork().retrieveObject(new DmoStoreId(fileStoreId));
                    fileItem.setDescriptiveMetadata(descriptiveMetadataMap.get(fileName));
                }
                catch (final RepositoryException e)
                {
                    throw new ServiceException(e);
                }
                filesToChange.remove(fileName);
            }
            else if (descriptiveMetadataMap.containsKey(fileName))
            {
                throw new ServiceException("found more than one file with the name " + fileName);
            }
        }
        if (filesToChange.size() > 0)
        {
            throw new ServiceException("did not find the files " + Arrays.toString(filesToChange.toArray()));
        }
    }

    private Map<String, String> getAllFiles(final DmoStoreId datasetStoreId) throws ServiceException
    {
        try
        {
            return Data.getFileStoreAccess().getAllFiles(datasetStoreId);
        }
        catch (final StoreAccessException e)
        {
            throw new ServiceException(e);
        }
    }
}
