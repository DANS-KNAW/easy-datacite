package nl.knaw.dans.easy.ldap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.ldap.OperationalAttributes;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.easy.domain.model.user.Group;
import nl.knaw.dans.easy.domain.model.user.Group.State;
import nl.knaw.dans.easy.domain.user.GroupImpl;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EasyGroupRepoOnlineTest extends AbstractOnlineTest
{

    private static final Logger logger = LoggerFactory.getLogger(EasyGroupRepoOnlineTest.class);

    private static EasyLdapGroupRepo repo;

    private boolean verbose = Tester.isVerbose();

    @BeforeClass
    public static void beforeClass()
    {
        repo = new EasyLdapGroupRepo(getLdapClient(), Tester.getString("ldap.context.groups"));
    }

    @Test(expected = ObjectNotInStoreException.class)
    public void findByIdNonExistent() throws ObjectNotInStoreException, RepositoryException
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
    public void add_update_delete() throws ObjectNotInStoreException, RepositoryException
    {
        Group test1 = new GroupImpl("test1");

        // remove player
        if (repo.exists(test1.getId()))
            repo.delete(test1);

        String id = repo.add(test1);
        Group rTest1 = repo.findById(id);
        assertEquals(test1, rTest1);

        assertEquals(State.ACTIVE, rTest1.getState());
        rTest1.setState(State.INACTIVE);
        rTest1.setDescription("This is a test group");
        repo.update(rTest1);

        GroupImpl rrTest1 = (GroupImpl) repo.findById("test1");
        assertEquals(State.INACTIVE, rrTest1.getState());
        assertEquals("This is a test group", rrTest1.getDescription());
        assertEquals(rTest1, rrTest1);

        repo.delete(test1);
        try
        {
            repo.findById("test1");
            fail("the object is supposed to be deleted!");
        }
        catch (ObjectNotInStoreException e)
        {
            // expected
        }
    }

    @Test
    public void operationalAttributes() throws RepositoryException
    {
        Group test2 = new GroupImpl("test2");
        // remove player
        if (repo.exists(test2.getId()))
            repo.delete(test2);
        repo.add(test2);

        OperationalAttributes opa = repo.getOperationalAttributes("test2");

        assertNotNull(opa.getCreateTimestamp());

        // goes for apacheds
        //assertNull(opa.getModifyTimestamp());

        // goes for openldap
        //assertNotNull(opa.getModifyTimestamp());

        repo.update(test2);

        opa = repo.getOperationalAttributes("test2");

        assertNotNull(opa.getCreateTimestamp());
        assertNotNull(opa.getModifyTimestamp());

        if (repo.exists(test2.getId()))
            repo.delete(test2);
    }

    @Test
    public void findAll() throws RepositoryException
    {
        List<Group> all = repo.findAll();
        assertNotNull(all);
        if (verbose)
            for (Group group : all)
            {
                logger.debug(group.getId());
            }
    }

    @Test
    public void findList() throws RepositoryException
    {
        Set<String> ids = new HashSet<String>();
        for (int i = 0; i < 10; i++)
        {
            Group group = new GroupImpl("testGroup" + i);
            group.setDescription("description " + i);
            if (repo.exists(group.getId()))
            {
                repo.delete(group);
            }
            repo.add(group);
            ids.add(group.getId());
        }

        List<String> idsToRemove = new ArrayList<String>(ids);

        ids.remove("testGroup3");
        ids.remove("testGroup5");

        List<Group> foundGroups = repo.findById(ids);

        assertEquals(8, foundGroups.size());
        Group group0 = foundGroups.get(0);
        assertEquals("description 0", group0.getDescription());
        assertEquals("testGroup0", group0.getId());
        Group group7 = foundGroups.get(7);
        assertEquals("description 9", group7.getDescription());
        assertEquals("testGroup9", group7.getId());

        for (String id : idsToRemove)
        {
            repo.delete(id);
        }
    }

}
