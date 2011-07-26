package nl.knaw.dans.common.ldap.repo;

import nl.knaw.dans.common.ldap.ds.AbstractOnlineTest;
import nl.knaw.dans.common.ldap.ds.DirContextSupplier;
import nl.knaw.dans.common.ldap.ds.LdapClient;

public abstract class AbstractRepoOnlineTest
{
    
    private static LdapClient CLIENT;
    
    protected AbstractRepoOnlineTest()
    {
        
    }
    
    protected static LdapClient getLdapClient()
    {
        if (CLIENT == null)
        {
            DirContextSupplier supplier = AbstractOnlineTest.getDirContextSupplier();

            CLIENT = new LdapClient(supplier);
        }
        return CLIENT;
    }
    
    
    

}
