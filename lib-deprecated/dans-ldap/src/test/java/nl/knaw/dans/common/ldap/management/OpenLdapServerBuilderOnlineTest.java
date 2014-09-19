package nl.knaw.dans.common.ldap.management;

import nl.knaw.dans.common.lang.test.Tester;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenLdapServerBuilderOnlineTest {
    private static final Logger logger = LoggerFactory.getLogger(OpenLdapServerBuilderOnlineTest.class);

    @Ignore("Not a test")
    @Test
    public void buildServer() throws Exception {
        String inUse = Tester.getString("ldap.in.use");
        logger.debug("Using " + inUse + " as ldap server");
        String providerUrl = Tester.getString("ldap." + inUse + ".providerURL");
        String securityPrincipal = Tester.getString("ldap." + inUse + ".securityPrincipal");
        String securityCredentials = Tester.getString("ldap." + inUse + ".securityCredentials");

        OpenLdapServerBuilder builder = new OpenLdapServerBuilder(providerUrl, securityPrincipal, securityCredentials);
        builder.buildServer();
    }

}
