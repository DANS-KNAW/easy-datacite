package nl.knaw.dans.easy.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.security.CodedAuthz;
import nl.knaw.dans.easy.security.ContextParameters;
import nl.knaw.dans.easy.security.SecurityOfficer;

import org.easymock.EasyMock;
import org.junit.BeforeClass;
import org.junit.Test;

public class EnableToLoggedInUserRuleTest
{
    private static SecurityOfficer rule;
    private static EasyUser user;
    private static ContextParameters ctx;
    
    @BeforeClass
    public static void beforeClass()
    {
        rule = new CodedAuthz().getEnableToLoggedInUserRule();
        user = EasyMock.createMock(EasyUser.class);
        ctx = new ContextParameters(user);
    }
    
    @Test
    public void testProposition()
    {
        String proposition = "[SessionUser has role USER or ARCHIVIST or ADMIN]";
        assertEquals(proposition, rule.getProposition());
    }
    
    @Test
    public void test000()
    {
        EasyMock.reset(user);
        EasyMock.expect(user.isActive()).andReturn(true).times(2);
        EasyMock.expect(user.hasRole(Role.USER, Role.ARCHIVIST, Role.ADMIN)).andReturn(false).times(2);
        EasyMock.expect(user.isAnonymous()).andReturn(false).anyTimes();
        
        EasyMock.replay(user);
        assertFalse(rule.isEnableAllowed(ctx));
        assertFalse(rule.isComponentVisible(ctx));
        EasyMock.verify(user);
    }
    
    @Test
    public void test001()
    {
        EasyMock.reset(user);
        EasyMock.expect(user.isActive()).andReturn(true).times(2);
        EasyMock.expect(user.hasRole(Role.USER, Role.ARCHIVIST, Role.ADMIN)).andReturn(true).times(2);
        EasyMock.expect(user.isAnonymous()).andReturn(false).anyTimes();
        
        EasyMock.replay(user);
        assertTrue(rule.isEnableAllowed(ctx));
        assertTrue(rule.isComponentVisible(ctx));
        EasyMock.verify(user);
    }
    
    @Test
    public void test1xx()
    {
        EasyMock.reset(user);
        EasyMock.expect(user.isActive()).andReturn(true).times(2);
        EasyMock.expect(user.hasRole(Role.USER, Role.ARCHIVIST, Role.ADMIN)).andReturn(true).times(2);
        EasyMock.expect(user.isAnonymous()).andReturn(false).anyTimes();
        
        EasyMock.replay(user);
        assertTrue(rule.isEnableAllowed(ctx));
        assertTrue(rule.isComponentVisible(ctx));
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
