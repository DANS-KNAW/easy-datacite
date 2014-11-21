package nl.knaw.dans.easy.servicelayer;

import static org.hamcrest.core.Is.is;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.user.User.State;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.db.testutil.InMemoryDatabase;
import nl.knaw.dans.easy.domain.dataset.AdministrativeMetadataImpl;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.AdministrativeMetadata;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FolderItem;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.model.user.Group;
import nl.knaw.dans.easy.domain.user.EasyUserAnonymous;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.domain.user.GroupImpl;
import nl.knaw.dans.easy.fedora.db.FedoraFileStoreAccess;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class DownloadFilterIntegrationTest {

    // A mocked FileStoreAcess has more chances on mistakes than the tested class so we skip the pure unit test.
    // With a real FedoraFileStoreAccess it is an integration test so it belongs where both are available.

    private static InMemoryDatabase inMemoryDB;
    private static final List<ItemVO> files = new ArrayList<ItemVO>();
    private static final List<ItemVO> folders = new ArrayList<ItemVO>();
    private static boolean permissionGranted;
    private static DatasetImpl dataset = new DatasetImpl(new DmoStoreId(Dataset.NAMESPACE, "1").getStoreId()) {
        private static final long serialVersionUID = 1L;

        public boolean isPermissionGrantedTo(EasyUser user) {
            return permissionGranted;
        }
    };

    @Before
    public void defaultValue() throws Exception {
        permissionGranted = false;
    };

    @BeforeClass
    public static void initDB() throws Exception {
        final AdministrativeMetadata amd = new AdministrativeMetadataImpl();
        amd.setAdministrativeState(DatasetState.PUBLISHED);
        dataset.setAdministrativeMetadata(amd);

        inMemoryDB = new InMemoryDatabase();

        inMemoryDB.insertFolder(1, dataset, "a");
        files.add(new FileItemVO(inMemoryDB.insertFile(1, dataset, "a/x.y", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.NONE)));
        files.add(new FileItemVO(inMemoryDB.insertFile(2, dataset, "a/x.y", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.ANONYMOUS)));
        files.add(new FileItemVO(inMemoryDB.insertFile(3, dataset, "a/x.y", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.KNOWN)));
        files.add(new FileItemVO(inMemoryDB.insertFile(4, dataset, "a/x.y", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.RESTRICTED_GROUP)));
        files.add(new FileItemVO(inMemoryDB.insertFile(5, dataset, "a/x.y", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.RESTRICTED_REQUEST)));
        inMemoryDB.insertFile(6, addNewFolder(2), "a1/x.y", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.NONE);
        inMemoryDB.insertFile(7, addNewFolder(3), "a2/x.y", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.ANONYMOUS);
        inMemoryDB.insertFile(8, addNewFolder(4), "a3/x.y", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.KNOWN);
        inMemoryDB.insertFile(9, addNewFolder(5), "a4/x.y", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.RESTRICTED_GROUP);
        inMemoryDB.insertFile(10, addNewFolder(6), "a5/x.y", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.RESTRICTED_REQUEST);
        inMemoryDB.flush();
        new Data().setFileStoreAccess(new FedoraFileStoreAccess());
    }

    private static FolderItem addNewFolder(int id) throws Exception {
        FolderItem folder = inMemoryDB.insertFolder(id, dataset, "a" + id);
        folders.add(new FolderItemVO(folder));
        return folder;
    }

    @AfterClass
    public static void closeDB() {
        inMemoryDB.close();
    }

    @Test
    public void anonymous() throws Exception {
        test(EasyUserAnonymous.getInstance(), 1, 1);
    }

    @Test
    public void active() throws Exception {
        test(createActiveUser(), 2, 2);
    }

    @Test
    public void archivist() throws Exception {
        final EasyUserImpl sessionUser = createActiveUser();
        sessionUser.addRole(Role.ARCHIVIST);
        test(sessionUser, 5, 5);
    }

    @Test
    public void archeologist() throws Exception {
        testGroup(Group.ID_ARCHEOLOGY, 3, 3);
    }

    @Test
    public void historian() throws Exception {
        testGroup(Group.ID_HISTORY, 3, 3);
    }

    @Test
    public void permissionRequests() throws Exception {
        permissionGranted = true;
        testGroup(Group.ID_HISTORY, 4, 4);
    }

    private void testGroup(final String idArcheology, final int expectedNrOfFiles, final int expectedNrOfFolders) throws DomainException {
        final GroupImpl group = new GroupImpl(idArcheology);
        final EasyUserImpl sessionUser = createActiveUser();
        sessionUser.addGroupId(group.getId());
        dataset.addGroup(group);
        test(sessionUser, expectedNrOfFiles, expectedNrOfFolders);
        dataset.removeGroup(group);
    }

    private EasyUserImpl createActiveUser() {
        final EasyUserImpl sessionUser = new EasyUserImpl();
        sessionUser.setState(State.ACTIVE);
        return sessionUser;
    }

    private void test(final EasyUser sessionUser, final int expectedNrOfFiles, int expectedNrOfFolders) throws DomainException {
        Assert.assertThat("number of folders", new DownloadFilter(sessionUser, dataset, Data.getFileStoreAccess()).apply(folders).size(),
                is(expectedNrOfFolders));
        Assert.assertThat("number of files", new DownloadFilter(sessionUser, dataset, Data.getFileStoreAccess()).apply(files).size(), is(expectedNrOfFiles));
    }
}
