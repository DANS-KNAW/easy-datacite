package nl.knaw.dans.easy.web.deposit;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.fail;
import static org.powermock.api.easymock.PowerMock.createMock;

import java.util.ArrayList;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.EasyApplicationContextMock;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.business.services.EasyDepositService;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.EasyStore;
import nl.knaw.dans.easy.db.testutil.InMemoryDatabase;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.fedora.db.FedoraFileStoreAccess;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.servicelayer.services.DepositService;
import nl.knaw.dans.easy.servicelayer.services.ItemService;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.template.AbstractEasyPage;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;

import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.tester.FormTester;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

public class DepositTest {
    private static final DmoStoreId DATASET_STORE_ID = new DmoStoreId(Dataset.NAMESPACE, "1");
    private EasyApplicationContextMock applicationContext;
    private InMemoryDatabase inMemoryDB;

    public static class DebugPage extends AbstractEasyPage {

        @SpringBean(name = "datasetService")
        private DatasetService datasetService;

        @SpringBean(name = "depositService")
        private DepositService depositService;

        public DatasetService getDatasetService() {
            return datasetService;
        }

        public DepositService getDepositService() {
            return depositService;
        }
    }

    @Before
    public void mockApplicationContext() throws Exception {

        inMemoryDB = new InMemoryDatabase();
        applicationContext = new EasyApplicationContextMock();
        applicationContext.expectStandardSecurity();
        applicationContext.expectDefaultResources();
        applicationContext.expectNoJumpoff();
        applicationContext.expectNoAudioVideoFiles();
        applicationContext.expectAuthenticatedAsVisitor();
        applicationContext.setDepositService(new EasyDepositService());
        applicationContext.setDatasetService(createMock(DatasetService.class));
        mockNewDatasets();
        mockNoChildItems();

        applicationContext.getDatasetService().saveEasyMetadata(isA(EasyUser.class), isA(Dataset.class));
        EasyMock.expectLastCall().anyTimes();

        // workaround because SpringBean injection fails
        new Services().setDatasetService(applicationContext.getDatasetService());
        new Services().setDepositService(applicationContext.getDepositService());
        new Services().setItemService(applicationContext.getItemService());
        new Data().setFileStoreAccess(new FedoraFileStoreAccess());
    }

    private void mockNoSubDisciplines() throws ObjectNotInStoreException, RepositoryException, DomainException {

        final EasyStore easyStore = createMock(EasyStore.class);
        final DisciplineContainer disciplineContainer = createMock(DisciplineContainer.class);
        expect(easyStore.retrieve(isA(DmoStoreId.class))).andStubReturn(disciplineContainer);
        expect(disciplineContainer.getSubDisciplines()).andStubReturn(new ArrayList<DisciplineContainer>());
        expect(disciplineContainer.isInvalidated()).andStubReturn(false);
        new Data().setEasyStore(easyStore);
    }

    private void mockNewDatasets() throws Exception {

        final DatasetService datasetService = applicationContext.getDatasetService();
        for (final MetadataFormat mdf : MetadataFormat.values()) {
            final DatasetImpl dataset = new DatasetImpl(DATASET_STORE_ID.getStoreId(), mdf);
            expect(datasetService.newDataset(eq(mdf))).andStubReturn(dataset);
        }
    }

    private void mockNoChildItems() throws ServiceException {

        final ItemService itemService = applicationContext.getItemService();
        expect(itemService.hasChildItems(isA(DmoStoreId.class))).andStubReturn(false);
    }

    @After
    public void reset() {

        PowerMock.resetAll();
        inMemoryDB.close();
    }

    @Test
    public void springBeanInjection() {

        PowerMock.replayAll();
        final EasyWicketTester tester = EasyWicketTester.create(applicationContext);
        // breakpoint in Powermock.doMock shows a single instance of DepositService, yet we get two different ones
        final DepositService depositService = applicationContext.getDepositService();
        Assume.assumeTrue(depositService == ((DebugPage) tester.startPage(DebugPage.class)).getDepositService());
        Assume.assumeTrue(depositService == ((DebugPage) tester.startPage(new DebugPage())).getDepositService());
        fail("SpringBean injection problem seems fixed, apply in DepositPage and remove calls to Services.set");
    }

    @Test
    public void firstArcheaologyPage() throws Exception {

        final EasyWicketTester tester = startIntroPage();
        tester.clickLink("disciplines:0:startDepositLink");
        tester.dumpPage();
    }

    @Ignore
    // only when executed alone it sees the expect(disciplineContainer.isInvalidated())
    // might be similar cause as why we had to work around SpringBean injection
    @Test
    public void firstLanguageLiteraturePage() throws Exception {

        mockNoSubDisciplines();
        final EasyWicketTester tester = startIntroPage();
        tester.clickLink("disciplines:4:startDepositLink");
        tester.dumpPage();
    }

    @Test
    public void otherSources() throws Exception {

        mockNoSubDisciplines();
        final EasyWicketTester tester = startIntroPage();
        final String path = "depositPanel:depositForm:navigationPanel:pageLinkContainer:listView:";

        tester.clickLink("disciplines:5:startDepositLink");
        tester.dumpPage("1a");
        addSecondCreator(tester);

        tester.clickLink(path + "1:pageLink");
        tester.dumpPage("2");

        tester.clickLink(path + "0:pageLink");
        tester.dumpPage("1b");// now changes by addSecondCreator are visible, for example the minus button

        tester.clickLink(path + "2:pageLink");
        tester.dumpPage("3");

        tester.clickLink(path + "3:pageLink");
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

    private EasyWicketTester startIntroPage() {

        PowerMock.replayAll();
        final EasyWicketTester tester = EasyWicketTester.create(applicationContext);

        // DepositIntroPage already tested by {@link EditableContentPageTest#depositIntroPage()}
        // so it is just a starting point for scenario's that click around
        tester.startPage(new DepositIntroPage());
        return tester;
    }
}
