package nl.knaw.dans.easy.ldap;

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
import java.util.Set;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.exception.ObjectExistsException;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.common.lang.user.User;
import nl.knaw.dans.common.lang.user.UserImpl;
import nl.knaw.dans.common.lang.util.Base64Coder;
import nl.knaw.dans.common.ldap.repo.MissingAttributeException;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.Group;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.domain.user.GroupImpl;

import org.junit.BeforeClass;
import org.junit.Test;

public class EasyUserRepoOnlineTest extends AbstractOnlineTest
{

    private static EasyLdapUserRepo repo;

    @BeforeClass
    public static void beforeClass()
    {
        repo = new EasyLdapUserRepo(getLdapClient(), Tester.getString("ldap.context.users"));
    }

    @Test
    public void add_update_delete() throws RepositoryException
    {
        EasyUser jan = new EasyUserImpl();
        jan.setId("acc_jan"); // uid
        jan.setSurname("Janssen"); // sn
        jan.setEmail("jan.jansen@bar.com");
        jan.setCity("Knollendam");
        jan.addRole(Role.USER);
        jan.addRole(Role.ARCHIVIST);
        jan.setAcceptedGeneralConditions(false);

        // there is no relational integrity
        Group group1 = new GroupImpl("test1");
        Group group2 = new GroupImpl("test2");
        jan.joinGroup(group1);
        jan.joinGroup(group2);

        // remove player
        if (repo.exists(jan.getId()))
            repo.delete(jan);

        String uid = repo.add(jan);

        EasyUser rjan = repo.findById(uid);
        assertEquals(jan, rjan);

        rjan.setEmail("jan.jansen@foo.baz");
        rjan.setCity(null);
        rjan.removeRole(Role.ARCHIVIST);
        repo.update(rjan);

        EasyUser rrjan = repo.findById(uid);
        assertEquals("jan.jansen@foo.baz", rrjan.getEmail());
        assertNull(rrjan.getCity());
        assertTrue(rrjan.hasRole(Role.USER));
        assertFalse(rrjan.hasRole(Role.ARCHIVIST));

        assertTrue(rrjan.isMemberOf(group1));
        assertTrue(rrjan.isMemberOf(group2));

        rrjan.leaveGroup(group1);
        rrjan.leaveGroup(group2);

        assertEquals(0, rrjan.getGroupIds().size());

        repo.update(rrjan);

        rrjan = repo.findById(uid);

        assertEquals(0, rrjan.getGroupIds().size());

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
        EasyUser zyxwvuts = new EasyUserImpl();
        zyxwvuts.setId("zyxwvuts");
        zyxwvuts.setSurname("Six");

        // remove player
        if (repo.exists(zyxwvuts.getId()))
            repo.delete(zyxwvuts);

        repo.add(zyxwvuts);

        EasyUser applicant = new EasyUserImpl();
        applicant.setId("zyxwvuts");
        applicant.setSurname("Pplicant");

        try
        {
            repo.add(applicant);
            fail("Expected IdNotUniqueException.");
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
        EasyUser zyxwvuts = new EasyUserImpl();
        repo.add(zyxwvuts);
    }

    @Test(expected = RepositoryException.class)
    public void addWithInsufficientData_2() throws RepositoryException
    {
        EasyUser zyxwvuts = new EasyUserImpl();
        zyxwvuts.setId("");
        repo.add(zyxwvuts);
    }

    @Test
    public void findByEmail() throws RepositoryException
    {
        EasyUser piet = new EasyUserImpl();
        piet.setId("piet");
        piet.setSurname("Pietersen");
        piet.setEmail("piet.pietersen@nowhere.com");

        // remove player
        if (repo.exists(piet.getId()))
            repo.delete(piet);

        repo.add(piet);

        List<EasyUser> users = repo.findByEmail("piet.pietersen@nowhere.com");
        assertEquals(1, users.size());
        assertEquals(piet, users.get(0));

        // cleanup
        repo.delete(piet);
    }

    @Test
    public void findByRole() throws RepositoryException
    {
        EasyUser piet = new EasyUserImpl();
        piet.setId("piet");
        piet.setSurname("Pietersen");
        piet.setEmail("piet.pietersen@nowhere.com");
        piet.addRole(Role.ARCHIVIST);

        // remove player
        if (repo.exists(piet.getId()))
            repo.delete(piet);

        repo.add(piet);

        List<EasyUser> users = repo.findByRole(Role.ARCHIVIST);
        assertTrue(users.contains(piet));

        piet.removeRole(Role.ARCHIVIST);
        piet.addRole(Role.ADMIN);
        piet.addRole(Role.USER);
        repo.update(piet);

        users = repo.findByRole(Role.ARCHIVIST);
        assertFalse(users.contains(piet));

        users = repo.findByRole(Role.ADMIN);
        assertTrue(users.contains(piet));

        users = repo.findByRole(Role.USER);
        assertTrue(users.contains(piet));

        // cleanup
        repo.delete(piet);
    }

    @Test
    public void findByCommonNameStub() throws RepositoryException
    {
        EasyUser piet = new EasyUserImpl();
        piet.setId("piet");
        piet.setSurname("Pietersen");

        EasyUser klaas = new EasyUserImpl();
        klaas.setId("klaas");
        klaas.setSurname("Pieterbuuren");

        // remove players
        if (repo.exists(piet.getId()))
            repo.delete(piet);
        if (repo.exists(klaas.getId()))
            repo.delete(klaas);

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
        for (String id : idNameMap.keySet())
        {
            System.out.println(id + " | " + idNameMap.get(id));
        }
        assertTrue(idNameMap.containsKey("piet"));
        assertTrue(idNameMap.containsKey("klaas"));

        // cleanup
        repo.delete(piet);
        repo.delete(klaas);
    }

    @Test
    public void authenticate() throws RepositoryException
    {
        EasyUser piet = new EasyUserImpl();
        piet.setId("piet");
        piet.setSurname("Pietersen");
        piet.setPassword("geheim");

        EasyUser klaas = new EasyUserImpl();
        klaas.setId("klaas");
        klaas.setSurname("Klaaszen");
        klaas.setPassword("geheim");

        // remove players
        if (repo.exists(piet.getId()))
            repo.delete(piet);
        if (repo.exists(klaas.getId()))
            repo.delete(klaas);

        repo.add(piet);
        repo.add(klaas);

        assertTrue(repo.authenticate("piet", "geheim"));
        assertTrue(repo.authenticate("klaas", "geheim"));

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
        repo.delete(piet);
        repo.delete(klaas);
    }

    @Test
    public void updatePassword() throws RepositoryException
    {
        EasyUser test = new EasyUserImpl();
        test.setId("test");
        test.setSurname("Pietersen");
        test.setPassword("geheim");

        // remove players
        if (repo.exists(test.getId()))
            repo.delete(test);

        repo.add(test);

        EasyUser test2 = repo.findById("test");
        assertNull(test2.getPassword());

        test2.setEmail("test@foo.com");
        repo.update(test2);
        assertTrue(repo.authenticate("test", "geheim"));

        test2.setPassword("newPassword");
        repo.update(test2);
        assertTrue(repo.authenticate("test", "newPassword"));
        assertFalse(repo.authenticate("test", "geheim"));

        // cleanup
        repo.delete(test);
    }

    @Test
    public void isPasswordStored() throws RepositoryException
    {
        EasyUser test = new EasyUserImpl();
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
        List<EasyUser> users = repo.findAll();
        assertNotNull(users);
    }

    @Test
    public void easyUserAttributes() throws RepositoryException
    {
        EasyUser easy = createAUser();

        // remove players
        if (repo.exists(easy.getId()))
            repo.delete(easy);

        repo.add(easy);

        EasyUser easy2 = repo.findById("easy");
        assertEquals(EasyUser.State.REGISTERED, easy2.getState());
        assertTrue(easy2.getAcceptConditionsOfUse());

        easy2.setState(EasyUser.State.ACTIVE);
        easy2.setAcceptConditionsOfUse(false);
        repo.update(easy2);

        EasyUser easy3 = repo.findById("easy");
        assertEquals(EasyUser.State.ACTIVE, easy3.getState());
        assertFalse(easy3.getAcceptConditionsOfUse());

        easy3.setState(null);
        repo.update(easy3);

        EasyUser easy4 = repo.findById("easy");
        assertNull(easy4.getState());

        // cleanup
        repo.delete(easy);
    }

    @Test
    public void testSetEncryptedPassword() throws RepositoryException, NoSuchAlgorithmException
    {

        EasyUser user = new EasyUserImpl();
        user.setTitle("dr.");
        user.setInitials("ABC");
        user.setFirstname("Alphons");
        user.setPrefixes("van de");
        user.setSurname("Wetering");
        user.setId("user 1");
        String encryptedPassword = hashPassword("secret", "SHA");
        user.setSHAEncryptedPassword(encryptedPassword);

        // remove players
        if (repo.exists(user.getId()))
            repo.delete(user);

        repo.add(user);

        EasyUser user2 = repo.findById(user.getId());
        assertTrue(repo.authenticate(user2.getId(), "secret"));

        // remove players
        if (repo.exists(user.getId()))
            repo.delete(user);
    }

    @Test
    public void getGroups() throws RepositoryException
    {
        EasyLdapGroupRepo groupRepo = new EasyLdapGroupRepo(getLdapClient(), Tester.getString("ldap.context.groups"));
        Data data = new Data();
        data.setGroupRepo(groupRepo);

        Group userTest1 = new GroupImpl("userTest1");
        userTest1.setDescription("userTest1 description");
        if (groupRepo.exists(userTest1.getId()))
        {
            groupRepo.delete(userTest1);
        }
        groupRepo.add(userTest1);

        Group userTest2 = new GroupImpl("userTest2");
        userTest2.setDescription("userTest2 description");
        if (groupRepo.exists(userTest2.getId()))
        {
            groupRepo.delete(userTest2);
        }
        groupRepo.add(userTest2);

        EasyUser easy = createAUser();
        easy.joinGroup(userTest1);
        // remove players
        if (repo.exists(easy.getId()))
            repo.delete(easy);

        repo.add(easy);

        Set<Group> groups = easy.getGroups();
        assertEquals(1, groups.size());
        Group foundGroup = groups.iterator().next();
        assertEquals(userTest1, foundGroup);
        assertEquals("userTest1 description", foundGroup.getDescription());

        easy.joinGroup(userTest2);
        repo.update(easy);

        groups = easy.getGroups();
        assertEquals(2, groups.size());
        assertTrue(groups.contains(userTest1));
        assertTrue(groups.contains(userTest2));

        easy.leaveGroup(userTest1);
        repo.update(easy);

        groups = easy.getGroups();
        assertEquals(1, groups.size());
        foundGroup = groups.iterator().next();
        assertEquals(userTest2, foundGroup);
        assertEquals("userTest2 description", foundGroup.getDescription());

        // clean up
        if (groupRepo.exists(userTest1.getId()))
        {
            groupRepo.delete(userTest1);
        }
        if (groupRepo.exists(userTest2.getId()))
        {
            groupRepo.delete(userTest2);
        }
        if (repo.exists(easy.getId()))
            repo.delete(easy);
    }

    private EasyUser createAUser()
    {

        EasyUser user = new EasyUserImpl();
        user.setTitle("dr.");
        user.setInitials("ABC");
        user.setFirstname("Alphons");
        user.setPrefixes("van de");
        user.setSurname("Wetering");
        user.setId("easy");
        user.setPassword("easyPass");
        user.setState(EasyUser.State.REGISTERED);
        user.setAddress("Anna van Saksenlaan 25");
        user.setCity("The Hague");
        user.setCountry("Netherlands");
        user.setDiscipline1("easy-discipline:1");
        user.setDiscipline2("easy-discipline:2");
        user.setDiscipline3("easy-discipline:3");
        user.setDepartment("SDG");
        user.setFunction("Software developer");
        user.setOrganization("DANS-KNAW");
        user.setPostalCode("1234 AB");
        user.setTelephone("+31 020 123 45 67");
        user.setAcceptConditionsOfUse(true);
        user.setLogMyActions(true);

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
