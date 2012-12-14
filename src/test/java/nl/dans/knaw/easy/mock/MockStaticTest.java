package nl.dans.knaw.easy.mock;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.EasyStore;
import nl.knaw.dans.easy.domain.model.Dataset;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {Data.class})
public class MockStaticTest
{
    // this code did not work when divided over the BusinessMocker and its helper classes
    // (while it was still a superclass for test classes)
    // it worked after replacing PowerMock.mockStatic by using the setters of the Data class

    private static EasyStore easyStoreMock;

    @Before
    public void reset()
    {
        // BusinessMocker.setUpMock
        PowerMock.mockStatic(Data.class);
        // BusinessMocker.setUpMock calling DatasetHelper.reset
        easyStoreMock = PowerMock.createMock(EasyStore.class);
        expect(Data.getEasyStore()).andStubReturn(easyStoreMock);
    }

    @Test
    public void test() throws Exception
    {
        final String storeId = "easy-dataset:1";

        final Dataset mockedDataset = mockDataset(storeId);

        // BusinessMocker.replayBusinessMocks
        PowerMock.replayAll();

        // BusinessMockerTest.easyStoreRetrieve
        final Dataset retrievedDataset = (Dataset) Data.getEasyStore().retrieve(new DmoStoreId(storeId));
        assertThat(retrievedDataset, sameInstance(mockedDataset));
    }

    private Dataset mockDataset(final String storeId) throws ObjectNotInStoreException, RepositoryException
    {
        // BusinessMocker.dataset calling DatasetHelper.constructor
        final DmoStoreId dmoStoreId = new DmoStoreId(storeId);
        final Dataset mockedDataset = PowerMock.createMock(Dataset.class);
        expect(easyStoreMock.retrieve(eq(dmoStoreId))).andReturn(mockedDataset).anyTimes();
        return mockedDataset;
    }
}
