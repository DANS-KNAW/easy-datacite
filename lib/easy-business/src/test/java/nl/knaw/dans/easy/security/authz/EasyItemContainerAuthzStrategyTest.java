package nl.knaw.dans.easy.security.authz;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.security.authz.AuthzMessage;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.FileStoreAccess;
import nl.knaw.dans.easy.data.store.StoreAccessException;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.DatasetItemContainerMetadata;
import nl.knaw.dans.easy.domain.model.user.EasyUser;

import org.junit.Ignore;
import org.junit.Test;

public class EasyItemContainerAuthzStrategyTest {

    // @Test
    // @Ignore
    // public void singleMessageTestYes() throws Exception {
    //
    // fileStoreAccessExpectations(AccessibleTo.ANONYMOUS);
    //
    // Dataset dataset = mockDataset(mockItemContainerMetadata());
    // EasyUser user = mockUser();
    //
    // replayAll();
    //
    // EasyItemContainerAuthzStrategy strategy = new EasyItemContainerAuthzStrategy(user, dataset, dataset);
    // AuthzMessage message = strategy.getSingleReadMessage();
    // assertEquals("dataset.authzstrategy.sm.yes", message.getMessageCode());
    //
    // verifyAll();
    // }

    // @Test
    // @Ignore
    // public void singleMessageTestLogin() throws Exception {
    //
    // fileStoreAccessExpectations(AccessibleTo.ANONYMOUS, AccessibleTo.KNOWN);
    //
    // Dataset dataset = mockDataset(mockItemContainerMetadata());
    // EasyUser user = mockUser();
    //
    // replayAll();
    //
    // EasyItemContainerAuthzStrategy strategy = new EasyItemContainerAuthzStrategy(user, dataset, dataset);
    // AuthzMessage message = strategy.getSingleReadMessage();
    // assertEquals("dataset.authzstrategy.sm.login", message.getMessageCode());
    //
    // verifyAll();
    // }

    // private void fileStoreAccessExpectations(AccessibleTo... accessibleToArray) throws StoreAccessException {
    //
    // HashSet<AccessibleTo> accessibleToSet = new HashSet<AccessibleTo>(Arrays.asList(accessibleToArray));
    // FileStoreAccess fileStoreAccess = createMock(FileStoreAccess.class);
    // expect(fileStoreAccess.getValuesFor(isA(DmoStoreId.class), eq(AccessibleTo.class))).andStubReturn(accessibleToSet);
    // new Data().setFileStoreAccess(fileStoreAccess);
    // }

    private DatasetItemContainerMetadata mockItemContainerMetadata() {

        DatasetItemContainerMetadata icmd = createMock(DatasetItemContainerMetadata.class);
        expect(icmd.getDatasetDmoStoreId()).andReturn(new DmoStoreId("dataset:1"));
        return icmd;
    }

    private Dataset mockDataset(DatasetItemContainerMetadata itemContainerMetadata) {

        // constructor
        Dataset dataset = createMock(Dataset.class);
        expect(dataset.getDmoStoreId()).andStubReturn(new DmoStoreId("dataset:1"));
        expect(dataset.getDatasetItemContainerMetadata()).andReturn(itemContainerMetadata).times(1);

        // isEnableAllowed?
        expect(dataset.getAdministrativeState()).andReturn(DatasetState.PUBLISHED);
        expect(dataset.isUnderEmbargo()).andReturn(false);
        return dataset;
    }

    private EasyUser mockUser() {

        EasyUser user = createMock(EasyUser.class);
        expect(user.isAnonymous()).andStubReturn(true);
        expect(user.isActive()).andReturn(true);
        return user;
    }
}
