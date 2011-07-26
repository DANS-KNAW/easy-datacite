package nl.knaw.dans.easy.business.aspect;

import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.business.item.ItemWorkDispatcher;
import nl.knaw.dans.easy.security.CodedAuthz;
import nl.knaw.dans.easy.security.Security;

import org.junit.BeforeClass;
import org.junit.Test;


public class SecurityAspectTest
{
    
    private static ItemWorkDispatcher workDispatcher;
    
    @BeforeClass
    public static void beforeClass()
    {
        new Security(new CodedAuthz());
        workDispatcher = new ItemWorkDispatcher();
    }
    

    @Test  (expected = CommonSecurityException.class)
    public void testSomeMethod() throws ServiceException
    {
        workDispatcher.addDirectoryContents(null, null, null, null, null, null, null);
        
    }
    
}
