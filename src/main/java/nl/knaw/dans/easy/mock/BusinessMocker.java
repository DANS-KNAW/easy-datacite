package nl.knaw.dans.easy.mock;

import static org.easymock.EasyMock.expect;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import nl.knaw.dans.common.lang.repo.DmoStoreEventListener;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.migration.MigrationRepo;
import nl.knaw.dans.easy.data.store.EasyStore;
import nl.knaw.dans.easy.data.store.FileStoreAccess;
import nl.knaw.dans.easy.data.userrepo.EasyUserRepo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.servicelayer.services.ItemService;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.junit.After;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Provides a fluent interface to mock a static configuration of repository objects. See test classes for
 * usage examples and troubleshooting.
 */
public class BusinessMocker
{
    private List<Dataset> mockedDatasets;
    private int fileCounter;

    /**
     * Creates mocked services. Typically called by a {@link Test} method of a {@link PowerMockRunner} class.
     * class.
     * 
     * @throws Exception
     */
    public BusinessMocker() throws Exception
    {
        mockedDatasets = new LinkedList<Dataset>();

        Data.unlock();
        Services.unlock();

        new Services().setItemService(PowerMock.createMock(ItemService.class));
        new Data().setUserRepo(PowerMock.createMock(EasyUserRepo.class));
        new Data().setEasyStore(PowerMock.createMock(EasyStore.class));
        new Data().setFileStoreAccess(PowerMock.createMock(FileStoreAccess.class));
        new Data().setMigrationRepo(PowerMock.createMock(MigrationRepo.class));

        expect(Data.getEasyStore().getListeners()).andReturn(new ArrayList<DmoStoreEventListener>()).anyTimes();
    }

    /**
     * Asserts that methods were called as expected. Typically to be called by a {@link After} method of
     * a {@link PowerMockRunner} class.
     * 
     * @throws Exception
     */
    public void verifyAll()
    {
        PowerMock.verifyAll();
        UserHelper.verifyAll();
        FileHelper.verifyAll();
        DatasetHelper.verifyAll();
    }

    /**
     * Switches the mocked objects and classes to replay mode. Note that you must call this method after
     * specifying your expectations but before executing the test.
     */
    public void replayAll()
    {
        PowerMock.replayAll();
        UserHelper.replayAll();
        FileHelper.replayAll();
        DatasetHelper.replayAll();
    }

    /**
     * Creates an object to configure possible/expected behavior related to a user.
     * 
     * @param userId
     * @throws Exception
     */
    public UserHelper user(final String userId) throws Exception
    {
        return new UserHelper(userId);
    }

    /**
     * Creates an object to configure possible/expected behavior related to a dataset in the repository.
     * 
     * @param datasetId
     * @return
     * @throws Exception
     */
    public DatasetHelper dataset(final String datasetId) throws Exception
    {
        final DatasetHelper datasetHelper = new DatasetHelper(datasetId);
        mockedDatasets.add(datasetHelper.getDataset());
        return datasetHelper;
    }

    /**
     * Creates an object to configure possible/expected behavior related to a file in the repository.
     * 
     * @param path
     * @return
     * @throws Exception
     */
    public FileHelper file(final String path) throws Exception
    {
        return new FileHelper(path, ++fileCounter);
    }

    /**
     * @return the mocked dataset objects.
     */
    public List<Dataset> getDatasets()
    {
        return mockedDatasets;
    }
}
