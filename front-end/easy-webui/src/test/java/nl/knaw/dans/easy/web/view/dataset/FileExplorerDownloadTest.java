package nl.knaw.dans.easy.web.view.dataset;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.resetAll;

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
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.db.testutil.InMemoryDatabase;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.Group;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.fedora.db.FedoraFileStoreAccess;
import nl.knaw.dans.easy.security.authz.EasyItemContainerAuthzStrategy;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.servicelayer.services.ItemService;
import nl.knaw.dans.pf.language.emd.EasyMetadataImpl;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;

import org.apache.wicket.PageParameters;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

@Ignore
public class FileExplorerDownloadTest {

    private static final String MESSAGE = "tabs:panel:fe:modalDownload:content:message";
    private static final String MESSAGE2 = "tabs:panel:fe:modalMessage:content:message";
    private static final String DOWNLOAD_LINK = "tabs:panel:fe:downloadLink";

    private EasyUserTestImpl sessionUser = new EasyUserTestImpl("normal", true);
    private InMemoryDatabase inMemoryDB;
    private FedoraFileStoreAccess fileStoreAccess;
    private Dataset datasetImplWithLargeFile = createDatasetImpl(1);
    private Dataset datasetImplWithTooManyFiles = createDatasetImpl(2);
    private EasyApplicationContextMock ctx;

    private Dataset createDatasetImpl(final int id) {

        final DmoStoreId dmoStoreId = new DmoStoreId(Dataset.NAMESPACE, "" + id);
        final EasyMetadataImpl emd = new EasyMetadataImpl(MetadataFormat.UNSPECIFIED);
        final DatasetImpl dataset = new DatasetImpl(dmoStoreId.getStoreId(), emd);
        dataset.setState(DatasetState.PUBLISHED.toString());
        dataset.setAuthzStrategy(new EasyItemContainerAuthzStrategy(sessionUser, dataset, dataset) {
            private static final long serialVersionUID = 1L;

            @Override
            public TriState canChildrenBeDiscovered() {
                return TriState.ALL;
            }

            @Override
            public TriState canChildrenBeRead() {
                return TriState.ALL;
            }
        });
        return dataset;
    }

    public void mockFiles() throws Exception {

        // size = Integer.MAXVALUE
        // can't use a mocked dataset here but rendering fails with an Impl
        inMemoryDB.insertFile(1, datasetImplWithLargeFile, "x.y", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.KNOWN);
        for (int i = 2; i < 404; i++) {
            inMemoryDB.insertFile(i, datasetImplWithTooManyFiles, i + "x.y", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.KNOWN);
        }

        inMemoryDB.flush();
    }

    @After
    public void cleanUp() {
        inMemoryDB.close();
    }

    @Before
    public void setUp() throws Exception {
        inMemoryDB = new InMemoryDatabase();
        fileStoreAccess = new FedoraFileStoreAccess();
        new Data().setFileStoreAccess(fileStoreAccess);

        ctx = new EasyApplicationContextMock();
        ctx.expectNoDatasetsInToolBar();
        ctx.setDatasetService(mockDatasetService());
        ctx.expectStandardSecurity();
        ctx.expectDefaultResources();
        ctx.expectAuthenticatedAs(sessionUser);
        ctx.expectNoAudioVideoFiles();
        ctx.putBean("fileStoreAccess", fileStoreAccess);
        expectRootFolder(datasetImplWithLargeFile);
        expectRootFolder(datasetImplWithTooManyFiles);
        mockFiles();
    }

    private void expectRootFolder(Dataset dataset) throws Exception {
        inMemoryDB.insertRootFolder(dataset);
        ItemService itemService = ctx.getItemService();
        FolderItemVO itemVO = fileStoreAccess.getFolderItemVO(dataset.getDmoStoreId());
        expect(itemService.getRootFolder(sessionUser, dataset, dataset.getDmoStoreId())).andStubReturn(itemVO);
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

    private static class EasyUserTestImpl extends EasyUserImpl {
        private static final long serialVersionUID = 1L;
        boolean hasAcceptedGeneralConditions;

        public EasyUserTestImpl(final String userId, final boolean hasAcceptedGeneralConditions) {
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
        final EasyWicketTester tester = EasyWicketTester.create(ctx);
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
        tester.assertLabelContains(MESSAGE2, "Downloading is limited");
        tester.assertLabelContains(MESSAGE2, "MB per download");
    }

    @Test
    public void downloadSizeTooLargeHasntAcceptedGeneralConditions() throws ServiceException {
        expectFileLengthException();
        sessionUser.setHasAcceptedGeneralConditions(false);
        final EasyWicketTester tester = renderPage(datasetImplWithLargeFile.getStoreId());

        tester.dumpPage();
        tester.clickLink(DOWNLOAD_LINK, true);
        tester.assertLabelContains(MESSAGE, "Downloading is limited");
        tester.assertLabelContains(MESSAGE, "MB per download");
    }

    @Test
    public void downloadTooManyFilesHasAcceptedGeneralConditions() throws ServiceException {
        expectTooManyFilesException();
        sessionUser.setHasAcceptedGeneralConditions(true);
        expect(ctx.getDatasetService().getAdditionalLicense(isA(Dataset.class))).andStubReturn(null);
        final EasyWicketTester tester = renderPage(datasetImplWithTooManyFiles.getStoreId());

        tester.dumpPage();
        tester.clickLink(DOWNLOAD_LINK, true);
        tester.debugComponentTrees();
        tester.assertLabelContains(MESSAGE2, "Downloading is limited");
        tester.assertLabelContains(MESSAGE2, "files per download");
    }

    @Test
    public void downloadTooManyFilesHasntAcceptedGeneralConditions() throws Exception {
        expectTooManyFilesException();
        sessionUser.setHasAcceptedGeneralConditions(false);
        final EasyWicketTester tester = renderPage(datasetImplWithTooManyFiles.getStoreId());

        tester.dumpPage();
        tester.clickLink(DOWNLOAD_LINK, true);
        tester.assertLabelContains(MESSAGE, "Downloading is limited");
        tester.assertLabelContains(MESSAGE, "files per download");
    }

    @SuppressWarnings("unchecked")
    private void expectFileLengthException() throws ServiceException {
        expect(ctx.getItemService().getZippedContent(isA(EasyUser.class), isA(Dataset.class), isA(List.class)))//
                .andThrow(new ZipFileLengthException(datasetImplWithLargeFile.getStoreId())).once();
    }

    @SuppressWarnings("unchecked")
    private void expectTooManyFilesException() throws ServiceException {
        expect(ctx.getItemService().getZippedContent(isA(EasyUser.class), isA(Dataset.class), isA(List.class)))//
                .andThrow(new TooManyFilesException(datasetImplWithTooManyFiles.getStoreId())).once();
    }

    @After
    public void tearDown() {
        resetAll();
    }
}
