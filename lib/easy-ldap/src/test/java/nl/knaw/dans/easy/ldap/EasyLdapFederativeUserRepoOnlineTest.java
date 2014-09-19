package nl.knaw.dans.easy.ldap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.exception.ObjectExistsException;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.easy.domain.federation.FederativeUserIdMap;

import org.junit.BeforeClass;
import org.junit.Test;

public class EasyLdapFederativeUserRepoOnlineTest extends AbstractOnlineTest {
    private static EasyLdapFederativeUserRepo repo;

    final String FAKE_FEDID = "some.fake.federatedUserId";
    final String FAKE_FEDID2 = FAKE_FEDID + ".2";
    final String FAKE_DANSID = "some.fake.dansUserId";
    final String FAKE_DANSID_UPDATED = FAKE_DANSID + ".updated";

    @BeforeClass
    public static void beforeClass() {
        repo = new EasyLdapFederativeUserRepo(getLdapClient(), Tester.getString("ldap.context.federation"));
    }

    @Test
    public void add_update_delete() throws Exception {
        FederativeUserIdMap idm1 = new FederativeUserIdMap(FAKE_FEDID, FAKE_DANSID);

        // Add
        String id = insertIdMap(idm1);
        assertEquals(FAKE_FEDID, id);

        // Update
        FederativeUserIdMap idm2 = repo.findById(id);
        assertEquals(FAKE_FEDID, idm2.getId());
        // No setters, so need to create a new object
        FederativeUserIdMap idm3 = new FederativeUserIdMap(id, FAKE_DANSID_UPDATED);
        repo.update(idm3);
        FederativeUserIdMap idm4 = repo.findById(id);
        assertEquals(FAKE_FEDID, idm4.getId());
        assertEquals(FAKE_DANSID_UPDATED, idm4.getDansUserId());

        // Delete
        repo.delete(id);
        assertFalse(repo.exists(id));
    }

    @Test
    public void findByDansUserIdNonExisting() throws Exception {
        List<FederativeUserIdMap> idMaps = repo.findByDansUserId("this.dansUserId.does.not.exist.in.the.federativeUserMap");
        assertTrue(idMaps.isEmpty());
    }

    @Test
    public void findByDansUserId() throws Exception {
        // Add one mapping
        FederativeUserIdMap idm1 = new FederativeUserIdMap(FAKE_FEDID, FAKE_DANSID);
        String id1 = insertIdMap(idm1);
        assertEquals(FAKE_FEDID, id1);
        // Add another
        FederativeUserIdMap idm2 = new FederativeUserIdMap(FAKE_FEDID2, FAKE_DANSID);
        String id2 = insertIdMap(idm2);
        assertEquals(FAKE_FEDID2, id2);

        // Find them
        List<FederativeUserIdMap> idMaps = repo.findByDansUserId(FAKE_DANSID);
        assertEquals(2, idMaps.size());
        FederativeUserIdMap idMap1 = idMaps.get(0);
        assertEquals(FAKE_DANSID, idMap1.getDansUserId());
        FederativeUserIdMap idMap2 = idMaps.get(1);
        assertEquals(FAKE_DANSID, idMap2.getDansUserId());
        // test if both fedIds are in the result list
        // but we don't now the order
        List<String> ids = new ArrayList<String>();
        ids.add(idMaps.get(0).getId());
        ids.add(idMaps.get(1).getId());
        assertTrue(ids.contains(id1));
        assertTrue(ids.contains(id2));

        // Delete
        repo.delete(id1);
        assertFalse(repo.exists(id1));
        repo.delete(id2);
        assertFalse(repo.exists(id2));
    }

    private String insertIdMap(FederativeUserIdMap idm) throws RepositoryException, ObjectExistsException {
        // remove map if it already exists
        if (repo.exists(idm.getId()))
            repo.delete(idm);

        return repo.add(idm);
    }
}
