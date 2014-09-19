package nl.knaw.dans.common.ldap.management;

import org.junit.Ignore;
import org.junit.Test;

public class ApacheDSServerBuilderOnlineTest {

    @Ignore("Not a test")
    @Test
    public void buildApacheDS() throws Exception {
        ApacheDSServerBuilder asb = new ApacheDSServerBuilder();
        asb.buildServer();
    }

}
