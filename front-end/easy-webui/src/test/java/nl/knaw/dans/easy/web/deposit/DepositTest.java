package nl.knaw.dans.easy.web.deposit;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.powermock.api.easymock.PowerMock.createMock;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.EasyApplicationContextMock;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.FileStoreMocker;
import nl.knaw.dans.easy.TestUtil;
import nl.knaw.dans.easy.business.services.EasyDepositService;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FolderItem;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;
import nl.knaw.dans.pf.language.emd.types.BasicString;

import org.apache.wicket.util.tester.FormTester;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DepositTest {
    private static final String LIST_VIEW = ":recursivePanel:listViewContainer:listView:";

    private static final String PANELS = "recursivePanel:levelContainer:recursivePanels:";

    private static final Logger LOGGER = LoggerFactory.getLogger(DepositTest.class);

    private final DmoStoreId DISCIPLINE_STORE_ID = new DmoStoreId(DisciplineContainer.NAMESPACE, "root");
    private final DmoStoreId DATASET_STORE_ID = new DmoStoreId(Dataset.NAMESPACE, "1");
    private EasyApplicationContextMock applicationContext;
    private FileStoreMocker fileStoreMocker;

    @Before
    public void mockApplicationContext() throws Exception {

        applicationContext = new EasyApplicationContextMock();
        fileStoreMocker = new FileStoreMocker();
        mockUploadedFiles();
        new Data().setFileStoreAccess(fileStoreMocker.getFileStoreAccess());
        applicationContext.expectStandardSecurity();
        applicationContext.expectDefaultResources();
        applicationContext.expectNoJumpoff();
        applicationContext.expectNoAudioVideoFiles();
        applicationContext.expectAuthenticatedAsVisitor();
        applicationContext.expectDisciplineObject(DISCIPLINE_STORE_ID, new ArrayList<DisciplineContainer>());
        applicationContext.setDepositService(new EasyDepositService());
        applicationContext.setDatasetService(createMock(DatasetService.class));
        mockNewDatasets();

        applicationContext.getDatasetService().saveEasyMetadata(isA(EasyUser.class), isA(Dataset.class));
        EasyMock.expectLastCall().anyTimes();
    }

    @After
    public void reset() throws Exception {
        TestUtil.cleanup();
        fileStoreMocker.close();
    }

    private void mockUploadedFiles() throws Exception {

        final Dataset dataset = new DatasetImpl(DATASET_STORE_ID.getStoreId());
        fileStoreMocker.insertRootFolder(dataset);
        final FolderItem folder = fileStoreMocker.insertFolder(1, dataset, "someFolder");
        fileStoreMocker.insertFile(1, dataset, "rootFile.txt", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.KNOWN);
        fileStoreMocker.insertFile(2, folder, "subFile.txt", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.KNOWN);
        fileStoreMocker.logContent(LOGGER);
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

    @Test
    public void browsePages() throws Exception {

        final EasyWicketTester tester = selectStartDeposit();
        tester.dumpPage("1");

        // trouble with the clarin radio button prevents selecting another page:
        // A possible reason is that component hierarchy changed between rendering and form submission.
        // for (int i = 2; i < 6; i++) {
        // switchPage(tester, i);
        // tester.dumpPage("" + i);
        // }
    }

    @Ignore("FIX WicketRuntimeException: Trying to select on null component (selectAccessRights)")
    @Test
    public void changeValues() throws Exception {

        final EasyWicketTester tester = selectStartDeposit();
        tester.dumpPage("1a");
        tester.debugComponentTrees();
        selectAccessRights(tester, 1); // FIXME moved to another page
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

    private void selectAccessRights(final EasyWicketTester tester, int index) {

        // TODO this attempt does not fix the WicketRuntimeException:
        // submitted http post value [...] for RadioGroup component [...] is illegal because it does not point to a Radio component.
        FormTester formTester = tester.newFormTester("depositPanel:depositForm");
        formTester.select(PANELS + "5:" + PANELS + "0" + LIST_VIEW + "0:repeatingPanel:radioList:choiceList", index);
    }

    private void addSecondCreator(final EasyWicketTester tester) throws Exception {

        final String formPath = "depositPanel:depositForm";
        final String creatorPath = PANELS + "1" + LIST_VIEW;

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

    private EasyWicketTester selectStartDeposit() {
        final EasyWicketTester tester = startIntroPage();
        tester.clickLink("disciplines:0:startDepositLink");
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
