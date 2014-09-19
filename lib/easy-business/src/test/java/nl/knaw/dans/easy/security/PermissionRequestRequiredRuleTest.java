package nl.knaw.dans.easy.security;

import static org.easymock.EasyMock.eq;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.user.User.State;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.FileStoreAccess;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserAnonymous;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;

import org.easymock.EasyMock;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

public class PermissionRequestRequiredRuleTest {
    @AfterClass
    public static void afterClass() {
        // the next test class should not inherit from this one
        new Data().setFileStoreAccess(null);
    }

    @Test
    public void noPermissionRequired() throws Exception {
        check(mockUser(), false, false);
    }

    @Test
    public void noPermissionRequired2() throws Exception {
        check(mockArchivist(), false, false);
    }

    @Test
    public void noPermissionRequired3() throws Exception {
        check(mockAdmin(), false, false);
    }

    @Test
    public void user() throws Exception {
        check(mockUser(), true, true);
    }

    @Test
    public void archivist() throws Exception {
        check(mockArchivist(), true, false);
    }

    @Test
    public void admin() throws Exception {
        check(mockAdmin(), true, false);
    }

    private void check(final EasyUser user, boolean datasetHasRestrictedFiles, final boolean warnThatPermissionIsRequired) throws Exception {
        if (user != EasyUserAnonymous.getInstance())
            user.setState(State.ACTIVE);

        DmoStoreId dmoStoreId = new DmoStoreId(Dataset.NAMESPACE, "1");
        Dataset dataset = EasyMock.createMock(Dataset.class);
        EasyMock.expect(dataset.getDmoStoreId()).andStubReturn(dmoStoreId);
        EasyMock.expect(dataset.getStoreId()).andStubReturn(dmoStoreId.getStoreId());
        EasyMock.expect(dataset.getOwnerId()).andStubReturn("easy-user:owner");
        EasyMock.expect(dataset.hasDepositor(user)).andStubReturn(false);

        FileStoreAccess fileStoreAccess = EasyMock.createMock(FileStoreAccess.class);
        EasyMock.expect(fileStoreAccess.hasMember(eq(dmoStoreId), eq(FileItemVO.class), eq(AccessibleTo.RESTRICTED_REQUEST))).andStubReturn(
                datasetHasRestrictedFiles);
        new Data().setFileStoreAccess(fileStoreAccess);
        final SecurityOfficer rule = new CodedAuthz().getPermissionRequestRequiredRule();
        final ContextParameters parameters = new ContextParameters(user, dataset);

        EasyMock.replay(dataset, fileStoreAccess);

        Assert.assertTrue(rule.explainComponentVisible(parameters), rule.isComponentVisible(parameters) == warnThatPermissionIsRequired);

        EasyMock.verify(dataset, Data.getFileStoreAccess());
    }

    private EasyUser mockArchivist() {
        final EasyUser requester = new EasyUserImpl("archivist");
        requester.addRole(EasyUser.Role.ARCHIVIST);
        return requester;
    }

    private EasyUser mockAdmin() {
        final EasyUser requester = new EasyUserImpl("admin");
        requester.addRole(EasyUser.Role.ADMIN);
        return requester;
    }

    private EasyUser mockUser() {
        final EasyUser requester = new EasyUserImpl("user");
        requester.addRole(EasyUser.Role.USER);
        return requester;
    }

    @Test
    public void noPermissionRequired4() throws Exception {
        check(EasyUserAnonymous.getInstance(), false, false);
    }

    @Test
    public void anonymous() throws Exception {
        check(EasyUserAnonymous.getInstance(), true, true);
    }
}
