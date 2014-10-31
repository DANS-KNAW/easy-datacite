package nl.knaw.dans.easy.web.view.dataset;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;

import java.util.ArrayList;
import java.util.Collection;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.user.User;
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
import nl.knaw.dans.easy.domain.user.EasyUserAnonymous;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.fedora.db.FedoraFileStoreAccess;
import nl.knaw.dans.easy.security.authz.EasyItemContainerAuthzStrategy;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.pf.language.emd.types.BasicString;

import org.apache.wicket.PageParameters;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.powermock.api.easymock.PowerMock;

@RunWith(Parameterized.class)
public class FileExplorerParameterizedTest {

    private static InMemoryDatabase inMemoryDatabase;
    private static DatasetImpl dataset;
    private EasyApplicationContextMock applicationContext;
    private final UserType userType;
    private final AccessCategory accessCategory;
    private EasyUserImpl sessionUser;

    enum UserType {
        ARCHIVIST, DEPOSITOR, KNOWN, ANONYMOUS;
    }

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

    @After
    public void clearDB() {

        PowerMock.resetAll();
        inMemoryDatabase.deleteAll(FileItemVO.class);
        inMemoryDatabase.deleteAll(FolderItemVO.class);
        inMemoryDatabase.flush();
    }

    public FileExplorerParameterizedTest(final UserType userType, final AccessCategory accessCategory) throws Exception {

        this.userType = userType;
        this.accessCategory = accessCategory;

        mockApplicationContext();
        switch (userType) {
        case ARCHIVIST:
            mockLogin(Role.ARCHIVIST);
            mockDataset(createDepositor());
            setDatasetAuthzStrategy(sessionUser);
            break;
        case DEPOSITOR:
            mockLogin(Role.USER);
            mockDataset(sessionUser);
            setDatasetAuthzStrategy(sessionUser);
            break;
        case KNOWN:
            mockLogin(Role.USER);
            mockDataset(createDepositor());
            setDatasetAuthzStrategy(sessionUser);
            break;
        case ANONYMOUS:
            mockDataset(createDepositor());
            setDatasetAuthzStrategy(EasyUserAnonymous.getInstance());
            break;
        default:
            break;
        }
        addMixedPermissionFilesToDataset();
        inMemoryDatabase.flush();

        PowerMock.replayAll();
    }

    private void mockLogin(Role user) throws ServiceException {

        sessionUser = applicationContext.expectAuthenticatedAsVisitor();
        sessionUser.addRole(user);
    }

    private void mockDataset(final EasyUserImpl depositor) throws Exception {

        final String datasetStoreId = new DmoStoreId(Dataset.NAMESPACE, "1").toString();
        dataset = new DatasetImpl(datasetStoreId);
        dataset.setState(DatasetState.PUBLISHED.toString());
        dataset.getEasyMetadata().getEmdRights().setAccessCategory(accessCategory);

        // show test parameters on the dumped page
        dataset.getEasyMetadata().getEmdTitle().getDcTitle().add(new BasicString("user: " + userType + " dataset rights: " + accessCategory));

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

    private void setDatasetAuthzStrategy(User user) {
        dataset.setAuthzStrategy(new EasyItemContainerAuthzStrategy(user, dataset, dataset) {
            // need a subclass because the constructors are protected
            private static final long serialVersionUID = 1L;
        });
    }

    private void addMixedPermissionFilesToDataset() throws Exception {

        int fileNr = 0;
        int folderNr = 0;
        final FolderItem mainFolder = inMemoryDatabase.insertFolder(++folderNr, dataset, "mainfolder");
        for (final CreatorRole creatorRole : CreatorRole.values())
            for (final VisibleTo visibleTo : VisibleTo.values())
                for (final AccessibleTo accessibleTo : AccessibleTo.values()) {
                    final FolderItem folder = inMemoryDatabase.insertFolder(++folderNr, dataset, pad(folderNr) + "folder");
                    inMemoryDatabase.insertFile(++fileNr, dataset, pad(fileNr) + "mainfile.txt", creatorRole, visibleTo, accessibleTo);
                    inMemoryDatabase.insertFile(++fileNr, folder, "subfile.txt", creatorRole, visibleTo, accessibleTo);
                    inMemoryDatabase.insertFile(++fileNr, mainFolder, pad(fileNr) + "subfile.txt", creatorRole, visibleTo, accessibleTo);
                }
    }

    private String pad(int fileNr) {
        return String.format("%03d", fileNr);
    }

    private EasyUserImpl createDepositor() {

        final EasyUserImpl depositor = new EasyUserTestImpl("x:y");
        depositor.setInitials("D.E.");
        depositor.setSurname("Positor");
        return depositor;
    }

    public void mockApplicationContext() throws Exception {

        applicationContext = new EasyApplicationContextMock();
        applicationContext.expectStandardSecurity();
        applicationContext.expectDefaultResources();
        applicationContext.expectNoAudioVideoFiles();
        applicationContext.putBean("fileStoreAccess", Data.getFileStoreAccess());
    }

    @Parameters
    public static Collection<Object[]> getConstructorSignatures() {
        final ArrayList<Object[]> signatures = new ArrayList<Object[]>();
        for (final UserType userType : UserType.values())
            for (final AccessCategory accessCategory : AccessCategory.values()) {
                final Object[] signature = {userType, accessCategory};
                signatures.add(signature);
            }
        return signatures;
    }

    @Test
    public void render() throws Exception {

        try {
            final PageParameters parameters = new PageParameters();
            parameters.add("id", dataset.getStoreId());
            parameters.add("tab", "2");
            final EasyWicketTester tester = EasyWicketTester.create(applicationContext);
            tester.startPage(new DatasetViewPage(parameters));
            tester.assertRenderedPage(DatasetViewPage.class);
            tester.clickLink("tabs:tabs-container:tabs:2:link");
            tester.dumpPage(userType + "-" + accessCategory);
        }
        catch (Exception e) {
            throw new Exception(userType + " " + accessCategory + " " + e.getMessage(), e);
        }
    }
}
