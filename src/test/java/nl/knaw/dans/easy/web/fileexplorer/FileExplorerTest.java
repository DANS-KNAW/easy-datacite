package nl.knaw.dans.easy.web.fileexplorer;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.isNull;
import static org.easymock.EasyMock.or;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.resetAll;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import nl.knaw.dans.common.lang.FileSystemHomeDirectory;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.service.exceptions.TooManyFilesException;
import nl.knaw.dans.common.lang.service.exceptions.ZipFileLengthException;
import nl.knaw.dans.common.lang.user.User.State;
import nl.knaw.dans.easy.domain.dataset.PermissionSequenceListImpl;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.ItemOrder;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.dataset.item.filter.ItemFilters;
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
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.model.user.Group;
import nl.knaw.dans.easy.domain.model.user.RepoAccess;
import nl.knaw.dans.easy.domain.model.user.RepoAccessDelegator;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.security.CodedAuthz;
import nl.knaw.dans.easy.security.ContextParameters;
import nl.knaw.dans.easy.security.Security;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.servicelayer.services.ItemService;
import nl.knaw.dans.easy.servicelayer.services.SearchService;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.EasySession;
import nl.knaw.dans.easy.web.EasyWicketApplication;
import nl.knaw.dans.easy.web.statistics.StatisticsLogger;
import nl.knaw.dans.easy.web.view.dataset.DatasetViewPage;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;

import org.apache.wicket.PageParameters;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.apache.wicket.util.tester.WicketTester;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {Services.class, Security.class, StatisticsLogger.class, EasySession.class, RepoAccess.class})
public class FileExplorerTest
{
    private WicketTester tester;
    private EasyUser normalUser;
    private EasySession easySessionMock;
    private Dataset datasetMock;
    private DatasetService datasetServiceMock;
    private SearchService searchServiceMock;
    private ItemService itemServiceMock;

    private String datasetSid = "test-dataset:1";
    private DmoStoreId datasetDmoStoreId = new DmoStoreId(datasetSid);

    @Before
    public void setUp() throws Exception
    {
        ApplicationContextMock ctx = new ApplicationContextMock();
        ctx.putBean("editableContentHome", new FileSystemHomeDirectory(new File("src/main/assembly/dist/res/example/editable")));
        EasyWicketApplication app = new EasyWicketApplication();
        app.setApplicationContext(ctx);
        tester = new WicketTester(app);
        setUpAuthz();
        setUpUsers();
        setUpEasySessionMock();
        setUpServices();
    }

    private void setUpAuthz()
    {
        mockStatic(Security.class);
        expect(Security.getAuthz()).andReturn(new CodedAuthz()).anyTimes();
    }

    private void setUpEasySessionMock()
    {
        mockStatic(EasySession.class);
        easySessionMock = PowerMock.createMock(EasySession.class);
        expect(EasySession.get()).andReturn(easySessionMock).anyTimes();
        expect(easySessionMock.hasRedirectPage(isA(Class.class))).andReturn(false).anyTimes();
    }

    private void expectUser(EasyUser user)
    {
        expect(easySessionMock.getUser()).andReturn(user).anyTimes();
        expect(EasySession.getSessionUser()).andReturn(user).anyTimes();
        expect(easySessionMock.getContextParameters()).andReturn(new ContextParameters(user)).anyTimes();
    }

    private void normalUserIsLoggedIn()
    {
        expectUser(normalUser);
    }

    private void setUpServices() throws ObjectNotAvailableException, CommonSecurityException, ServiceException, RepositoryException, ObjectNotFoundException,
            DomainException
    {
        mockStatic(Services.class);
        setUpDatasetServiceMock();
        setUpItemServiceMock();
        setUpSearchServiceMock();
        expect(Services.getDatasetService()).andReturn(datasetServiceMock).anyTimes();
        expect(Services.getItemService()).andReturn(itemServiceMock).anyTimes();
        expect(Services.getSearchService()).andReturn(searchServiceMock).anyTimes();
    }

    private void setUpSearchServiceMock() throws ServiceException
    {
        searchServiceMock = PowerMock.createMock(SearchService.class);

        expect(searchServiceMock.getNumberOfDatasets(isA(EasyUser.class))).andReturn(1).anyTimes();
        expect(searchServiceMock.getNumberOfRequests(isA(EasyUser.class))).andReturn(1).anyTimes();

        expect(searchServiceMock.getNumberOfItemsInTrashcan(isA(EasyUser.class))).andReturn(1).anyTimes();
        expect(searchServiceMock.getNumberOfItemsInAllWork(isA(EasyUser.class))).andReturn(1).anyTimes();
        expect(searchServiceMock.getNumberOfItemsInOurWork(isA(EasyUser.class))).andReturn(1).anyTimes();
        expect(searchServiceMock.getNumberOfItemsInMyWork(isA(EasyUser.class))).andReturn(1).anyTimes();
    }

    private void setUpItemServiceMock() throws ServiceException
    {
        itemServiceMock = PowerMock.createMock(ItemService.class);
        ArrayList<ItemVO> filesAndFolders = new ArrayList<ItemVO>();
        filesAndFolders.add(mockFile());
        expect(
                itemServiceMock.getFilesAndFolders(isA(EasyUser.class), isA(Dataset.class), isA(DmoStoreId.class), isA(Integer.class), isA(Integer.class), or(
                        isNull(ItemOrder.class), isA(ItemOrder.class)), or(isNull(ItemFilters.class), isA(ItemFilters.class)))).andReturn(filesAndFolders)
                .anyTimes();
        expect(itemServiceMock.hasChildItems(isA(DmoStoreId.class))).andReturn(false).anyTimes();
    }

    private FileItemVO mockFile()
    {
        FileItemVO file = new FileItemVO();
        file.setName("file1");
        file.setDatasetSid(datasetSid);
        file.setParentSid(datasetSid);
        file.setSize(Integer.MAX_VALUE);
        file.setMimetype("text/html");
        file.setCreatorRole(CreatorRole.DEPOSITOR);
        file.setSid("file-test:1");
        file.setAuthzStrategy(new AuthzStrategyTestImpl());
        file.setVisibleTo(VisibleTo.ANONYMOUS);
        file.setAccessibleTo(AccessibleTo.KNOWN);
        return file;
    }

    private void setUpDatasetServiceMock() throws ObjectNotAvailableException, CommonSecurityException, ServiceException, RepositoryException,
            ObjectNotFoundException, DomainException
    {
        mockDataset();
        datasetServiceMock = PowerMock.createMock(DatasetService.class);
        expect(datasetServiceMock.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).andReturn(datasetMock).anyTimes();
        expect(datasetServiceMock.getAdditionalLicense(isA(Dataset.class))).andReturn(null).anyTimes();
    }

    private void mockDataset() throws RepositoryException, ObjectNotFoundException, DomainException
    {
        datasetMock = PowerMock.createMock(Dataset.class);
        expect(datasetMock.getStoreId()).andReturn(datasetSid).anyTimes();
        expect(datasetMock.getDmoStoreId()).andReturn(datasetDmoStoreId).anyTimes();
        expect(datasetMock.getMetadataFormat()).andReturn(MetadataFormat.ARCHAEOLOGY).anyTimes();
        expect(datasetMock.getAuthzStrategy()).andReturn(new AuthzStrategyTestImpl()).anyTimes();
        expect(datasetMock.getDepositor()).andReturn(normalUser).anyTimes();
        expect(datasetMock.hasDepositor(isA(EasyUser.class))).andReturn(true).anyTimes();
        expect(datasetMock.isInvalidated()).andReturn(false).anyTimes();
        expect(datasetMock.getPreferredTitle()).andReturn("Test Title").anyTimes();
        expect(datasetMock.hasVisibleItems(isA(EasyUser.class))).andReturn(true).anyTimes();
        expect(datasetMock.getPermissionSequenceList()).andReturn(new PermissionSequenceListImpl()).anyTimes();
        expect(datasetMock.getAdministrativeState()).andReturn(DatasetState.PUBLISHED).anyTimes();
        expect(datasetMock.getParentDisciplines()).andReturn(new ArrayList<DisciplineContainer>()).anyTimes();
        expect(datasetMock.hasPermissionRestrictedItems()).andReturn(false).anyTimes();

        AdministrativeMetadata amd = PowerMock.createMock(AdministrativeMetadata.class);
        expect(amd.getStateChangeDates()).andReturn(new ArrayList<StateChangeDate>()).anyTimes();
        expect(amd.getDepositor()).andReturn(normalUser).anyTimes();

        expect(datasetMock.getAdministrativeMetadata()).andReturn(amd).anyTimes();
        expect(datasetMock.getLastModified()).andReturn(new DateTime()).anyTimes();
        expect(datasetMock.getTotalFileCount()).andReturn(1).anyTimes();
        expect(datasetMock.getTotalFolderCount()).andReturn(0).anyTimes();
    }

    private void setUpUsers()
    {
        mockStatic(RepoAccess.class);
        RepoAccessDelegator delegatorMock = PowerMock.createMock(RepoAccessDelegator.class);
        expect(RepoAccess.getDelegator()).andReturn(delegatorMock).anyTimes();
        expect(delegatorMock.getGroups(isA(EasyUser.class))).andReturn(new LinkedList<Group>());

        normalUser = new EasyUserTestImpl("normal", true);
        normalUser.setFirstname("Norman");
        normalUser.setSurname("Normal");
        normalUser.addRole(Role.USER);
        normalUser.setState(State.ACTIVE);
    }

    private static class EasyUserTestImpl extends EasyUserImpl
    {
        private static final long serialVersionUID = 1L;
        boolean hasAcceptedGeneralConditions;

        public EasyUserTestImpl(String userId, boolean hasAcceptedGeneralConditions)
        {
            super(userId);
            this.hasAcceptedGeneralConditions = hasAcceptedGeneralConditions;
        }

        public void setHasAcceptedGeneralConditions(boolean hasAcceptedGeneralConditions)
        {
            this.hasAcceptedGeneralConditions = hasAcceptedGeneralConditions;
        }

        @Override
        public boolean hasAcceptedGeneralConditions()
        {
            return hasAcceptedGeneralConditions;
        }
    }

    private void renderPage()
    {
        PageParameters parameters = new PageParameters();
        parameters.add("id", datasetSid);
        parameters.add("tab", "2");
        tester.startPage(new DatasetViewPage(parameters));
        tester.assertRenderedPage(DatasetViewPage.class);
    }

    @Test
    public void testDownloadSizeTooLargeHasAcceptedConditions() throws ServiceException
    {
        expect(itemServiceMock.getZippedContent(isA(EasyUser.class), isA(Dataset.class), isA(List.class))).andThrow(new ZipFileLengthException(datasetSid))
                .once();
        normalUserIsLoggedIn();
        replayAll();
        renderPage();
        ((EasyUserTestImpl) normalUser).setHasAcceptedGeneralConditions(true);

        tester.clickLink("tabs:panel:fe:downloadLink", true);
        tester.assertContains("Downloading is limited");
    }

    @Test
    public void testDownloadSizeTooLargeHasntAcceptedGeneralConditions() throws ServiceException
    {
        expect(itemServiceMock.getZippedContent(isA(EasyUser.class), isA(Dataset.class), isA(List.class))).andThrow(new ZipFileLengthException(datasetSid))
                .once();
        normalUserIsLoggedIn();
        replayAll();
        renderPage();
        ((EasyUserTestImpl) normalUser).setHasAcceptedGeneralConditions(false);

        tester.clickLink("tabs:panel:fe:downloadLink", true);
        tester.assertContains("Downloading is limited");
    }

    @Test
    public void testDownloadTooManyFilesHasAcceptedGeneralConditions() throws ServiceException
    {
        expect(itemServiceMock.getZippedContent(isA(EasyUser.class), isA(Dataset.class), isA(List.class))).andThrow(new TooManyFilesException(datasetSid))
                .once();
        normalUserIsLoggedIn();
        replayAll();
        renderPage();
        ((EasyUserTestImpl) normalUser).setHasAcceptedGeneralConditions(true);

        tester.clickLink("tabs:panel:fe:downloadLink", true);
        tester.assertContains("Downloading is limited");
    }

    @Test
    public void testDownloadTooManyFilesHasntAcceptedGeneralConditions() throws ServiceException
    {
        expect(itemServiceMock.getZippedContent(isA(EasyUser.class), isA(Dataset.class), isA(List.class))).andThrow(new TooManyFilesException(datasetSid))
                .once();
        normalUserIsLoggedIn();
        replayAll();
        renderPage();
        ((EasyUserTestImpl) normalUser).setHasAcceptedGeneralConditions(false);

        tester.clickLink("tabs:panel:fe:downloadLink", true);
        tester.assertContains("Downloading is limited");
    }

    @After
    public void tearDown()
    {
        resetAll();
    }

}
