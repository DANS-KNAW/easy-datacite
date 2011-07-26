package nl.knaw.dans.easy.security;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.easy.data.store.DummyFileStoreAccess;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.security.CodedAuthz;
import nl.knaw.dans.easy.security.ContextParameters;
import nl.knaw.dans.easy.security.SecurityOfficer;

import org.easymock.EasyMock;
import org.junit.Ignore;
import org.junit.Test;

public class PermissionRequestRequiredRuleTest
{
    @Test
    public void noPermissionRequired()
    {
        check(datasetWithOpenAccess(), mockUser(), false);
    }
    
    @Test
    public void noPermissionRequired2()
    {
        check(datasetWithOpenAccess(), mockArchivist(), false);
    }
    
    @Test
    public void noPermissionRequired3()
    {
        check(datasetWithOpenAccess(), mockAdmin(), false);
    }

    @Test
    public void user()
    {
        Dataset dataset = datasetThatRequiresPermission();
        check(dataset, mockUser(), true);
        EasyMock.verify(dataset);
    }

    @Ignore
    @Test
    public void archivist()
    {
        Dataset dataset = datasetThatRequiresPermission();
        check(dataset, mockArchivist(), false);
        EasyMock.verify(dataset);
    }
    
    @Ignore
    @Test
    public void admin()
    {
        Dataset dataset = datasetThatRequiresPermission();
        check(dataset, mockAdmin(), false);
        EasyMock.verify(dataset);
    }

    private void check(final Dataset dataset, final EasyUser user, final boolean permissionRequired)
    {
        final SecurityOfficer rule = new CodedAuthz().getPermissionRequestRequiredRule();
        final ContextParameters parameters = new ContextParameters(user, dataset);
        final boolean result = rule.isComponentVisible(parameters);
        assertThat(result, is(permissionRequired));
    }

    private Dataset datasetWithOpenAccess()
    {
        final DatasetImpl dataset = new DatasetImpl(DummyFileStoreAccess.DUMMY_DATASET_SID);
        assertThat(dataset.getAccessCategory(), is(AccessCategory.OPEN_ACCESS));
        return dataset;
    }

    private Dataset datasetThatRequiresPermission()
    {
        Dataset dataset = EasyMock.createMock(Dataset.class);
        EasyMock.expect(dataset.hasPermissionRestrictedItems()).andReturn(true);
        EasyMock.replay(dataset);
        return dataset;
    }

    private EasyUser mockArchivist()
    {
        final EasyUser requester = new EasyUserImpl("archivist");
        requester.addRole(EasyUser.Role.ARCHIVIST);
        return requester;
    }

    private EasyUser mockAdmin()
    {
        final EasyUser requester = new EasyUserImpl("admin");
        requester.addRole(EasyUser.Role.ADMIN);
        return requester;
    }

    private EasyUser mockUser()
    {
        final EasyUser requester = new EasyUserImpl("user");
        requester.addRole(EasyUser.Role.USER);
        return requester;
    }
}
