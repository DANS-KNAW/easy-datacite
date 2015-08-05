package nl.knaw.dans.easy.web.view.dataset;

import static org.easymock.EasyMock.expect;

import java.text.MessageFormat;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.dataset.DatasetState;
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
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.FolderItem;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.domain.user.EasyUserAnonymous;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.security.authz.EasyItemContainerAuthzStrategy;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.pf.language.emd.EmdRights;
import nl.knaw.dans.pf.language.emd.types.BasicString;

import org.apache.wicket.PageParameters;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** See also DatasetPermissionTest.fileExplorerTab */
@Ignore
public class FileExplorerTabTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileExplorerTabTest.class);

    private static final DmoStoreId DATASET_STORE_ID = new DmoStoreId(Dataset.NAMESPACE, "1");
    private FileStoreMocker fileStoreMocker;
    private DatasetImpl dataset;
    private EasyApplicationContextMock applicationContext;

    @Before
    public void mockApplicationContext() throws Exception {

        applicationContext = new EasyApplicationContextMock();
        fileStoreMocker = new FileStoreMocker();
        fileStoreMocker.logContent(LOGGER);
        new Data().setFileStoreAccess(fileStoreMocker.getFileStoreAccess());
        applicationContext.putBean("fileStoreAccess", Data.getFileStoreAccess());
        applicationContext.expectStandardSecurity();
        applicationContext.expectDefaultResources();
        applicationContext.expectDisciplineChoices();
        applicationContext.expectNoJumpoff();
        applicationContext.expectNoAudioVideoFiles();
    }

    @After
    public void cleanup() throws Exception {

        TestUtil.cleanup();
        fileStoreMocker.close();
    }

    @Test
    public void file() throws Exception {

        final String fileName = "mainfile.txt";
        mockDataset(createDepositor());

        fileStoreMocker.insertRootFolder(dataset);
        setDatasetTitle(fileStoreMocker.insertFile(1, dataset, fileName, CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.KNOWN));

        final EasyWicketTester tester = render();
        tester.dumpPage();
        tester.debugComponentTrees();
        tester.assertLabel("tabs:panel:fe:explorer:datatable:body:rows:1", fileName);
    }

    @Test
    public void folder() throws Exception {

        mockDataset(createDepositor());
        fileStoreMocker.insertRootFolder(dataset);
        final FolderItem folder = fileStoreMocker.insertFolder(1, dataset, "folder");
        setDatasetTitle(fileStoreMocker.insertFile(1, folder, "mainfile.txt", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.KNOWN));

        final EasyWicketTester tester = render();
        tester.dumpPage();
        tester.debugComponentTrees();
        tester.assertLabel("tabs:panel:fe:explorer:datatable:bottomToolbars:1:toolbar:td:msg", "No visible files.");
        // with a manual test we would get
        // tester.assertLabel("tabs:panel:fe:explorer:datatable:body:rows:1", "folder");
    }

    private void setDatasetTitle(final FileItem file) {
        // show test purpose on the dumped page
        String format = "dataset rights: {0}; file visible to: {1}, file accessible to: {2}";
        EmdRights emdRights = dataset.getEasyMetadata().getEmdRights();
        final String title = MessageFormat.format(format, emdRights.getAccessCategory(), file.getVisibleTo(), file.getAccessibleTo());
        dataset.getEasyMetadata().getEmdTitle().getDcTitle().add(new BasicString(title));
    }

    private void mockDataset(final EasyUserImpl depositor) throws Exception {

        dataset = new DatasetImpl(DATASET_STORE_ID.toString());
        final EasyUserAnonymous sessionUser = EasyUserAnonymous.getInstance();
        dataset.setState(DatasetState.PUBLISHED.toString());
        dataset.getEasyMetadata().getEmdRights().setAccessCategory(AccessCategory.OPEN_ACCESS_FOR_REGISTERED_USERS);
        dataset.setAuthzStrategy(new EasyItemContainerAuthzStrategy(sessionUser, dataset, dataset) {
            // a subclass needed because the constructors are protected
            private static final long serialVersionUID = 1L;
        });

        // needed twice because considered dirty
        dataset.getAdministrativeMetadata().setDepositor(depositor);
        dataset.getAdministrativeMetadata().setDepositor(depositor);

        applicationContext.expectDataset(dataset.getDmoStoreId(), dataset);
        expectNoLicenseInfo(dataset);
    }

    private void expectNoLicenseInfo(final Dataset dataset) throws Exception {
        final DatasetService datasetService = applicationContext.getDatasetService();
        expect(datasetService.getLicenseVersions(dataset)).andStubReturn(null);
        expect(datasetService.getAdditionalLicense(dataset)).andStubReturn(null);
        expect(datasetService.getAdditionalLicenseVersions(dataset)).andStubReturn(null);
    }

    private EasyUserImpl createDepositor() {

        final EasyUserImpl depositor = new EasyUserTestImpl("x:y");
        depositor.setInitials("D.E.");
        depositor.setSurname("Positor");
        return depositor;
    }

    protected EasyWicketTester render() {

        fileStoreMocker.logContent(LOGGER);
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
