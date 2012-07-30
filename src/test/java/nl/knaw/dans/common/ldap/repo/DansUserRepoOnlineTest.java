package nl.knaw.dans.common.ldap.repo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.exception.ObjectExistsException;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.common.lang.user.User;
import nl.knaw.dans.common.lang.user.UserImpl;
import nl.knaw.dans.common.lang.util.Base64Coder;
import nl.knaw.dans.common.ldap.repo.DansUserRepo;

import org.junit.BeforeClass;
import org.junit.Test;

public class DansUserRepoOnlineTest extends AbstractRepoOnlineTest
{

    private static DansUserRepo repo;

    @BeforeClass
    public static void beforeClass()
    {
        repo = new DansUserRepo(getLdapClient(), Tester.getString("ldap.context.users"));
    }

    @Test(expected = ObjectNotInStoreException.class)
    public void findByIdNonExistent() throws RepositoryException
    {
        repo.findById("this id does not exist in the ds");
    }

    @Test(expected = ObjectNotInStoreException.class)
    public void findByIdNull() throws ObjectNotInStoreException, RepositoryException
    {
        String id = null;
        repo.findById(id);
    }

    @Test
    public void add_update_delete() throws RepositoryException
    {
        User jan = new UserImpl();
        jan.setId("acc_jan");   // uid
        jan.setSurname("Janssen");     // sn

        jan.setFirstname("Jantje");
        jan.setEmail("jan.jansen@bar.com");
        jan.setCity("Knollendam");

        // remove player
        if (repo.exists(jan.getId())) repo.delete(jan);

        String uid = repo.add(jan);

        User rjan = repo.findById(uid);
        assertEquals(jan, rjan);

        rjan.setEmail("jan.jansen@foo.baz");
        rjan.setCity(null);
        repo.update(rjan);

        User rrjan = repo.findById(uid);
        assertEquals("jan.jansen@foo.baz", rrjan.getEmail());
        assertNull(rrjan.getCity());

        repo.delete(rrjan);
        try
        {
            repo.findById(uid);
            fail("the object is supposed to be deleted!");
        }
        catch (ObjectNotInStoreException e)
        {
            // expected
        }
    }

    @Test
    public void addWithExistingUserId() throws RepositoryException
    {
        User zyxwvuts = new UserImpl();
        zyxwvuts.setId("zyxwvuts");
        zyxwvuts.setSurname("Six");

        // remove player
        if (repo.exists(zyxwvuts.getId())) repo.delete(zyxwvuts);

        repo.add(zyxwvuts);

        User applicant = new UserImpl();
        applicant.setId("zyxwvuts");
        applicant.setSurname("Pplicant");

        try
        {
            repo.add(applicant);
            fail("Expected ObjectExistsException.");
        }
        catch (ObjectExistsException e)
        {
            // expected
        }

        // cleanup
        repo.delete(zyxwvuts);
    }

    @Test(expected = MissingAttributeException.class)
    public void addWithInsufficientData_1() throws RepositoryException
    {
        User zyxwvuts = new UserImpl();
        repo.add(zyxwvuts);
    }

    @Test(expected = RepositoryException.class)
    public void addWithInsufficientData_2() throws RepositoryException
    {
        User zyxwvuts = new UserImpl();
        zyxwvuts.setId("");
        repo.add(zyxwvuts);
    }

    @Test
    public void findByEmail() throws RepositoryException
    {
        User piet = new UserImpl();
        piet.setId("piet");
        piet.setSurname("Pietersen");
        piet.setEmail("piet.pietersen@nowhere.com");

        // remove player
        if (repo.exists(piet.getId())) repo.delete(piet);

        repo.add(piet);

        List<User> users = repo.findByEmail("piet.pietersen@nowhere.com");
        assertEquals(1, users.size());
        assertEquals(piet, users.get(0));

        // cleanup
        repo.delete(piet);
    }

    @Test
    public void findByCommonNameStub() throws RepositoryException
    {
        User piet = new UserImpl();
        piet.setId("piet");
        piet.setSurname("Pietersen");

        User klaas = new UserImpl();
        klaas.setId("klaas");
        klaas.setSurname("Pieterbuuren");

        // remove players
        if (repo.exists(piet.getId())) repo.delete(piet);
        if (repo.exists(klaas.getId())) repo.delete(klaas);

        repo.add(piet);
        repo.add(klaas);

        Map<String, String> idNameMap = repo.findByCommonNameStub("pieter", 10L);

        assertTrue(idNameMap.containsKey("piet"));
        assertTrue(idNameMap.containsKey("klaas"));

        assertTrue(idNameMap.containsValue("Pietersen,"));
        assertTrue(idNameMap.containsValue("Pieterbuuren,"));

        idNameMap = repo.findByCommonNameStub("pieter", 1L);
        assertEquals(1, idNameMap.size());

        idNameMap = repo.findByCommonNameStub("pi(ter", 10L);
        assertTrue(idNameMap.containsKey("piet"));
        assertTrue(idNameMap.containsKey("klaas"));

        // cleanup
        repo.delete(piet);
        repo.delete(klaas);
    }

    @Test
    public void authenticate() throws RepositoryException
    {
        User piet = new UserImpl();
        piet.setId("piet");
        piet.setSurname("Pietersen");
        piet.setPassword("geheim");

        User klaas = new UserImpl();
        klaas.setId("klaas");
        klaas.setSurname("Klaaszen");
        klaas.setPassword("geheim");

        // remove players
        if (repo.exists(piet.getId())) repo.delete(piet);
        if (repo.exists(klaas.getId())) repo.delete(klaas);

        repo.add(piet);
        repo.add(klaas);
        
        User pietR = repo.findById("piet");
        assertNull(pietR.getLastLoginDate());
        User klaasR = repo.findById("klaas");
        assertNull(klaasR.getLastLoginDate());

        getLdapClient().setUpdatingLastLogin(true);
        assertTrue(repo.authenticate("piet", "geheim"));
        
        getLdapClient().setUpdatingLastLogin(false);
        assertTrue(repo.authenticate("klaas", "geheim"));
        
        pietR = repo.findById("piet");
        assertNotNull(pietR.getLastLoginDate());
        klaasR = repo.findById("klaas");
        assertNull(klaasR.getLastLoginDate());
        
        assertFalse(repo.authenticate("klaas", null));
        assertFalse(repo.authenticate("klaas", ""));

        assertFalse(repo.authenticate("karel", null));
        assertFalse(repo.authenticate("karel", ""));
        assertFalse(repo.authenticate("karel", "geheim"));

        assertFalse(repo.authenticate(null, null));
        assertFalse(repo.authenticate(null, ""));
        assertFalse(repo.authenticate(null, "geheim"));

        assertFalse(repo.authenticate("", null));
        assertFalse(repo.authenticate("", ""));
        assertFalse(repo.authenticate("", "geheim"));


        // cleanup
        //repo.delete(piet);
        //repo.delete(klaas);
    }

    @Test
    public void updatePassword() throws RepositoryException
    {
        User test = new UserImpl();
        test.setId("test");
        test.setSurname("Pietersen");
        test.setPassword("geheim");

        // remove players
        if (repo.exists(test.getId())) repo.delete(test);

        repo.add(test);

        User test2 = repo.findById("test");
        assertNull(test2.getPassword());

        test2.setEmail("test@foo.com");
        repo.update(test2);
        assertTrue(repo.authenticate("test", "geheim"));

        test2.setPassword("newPassword");
        repo.update(test2);
        assertTrue(repo.authenticate("test", "newPassword"));
        assertFalse(repo.authenticate("test", "geheim"));

        //cleanup
        repo.delete(test);
    }

    @Test
    public void isPasswordStored() throws RepositoryException
    {
        User test = new UserImpl();
        test.setId("test");
        test.setSurname("Pietersen");
        // NO password set

        // remove players
        if (repo.exists(test.getId()))
            repo.delete(test);

        repo.add(test);
        
        assertFalse(repo.isPasswordStored(test.getId()));
        repo.delete(test);
        
        // Now set the password
        test.setPassword("geheim");
        repo.add(test);
        
        assertTrue(repo.isPasswordStored(test.getId()));
        
        // cleanup
        repo.delete(test);
    }
    
    @Test
    public void findAll() throws RepositoryException
    {
        List<User> users = repo.findAll();
        assertNotNull(users);
    }

    @Test
    public void easyUserAttributes() throws RepositoryException
    {
        User easy = createAUser();

        // remove players
        if (repo.exists(easy.getId())) repo.delete(easy);

        repo.add(easy);

        User easy2 = repo.findById("easy");
        assertEquals(User.State.REGISTERED, easy2.getState());
        assertTrue(easy2.getAcceptConditionsOfUse());

        easy2.setState(User.State.ACTIVE);
        easy2.setAcceptConditionsOfUse(false);
        repo.update(easy2);

        User easy3 = repo.findById("easy");
        assertEquals(User.State.ACTIVE, easy3.getState());
        assertFalse(easy3.getAcceptConditionsOfUse());

        easy3.setState(null);
        repo.update(easy3);

        User easy4 = repo.findById("easy");
        assertNull(easy4.getState());

        // cleanup
        repo.delete(easy);
    }

    @Test
    public void testSetEncryptedPassword() throws RepositoryException, NoSuchAlgorithmException {

        User user = new UserImpl();
        user.setTitle("dr.");
        user.setInitials("ABC");
        user.setFirstname("Alphons");
        user.setPrefixes("van de");
        user.setSurname("Wetering");
        user.setId("user 1");
        String encryptedPassword = hashPassword("secret", "SHA");
        user.setSHAEncryptedPassword(encryptedPassword);

        // remove players
        if (repo.exists(user.getId())) repo.delete(user);

        repo.add(user);

        User user2 = repo.findById(user.getId());
        assertTrue(repo.authenticate(user2.getId(), "secret"));

        // remove players
        if (repo.exists(user.getId())) repo.delete(user);
    }


    /**
     *
     */
    private User createAUser() {

        User user = new UserImpl();
        user.setTitle("dr.");
        user.setInitials("ABC");
        user.setFirstname("Alphons");
        user.setPrefixes("van de");
        user.setSurname("Wetering");
        user.setId("easy");
        user.setPassword("easyPass");
        user.setState(User.State.REGISTERED);
        user.setAddress("Anna van Saksenlaan 25");
        user.setCity("The Hague");
        user.setCountry("Netherlands");
        user.setDepartment("SDG");
        user.setFunction("Software developer");
        user.setOrganization("DANS-KNAW");
        user.setPostalCode("1234 AB");
        user.setTelephone("+31 020 123 45 67");
        user.setAcceptConditionsOfUse(true);

        return user;
    }

    private static String hashPassword(final String password, final String algorithm) throws NoSuchAlgorithmException
    {
        // Calculate hash value
        MessageDigest md = MessageDigest.getInstance(algorithm);
        md.update(password.getBytes());
        byte[] bytes = md.digest();

        String hash = new String(Base64Coder.encode(bytes));
        return hash;
    }

}
