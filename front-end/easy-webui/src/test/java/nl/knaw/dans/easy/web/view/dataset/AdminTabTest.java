package nl.knaw.dans.easy.web.view.dataset;

import static org.easymock.EasyMock.expect;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.EasyApplicationContextMock;
import nl.knaw.dans.easy.EasyUserTestImpl;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.FileStoreMocker;
import nl.knaw.dans.easy.TestUtil;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.PermissionSequence.State;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.security.authz.EasyItemContainerAuthzStrategy;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;

import org.apache.wicket.PageParameters;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminTabTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminTabTest.class);
    private static final String TAB_PATH = "tabs:tabs-container:tabs:3:link";

    private FileStoreMocker fileStoreMocker;
    private EasyApplicationContextMock applicationContext;
    private DmoStoreId datasetStoreId;

    private Dataset mockDataset() throws Exception {
        final EasyUserImpl depositor = new EasyUserTestImpl("x:y");
        depositor.setInitials("D.E.");
        depositor.setSurname("Positor");
        datasetStoreId = new DmoStoreId(Dataset.NAMESPACE, "1");
        final DatasetImpl dataset = new DatasetImpl(datasetStoreId.toString());
        dataset.setState(State.Submitted.toString());
        dataset.setAuthzStrategy(new EasyItemContainerAuthzStrategy() {
            // need a subclass because the constructors are protected
            private static final long serialVersionUID = 1L;
        });

        // needed twice because considered dirty
        dataset.getAdministrativeMetadata().setDepositor(depositor);
        dataset.getAdministrativeMetadata().setDepositor(depositor);
        return dataset;
    }

    private void expectNoLicenseInfo(final Dataset dataset) throws Exception {
        final DatasetService datasetService = applicationContext.getDatasetService();
        expect(datasetService.getLicenseVersions(dataset)).andStubReturn(null);
        expect(datasetService.getAdditionalLicense(dataset)).andStubReturn(null);
        expect(datasetService.getAdditionalLicenseVersions(dataset)).andStubReturn(null);
    }

    @Before
    public void mockApplicationContext() throws Exception {
        applicationContext = new EasyApplicationContextMock();
        fileStoreMocker = new FileStoreMocker();
        final Dataset dataset = mockDataset();
        fileStoreMocker.insertRootFolder(dataset);
        fileStoreMocker.insertFile(1, dataset, "tmp.txt", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.ANONYMOUS);
        fileStoreMocker.logContent(LOGGER);
        new Data().setFileStoreAccess(fileStoreMocker.getFileStoreAccess());
        applicationContext.putBean("fileStoreAccess", Data.getFileStoreAccess());
        applicationContext.expectStandardSecurity();
        applicationContext.expectDefaultResources();
        applicationContext.expectDisciplineChoices();
        applicationContext.expectNoJumpoff();
        applicationContext.expectNoAudioVideoFiles();
        applicationContext.expectDataset(dataset.getDmoStoreId(), dataset);
        expectNoLicenseInfo(dataset);
    }

    @After
    public void cleanup() throws Exception {
        TestUtil.cleanup();
        fileStoreMocker.close();
    }

    @Test
    public void adminTabInVisible() throws Exception {
        applicationContext.expectAuthenticatedAsVisitor();
        final EasyWicketTester tester = startPage();
        tester.assertInvisible(TAB_PATH);
    }

    @Test
    public void adminTabVisible() throws Exception {
        applicationContext.expectAuthenticatedAsVisitor().addRole(Role.ARCHIVIST);
        final EasyWicketTester tester = startPage();
        tester.dumpPage();
        tester.assertVisible(TAB_PATH);
        tester.assertEnabled(TAB_PATH);
        tester.assertLabel(TAB_PATH + ":title", "Administration");
    }

    @Test
    public void adminTab() throws Exception {
        applicationContext.expectAuthenticatedAsVisitor().addRole(Role.ARCHIVIST);
        final EasyWicketTester tester = startPage();
        tester.clickLink(TAB_PATH);
        tester.dumpPage();
        tester.debugComponentTrees();
    }

    private EasyWicketTester startPage() {
        final EasyWicketTester tester = EasyWicketTester.create(applicationContext);
        PowerMock.replayAll();
        final PageParameters pageParameters = new PageParameters();
        pageParameters.add(DatasetViewPage.PM_DATASET_ID, datasetStoreId.getStoreId());
        pageParameters.add(DatasetViewPage.PM_VIEW_MODE, DatasetViewPage.Mode.VIEW.name());
        tester.startPage(DatasetViewPage.class, pageParameters);
        return tester;
    }
}
