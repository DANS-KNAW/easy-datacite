package nl.knaw.dans.easy.web.view.dataset;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.AuthzStrategyTestImpl;
import nl.knaw.dans.easy.EasyApplicationContextMock;
import nl.knaw.dans.easy.EasyUserTestImpl;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.db.testutil.InMemoryDatabase;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.PermissionSequence.State;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.fedora.db.FedoraFileStoreAccess;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.powermock.api.easymock.PowerMock;

public class TabTestFixture {

    protected static final DmoStoreId DATASET_STORE_ID = new DmoStoreId(Dataset.NAMESPACE, "1");
    protected EasyApplicationContextMock applicationContext;
    private static DatasetImpl dataset;
    protected static InMemoryDatabase inMemoryDatabase;

    static public class PageWrapper extends DatasetViewPage {
        public PageWrapper() {
            super(dataset, DatasetViewPage.Mode.VIEW);
        }
    }

    private DatasetService mockDataset() throws Exception {
        final EasyUserImpl depositor = new EasyUserTestImpl("x:y");
        depositor.setInitials("D.E.");
        depositor.setSurname("Positor");
        dataset = new DatasetImpl(DATASET_STORE_ID.toString());
        dataset.setState(State.Submitted.toString());
        dataset.setAuthzStrategy(new AuthzStrategyTestImpl());

        // needed twice because considered dirty
        dataset.getAdministrativeMetadata().setDepositor(depositor);
        dataset.getAdministrativeMetadata().setDepositor(depositor);

        final DatasetService datasetService = PowerMock.createMock(DatasetService.class);
        expect(datasetService.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).andStubReturn(dataset);
        expect(datasetService.getAdditionalLicense(dataset)).andStubReturn(null);
        expect(datasetService.getLicenseVersions(dataset)).andStubReturn(null);
        expect(datasetService.getAdditionalLicenseVersions(dataset)).andStubReturn(null);

        return datasetService;
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

    @Before
    public void mockApplicationContext() throws Exception {
        applicationContext = new EasyApplicationContextMock();
        applicationContext.expectStandardSecurity();
        applicationContext.expectDefaultResources();
        applicationContext.expectDisciplineChoices();
        applicationContext.setDatasetService(mockDataset());
        applicationContext.expectNoJumpoff();
        applicationContext.expectNoAudioVideoFiles();
        applicationContext.putBean("fileStoreAccess", Data.getFileStoreAccess());
    }

    @After
    public void reset() {
        PowerMock.resetAll();
    }

    protected EasyWicketTester startPage() {
        final EasyWicketTester tester = EasyWicketTester.create(applicationContext);
        PowerMock.replayAll();
        tester.startPage(PageWrapper.class);
        return tester;
    }
}
