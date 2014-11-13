package nl.dans.knaw.easy.mock.demo;

import java.net.URL;
import java.util.List;

import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.FileStoreAccess;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.mock.BusinessMocker;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.apache.commons.io.IOUtils;

/**
 * This is an example of a class using easy-business classes. Its purpose to show the usage of the {@link BusinessMocker}.
 */
class ClassUnderTest {
    /**
     * Removes files from a dataset.
     * 
     * @param datasetId
     *        the dataset to clean
     * @param pattern
     *        files with a path that match this pattern are removed
     */
    static void cleanUp(final String datasetId, final String pattern) throws Exception {
        for (final FileItemVO fileItemVO : Data.getFileStoreAccess().getDatasetFiles(new DmoStoreId(datasetId))) {
            if (fileItemVO.getPath().toLowerCase().matches(pattern)) {
                final DataModelObject fileItem = Data.getEasyStore().retrieve(new DmoStoreId(fileItemVO.getSid()));
                Data.getEasyStore().purge(fileItem, true, " purged ");
            }
        }
    }

    /**
     * Reads the content a file of a dataset.
     * 
     * @param userId
     *        the ID of an {@link EasyUser}
     * @param datasetId
     *        the {@link DmoStoreId} of a {@link Dataset}
     * @param fileId
     *        the {@link DmoStoreId} of a {@link FileItem}
     * @return the content of the file
     * @throws Exception
     */
    static String readFile(final String userId, final String datasetId, final String fileId) throws Exception {
        final Dataset dataset = (Dataset) Data.getEasyStore().retrieve(new DmoStoreId(datasetId));
        final FileItem fileItem = (FileItem) Data.getEasyStore().retrieve(new DmoStoreId(fileId));
        final EasyUser user = Data.getUserRepo().findById(userId);
        final URL fileContentURL = Services.getItemService().getFileContentURL(user, dataset, fileItem);
        return IOUtils.toString(fileContentURL.openConnection().getInputStream(), "UTF-8");
    }

    static int getNrOfFilesAndFolders(final String storeId) throws StoreAccessException {
        final FileStoreAccess fsa = Data.getFileStoreAccess();
        final List<ItemVO> filesAndFolders = fsa.getFilesAndFolders(new DmoStoreId(storeId));
        return filesAndFolders.size();
    }
}
