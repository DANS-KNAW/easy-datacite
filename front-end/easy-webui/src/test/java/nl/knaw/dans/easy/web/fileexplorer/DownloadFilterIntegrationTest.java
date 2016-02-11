package nl.knaw.dans.easy.web.fileexplorer;

import static org.hamcrest.core.Is.is;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.user.User.State;
import nl.knaw.dans.easy.FileStoreMocker;
import nl.knaw.dans.easy.TestUtil;
import nl.knaw.dans.easy.data.Data;
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
import nl.knaw.dans.easy.servicelayer.DownloadFilter;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DownloadFilterIntegrationTest {

    // A mocked FileStoreAcess has more chances on mistakes than the tested class so we skip the pure unit test.
    // With a real FedoraFileStoreAccess it is an integration test and we need InMemoryDatabase.

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadFilterIntegrationTest.class);
    private FileStoreMocker fileStoreMocker;
    private final List<ItemVO> files = new ArrayList<ItemVO>();
    private final List<ItemVO> folders = new ArrayList<ItemVO>();
    private boolean permissionGranted;
    private DatasetImpl dataset = new DatasetImpl(new DmoStoreId(Dataset.NAMESPACE, "1").getStoreId()) {
        private static final long serialVersionUID = 1L;

        public boolean isPermissionGrantedTo(EasyUser user) {
            return permissionGranted;
        }
    };

    @Before
    public void defaultValue() throws Exception {
        permissionGranted = false;
    };

    @Before
    public void initDB() throws Exception {
        final AdministrativeMetadata amd = new AdministrativeMetadataImpl();
        amd.setAdministrativeState(DatasetState.PUBLISHED);
        dataset.setAdministrativeMetadata(amd);

        fileStoreMocker = new FileStoreMocker();

        fileStoreMocker.insertRootFolder(dataset);
        files.add(new FileItemVO(fileStoreMocker.insertFile(1, dataset, "a/x.y", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.NONE)));
        files.add(new FileItemVO(fileStoreMocker.insertFile(2, dataset, "a/x.y", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.ANONYMOUS)));
        files.add(new FileItemVO(fileStoreMocker.insertFile(3, dataset, "a/x.y", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.KNOWN)));
        files.add(new FileItemVO(fileStoreMocker.insertFile(4, dataset, "a/x.y", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.RESTRICTED_GROUP)));
        files.add(new FileItemVO(fileStoreMocker.insertFile(5, dataset, "a/x.y", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.RESTRICTED_REQUEST)));
        fileStoreMocker.insertFile(6, addNewFolder(2), "a1/x.y", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.NONE);
        fileStoreMocker.insertFile(7, addNewFolder(3), "a2/x.y", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.ANONYMOUS);
        fileStoreMocker.insertFile(8, addNewFolder(4), "a3/x.y", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.KNOWN);
        fileStoreMocker.insertFile(9, addNewFolder(5), "a4/x.y", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.RESTRICTED_GROUP);
        fileStoreMocker.insertFile(10, addNewFolder(6), "a5/x.y", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.RESTRICTED_REQUEST);
        fileStoreMocker.logContent(LOGGER);
        new Data().setFileStoreAccess(fileStoreMocker.getFileStoreAccess());
    }

    private FolderItem addNewFolder(int id) throws Exception {
        FolderItem folder = fileStoreMocker.insertFolder(id, dataset, "a" + id);
        folders.add(new FolderItemVO(folder));
        return folder;
    }

    @After
    public void cleanup() throws Exception {
        TestUtil.cleanup();
        fileStoreMocker.close();
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
