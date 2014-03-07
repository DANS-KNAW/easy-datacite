package nl.knaw.dans.easy.security;

import static org.junit.Assert.*;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.security.CodedAuthz;
import nl.knaw.dans.easy.security.ContextParameters;
import nl.knaw.dans.easy.security.SecurityOfficer;

import org.easymock.EasyMock;
import org.junit.BeforeClass;
import org.junit.Test;

public class VisibleToArchivistEnableToAdminRuleTest
{

    private static SecurityOfficer rule;
    private static EasyUser user;
    private static ContextParameters ctx;

    @BeforeClass
    public static void beforeClass()
    {
        rule = new CodedAuthz().getVisibleToArchivistEnableToAdminRule();
        user = EasyMock.createMock(EasyUser.class);
        ctx = new ContextParameters(user);
    }

    @Test
    public void testProposition()
    {
        String proposition = "Split answer: ComponentVisisble <== [SessionUser has role ARCHIVIST or ADMIN] EnableAllowed <== [SessionUser has role ADMIN]";
        assertEquals(proposition, rule.getProposition());
    }

    @Test
    public void testUser()
    {
        EasyMock.reset(user);
        EasyMock.expect(user.isActive()).andReturn(true).times(2);
        EasyMock.expect(user.hasRole(Role.ARCHIVIST, Role.ADMIN)).andReturn(false).times(1);
        EasyMock.expect(user.hasRole(Role.ADMIN)).andReturn(false).times(1);
        EasyMock.expect(user.isAnonymous()).andReturn(false).anyTimes();

        EasyMock.replay(user);
        assertFalse(rule.isComponentVisible(ctx));
        assertFalse(rule.isEnableAllowed(ctx));
        EasyMock.verify(user);
    }

    @Test
    public void testArchivist()
    {
        EasyMock.reset(user);
        EasyMock.expect(user.isActive()).andReturn(true).times(2);
        EasyMock.expect(user.hasRole(Role.ARCHIVIST, Role.ADMIN)).andReturn(true).times(1);
        EasyMock.expect(user.hasRole(Role.ADMIN)).andReturn(false).times(1);
        EasyMock.expect(user.isAnonymous()).andReturn(false).anyTimes();

        EasyMock.replay(user);
        assertTrue(rule.isComponentVisible(ctx));
        assertFalse(rule.isEnableAllowed(ctx));
        EasyMock.verify(user);
    }

    @Test
    public void testAdmin()
    {
        EasyMock.reset(user);
        EasyMock.expect(user.isActive()).andReturn(true).times(2);
        EasyMock.expect(user.hasRole(Role.ARCHIVIST, Role.ADMIN)).andReturn(true).times(1);
        EasyMock.expect(user.hasRole(Role.ADMIN)).andReturn(true).times(1);
        EasyMock.expect(user.isAnonymous()).andReturn(false).anyTimes();

        EasyMock.replay(user);
        assertTrue(rule.isComponentVisible(ctx));
        assertTrue(rule.isEnableAllowed(ctx));
        EasyMock.verify(user);
    }

    @Test
    public void testNull()
    {
        ContextParameters ctxParameters = new ContextParameters();
        assertFalse(rule.isEnableAllowed(ctxParameters));
        assertFalse(rule.isComponentVisible(ctxParameters));
    }

}
