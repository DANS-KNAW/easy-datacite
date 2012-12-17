package nl.dans.knaw.easy.mock.demo;

import java.io.InputStream;

import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.Services;

class ClassUnderTest
{
    static void purgeTiffFiles(final String datasetId) throws Exception
    {
        for (final FileItemVO fileItemVO : Data.getFileStoreAccess().getDatasetFiles(new DmoStoreId(datasetId)))
        {
            if (fileItemVO.getPath().toLowerCase().matches("(.*/)?tiff?/[^/]*[.]tif"))
            {
                final DataModelObject fileItem = Data.getEasyStore().retrieve(new DmoStoreId(fileItemVO.getSid()));
                Data.getEasyStore().purge(fileItem, true, " purged ");
            }
        }
    }

    static InputStream openFile(final String userId, final String datasetId, final String fileId) throws Exception
    {
        final Dataset dataset = (Dataset) Data.getEasyStore().retrieve(new DmoStoreId(datasetId));
        final FileItem fileItem = (FileItem) Data.getEasyStore().retrieve(new DmoStoreId(fileId));
        final EasyUser user = Data.getUserRepo().findById(userId);
        return Services.getItemService().getFileContentURL(user, dataset, fileItem).openConnection().getInputStream();
    }
}
