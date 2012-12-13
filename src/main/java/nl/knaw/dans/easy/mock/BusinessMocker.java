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
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.servicelayer.services.ItemService;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Provides a fluent interface to mock a static configuration of repository objects. Note that methods
 * are mocked on an as-needed bases. Unless specified otherwise, implemented methods are expected to be
 * called any times. Trouble shooting hints:
 * <dl>
 * <dt>Unexpected method call</dt>
 * <dd>caused by not yet implemented expectations. Add the required expectations to thisto the helper
 * classes.</dd>
 * <dt>last method called on mock already has a non-fixed count set</dt>
 * <dd>caused by duplicate configuration of similar expectations, like multiple sets of files for a
 * {@link Dataset} or multiple expectations for a property like {@link AccessibleTo}.</dd>
 * <dt>{@link NullPointerException} caused by {@link IllegalStateException} : calling verify is not
 * allowed in record state</dt>
 * <dd>please do call {@link #replayBusinessMocks}</dd>
 * <dt></dt>
 * <dd></dd>
 * </dl>
 */
@RunWith(PowerMockRunner.class)
public class BusinessMocker
{
    private List<Dataset> mockedDatasets;

    /**
     * Creates mocked services. Called by the {@link PowerMockRunner}.
     * 
     * @throws Exception
     */
    @Before
    public void setUpMock() throws Exception
    {
        mockedDatasets = new LinkedList<Dataset>();

        Data.unlock();
        Services.unlock();

        new Services().setItemService(PowerMock.createMock(ItemService.class));
        new Data().setUserRepo(PowerMock.createMock(EasyUserRepo.class));
        new Data().setEasyStore(PowerMock.createMock(EasyStore.class));
        new Data().setFileStoreAccess(PowerMock.createMock(FileStoreAccess.class));
        new Data().setMigrationRepo(PowerMock.createMock(MigrationRepo.class));

        FileHelper.reset();
        DatasetHelper.reset();
        UserHelper.reset();

        expect(Data.getEasyStore().getListeners()).andReturn(new ArrayList<DmoStoreEventListener>()).anyTimes();
    }

    /**
     * Asserts that methods were called as expected. Called by the {@link PowerMockRunner}.
     * 
     * @throws Exception
     */
    @After
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
    protected void replayAll()
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
    protected UserHelper user(final String userId) throws Exception
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
    protected DatasetHelper dataset(final String datasetId) throws Exception
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
    protected FileHelper file(final String path) throws Exception
    {
        return new FileHelper(path);
    }

    /**
     * @return the mocked dataset objects.
     */
    protected List<Dataset> getDatasets()
    {
        return mockedDatasets;
    }
}
