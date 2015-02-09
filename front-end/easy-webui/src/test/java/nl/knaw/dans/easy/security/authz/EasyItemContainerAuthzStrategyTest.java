package nl.knaw.dans.easy.security.authz;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.security.authz.AuthzMessage;
import nl.knaw.dans.easy.FileStoreMocker;
import nl.knaw.dans.easy.TestUtil;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.DatasetItemContainerMetadata;
import nl.knaw.dans.easy.domain.model.FolderItem;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore(value = "expectation does not work, see TODO")
public class EasyItemContainerAuthzStrategyTest {

    private FileStoreMocker fileStoreMocker;
    private Dataset dataset;
    private FolderItem folder;

    @Before
    public void initDB() throws Exception {
        mockDataset(mockItemContainerMetadata());
        fileStoreMocker = new FileStoreMocker();
        fileStoreMocker.insertRootFolder(dataset);
        folder = fileStoreMocker.insertFolder(1, dataset, "folder1");
        new Data().setFileStoreAccess(fileStoreMocker.getFileStoreAccess());
    }

    @After
    public void reset() throws Exception {
        fileStoreMocker.close();
        TestUtil.cleanup();
    }

    @Test
    public void singleMessageTestYes() throws Exception {

        // fileStoreAccessExpectations(AccessibleTo.ANONYMOUS);
        fileStoreMocker.insertFile(1, folder, "file1", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.ANONYMOUS);

        EasyUser user = mockUser();

        replayAll();

        EasyItemContainerAuthzStrategy strategy = new EasyItemContainerAuthzStrategy(user, dataset, dataset);
        AuthzMessage message = strategy.getSingleReadMessage();
        assertEquals("dataset.authzstrategy.sm.yes", message.getMessageCode());
    }

    @Test
    public void singleMessageTestLogin() throws Exception {

        // fileStoreAccessExpectations(AccessibleTo.ANONYMOUS, AccessibleTo.KNOWN);
        fileStoreMocker.insertFile(1, folder, "file1", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.ANONYMOUS);
        fileStoreMocker.insertFile(2, folder, "file2", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.KNOWN);

        EasyUser user = mockUser();

        replayAll();

        EasyItemContainerAuthzStrategy strategy = new EasyItemContainerAuthzStrategy(user, dataset, dataset);
        AuthzMessage message = strategy.getSingleReadMessage();
        assertEquals("dataset.authzstrategy.sm.login", message.getMessageCode());
    }

    private DatasetItemContainerMetadata mockItemContainerMetadata() {

        DatasetItemContainerMetadata icmd = createMock(DatasetItemContainerMetadata.class);
        expect(icmd.getDatasetDmoStoreId()).andReturn(new DmoStoreId("dataset:1"));
        return icmd;
    }

    private void mockDataset(DatasetItemContainerMetadata itemContainerMetadata) {

        // constructor
        dataset = createMock(Dataset.class);
        expect(dataset.getDmoStoreId()).andStubReturn(new DmoStoreId("dataset:1"));
        expect(dataset.getStoreId()).andStubReturn("dataset:1"); // TODO why doesn't this fix exception: missing behavior Dataset.getStoreID()
        expect(dataset.getDatasetItemContainerMetadata()).andReturn(itemContainerMetadata).times(1);

        // isEnableAllowed?
        expect(dataset.getAdministrativeState()).andReturn(DatasetState.PUBLISHED);
        expect(dataset.isUnderEmbargo()).andReturn(false);
    }

    private EasyUser mockUser() {

        EasyUser user = createMock(EasyUser.class);
        expect(user.isAnonymous()).andStubReturn(true);
        expect(user.isActive()).andReturn(true);
        return user;
    }
}
