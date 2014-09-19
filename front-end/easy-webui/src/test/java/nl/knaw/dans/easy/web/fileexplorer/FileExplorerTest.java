package nl.knaw.dans.easy.web.fileexplorer;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.resetAll;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.service.exceptions.TooManyFilesException;
import nl.knaw.dans.common.lang.service.exceptions.ZipFileLengthException;
import nl.knaw.dans.easy.AuthzStrategyTestImpl;
import nl.knaw.dans.easy.EasyApplicationContextMock;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.db.testutil.InMemoryDatabase;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.dataset.PermissionSequenceListImpl;
import nl.knaw.dans.easy.domain.exceptions.DomainException;
import nl.knaw.dans.easy.domain.exceptions.ObjectNotFoundException;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.AdministrativeMetadata;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.StateChangeDate;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.Group;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.fedora.db.FedoraFileStoreAccess;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.servicelayer.services.SearchService;
import nl.knaw.dans.easy.web.view.dataset.DatasetViewPage;
import nl.knaw.dans.pf.language.emd.EasyMetadataImpl;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;

import org.apache.wicket.PageParameters;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

public class FileExplorerTest {
    private static final AuthzStrategyTestImpl AUTHZ_STRATEGY = new AuthzStrategyTestImpl();
    private static final String MESSAGE = "tabs:panel:fe:modalDownload:content:message";
    private static final String MESSAGE2 = "tabs:panel:fe:modalMessage:content:message";
    private static final String DOWNLOAD_LINK = "tabs:panel:fe:downloadLink";

    private static EasyUserTestImpl sessionUser = new EasyUserTestImpl("normal", true);
    private static InMemoryDatabase inMemoryDB;
    private static FedoraFileStoreAccess fileStoreAccess;
    private static Dataset datasetImplWithLargeFile = createDatasetImpl(1);
    private static Dataset datasetImplWithTooManyFiles = createDatasetImpl(2);
    private final Dataset datasetMockWithLargeFile = createDatasetMock(1);
    private final Dataset datasetMockWithTooManyFiles = createDatasetMock(2);
    private EasyApplicationContextMock ctx;

    private static Dataset createDatasetImpl(final int id) {
        final DmoStoreId dmoStoreId = new DmoStoreId(Dataset.NAMESPACE, "" + id);
        final EasyMetadataImpl emd = new EasyMetadataImpl(MetadataFormat.UNSPECIFIED);
        final DatasetImpl dataset = new DatasetImpl(dmoStoreId.getStoreId(), emd);
        dataset.setAuthzStrategy(AUTHZ_STRATEGY);
        dataset.setState(DatasetState.PUBLISHED.toString());
        return dataset;
    }

    private Dataset createDatasetMock(final int id) {
        final DmoStoreId dmoStoreId = new DmoStoreId(Dataset.NAMESPACE, "" + id);
        final AdministrativeMetadata amd = PowerMock.createMock(AdministrativeMetadata.class);
        expect(amd.getStateChangeDates()).andStubReturn(new ArrayList<StateChangeDate>());
        expect(amd.getDepositor()).andStubReturn(sessionUser);

        // TODO eliminate this mock, but with the Impl page rendering throws a MissingResourceException
        // because cell 4 tries to get property "" from a DatasetModel
        final Dataset datasetMock = PowerMock.createMock(Dataset.class);
        expect(datasetMock.getAdministrativeMetadata()).andStubReturn(amd);
        expect(datasetMock.getAdministrativeState()).andStubReturn(DatasetState.PUBLISHED);
        expect(datasetMock.getAuthzStrategy()).andStubReturn(AUTHZ_STRATEGY);
        expect(datasetMock.getDmoStoreId()).andStubReturn(dmoStoreId);
        expect(datasetMock.getDepositor()).andStubReturn(sessionUser);
        expect(datasetMock.hasDepositor(isA(EasyUser.class))).andStubReturn(true);
        expect(datasetMock.getEasyMetadata()).andStubReturn(new EasyMetadataImpl(MetadataFormat.UNSPECIFIED));
        expect(datasetMock.getLastModified()).andStubReturn(new DateTime());
        expect(datasetMock.getMetadataFormat()).andStubReturn(MetadataFormat.UNSPECIFIED);
        expect(datasetMock.getPreferredTitle()).andStubReturn("Test Title");
        expect(datasetMock.getPermissionSequenceList()).andStubReturn(new PermissionSequenceListImpl());
        expect(datasetMock.getStoreId()).andStubReturn(dmoStoreId.getStoreId());
        expect(datasetMock.getState()).andStubReturn(DatasetState.PUBLISHED.toString());
        expect(datasetMock.isUnderEmbargo()).andStubReturn(false);
        expect(datasetMock.isPermissionGrantedTo(isA(EasyUser.class))).andStubReturn(false);
        expect(datasetMock.isGroupAccessGrantedTo(isA(EasyUser.class))).andStubReturn(false);
        try {
            expect(datasetMock.isInvalidated()).andStubReturn(false);
            expect(datasetMock.getParentDisciplines()).andStubReturn(new ArrayList<DisciplineContainer>());
        }
        catch (final RepositoryException canNotHappen) {}
        catch (final ObjectNotFoundException canNotHappen) {}
        catch (final DomainException canNotHappen) {}

        return datasetMock;
    }

    @BeforeClass
    public static void mockFileStoreAccess() throws Exception {
        inMemoryDB = new InMemoryDatabase();

        // size = Integer.MAXVALUE
        // can't use a mocked dataset here but rendering fails with an Impl
        inMemoryDB.insertFile(1, datasetImplWithLargeFile, "a/x.y", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.KNOWN);
        for (int i = 2; i < 404; i++) {
            inMemoryDB.insertFile(i, datasetImplWithTooManyFiles, i + "a/x.y", CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.KNOWN);
        }

        inMemoryDB.flush();
        fileStoreAccess = new FedoraFileStoreAccess();
        new Data().setFileStoreAccess(fileStoreAccess);
    }

    @AfterClass
    public static void cleanUp() {
        inMemoryDB.close();
    }

    @Before
    public void setUp() throws Exception {
        ctx = new EasyApplicationContextMock();
        ctx.setSearchService(mockSearchService());
        ctx.setDatasetService(mockDatasetService());
        ctx.expectStandardSecurity(false);
        ctx.expectDefaultResources();
        ctx.expectAuthenticatedAs(sessionUser);
        ctx.expectNoAudioVideoFiles();
        ctx.putBean("fileStoreAccess", fileStoreAccess);
    }

    private DatasetService mockDatasetService() throws ObjectNotAvailableException, CommonSecurityException, ServiceException {
        final DatasetService datasetServiceMock = PowerMock.createMock(DatasetService.class);
        expect(datasetServiceMock.getDataset(isA(EasyUser.class), EasyMock.eq(datasetImplWithLargeFile.getDmoStoreId()))).andStubReturn(
                datasetMockWithLargeFile);
        expect(datasetServiceMock.getDataset(isA(EasyUser.class), EasyMock.eq(datasetImplWithTooManyFiles.getDmoStoreId()))).andStubReturn(
                datasetMockWithTooManyFiles);
        expect(datasetServiceMock.getAdditionalLicense(isA(Dataset.class))).andStubReturn(null);
        return datasetServiceMock;
    }

    private SearchService mockSearchService() throws ServiceException {
        final SearchService searchServiceMock = PowerMock.createMock(SearchService.class);

        expect(searchServiceMock.getNumberOfDatasets(isA(EasyUser.class))).andStubReturn(1);
        expect(searchServiceMock.getNumberOfRequests(isA(EasyUser.class))).andStubReturn(1);

        expect(searchServiceMock.getNumberOfItemsInTrashcan(isA(EasyUser.class))).andStubReturn(1);
        expect(searchServiceMock.getNumberOfItemsInAllWork(isA(EasyUser.class))).andStubReturn(1);
        expect(searchServiceMock.getNumberOfItemsInOurWork(isA(EasyUser.class))).andStubReturn(1);
        expect(searchServiceMock.getNumberOfItemsInMyWork(isA(EasyUser.class))).andStubReturn(1);
        return searchServiceMock;
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
    public void testDownloadSizeTooLargeHasAcceptedConditions() throws ServiceException {
        expectFileLengthException();
        sessionUser.setHasAcceptedGeneralConditions(true);
        final EasyWicketTester tester = renderPage(datasetImplWithLargeFile.getStoreId());

        tester.dumpPage();
        tester.clickLink(DOWNLOAD_LINK, true);
        tester.assertLabelContains(MESSAGE2, "Downloading is limited");
        tester.assertLabelContains(MESSAGE2, "MB per download");
    }

    @Test
    public void testDownloadSizeTooLargeHasntAcceptedGeneralConditions() throws ServiceException {
        expectFileLengthException();
        sessionUser.setHasAcceptedGeneralConditions(false);
        final EasyWicketTester tester = renderPage(datasetImplWithLargeFile.getStoreId());

        tester.dumpPage();
        tester.clickLink(DOWNLOAD_LINK, true);
        tester.assertLabelContains(MESSAGE, "Downloading is limited");
        tester.assertLabelContains(MESSAGE, "MB per download");
    }

    @Test
    public void testDownloadTooManyFilesHasAcceptedGeneralConditions() throws ServiceException {
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
    public void testDownloadTooManyFilesHasntAcceptedGeneralConditions() throws Exception {
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
