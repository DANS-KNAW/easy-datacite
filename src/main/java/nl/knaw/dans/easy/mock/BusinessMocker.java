package nl.knaw.dans.easy.mock;

import java.util.LinkedList;
import java.util.List;

import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Provides starting points for a fluent interface to mock the business layer. Note that methods are
 * mocked on an as-needed bases. Trouble shooting:
 * <dl>
 * <dt>Unexpected method call</dt>
 * <dd>caused by not yet implemented expectations. Add the required expectations to this class and/or to
 * the helper classes.</dd>
 * <dt>last method called on mock already has a non-fixed count set</dt>
 * <dd>caused by duplicate configuration of similar expectations, like multiple sets of files for a
 * {@link Dataset} or multiple expectations for a property like {@link AccessibleTo}.</dd>
 * <dt>{@link NullPointerException} caused by {@link IllegalStateException} : calling verify is not allowed in
 * record state</dt>
 * <dd>please do call {@link #replayBusinessMocks}</dd>
 * <dt></dt>
 * <dd></dd>
 * </dl>
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest( {Data.class, Services.class})
public class BusinessMocker
{
    private List<Dataset> mockedDatasets;

    /**
     * Creates all mocks. Called by the {@link Runner}.
     * 
     * @throws Exception
     */
    @Before
    public void setUpMock() throws Exception
    {
        mockedDatasets = new LinkedList<Dataset>();

        PowerMock.mockStatic(Data.class);
        PowerMock.mockStatic(Services.class);

        UserHelper.reset();
        FileHelper.reset();
        DatasetHelper.reset();
    }

    /**
     * Asserts that methods were called as expected. Called by the {@link Runner}.
     * 
     * @throws Exception
     */
    @After
    public void verifyBusinessMocks()
    {
        PowerMock.verifyAll();
    }

    /**
     * Switches the mocked objects and classes to replay mode. Note that you must use this method after
     * specifying your expectations but before executing the test.
     */
    protected void replayBusinessMocks()
    {
        PowerMock.replayAll();
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
     */
    protected DatasetHelper dataset(final String datasetId)
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
