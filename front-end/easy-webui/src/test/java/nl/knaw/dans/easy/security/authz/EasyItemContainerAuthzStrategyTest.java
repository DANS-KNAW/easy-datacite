package nl.knaw.dans.easy.security.authz;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.security.authz.AuthzMessage;
import nl.knaw.dans.easy.EasyUserTestImpl;
import nl.knaw.dans.easy.FileStoreMocker;
import nl.knaw.dans.easy.TestUtil;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FolderItem;
import nl.knaw.dans.easy.domain.model.PermissionSequence.State;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class EasyItemContainerAuthzStrategyTest {

    private FileStoreMocker fileStoreMocker;

    @Before
    public void initDB() throws Exception {
        fileStoreMocker = new FileStoreMocker();
    }

    @After
    public void reset() {
        TestUtil.cleanup();
    }

    @Test
    public void singleMessageTestYes() throws Exception {

        EasyUser user = mockUser();
        Dataset dataset = mockDataset();
        mockFileStoreAccess(dataset, AccessibleTo.ANONYMOUS);

        replayAll();

        AuthzMessage message = new EasyItemContainerAuthzStrategy(user, dataset, dataset).getSingleReadMessage();
        assertEquals("dataset.authzstrategy.sm.yes", message.getMessageCode());

        verifyAll();
    }

    @Test
    public void singleMessageTestLogin() throws Exception {

        EasyUser user = mockUser();
        Dataset dataset = mockDataset();
        mockFileStoreAccess(dataset, AccessibleTo.ANONYMOUS, AccessibleTo.KNOWN);

        replayAll();

        AuthzMessage message = new EasyItemContainerAuthzStrategy(user, dataset, dataset).getSingleReadMessage();
        assertEquals("dataset.authzstrategy.sm.login", message.getMessageCode());

        verifyAll();
    }

    private void mockFileStoreAccess(Dataset dataset, AccessibleTo... accessibleToArray) throws StoreAccessException, Exception {

        fileStoreMocker.insertRootFolder(dataset);
        FolderItem folder = fileStoreMocker.insertFolder(1, dataset, "folder1");
        int fileNr = 0;
        for (AccessibleTo accessibleTo : accessibleToArray)
            fileStoreMocker.insertFile(++fileNr, folder, "file" + fileNr, CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, accessibleTo);
        new Data().setFileStoreAccess(fileStoreMocker.getFileStoreAccess());
    }

    private Dataset mockDataset() throws Exception {
        final EasyUserImpl depositor = new EasyUserTestImpl("x:y");
        DmoStoreId datasetStoreId = new DmoStoreId(Dataset.NAMESPACE, "1");
        final DatasetImpl dataset = new DatasetImpl(datasetStoreId.toString());
        dataset.setState(State.Submitted.toString());
        dataset.setAuthzStrategy(new EasyItemContainerAuthzStrategy() {
            // need a subclass because the constructors are protected
            private static final long serialVersionUID = 1L;
        });

        // needed twice because considered dirty
        dataset.getAdministrativeMetadata().setDepositor(depositor);
        dataset.getAdministrativeMetadata().setDepositor(depositor);
        return dataset;
    }

    private EasyUser mockUser() {

        EasyUser user = createMock(EasyUser.class);
        expect(user.isAnonymous()).andStubReturn(true);
        expect(user.isActive()).andReturn(true);
        return user;
    }
}
