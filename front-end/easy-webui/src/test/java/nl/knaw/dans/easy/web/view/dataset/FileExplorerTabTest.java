package nl.knaw.dans.easy.web.view.dataset;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.AuthzStrategyTestImpl;
import nl.knaw.dans.easy.EasyApplicationContextMock;
import nl.knaw.dans.easy.EasyUserTestImpl;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.db.testutil.InMemoryDatabase;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FolderItem;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.fedora.db.FedoraFileStoreAccess;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.pf.language.emd.EasyMetadataImpl;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;

import org.apache.wicket.PageParameters;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

public class FileExplorerTabTest {

    private static final DmoStoreId DATASET_STORE_ID = new DmoStoreId(Dataset.NAMESPACE, "1");
    private static InMemoryDatabase inMemoryDatabase;
    private static DatasetImpl dataset;
    private EasyApplicationContextMock applicationContext;

    @BeforeClass
    public static void initDB() throws Exception {

        inMemoryDatabase = new InMemoryDatabase();
        new Data().setFileStoreAccess(new FedoraFileStoreAccess());
    }

    @AfterClass
    public static void closeDB() throws Exception {

        inMemoryDatabase.close();
        new Data().setFileStoreAccess(null);
    }

    @Before
    public void mockApplicationContext() throws Exception {

        applicationContext = new EasyApplicationContextMock();
        applicationContext.expectStandardSecurity();
        applicationContext.expectDefaultResources();
        applicationContext.expectDisciplineChoices();
        applicationContext.expectNoJumpoff();
        applicationContext.expectNoAudioVideoFiles();
        applicationContext.putBean("fileStoreAccess", Data.getFileStoreAccess());
    }

    @After
    public void clearDB() {

        PowerMock.resetAll();
        inMemoryDatabase.deleteAll(FileItemVO.class);
        inMemoryDatabase.deleteAll(FolderItemVO.class);
        inMemoryDatabase.flush();
    }

    @Test
    public void asArchivist() throws Exception {

        mockDataset(createDepositor(), AccessCategory.ANONYMOUS_ACCESS);
        applicationContext.expectAuthenticatedAsVisitor().addRole(Role.ARCHIVIST);
        insertMixedPermissionFiles();

        renderFileExplorerTab().dumpPage();
    }

    @Test
    public void asVisitor() throws Exception {

        mockDataset(createDepositor(), AccessCategory.ANONYMOUS_ACCESS);
        applicationContext.expectAuthenticatedAsVisitor().addRole(Role.USER);
        insertMixedPermissionFiles();

        renderFileExplorerTab().dumpPage();
    }

    @Test
    public void asDepositor() throws Exception {

        final EasyUserImpl sessionUser = applicationContext.expectAuthenticatedAsVisitor();
        sessionUser.addRole(Role.USER);
        mockDataset(sessionUser, AccessCategory.ANONYMOUS_ACCESS);
        insertMixedPermissionFiles();

        renderFileExplorerTab().dumpPage();
    }

    @Test
    public void asAnonymous() throws Exception {

        mockDataset(createDepositor(), AccessCategory.ANONYMOUS_ACCESS);
        insertMixedPermissionFiles();

        renderFileExplorerTab().dumpPage();
    }

    @Test
    public void visibleToAnonymous() throws Exception {

        final String folderPath = "folder";
        mockDataset(createDepositor(), AccessCategory.OPEN_ACCESS_FOR_REGISTERED_USERS);
        final FolderItem folder = inMemoryDatabase.insertFolder(1, dataset, folderPath);
        inMemoryDatabase.insertFile(1, folder, "mainfile.txt", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.KNOWN);

        final EasyWicketTester tester = renderFileExplorerTab();
        tester.dumpPage();
        // tester.assertLabel("tabs:panel:fe:explorer:datatable:body:rows:1", folderPath);
    }

    private void insertMixedPermissionFiles() throws Exception {

        final FolderItem folder = inMemoryDatabase.insertFolder(1, dataset, "folder");
        int i = 0;
        for (final CreatorRole creatorRole : CreatorRole.values())
            for (final VisibleTo visibleTo : VisibleTo.values())
                for (final AccessibleTo accessibleTo : AccessibleTo.values()) {
                    inMemoryDatabase.insertFile(++i, folder, "folder/" + i + "subfile.txt", creatorRole, visibleTo, accessibleTo);
                    inMemoryDatabase.insertFile(++i, dataset, i + "mainfile.txt", creatorRole, visibleTo, accessibleTo);
                }
    }

    private void mockDataset(final EasyUserImpl depositor, final AccessCategory accesCategory) throws Exception {

        final EasyMetadataImpl emd = new EasyMetadataImpl(MetadataFormat.UNSPECIFIED);
        emd.getEmdRights().setAccessCategory(accesCategory);

        dataset = new DatasetImpl(DATASET_STORE_ID.toString(), emd);
        dataset.setState(DatasetState.PUBLISHED.toString());
        dataset.setAuthzStrategy(new AuthzStrategyTestImpl());

        // needed twice because considered dirty
        dataset.getAdministrativeMetadata().setDepositor(depositor);
        dataset.getAdministrativeMetadata().setDepositor(depositor);

        final DatasetService datasetService = PowerMock.createMock(DatasetService.class);
        expect(datasetService.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).andStubReturn(dataset);
        expect(datasetService.getAdditionalLicense(dataset)).andStubReturn(null);
        expect(datasetService.getLicenseVersions(dataset)).andStubReturn(null);
        expect(datasetService.getAdditionalLicenseVersions(dataset)).andStubReturn(null);

        applicationContext.setDatasetService(datasetService);
    }

    private EasyUserImpl createDepositor() {

        final EasyUserImpl depositor = new EasyUserTestImpl("x:y");
        depositor.setInitials("D.E.");
        depositor.setSurname("Positor");
        return depositor;
    }

    protected EasyWicketTester renderFileExplorerTab() {

        inMemoryDatabase.flush();
        PowerMock.replayAll();

        final EasyWicketTester tester = EasyWicketTester.create(applicationContext);
        startPage(tester);
        tester.clickLink("tabs:tabs-container:tabs:2:link");
        return tester;
    }

    private void startPage(final EasyWicketTester tester) {
        final PageParameters parameters = new PageParameters();
        parameters.add("id", DATASET_STORE_ID.toString());
        parameters.add("tab", "2");
        tester.startPage(new DatasetViewPage(parameters));
        tester.assertRenderedPage(DatasetViewPage.class);
    }
}
