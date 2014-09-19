package nl.knaw.dans.common.ldap.repo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import nl.knaw.dans.common.lang.annotations.ldap.LdapAttribute;
import nl.knaw.dans.common.lang.annotations.ldap.LdapObject;
import nl.knaw.dans.common.lang.ldap.DateTimeTranslator;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.common.ldap.ds.AbstractOnlineTest;
import nl.knaw.dans.common.ldap.ds.LdapClient;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;

public class LdapMapperOnlineTest extends AbstractOnlineTest {

    private static String contextPath;

    private static LdapClient client;

    @BeforeClass
    public static void beforeClass() {
        client = getLdapClient();
        contextPath = Tester.getString("ldap.context.users");
    }

    @Test
    public void testMapping() throws MissingAttributeException, LdapMappingException, NameAlreadyBoundException, NamingException {
        String uid = "test Mapping";
        String rdn = "uid=" + uid;
        try {
            client.deleteEntry(rdn, contextPath);
        }
        catch (NameNotFoundException e) {
            //
        }

        LdapMapper<TestClass> mapper = new LdapMapper<TestClass>(TestClass.class);
        TestClass tc = new TestClass();
        tc.uid = uid;
        tc.cn = "common name";
        tc.sn = "surname";
        tc.accept = true;
        tc.telephone = "123";
        tc.lastLogin = new DateTime(123456789L);

        Attributes attrs = mapper.marshal(tc, false);
        client.addEntry(rdn, contextPath, attrs);

        Attributes attrs2 = client.getAttributes(rdn, contextPath);
        TestClass tc2 = mapper.unmarshal(attrs2);

        assertEquals("common name", tc2.cn);
        assertTrue(tc2.accept);
        assertEquals("123", tc2.telephone);
        assertEquals("1970-01-02T11:17:36.000+01:00", tc2.lastLogin.toString());

        // modify
        tc2.cn = "Ling Ping";
        tc2.accept = false;
        tc2.telephone = null;
        tc2.lastLogin = new DateTime(1234567890123L);

        attrs = mapper.marshal(tc2, true);
        client.modifyEntry(rdn, contextPath, attrs);

        Attributes attrs3 = client.getAttributes(rdn, contextPath);
        TestClass tc3 = mapper.unmarshal(attrs3);

        assertEquals("Ling Ping", tc3.cn);
        assertFalse(tc3.accept);
        assertNull(tc3.telephone);
        assertEquals("2009-02-14T00:31:30.000+01:00", tc3.lastLogin.toString());

        client.deleteEntry(rdn, contextPath);
    }

    @LdapObject(objectClasses = {"dansUser", "inetOrgPerson", "organizationalPerson", "person"})
    private static class TestClass {
        @LdapAttribute(id = "uid")
        String uid;
        @LdapAttribute(id = "cn")
        String cn;
        @LdapAttribute(id = "sn")
        String sn;
        @LdapAttribute(id = "dansAcceptConditionsOfUse")
        boolean accept;
        @LdapAttribute(id = "telephoneNumber")
        String telephone;
        @LdapAttribute(id = "dansLastLogin", valueTranslator = DateTimeTranslator.class)
        DateTime lastLogin;

        public TestClass() {
            //
        }

    }

}
