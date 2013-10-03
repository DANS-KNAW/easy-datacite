package nl.knaw.dans.easy.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.security.CodedAuthz;
import nl.knaw.dans.easy.security.ContextParameters;
import nl.knaw.dans.easy.security.SecurityOfficer;

import org.easymock.EasyMock;
import org.junit.BeforeClass;
import org.junit.Test;

public class RestoreDatasetRuleTest
{
    private static SecurityOfficer rule;
    private static Dataset dataset;
    private static EasyUser user;
    private static ContextParameters ctx;

    @BeforeClass
    public static void beforeClass()
    {
        rule = new CodedAuthz().getRestoreDatasetRule();
        dataset = EasyMock.createMock(Dataset.class);
        user = EasyMock.createMock(EasyUser.class);
        ctx = new ContextParameters(user, dataset);
    }

    @Test
    public void testProposition()
    {
        String proposition = "([SessionUser has role ADMIN] AND [Dataset state is DELETED])";
        assertEquals(proposition, rule.getProposition());
    }

    @Test
    public void test10()
    {
        EasyMock.reset(dataset, user);
        EasyMock.expect(user.isActive()).andReturn(true).times(2);
        EasyMock.expect(user.hasRole(Role.ADMIN)).andReturn(false).times(2);
        EasyMock.expect(user.isAnonymous()).andReturn(false).anyTimes();

        EasyMock.replay(dataset, user);
        assertFalse(rule.isEnableAllowed(ctx));
        assertFalse(rule.isComponentVisible(ctx));
        EasyMock.verify(dataset, user);
    }

    @Test
    public void test11()
    {
        EasyMock.reset(dataset, user);
        EasyMock.expect(user.isActive()).andReturn(true).times(2);
        EasyMock.expect(dataset.getAdministrativeState()).andReturn(DatasetState.DELETED).times(2);
        EasyMock.expect(user.hasRole(Role.ADMIN)).andReturn(true).times(2);
        EasyMock.expect(user.isAnonymous()).andReturn(false).anyTimes();

        EasyMock.replay(dataset, user);
        assertTrue(rule.isEnableAllowed(ctx));
        assertTrue(rule.isComponentVisible(ctx));
        EasyMock.verify(dataset, user);
    }

    @Test
    public void testNull()
    {
        ContextParameters ctxParameters = new ContextParameters();
        assertFalse(rule.isEnableAllowed(ctxParameters));
        assertFalse(rule.isComponentVisible(ctxParameters));
    }
}
