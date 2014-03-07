package nl.knaw.dans.i.security.annotations;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class SecuredOperationUtilTest
{
    
    @Test
    public void getDeclaredSecurityIds()
    {
        List<String> securityIds = SecuredOperationUtil.getDeclaredSecurityIdsOnInterface(IA.class);
        assertEquals(2, securityIds.size());
        assertTrue(securityIds.contains("nl.knaw.dans.i.security.annotations.IA.iASecuredOperation"));
        
        securityIds = SecuredOperationUtil.getDeclaredSecurityIdsOnInterface(IB.class);
        assertEquals(1, securityIds.size());
        assertTrue(securityIds.contains("nl.knaw.dans.i.security.annotations.IB.iBSecuredOperation"));
    }
    
    @Test
    public void getInterfaceSecurityIds()
    {
        List<String> securityIds = SecuredOperationUtil.getInterfaceSecurityIds(ClassWithNonInterfaceSecuredOperation.class);
        assertEquals(3, securityIds.size());
        assertTrue(securityIds.contains("nl.knaw.dans.i.security.annotations.IB.iBSecuredOperation"));
        assertTrue(securityIds.contains("nl.knaw.dans.i.security.annotations.IA.iASecuredOperation"));
        assertFalse(securityIds.contains("nl.knaw.dans.i.security.annotations.ClassC.classCSecuredPublicMethod"));
    }
    
    @Test(expected = RuntimeException.class)
    public void checkWithNonInterfaceSecuredOperation()
    {
        SecuredOperationUtil.checkSecurityIds(ClassWithNonInterfaceSecuredOperation.class);
    }
    
    @Test(expected = RuntimeException.class)
    public void checkWithMissingAnnotation()
    {
        SecuredOperationUtil.checkSecurityIds(ClassWithMissingAnnotation.class);
    }
    
    @Test(expected = RuntimeException.class)
    public void checkWithWrongAnnotations()
    {
        SecuredOperationUtil.checkSecurityIds(ClassWithWrongAnnotations.class);
    }
    
    @Test(expected = RuntimeException.class)
    public void checkWithMissingAnnotationWithSameId()
    {
        SecuredOperationUtil.checkSecurityIds(ClassWithMissingAnnotationWithSameId.class);
    }
    
    @Test
    public void checkSecurityIds()
    {
        SecuredOperationUtil.checkSecurityIds(ValidClass.class);
    }
    

}
