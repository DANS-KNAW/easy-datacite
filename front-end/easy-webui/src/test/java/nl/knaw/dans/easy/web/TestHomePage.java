package nl.knaw.dans.easy.web;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertNull;
import static org.powermock.api.easymock.PowerMock.replayAll;
import nl.knaw.dans.common.lang.dataset.DatasetSB;
import nl.knaw.dans.common.lang.search.simple.EmptySearchResult;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.user.User.State;
import nl.knaw.dans.easy.EasyApplicationContextMock;
import nl.knaw.dans.easy.EasyUserTestImpl;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.TestUtil;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.servicelayer.services.SearchService;
import nl.knaw.dans.easy.web.main.SystemReadOnlyLink;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

public class TestHomePage {

    private WicketTester tester;
    private EasyUser normalUser;
    private EasyUser archivistUser;
    private EasyUser adminUser;
    private SearchService searchServiceMock;

    @Test
    public void testRenderLoggedOff() throws Exception {
        startPage(mockContext());
        tester.dumpPage();

        assertLinkVisibilityConformsToLoggedOffStatus();
        tester.assertInvisible(SystemReadOnlyLink.WICKET_ID_LINK);
        assertHomeBrowseAdvSearchVisible();
        assertNavDepositVisible();
        assertPersonalBarItemsNotRendered();
        assertManagementPanelNotRendered();
    }

    private EasyApplicationContextMock mockContext() throws Exception {
        EasyApplicationContextMock ctx = new EasyApplicationContextMock();
        ctx.expectStandardSecurity();
        ctx.expectDefaultResources();
        ctx.setSearchService(searchServiceMock);
        ctx.expectNoDatasetsInToolBar(new EmptySearchResult<DatasetSB>());
        return ctx;
    }

    private EasyApplicationContextMock mockContext(EasyUser user) throws Exception, ServiceException {
        EasyApplicationContextMock ctx = mockContext();
        ctx.expectAuthenticatedAs(user);
        return ctx;
    }

    private void startPage(EasyApplicationContextMock ctx) {
        tester = EasyWicketTester.create(ctx);
        replayAll();
        tester.startPage(HomePage.class);
        tester.assertRenderedPage(HomePage.class);
    }

    @Before
    public void setUp() throws Exception {
        setUpUsers();
        setupSearchServiceMock();
    }

    @After
    public void tearDown() {
        TestUtil.cleanup();
    }

    private void assertLinkVisibilityConformsToLoggedOffStatus() {
        tester.assertVisible("login");
        tester.assertVisible("register");
        tester.assertInvisible("logoff");
    }

    private void assertHomeBrowseAdvSearchVisible() {
        tester.assertVisible("homePage");
        tester.assertVisible("browsePage");
        tester.assertVisible("advancedSearchPage");
    }

    private void assertNavDepositVisible() {
        tester.assertVisible("navDeposit");
    }

    private void assertPersonalBarItemsNotRendered() {
        assertNull("MyDatasets rendered but should not be", tester.getTagByWicketId("myDatasets"));
        assertNull("MyRequests rendered but should not be", tester.getTagByWicketId("myRequests"));
    }

    private void assertManagementPanelNotRendered() {
        assertNull("Management bar panel rendered but should not be", tester.getTagByWicketId("managementBarPanel"));
    }

    private void assertNormalUserNameInDisplayName() {
        tester.assertLabel("displayName", "Norman Normal");
    }

    @Test
    public void testRenderLoggedInAsUser() throws Exception {
        startPage(mockContext(normalUser));
        tester.dumpPage();

        assertLinkVisibilityConformsToLoggedInStatus();
        assertHomeBrowseAdvSearchVisible();
        tester.assertInvisible(SystemReadOnlyLink.WICKET_ID_LINK);
        assertNavDepositVisible();
        assertPersonalBarItemsVisible();
        assertManagementPanelNotRendered();
        assertNormalUserNameInDisplayName();
    }

    private void assertLinkVisibilityConformsToLoggedInStatus() {
        tester.assertInvisible("login");
        tester.assertInvisible("register");
        tester.assertVisible("logoff");
    }

    @Test
    public void testRenderLoggedInAsArchivist() throws Exception {
        startPage(mockContext(archivistUser));
        tester.dumpPage();

        assertLinkVisibilityConformsToLoggedInStatus();
        assertHomeBrowseAdvSearchVisible();
        tester.assertInvisible(SystemReadOnlyLink.WICKET_ID_LINK);
        assertNavDepositVisible();
        assertPersonalBarItemsVisible();
        tester.assertLabel("displayName", "Archie Archiver (Archivist)");
    }

    @Test
    public void testRenderLoggedInAsAdmin() throws Exception {
        startPage(mockContext(adminUser));
        tester.dumpPage();

        assertLinkVisibilityConformsToLoggedInStatus();
        assertHomeBrowseAdvSearchVisible();
        tester.assertVisible(SystemReadOnlyLink.WICKET_ID_LINK);
        assertNavDepositVisible();
        assertPersonalBarItemsVisible();
        tester.assertLabel("displayName", "Ad Administrator (Administrator)");
    }

    private void setUpUsers() {
        normalUser = new EasyUserTestImpl("normal");
        normalUser.setFirstname("Norman");
        normalUser.setSurname("Normal");
        normalUser.addRole(Role.USER);
        normalUser.setState(State.ACTIVE);

        archivistUser = new EasyUserTestImpl("archie");
        archivistUser.setFirstname("Archie");
        archivistUser.setSurname("Archiver");
        archivistUser.addRole(Role.USER);
        archivistUser.addRole(Role.ARCHIVIST);
        archivistUser.setState(State.ACTIVE);

        adminUser = new EasyUserTestImpl("ad");
        adminUser.setFirstname("Ad");
        adminUser.setSurname("Administrator");
        adminUser.addRole(Role.USER);
        adminUser.addRole(Role.ADMIN);
        adminUser.setState(State.ACTIVE);
    }

    private void setupSearchServiceMock() throws Exception {
        searchServiceMock = PowerMock.createMock(SearchService.class);
        // we want those items to be visible on the Personal bar
        expect(searchServiceMock.getNumberOfDatasets(isA(EasyUser.class))).andReturn(1).anyTimes();
        expect(searchServiceMock.getNumberOfRequests(isA(EasyUser.class))).andReturn(1).anyTimes();

        // show a number for the management items (management bar)
        expect(searchServiceMock.getNumberOfItemsInTrashcan(isA(EasyUser.class))).andReturn(1).anyTimes();
        expect(searchServiceMock.getNumberOfItemsInAllWork(isA(EasyUser.class))).andReturn(1).anyTimes();
        expect(searchServiceMock.getNumberOfItemsInOurWork(isA(EasyUser.class))).andReturn(1).anyTimes();
        expect(searchServiceMock.getNumberOfItemsInMyWork(isA(EasyUser.class))).andReturn(1).anyTimes();
    }

    private void assertPersonalBarItemsVisible() {
        tester.assertVisible("myDatasets");
        tester.assertVisible("myRequests");
    }
}
