package nl.knaw.dans.common.ldap.ds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;

import javax.naming.Binding;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchResult;

import nl.knaw.dans.common.lang.util.Base64Coder;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests for LdapClient.
 * 
 * @author ecco Feb 6, 2009
 */
public class LdapClientOnlineTest extends AbstractOnlineTest
{

    private static LdapClient client;
    
    private static final Logger              logger               = LoggerFactory.getLogger(LdapClientOnlineTest.class);
    
    @BeforeClass
    public static void beforeClass()
    {
        client = getLdapClient();
    }
    
    @Test
    public void logEnvironment() throws NamingException
    {
        DirContext ctx = client.getDirContextSupplier().getDirContext();
        Hashtable<?, ?> env = ctx.getEnvironment();
        StringBuilder sb = new StringBuilder(ctx + " environment:");
        for (Entry<?, ?> entry : env.entrySet())
        {
            sb.append("\n\t" + entry.getKey() + "=" + entry.getValue());
        }
        sb.append("\n\t-----------------------------------------------");
        logger.debug(sb.toString());
    }
    
    @Test
    public void listBindings() throws NamingException
    {
        NamingEnumeration<Binding> bindings = client.listBindings(Constants.DANS_CONTEXT);
        boolean testContextExists = false;
        while (bindings.hasMore())
        {
            String name = bindings.next().getName();
            testContextExists |= name.startsWith("ou=test");
        }
        assertTrue("No test context", testContextExists);
        
        NamingEnumeration<Binding> easyBindings = client.listBindings(Constants.TEST_CONTEXT);
        boolean usersContextExists = false;
        while (easyBindings.hasMore())
        {
            String name = easyBindings.next().getName();
            usersContextExists |= name.startsWith("ou=users");          
        }
        assertTrue("No users context", usersContextExists);
    }
    
    @Test
    public void add_get_modify_delete() throws NameAlreadyBoundException, NamingException
    {   
        String subContext = Constants.TEST_USERS_CONTEXT;
        String rdn = "cn=common name of jan";
        
        // create some attributes
        Attributes attrs = createUser("jan5", "pass5", "common name of jan", "surname of jan");
        
        // remove player if already in context
        deleteEntryIfExists(rdn, subContext);
        
        // add entry with given attributes        
        client.addEntry(rdn, subContext, attrs);
        
        // retrieve the attributes
        Attributes rAttrs = client.getAttributes(rdn, subContext);
        Attribute attrSN = rAttrs.get("sn");
        String sn = (String) attrSN.get();
        assertEquals("surname of jan", sn);
        
        // modify the attributes
        attrSN.remove(0);
        attrSN.add("Jan van Planken");
        client.modifyEntry(rdn, subContext, rAttrs);
        
        // retrieve the new attributes
        Attributes rAttrs2 = client.getAttributes(rdn, subContext);
        Attribute attrSN2 = rAttrs2.get("sn");
        String sn2 = (String) attrSN2.get();
        assertEquals("Jan van Planken", sn2);
        
        client.deleteEntry(rdn, subContext);
    }
    
    @Test
    public void search() throws NamingException
    {
        String subContext = Constants.TEST_USERS_CONTEXT;
        String emailAddress = "jan.en.piet@foo.bar.com";
        String searchAddress = "Jan.en.Piet@FOO.BAR.com";
        String rdnJan = "cn=common name of jan";
        String rdnPiet = "cn=common name of piet";
        
        // create some attributes
        Attributes janAttrs = createUser("jan5", "pass5", "common name of jan", "surname of jan", emailAddress);
        Attributes pietAttrs = createUser("piet5", "pass5", "common name of piet", "surname of piet", emailAddress);
        
        // remove players if already in context
        deleteEntryIfExists(rdnJan, subContext);
        deleteEntryIfExists(rdnPiet, subContext);
        
        // add entry with given attributes        
        client.addEntry(rdnJan, subContext, janAttrs);
        client.addEntry(rdnPiet, subContext, pietAttrs);
        
        // do search
        //searchAddress = "Henk.van.den.berg@DANS.knaw.nl";
        String filter = "(&(objectClass=inetOrgPerson)(mail=" + searchAddress + "))";
        NamingEnumeration<SearchResult> resultEnum = client.search(subContext, filter);
        //printSearchResults(resultEnum);
        
        // and assert
        List<Object> list = new ArrayList<Object>();
        while (resultEnum.hasMore())
        {
            SearchResult result = resultEnum.next();
            Attributes attrs = result.getAttributes();
            Object obj = attrs.get("cn").get();
            list.add(obj);
            
        }
        
        assertEquals(2, list.size());
        assertTrue(list.contains("common name of jan"));
        assertTrue(list.contains("common name of piet"));
        
        // cleanup
        deleteEntryIfExists(rdnJan, subContext);
        deleteEntryIfExists(rdnPiet, subContext);
    }
    
    @SuppressWarnings("unused")
	private void printSearchResults(NamingEnumeration<SearchResult> resultEnum) throws NamingException
    {
        while (resultEnum.hasMore())
        {
            SearchResult result = resultEnum.next();
            printSearchResult(result);
        }        
    }

    private void printSearchResult(SearchResult result) throws NamingException
    {
        Attributes attrs = result.getAttributes();
        printAttributes(attrs);        
    }

    private void printAttributes(Attributes attrs) throws NamingException
    {
        NamingEnumeration<? extends Attribute> attrEnum = attrs.getAll();
        while (attrEnum.hasMore())
        {
            Attribute attr = attrEnum.next();
            for (int i = 0; i < attr.size(); i++)
            {
                System.out.println(attr.getID() + "=" + attr.get(i));
            }
        }
    }
    


    @Test
    public void authenticate() throws NamingException
    {
        String passSix = "secret42";
        String encryptedPassSix = passSix;
        
        String passOne = "12Pass";
        String encryptedPassOne = passOne;
        
        testScenarios(passSix, encryptedPassSix, passOne, encryptedPassOne);
    }
    
    @Test 
    public void authenticateOneWayEncrypted() throws NameAlreadyBoundException, NamingException, NoSuchAlgorithmException
    {
        String passSix = "secret42";
        String encryptedPassSix = hashPassword(passSix, "SHA");
        
        String passOne = "12Pass";
        String encryptedPassOne = hashPassword(passOne, "SHA");
        
        testScenarios(passSix, encryptedPassSix, passOne, encryptedPassOne);
    }

    private void testScenarios(String passSix, String encryptedPassSix, String passOne, String encryptedPassOne)
            throws NamingException, NameAlreadyBoundException
    {
        String subContext = Constants.TEST_USERS_CONTEXT;
        
        String uidSix = "zyxwvuts";       
        String rdnSix = "uid=zyxwvuts";
        
        String uidOne = "one";       
        String rdnOne = "uid=one";
        
        // remove players if they are already in the context
        deleteEntryIfExists(rdnSix, subContext);
        deleteEntryIfExists(rdnOne, subContext);
        
        // create some users
        Attributes six = createUser(uidSix, encryptedPassSix, "Zyxwvuts Six", "Six");
        Attributes one = createUser(uidOne, encryptedPassOne, "One User", "User");
        
        // add entries with given attributes       
        client.addEntry(rdnSix, Constants.TEST_USERS_CONTEXT, six);
        client.addEntry(rdnOne, Constants.TEST_USERS_CONTEXT, one);
        
        // ------------ Testing scenarios --------------
        // existing user, valid pass
        String filterSix = "(&(objectClass=inetOrgPerson)(uid=" + uidSix + "))";
        assertTrue(client.authenticate(passSix, subContext, filterSix));
        
        // existing user, invalid pass
        String invalidPass = "secret32";
        assertFalse(client.authenticate(invalidPass, subContext, filterSix));
        
        // nonexistent user, pass borrowed from zyxwvuts
        String nonExistentUserFilter = "(&(objectClass=inetOrgPerson)(uid=bestaatechnie))";
        assertFalse(client.authenticate(passSix, subContext, nonExistentUserFilter));
        
        // one can authenticate with his own pass (same as 'existing user, valid pass')
        String filterOne = "(&(objectClass=inetOrgPerson)(uid=" + uidOne + "))";
        assertTrue(client.authenticate(passOne, subContext, filterOne));
        // but cannot use zyxwvuts' pass
        assertFalse(client.authenticate(passSix, subContext, filterOne));
        
        // ----------------------------------------------
        
        //client.deleteEntry(rdnSix, subContext);
        //client.deleteEntry(rdnOne, subContext);
    }

    private void deleteEntryIfExists(String rdn, String subContext) throws NamingException
    {
        try
        {
            client.deleteEntry(rdn, subContext);
        }
        catch (NameNotFoundException e)
        {
            // 
        }
    }

    private Attributes createUser(String uid, String pass, String cn, String sn)
    {
        Attributes attrs = new BasicAttributes();
        Attribute oc = new BasicAttribute("objectclass");
        oc.add("inetOrgPerson");
        oc.add("organizationalPerson");
        oc.add("person");
        oc.add("top");
        
        attrs.put(oc);
        attrs.put("cn", cn);
        attrs.put("sn", sn);
        attrs.put("uid", uid);
        attrs.put("userPassword", pass);
        
        return attrs;
    }
    
    private Attributes createUser(String uid, String pass, String cn, String sn, String mail)
    {
        Attributes attrs = createUser(uid, pass, cn, sn);
        attrs.put("mail", mail);
        return attrs;
    }
    
    private static String hashPassword(final String password, final String algorithm) throws NoSuchAlgorithmException
    {
        // Calculate hash value
        MessageDigest md = MessageDigest.getInstance(algorithm);
        md.update(password.getBytes());
        byte[] bytes = md.digest();

        String hash = new String(Base64Coder.encode(bytes));
        return "{" + algorithm + "}" + hash;
    }
    
    
    
}
