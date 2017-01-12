package nl.knaw.dans.easy.web.view.dataset;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.replayAll;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.service.exceptions.TooManyFilesException;
import nl.knaw.dans.common.lang.service.exceptions.ZipFileLengthException;
import nl.knaw.dans.easy.EasyApplicationContextMock;
import nl.knaw.dans.easy.EasyUserTestImpl;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.FileStoreMocker;
import nl.knaw.dans.easy.TestUtil;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.domain.dataset.AdministrativeMetadataImpl;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.Group;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.security.authz.EasyItemContainerAuthzStrategy;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.pf.language.emd.EasyMetadataImpl;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;

import org.apache.wicket.PageParameters;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileExplorerDownloadTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileExplorerDownloadTest.class);

    private static final String MODAL_MESSAGE = "tabs:panel:fe:modalMessage:content:message";
    private static final String DOWNLOAD_LINK = "tabs:panel:fe:downloadLink";

    private SessionUserImpl sessionUser = new SessionUserImpl("normal", true);
    private FileStoreMocker fileStoreMocker;
    private Dataset datasetImplWithLargeFile = createDatasetImpl(1);
    private Dataset datasetImplWithTooManyFiles = createDatasetImpl(2);
    private EasyApplicationContextMock applicationContext;

    private Dataset createDatasetImpl(final int id) {

        final DmoStoreId dmoStoreId = new DmoStoreId(Dataset.NAMESPACE, "" + id);
        final EasyMetadataImpl emd = new EasyMetadataImpl(MetadataFormat.DEFAULT);
        final DatasetImpl dataset = new DatasetImpl(dmoStoreId.getStoreId(), emd);
        dataset.setState(DatasetState.PUBLISHED.toString());
        dataset.setAuthzStrategy(createUthzStrategy(dataset));
        dataset.setAdministrativeMetadata(createAMD(createUser()));
        return dataset;
    }

    private AdministrativeMetadataImpl createAMD(final EasyUser depositor) {
        AdministrativeMetadataImpl administrativeMetadata = new AdministrativeMetadataImpl() {
            private static final long serialVersionUID = 1L;

            public EasyUser getDepositor() {
                return depositor;
            }
        };
        return administrativeMetadata;
    }

    private EasyUser createUser() {
        final EasyUser depositor = new EasyUserTestImpl("easy_user:1");
        depositor.setFirstname("voornaam");
        depositor.setSurname("achternaam");
        depositor.setInitials("v.n.");
        return depositor;
    }

    private EasyItemContainerAuthzStrategy createUthzStrategy(final DatasetImpl dataset) {
        return new EasyItemContainerAuthzStrategy(sessionUser, dataset, dataset) {
            private static final long serialVersionUID = 1L;

            @Override
            public TriState canChildrenBeDiscovered() {
                return TriState.ALL;
            }

            @Override
            public TriState canChildrenBeRead() {
                return TriState.ALL;
            }
        };
    }

    public void mockFiles() throws Exception {

        fileStoreMocker.insertRootFolder(datasetImplWithLargeFile);
        fileStoreMocker.insertRootFolder(datasetImplWithTooManyFiles);
        fileStoreMocker.insertFile(1, datasetImplWithLargeFile, "x.y", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.ANONYMOUS);
        for (int i = 2; i < 404; i++) {
            fileStoreMocker.insertFile(i, datasetImplWithTooManyFiles, i + "x.y", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.ANONYMOUS);
        }
        fileStoreMocker.logContent(LOGGER);
    }

    @Before
    public void setUp() throws Exception {
        applicationContext = new EasyApplicationContextMock();
        fileStoreMocker = new FileStoreMocker();
        fileStoreMocker.logContent(LOGGER);
        new Data().setFileStoreAccess(fileStoreMocker.getFileStoreAccess());

        applicationContext.expectNoDatasetsInToolBar();
        applicationContext.setDatasetService(mockDatasetService());
        applicationContext.expectStandardSecurity();
        applicationContext.expectDefaultResources();
        applicationContext.expectAuthenticatedAs(sessionUser);
        applicationContext.expectNoAudioVideoFiles();
        applicationContext.putBean("fileStoreAccess", fileStoreMocker.getFileStoreAccess());
        mockFiles();
    }

    private DatasetService mockDatasetService() throws ObjectNotAvailableException, CommonSecurityException, ServiceException {
        final DatasetService datasetServiceMock = PowerMock.createMock(DatasetService.class);
        expect(datasetServiceMock.getDataset(isA(EasyUser.class), EasyMock.eq(datasetImplWithLargeFile.getDmoStoreId()))).andStubReturn(
                datasetImplWithLargeFile);
        expect(datasetServiceMock.getDataset(isA(EasyUser.class), EasyMock.eq(datasetImplWithTooManyFiles.getDmoStoreId()))).andStubReturn(
                datasetImplWithTooManyFiles);
        expect(datasetServiceMock.getAdditionalLicense(isA(Dataset.class))).andStubReturn(null);
        return datasetServiceMock;
    }

    private static class SessionUserImpl extends EasyUserImpl {
        private static final long serialVersionUID = 1L;
        boolean hasAcceptedGeneralConditions;

        public SessionUserImpl(final String userId, final boolean hasAcceptedGeneralConditions) {
            super(userId);
            this.hasAcceptedGeneralConditions = hasAcceptedGeneralConditions;
            setFirstname("Norman");
            setSurname("Normal");
            addRole(Role.USER);
            setState(State.ACTIVE);
        }

        public void setHasAcceptedGeneralConditions(final boolean hasAcceptedGeneralConditions) {
            this.hasAcceptedGeneralConditions = hasAcceptedGeneralConditions;
        }

        public Set<Group> getGroups() {
            return new HashSet<Group>();
        }

        @Override
        public boolean hasAcceptedGeneralConditions() {
            return hasAcceptedGeneralConditions;
        }
    }

    private EasyWicketTester renderPage(final String datasetStoreId) {
        final EasyWicketTester tester = EasyWicketTester.create(applicationContext);
        replayAll();
        final PageParameters parameters = new PageParameters();
        parameters.add("id", datasetStoreId);
        parameters.add("tab", "2");
        tester.startPage(new DatasetViewPage(parameters));
        tester.assertRenderedPage(DatasetViewPage.class);
        return tester;
    }

    @Test
    public void downloadSizeTooLargeHasAcceptedConditions() throws ServiceException {
        expectFileLengthException();
        sessionUser.setHasAcceptedGeneralConditions(true);
        final EasyWicketTester tester = renderPage(datasetImplWithLargeFile.getStoreId());

        tester.dumpPage();
        tester.clickLink(DOWNLOAD_LINK, true);
        tester.assertLabelContains(MODAL_MESSAGE, "Downloading is limited");
        tester.assertLabelContains(MODAL_MESSAGE, "MB per download");
    }

    @Test
    public void downloadSizeTooLargeHasntAcceptedGeneralConditions() throws ServiceException {
        expectFileLengthException();
        sessionUser.setHasAcceptedGeneralConditions(false);
        final EasyWicketTester tester = renderPage(datasetImplWithLargeFile.getStoreId());

        tester.dumpPage();
        tester.clickLink(DOWNLOAD_LINK, true);
        tester.debugComponentTrees();
        tester.assertLabelContains(MODAL_MESSAGE, "Downloading is limited");
        tester.assertLabelContains(MODAL_MESSAGE, "MB per download");
    }

    @Test
    public void downloadTooManyFilesHasAcceptedGeneralConditions() throws ServiceException {
        expectTooManyFilesException();
        sessionUser.setHasAcceptedGeneralConditions(true);
        expect(applicationContext.getDatasetService().getAdditionalLicense(isA(Dataset.class))).andStubReturn(null);
        final EasyWicketTester tester = renderPage(datasetImplWithTooManyFiles.getStoreId());

        tester.dumpPage();
        tester.clickLink(DOWNLOAD_LINK, true);
        tester.assertLabelContains(MODAL_MESSAGE, "Downloading is limited");
        tester.assertLabelContains(MODAL_MESSAGE, "files per download");
        assertTrue(tester.getServletResponse().getDocument().contains("files per download"));
    }

    @Test
    public void downloadTooManyFilesHasntAcceptedGeneralConditions() throws Exception {
        expectTooManyFilesException();
        sessionUser.setHasAcceptedGeneralConditions(false);
        final EasyWicketTester tester = renderPage(datasetImplWithTooManyFiles.getStoreId());

        tester.dumpPage();
        tester.clickLink(DOWNLOAD_LINK, true);
        tester.assertLabelContains(MODAL_MESSAGE, "Downloading is limited");
        tester.assertLabelContains(MODAL_MESSAGE, "files per download");
    }

    @SuppressWarnings("unchecked")
    private void expectFileLengthException() throws ServiceException {
        expect(applicationContext.getItemService().getZippedContent(isA(EasyUser.class), isA(Dataset.class), isA(List.class)))//
                .andThrow(new ZipFileLengthException(datasetImplWithLargeFile.getStoreId())).once();
    }

    @SuppressWarnings("unchecked")
    private void expectTooManyFilesException() throws ServiceException {
        expect(applicationContext.getItemService().getZippedContent(isA(EasyUser.class), isA(Dataset.class), isA(List.class)))//
                .andThrow(new TooManyFilesException(datasetImplWithTooManyFiles.getStoreId())).once();
    }

    @After
    public void cleanup() throws Exception {
        PowerMock.verifyAll();
        TestUtil.cleanup();
        fileStoreMocker.close();
    }
}
