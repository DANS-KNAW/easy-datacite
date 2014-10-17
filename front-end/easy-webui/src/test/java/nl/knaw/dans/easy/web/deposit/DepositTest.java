package nl.knaw.dans.easy.web.deposit;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.powermock.api.easymock.PowerMock.createMock;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.easy.EasyApplicationContextMock;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.business.services.EasyDepositService;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.EasyStore;
import nl.knaw.dans.easy.db.testutil.InMemoryDatabase;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FolderItem;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainerImpl;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.fedora.db.FedoraFileStoreAccess;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.pf.language.emd.types.BasicString;

import org.apache.wicket.util.tester.FormTester;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

public class DepositTest {

    private static final DmoStoreId DISCIPLINE_STORE_ID = new DmoStoreId(DisciplineContainer.NAMESPACE, "1");
    private static final DmoStoreId DATASET_STORE_ID = new DmoStoreId(Dataset.NAMESPACE, "1");
    private EasyApplicationContextMock applicationContext;
    private static InMemoryDatabase inMemoryDB;

    private static class DisciplineContainerImplStub extends DisciplineContainerImpl {

        // somehow a mock is only seen properly by one test

        private final List<DisciplineContainer> subDisciplines = new ArrayList<DisciplineContainer>();

        public DisciplineContainerImplStub(final String storeId) {
            super(storeId);
        }

        @Override
        public List<DisciplineContainer> getSubDisciplines() throws DomainException {
            return subDisciplines;
        }

        private static final long serialVersionUID = 1L;
    }

    @BeforeClass
    public static void mockDB() throws Exception {

        inMemoryDB = new InMemoryDatabase();
        mockUploadedFiles();
        new Data().setFileStoreAccess(new FedoraFileStoreAccess());
    }

    @AfterClass
    public static void closeDB() throws Exception {

        inMemoryDB.close();

        // other tests should not accidently reuse this instance
        new Data().setFileStoreAccess(null);
    }

    @Before
    public void mockApplicationContext() throws Exception {

        applicationContext = new EasyApplicationContextMock();
        applicationContext.expectStandardSecurity();
        applicationContext.expectDefaultResources();
        applicationContext.expectNoJumpoff();
        applicationContext.expectNoAudioVideoFiles();
        applicationContext.expectAuthenticatedAsVisitor();
        applicationContext.setDepositService(new EasyDepositService());
        applicationContext.setDatasetService(createMock(DatasetService.class));
        mockNewDatasets();
        mockNoSubDisciplines();

        applicationContext.getDatasetService().saveEasyMetadata(isA(EasyUser.class), isA(Dataset.class));
        EasyMock.expectLastCall().anyTimes();
    }

    @After
    public void reset() {

        PowerMock.resetAll();
    }

    private static void mockUploadedFiles() throws Exception {

        final Dataset dataset = new DatasetImpl(DATASET_STORE_ID.getStoreId());
        final FolderItem folder = inMemoryDB.insertFolder(1, dataset, "someFolder");
        inMemoryDB.insertFile(1, dataset, "rootFile.txt", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.KNOWN);
        inMemoryDB.insertFile(2, folder, "subFile.txt", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.KNOWN);
        inMemoryDB.flush();
    }

    private void mockNoSubDisciplines() throws ObjectNotInStoreException, RepositoryException, DomainException {

        final DisciplineContainerImplStub rootDiscipline = new DisciplineContainerImplStub(DISCIPLINE_STORE_ID.getStoreId());
        final EasyStore easyStore = createMock(EasyStore.class);
        expect(easyStore.retrieve(isA(DmoStoreId.class))).andStubReturn(rootDiscipline);
        new Data().setEasyStore(easyStore); // no SpringBean injection in easy-business project
    }

    private void mockNewDatasets() throws Exception {

        final DatasetService datasetService = applicationContext.getDatasetService();
        for (final MetadataFormat mdf : MetadataFormat.values()) {
            final DatasetImpl dataset = new DatasetImpl(DATASET_STORE_ID.getStoreId(), mdf);
            provideContentForSuppliedMetadata(dataset);
            expect(datasetService.newDataset(eq(mdf))).andStubReturn(dataset);
        }
    }

    private void provideContentForSuppliedMetadata(final DatasetImpl dataset) {

        final List<BasicString> dcTitle = new ArrayList<BasicString>();
        dcTitle.add(new BasicString("just a test"));
        dataset.getEasyMetadata().getEmdTitle().setDcTitle(dcTitle);
    }

    @Ignore // speed up the build by skipping this debug tool
    @Test
    public void introPage() throws Exception {
        // page already dumped by {@link EditableContentPageTest#depositIntroPage()}
        // the logging allows to check for the proper argument of selectDepositTypeOnIntroPage
        startIntroPage().debugComponentTrees();
    }

    @Test
    public void archeaologyPages() throws Exception {

        final EasyWicketTester tester = selectDepositTypeOnIntroPage(0);
        tester.dumpPage("1");

        for (int i = 2; i < 8; i++) {
            switchPage(tester, i);
            tester.dumpPage("" + i);
        }
    }

    @Test
    public void historyPages() throws Exception {

        final EasyWicketTester tester = selectDepositTypeOnIntroPage(1);
        tester.dumpPage("1");

        for (int i = 2; i < 5; i++) {
            switchPage(tester, i);
            tester.dumpPage("" + i);
        }
    }

    @Test
    public void socialAndBehaviouralPages() throws Exception {

        final EasyWicketTester tester = selectDepositTypeOnIntroPage(2);
        tester.dumpPage("1");

        for (int i = 2; i < 5; i++) {
            switchPage(tester, i);
            tester.dumpPage("" + i);
        }
    }

    @Test
    public void lifeScienceAndMedicinePages() throws Exception {

        final EasyWicketTester tester = selectDepositTypeOnIntroPage(3);
        tester.dumpPage("1");

        for (int i = 2; i < 5; i++) {
            switchPage(tester, i);
            tester.dumpPage("" + i);
        }
    }

    @Test
    public void languageAndLiterature() throws Exception {

        final EasyWicketTester tester = selectDepositTypeOnIntroPage(5);
        tester.dumpPage("1");

        for (int i = 2; i < 5; i++) {
            switchPage(tester, i);
            tester.dumpPage("" + i);
        }
    }

    @Test
    public void otherSources() throws Exception {

        final EasyWicketTester tester = selectDepositTypeOnIntroPage(5);
        tester.dumpPage("1a");
        addSecondCreator(tester);

        switchPage(tester, 2);
        tester.dumpPage("2");

        switchPage(tester, 1);
        tester.dumpPage("1b");// now changes by addSecondCreator are visible, for example the minus button

        switchPage(tester, 3);
        tester.dumpPage("3");

        switchPage(tester, 4);
        tester.dumpPage("4");
    }

    private void addSecondCreator(final EasyWicketTester tester) throws Exception {

        tester.debugComponentTrees();// log the paths
        final String formPath = "depositPanel:depositForm";
        final String creatorPath = "recursivePanel:levelContainer:recursivePanelContainer:recursivePanels:1:recursivePanel:listViewContainer:listView:";

        tester.clickLink(formPath + ":" + creatorPath + "0:buttonsHolder:plusLink");
        // a page dump at this moment only shows the HTML of ajax changes
        // that are just the new fields without the context of the page

        // to keep the added field when returning to the current page, they need values
        final FormTester formTester = tester.newFormTester(formPath);
        formTester.setValue(creatorPath + "0:repeatingPanel:initialsField", "initialsvalue");
        formTester.setValue(creatorPath + "0:repeatingPanel:surnameField", "surnamevalue");
        formTester.setValue(creatorPath + "1:repeatingPanel:initialsField", "initialsvalueToo");
        formTester.setValue(creatorPath + "1:repeatingPanel:surnameField", "surnamevalueToo");
    }

    private void switchPage(final EasyWicketTester tester, final int i) {
        tester.clickLink("depositPanel:depositForm:navigationPanel:pageLinkContainer:listView:" + (i - 1) + ":pageLink");
    }

    private EasyWicketTester selectDepositTypeOnIntroPage(final int string) {
        final EasyWicketTester tester = startIntroPage();
        tester.clickLink("disciplines:" + string + ":startDepositLink");
        tester.assertRenderedPage(DepositPage.class);
        return tester;
    }

    private EasyWicketTester startIntroPage() {

        PowerMock.replayAll();
        final EasyWicketTester tester = EasyWicketTester.create(applicationContext);
        tester.startPage(new DepositIntroPage());
        return tester;
    }
}
