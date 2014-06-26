package nl.knaw.dans.easy.web.fileexplorer;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.isNull;
import static org.easymock.EasyMock.or;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.resetAll;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
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
import nl.knaw.dans.easy.domain.model.user.Group;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.servicelayer.services.ItemService;
import nl.knaw.dans.easy.servicelayer.services.SearchService;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.view.dataset.DatasetViewPage;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EmdFormat;
import nl.knaw.dans.pf.language.emd.types.ApplicationSpecific.MetadataFormat;

import org.apache.wicket.PageParameters;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

public class FileExplorerTest
{
    private static final String MESSAGE = "tabs:panel:fe:modalDownload:content:message";
    private static final String MESSAGE2 = "tabs:panel:fe:modalMessage:content:message";
    private static final String DOWNLOAD_LINK = "tabs:panel:fe:downloadLink";
    private EasyWicketTester tester;
    private EasyUserTestImpl sessionUser;
    private Dataset datasetMock;
    private DatasetService datasetServiceMock;
    private SearchService searchServiceMock;
    private ItemService itemServiceMock;

    private String datasetSid = "test-dataset:1";
    private DmoStoreId datasetDmoStoreId = new DmoStoreId(datasetSid);

    @Before
    public void setUp() throws Exception
    {
        sessionUser = new EasyUserTestImpl("normal", true);
        setUpDatasetServiceMock();
        setUpItemServiceMock();
        setUpSearchServiceMock();

        EasyApplicationContextMock ctx = new EasyApplicationContextMock();
        ctx.setItemService(itemServiceMock);
        ctx.setSearchService(searchServiceMock);
        ctx.setDatasetService(datasetServiceMock);
        ctx.expectStandardSecurity(false);
        ctx.expectDefaultResources();
        ctx.expectAuthenticatedAs(sessionUser);
        tester = EasyWicketTester.create(ctx);
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
                itemServiceMock.getFilesAndFolders(isA(EasyUser.class), isA(Dataset.class), isA(DmoStoreId.class), isA(Integer.class), isA(Integer.class),
                        or(isNull(ItemOrder.class), isA(ItemOrder.class)), or(isNull(ItemFilters.class), isA(ItemFilters.class)))).andReturn(filesAndFolders)
                .anyTimes();
        expect(itemServiceMock.hasChildItems(isA(DmoStoreId.class))).andReturn(false).anyTimes();
        expect(itemServiceMock.getAccessibleAudioVideoFiles(isA(EasyUser.class), isA(Dataset.class))).andReturn(new LinkedList<FileItemVO>()).anyTimes();
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
        expect(datasetMock.getDepositor()).andReturn(sessionUser).anyTimes();
        expect(datasetMock.hasDepositor(isA(EasyUser.class))).andReturn(true).anyTimes();
        expect(datasetMock.isInvalidated()).andReturn(false).anyTimes();
        expect(datasetMock.getPreferredTitle()).andReturn("Test Title").anyTimes();
        expect(datasetMock.hasVisibleItems(isA(EasyUser.class))).andReturn(true).anyTimes();
        expect(datasetMock.getPermissionSequenceList()).andReturn(new PermissionSequenceListImpl()).anyTimes();
        expect(datasetMock.getAdministrativeState()).andReturn(DatasetState.PUBLISHED).anyTimes();
        expect(datasetMock.getState()).andReturn(DatasetState.PUBLISHED.toString()).anyTimes();
        expect(datasetMock.getParentDisciplines()).andReturn(new ArrayList<DisciplineContainer>()).anyTimes();
        expect(datasetMock.hasPermissionRestrictedItems()).andReturn(false).anyTimes();

        AdministrativeMetadata amd = PowerMock.createMock(AdministrativeMetadata.class);
        expect(amd.getStateChangeDates()).andReturn(new ArrayList<StateChangeDate>()).anyTimes();
        expect(amd.getDepositor()).andReturn(sessionUser).anyTimes();

        expect(datasetMock.getAdministrativeMetadata()).andReturn(amd).anyTimes();
        expect(datasetMock.getLastModified()).andReturn(new DateTime()).anyTimes();
        expect(datasetMock.getTotalFileCount()).andReturn(1).anyTimes();
        expect(datasetMock.getTotalFolderCount()).andReturn(0).anyTimes();

        EasyMetadata emd = PowerMock.createMock(EasyMetadata.class);
        /*
         * Poor man's mocking. You can't seem to mock toString, so I am using this hack (JvM) See:
         * http://sourceforge.net/p/easymock/bugs/33/#b49f
         */
        EmdFormat emdFormat = new EmdFormat()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public String toString()
            {
                return "";
            }
        };
        expect(datasetMock.getEasyMetadata()).andReturn(emd).anyTimes();
        expect(emd.getEmdFormat()).andReturn(emdFormat).anyTimes();

    }

    private static class EasyUserTestImpl extends EasyUserImpl
    {
        private static final long serialVersionUID = 1L;
        boolean hasAcceptedGeneralConditions;

        public EasyUserTestImpl(String userId, boolean hasAcceptedGeneralConditions)
        {
            super(userId);
            this.hasAcceptedGeneralConditions = hasAcceptedGeneralConditions;
            setFirstname("Norman");
            setSurname("Normal");
            addRole(Role.USER);
            setState(State.ACTIVE);
        }

        public void setHasAcceptedGeneralConditions(boolean hasAcceptedGeneralConditions)
        {
            this.hasAcceptedGeneralConditions = hasAcceptedGeneralConditions;
        }

        public Set<Group> getGroups()
        {
            return new HashSet<Group>();
        }

        @Override
        public boolean hasAcceptedGeneralConditions()
        {
            return hasAcceptedGeneralConditions;
        }
    }

    private void renderPage()
    {
        replayAll();
        PageParameters parameters = new PageParameters();
        parameters.add("id", datasetSid);
        parameters.add("tab", "2");
        tester.startPage(new DatasetViewPage(parameters));
        tester.assertRenderedPage(DatasetViewPage.class);
        tester.dumpPage();
    }

    @Test
    public void testDownloadSizeTooLargeHasAcceptedConditions() throws ServiceException
    {
        expectFileLengthException();
        sessionUser.setHasAcceptedGeneralConditions(true);
        renderPage();

        tester.clickLink(DOWNLOAD_LINK, true);
        tester.assertLabelContains(MESSAGE2, "Downloading is limited");
        tester.assertLabelContains(MESSAGE2, "MB per download");
        tester.dumpPage();
    }

    @Test
    public void testDownloadSizeTooLargeHasntAcceptedGeneralConditions() throws ServiceException
    {
        expectFileLengthException();
        sessionUser.setHasAcceptedGeneralConditions(false);
        renderPage();

        tester.clickLink(DOWNLOAD_LINK, true);
        tester.assertLabelContains(MESSAGE, "Downloading is limited");
        tester.assertLabelContains(MESSAGE, "MB per download");
        tester.dumpPage();
    }

    @Test
    public void testDownloadTooManyFilesHasAcceptedGeneralConditions() throws ServiceException
    {
        expectTooManyFilesException();
        sessionUser.setHasAcceptedGeneralConditions(true);
        renderPage();

        tester.clickLink(DOWNLOAD_LINK, true);
        tester.debugComponentTrees();
        tester.assertLabelContains(MESSAGE2, "Downloading is limited");
        tester.assertLabelContains(MESSAGE2, "files per download");
        tester.dumpPage();
    }

    @Test
    public void testDownloadTooManyFilesHasntAcceptedGeneralConditions() throws ServiceException
    {
        expectTooManyFilesException();
        sessionUser.setHasAcceptedGeneralConditions(false);
        renderPage();

        tester.clickLink(DOWNLOAD_LINK, true);
        tester.assertLabelContains(MESSAGE, "Downloading is limited");
        tester.assertLabelContains(MESSAGE, "files per download");
        tester.dumpPage();
    }

    private void expectFileLengthException() throws ServiceException
    {
        expect(itemServiceMock.getZippedContent(isA(EasyUser.class), isA(Dataset.class), isA(List.class)))//
                .andThrow(new ZipFileLengthException(datasetSid)).once();
    }

    private void expectTooManyFilesException() throws ServiceException
    {
        expect(itemServiceMock.getZippedContent(isA(EasyUser.class), isA(Dataset.class), isA(List.class)))//
                .andThrow(new TooManyFilesException(datasetSid)).once();
    }

    @After
    public void tearDown()
    {
        resetAll();
    }
}
