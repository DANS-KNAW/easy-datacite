package nl.knaw.dans.common.ldap.management;

import org.junit.Test;

public class ApacheDSServerBuilderOnlineTest
{
    
    @Test
    public void buildApacheDS() throws Exception
    {
        ApacheDSServerBuilder asb = new ApacheDSServerBuilder();
        asb.buildServer();
    }

}
