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

public class EditProtectedUserAttributesRuleTest
{
    
    
    private static SecurityOfficer rule;
    private static EasyUser sessionUser;
    private static EasyUser userUnderEdit;
    private static ContextParameters ctx;
    
    @BeforeClass
    public static void beforeClass()
    {
        rule = new CodedAuthz().getEditProtectedUserAttributesRule();
        sessionUser = EasyMock.createMock(EasyUser.class);
        userUnderEdit = EasyMock.createMock(EasyUser.class);
        ctx = new ContextParameters(sessionUser, userUnderEdit);
    }
    
    @Test
    public void testProposition()
    {
        String proposition = "Split answer: ComponentVisisble <== [SessionUser has role ARCHIVIST or ADMIN] EnableAllowed <== ([SessionUser has role ADMIN] AND NOT([SessionUser is user under edit]))";
        assertEquals(proposition, rule.getProposition());
    }
    
    @Test
    public void testUser()
    {
        EasyMock.reset(sessionUser, userUnderEdit);
        EasyMock.expect(sessionUser.isActive()).andReturn(true).times(2);
        // visible
        EasyMock.expect(sessionUser.hasRole(Role.ARCHIVIST, Role.ADMIN)).andReturn(false).times(1);
        // enabled
        EasyMock.expect(sessionUser.hasRole(Role.ADMIN)).andReturn(false).times(1);
        // not anonymous
        EasyMock.expect(sessionUser.isAnonymous()).andReturn(false).anyTimes();
        
        EasyMock.replay(sessionUser, userUnderEdit);
        assertFalse(rule.isComponentVisible(ctx));
        assertFalse(rule.isEnableAllowed(ctx));
        EasyMock.verify(sessionUser, userUnderEdit); 
    }
    
    @Test
    public void testArchivist()
    {
        EasyMock.reset(sessionUser, userUnderEdit);
        EasyMock.expect(sessionUser.isActive()).andReturn(true).times(2);
        // visible
        EasyMock.expect(sessionUser.hasRole(Role.ARCHIVIST, Role.ADMIN)).andReturn(true).times(1);
        // enabled
        EasyMock.expect(sessionUser.hasRole(Role.ADMIN)).andReturn(false).times(1);
        // not anonymous
        EasyMock.expect(sessionUser.isAnonymous()).andReturn(false).anyTimes();
        
        EasyMock.replay(sessionUser, userUnderEdit);
        assertTrue(rule.isComponentVisible(ctx));
        assertFalse(rule.isEnableAllowed(ctx));
        EasyMock.verify(sessionUser, userUnderEdit); 
    }
    
    @Test
    public void testAdminNotSelf()
    {
        EasyMock.reset(sessionUser, userUnderEdit);
        EasyMock.expect(sessionUser.isActive()).andReturn(true).times(2);
        // visible
        EasyMock.expect(sessionUser.hasRole(Role.ARCHIVIST, Role.ADMIN)).andReturn(true).times(1);
        // enabled
        EasyMock.expect(sessionUser.hasRole(Role.ADMIN)).andReturn(true).times(1);
        
        EasyMock.expect(sessionUser.isAnonymous()).andReturn(false).anyTimes();
        EasyMock.expect(sessionUser.getId()).andReturn("aleph").times(1);
        EasyMock.expect(userUnderEdit.getId()).andReturn("beth").times(1);
        
        EasyMock.replay(sessionUser, userUnderEdit);
        assertTrue(rule.isComponentVisible(ctx));
        assertTrue(rule.isEnableAllowed(ctx));
        EasyMock.verify(sessionUser, userUnderEdit); 
    }
    
    @Test
    public void testAdminButSelf()
    {
        EasyMock.reset(sessionUser, userUnderEdit);
        EasyMock.expect(sessionUser.isActive()).andReturn(true).times(2);
        // visible
        EasyMock.expect(sessionUser.hasRole(Role.ARCHIVIST, Role.ADMIN)).andReturn(true).times(1);
        // enabled
        EasyMock.expect(sessionUser.hasRole(Role.ADMIN)).andReturn(true).times(1);
        EasyMock.expect(sessionUser.isAnonymous()).andReturn(false).anyTimes();
        EasyMock.expect(sessionUser.getId()).andReturn("aleph").times(1);
        EasyMock.expect(userUnderEdit.getId()).andReturn("aleph").times(1);
        
        EasyMock.replay(sessionUser, userUnderEdit);
        assertTrue(rule.isComponentVisible(ctx));
        assertFalse(rule.isEnableAllowed(ctx));
        EasyMock.verify(sessionUser, userUnderEdit); 
    }
    
    @Test
    public void testNull()
    {
        Object[] args = null;
        ContextParameters ctxParameters = new ContextParameters(args);
        assertFalse(rule.isEnableAllowed(ctxParameters));
        assertFalse(rule.isComponentVisible(ctxParameters));
    }
    

}
