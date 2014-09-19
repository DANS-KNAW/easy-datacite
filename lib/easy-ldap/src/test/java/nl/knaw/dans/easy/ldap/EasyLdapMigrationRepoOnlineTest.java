package nl.knaw.dans.easy.ldap;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.exception.ObjectExistsException;
import nl.knaw.dans.common.lang.test.Tester;
import nl.knaw.dans.easy.domain.migration.IdMap;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;

public class EasyLdapMigrationRepoOnlineTest extends AbstractOnlineTest {

    private static EasyLdapMigrationRepo repo;

    @BeforeClass
    public static void beforeClass() {
        repo = new EasyLdapMigrationRepo(getLdapClient(), Tester.getString("ldap.context.migration"));
    }

    @Test
    public void add_update_delete() throws Exception {
        DateTime migrationDate = new DateTime();
        IdMap idm1 = new IdMap("easy-dataset:123", "twips.dans.knaw.nl--7354684735049108415-1167745265100", "urn:nbn:nl:ui:13-9hp-3ap", migrationDate);

        // remove player
        if (repo.exists(idm1.getId()))
            repo.delete(idm1);

        String id = repo.add(idm1);
        assertEquals("easy-dataset:123", id);

        IdMap idm2 = repo.findById(id);
        assertEquals("easy-dataset:123", idm2.getId());
        assertEquals("twips.dans.knaw.nl--7354684735049108415-1167745265100", idm2.getAipId());
        assertEquals("urn:nbn:nl:ui:13-9hp-3ap", idm2.getPersistentIdentifier());
        assertEquals(migrationDate.year(), idm2.getMigrationDate().year());
        assertEquals(migrationDate.hourOfDay(), idm2.getMigrationDate().hourOfDay());
        assertEquals(migrationDate.secondOfDay(), idm2.getMigrationDate().secondOfDay());

        DateTime newMigrationDate = new DateTime().plusDays(2);
        IdMap idm3 = new IdMap("easy-dataset:123", "twips.dans.knaw.nl--anders", "ook anders", newMigrationDate);

        repo.update(idm3);

        IdMap idm4 = repo.findById(id);
        assertEquals("easy-dataset:123", idm4.getId());
        assertEquals("twips.dans.knaw.nl--anders", idm4.getAipId());
        assertEquals("ook anders", idm4.getPersistentIdentifier());
        assertEquals(newMigrationDate.year(), idm4.getMigrationDate().year());
        assertEquals(newMigrationDate.hourOfDay(), idm4.getMigrationDate().hourOfDay());
        assertEquals(newMigrationDate.secondOfDay(), idm4.getMigrationDate().secondOfDay());

    }

    @Test
    public void findByAipId() throws Exception {
        String aipId = "twips.dans.knaw.nl--7354684735049108415-1167745265100";
        String persistentIdentifier = "urn:nbn:nl:ui:13-9hp-3ap";
        String id1 = insertFirst(aipId, persistentIdentifier);
        String id2 = insertSecond(aipId, persistentIdentifier);

        List<IdMap> idMaps = repo.findByAipId(aipId);
        assertEquals(2, idMaps.size());
        List<String> ids = new ArrayList<String>();
        ids.add(idMaps.get(0).getId());
        ids.add(idMaps.get(1).getId());
        assertTrue(ids.contains(id1));
        assertTrue(ids.contains(id2));
    }

    @Test
    public void findByAipId2() throws Exception {
        List<IdMap> idMaps = repo.findByAipId("this.aipId.does.not.exist");
        assertTrue(idMaps.isEmpty());
    }

    @Test
    public void findByPersistentIdentifier() throws Exception {
        String aipId = "twips.dans.knaw.nl--7354684735049108415-1167745265100";
        String persistentIdentifier = "urn:nbn:nl:ui:13-9hp-3ap";
        String id1 = insertFirst(aipId, persistentIdentifier);
        String id2 = insertSecond(aipId, persistentIdentifier);

        List<IdMap> idMaps = repo.findByPersistentIdentifier(persistentIdentifier);
        assertEquals(2, idMaps.size());
        List<String> ids = new ArrayList<String>();
        ids.add(idMaps.get(0).getId());
        ids.add(idMaps.get(1).getId());
        assertTrue(ids.contains(id1));
        assertTrue(ids.contains(id2));
    }

    @Test
    public void getMostRecentByAipId() throws Exception {
        String aipId = "twips.dans.knaw.nl--7354684735049108415-1167745265100";
        String persistentIdentifier = "urn:nbn:nl:ui:13-9hp-3ap";
        insertFirst(aipId, persistentIdentifier);
        String id2 = insertSecond(aipId, persistentIdentifier);

        assertEquals(id2, repo.getMostRecentByAipId(aipId).getId());
    }

    @Test
    public void getMostRecentByPersistentIdentifier() throws Exception {
        String aipId = "twips.dans.knaw.nl--7354684735049108415-1167745265100";
        String persistentIdentifier = "urn:nbn:nl:ui:13-9hp-3ap";
        insertFirst(aipId, persistentIdentifier);
        String id2 = insertSecond(aipId, persistentIdentifier);

        assertEquals(id2, repo.getMostRecentByPersistentIdentifier(persistentIdentifier).getId());
    }

    @Test
    public void deleteAllEntries() throws Exception {
        String aipId = "twips.dans.knaw.nl--tobedeleted";
        String persistentIdentifier = "urn:nbn:nl:ui:13-9hp-3ap";
        insertFirst(aipId, persistentIdentifier);
        insertSecond(aipId, persistentIdentifier);

        List<String> ids = repo.findAllEntries(0);
        assertTrue(ids.size() >= 2);

        for (String id : ids) {
            repo.delete(id);
        }

        ids = repo.findAllEntries(100);
        assertEquals(0, ids.size());
    }

    private String insertFirst(String aipId, String persistentIdentifier) throws RepositoryException, ObjectExistsException {
        DateTime migrationDate1 = new DateTime().minusMinutes(1);
        IdMap idm1 = new IdMap("easy-dataset:123", aipId, persistentIdentifier, migrationDate1);

        return insertIdMap(idm1);
    }

    private String insertSecond(String aipId, String persistentIdentifier) throws RepositoryException, ObjectExistsException {
        DateTime migrationDate2 = new DateTime();
        IdMap idm2 = new IdMap("easy-dataset:124", aipId, persistentIdentifier, migrationDate2);

        return insertIdMap(idm2);
    }

    private String insertIdMap(IdMap idm) throws RepositoryException, ObjectExistsException {
        // remove player
        if (repo.exists(idm.getId()))
            repo.delete(idm);

        return repo.add(idm);
    }
}
