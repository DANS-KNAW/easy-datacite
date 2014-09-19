package nl.knaw.dans.easy.mock;

import static org.easymock.EasyMock.expect;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreEventListener;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.migration.MigrationRepo;
import nl.knaw.dans.easy.data.store.EasyStore;
import nl.knaw.dans.easy.data.store.FileStoreAccess;
import nl.knaw.dans.easy.data.userrepo.EasyUserRepo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FolderItem;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.servicelayer.services.ItemService;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Provides a fluent interface to mock a static configuration of repository objects. See demo test classes for usage examples and troubleshooting.
 */
public class BusinessMocker {
    private List<Dataset> mockedDatasets;
    private StoreIdGenerator storeIdGenerator = new StoreIdGenerator();

    /**
     * Initializes mocked services. Typically called by a {@link Test} method or a {@link Before} method of a {@link PowerMockRunner} class. Call
     * {@link PowerMock#replayAll(Object...) } between configuring the desired mocked objects and executing your test. After completion of the test you may want
     * to call {@link PowerMock#verifyAll(Object...) }.
     * 
     * @throws Exception
     */
    public BusinessMocker() throws Exception {
        mockedDatasets = new LinkedList<Dataset>();

        // TRIED PowerMock.mockStatic(Data.class);
        // before revision 12014 while this class was still a superclass for test classes
        // it only worked when everything was reduced into a single class
        // using setters did worked with expectations in the individual classes

        new Services().setItemService(PowerMock.createMock(ItemService.class));
        new Services().setDatasetService(PowerMock.createMock(DatasetService.class));
        new Data().setUserRepo(PowerMock.createMock(EasyUserRepo.class));
        new Data().setEasyStore(PowerMock.createMock(EasyStore.class));
        new Data().setFileStoreAccess(PowerMock.createMock(FileStoreAccess.class));
        new Data().setMigrationRepo(PowerMock.createMock(MigrationRepo.class));

        expect(Data.getEasyStore().getListeners()).andStubReturn(new ArrayList<DmoStoreEventListener>());
    }

    /**
     * Creates an object to configure possible/expected behavior of the mocked services related to a user.
     * 
     * @param userId
     * @throws Exception
     */
    public UserMocker user(final String userId) throws Exception {
        return new UserMocker(userId);
    }

    /**
     * Creates an object to configure possible/expected behavior of a mocked {@link Dataset} and related services.
     * 
     * @param datasetId
     * @return
     * @throws Exception
     */
    public DatasetMocker dataset(final String datasetId) throws Exception {
        final DatasetMocker datasetMocker = new DatasetMocker(datasetId, storeIdGenerator);
        mockedDatasets.add(datasetMocker.getDataset());
        return datasetMocker;
    }

    /**
     * Creates an object to configure possible/expected behavior of the mocked services related to a file. The file gets a generated {@link DmoStoreId}.
     * 
     * @param path
     *        of the the file within the dataset
     * @return
     * @throws Exception
     */
    public FileMocker file(final String path) throws Exception {
        return new FileMocker(path, storeIdGenerator.getNext(FileItem.NAMESPACE));
    }

    /**
     * Creates an object to configure possible/expected behavior of the mocked services related to a folder. The folder gets a generated {@link DmoStoreId}.
     * 
     * @param path
     *        of the the folder within the dataset
     * @return
     * @throws Exception
     */
    public FolderMocker folder(final String path) throws Exception {
        return new FolderMocker(path, storeIdGenerator.getNext(FolderItem.NAMESPACE));
    }

    /**
     * Creates an object to configure possible/expected behavior of the mocked services related to a file.
     * 
     * @param path
     *        of the the file within the dataset
     * @param dmoStoreId
     *        use the result of {@link #nextDmoStoreId(DmoNamespace)} with {@link FileItem#NAMESPACE} to avoid ID clashes with files created with
     *        {@link #file(String)}.
     * @return
     * @throws Exception
     */
    public FileMocker file(final String path, final String dmoStoreId) throws Exception {
        return new FileMocker(path, dmoStoreId);
    }

    /**
     * Creates an object to configure possible/expected behavior of the mocked services related to a folder.
     * 
     * @param path
     *        of the the folder within the dataset
     * @param dmoStoreId
     *        use the result of {@link #nextDmoStoreId(DmoNamespace)} with {@link FolderItem#NAMESPACE} to avoid ID clashes with folders created by
     *        {@link #folder(String)} or by folders implicitly created by {@link DatasetMocker#with(FileMocker...)}.
     * @return
     * @throws Exception
     */
    public FolderMocker folder(final String path, final String dmoStoreId) throws Exception {
        return new FolderMocker(path, dmoStoreId);
    }

    public String nextDmoStoreId(final DmoNamespace dmoNamespace) {
        return storeIdGenerator.getNext(dmoNamespace);
    }

    /**
     * @return the mocked dataset objects.
     */
    public List<Dataset> getDatasets() {
        return mockedDatasets;
    }
}
