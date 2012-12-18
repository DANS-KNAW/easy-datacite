package nl.dans.knaw.easy.mock.demo;

import java.io.InputStream;

import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.EasyStore;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.mock.BusinessMocker;
import nl.knaw.dans.easy.servicelayer.services.Services;

/**
 * This is an example of a class using easy-business classes. Its purpose to show the usage of the
 * {@link BusinessMocker}.
 */
class ClassUnderTest
{
    /**
     * Removes files from a dataset. The cleanup is performed twice to illustrate the difference between
     * the stubs expectations of the "with" methods of the mockers and the explicit expectations. As the
     * cleanup should change the files belonging to a dataset, {@link EasyStore#retrieve(DmoStoreId)}
     * should return less files in the second iteration.
     * 
     * @param datasetId
     *        the dataset to clean
     * @param pattern
     *        files with a path that match this pattern are removed
     */
    static void cleanUp(final String datasetId, final String pattern) throws Exception
    {
        for (int i = 0; i < 2; i++)
        {
            for (final FileItemVO fileItemVO : Data.getFileStoreAccess().getDatasetFiles(new DmoStoreId(datasetId)))
            {
                if (fileItemVO.getPath().toLowerCase().matches(pattern))
                {
                    final DataModelObject fileItem = Data.getEasyStore().retrieve(new DmoStoreId(fileItemVO.getSid()));
                    Data.getEasyStore().purge(fileItem, true, " purged ");
                }
            }
        }
    }

    /**
     * Creates on inputStream from a file of a dataset.
     * 
     * @param userId
     *        the ID of an {@link EasyUser}
     * @param datasetId
     *        the {@link DmoStoreId} of a {@link Dataset}
     * @param fileId
     *        the {@link DmoStoreId} of a {@link FileItem}
     * @return
     * @throws Exception
     */
    static InputStream openFile(final String userId, final String datasetId, final String fileId) throws Exception
    {
        final Dataset dataset = (Dataset) Data.getEasyStore().retrieve(new DmoStoreId(datasetId));
        final FileItem fileItem = (FileItem) Data.getEasyStore().retrieve(new DmoStoreId(fileId));
        final EasyUser user = Data.getUserRepo().findById(userId);
        return Services.getItemService().getFileContentURL(user, dataset, fileItem).openConnection().getInputStream();
    }
}
