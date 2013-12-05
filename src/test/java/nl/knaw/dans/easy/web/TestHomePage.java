package nl.knaw.dans.easy.web;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertNull;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.resetAll;

import java.io.File;

import nl.knaw.dans.common.lang.FileSystemHomeDirectory;
import nl.knaw.dans.common.lang.user.User.State;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.user.EasyUserAnonymous;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.security.CodedAuthz;
import nl.knaw.dans.easy.security.ContextParameters;
import nl.knaw.dans.easy.security.Security;
import nl.knaw.dans.easy.servicelayer.SystemReadonlyStatusCamelCaseChangePreparation;
import nl.knaw.dans.easy.servicelayer.services.SearchService;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.main.SystemReadOnlyLink;
import nl.knaw.dans.easy.web.statistics.StatisticsEvent;
import nl.knaw.dans.easy.web.statistics.StatisticsLogger;

import org.apache.wicket.spring.test.ApplicationContextMock;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Services.class, Security.class, StatisticsLogger.class, EasySession.class})
public class TestHomePage
{

    private WicketTester tester;
    private EasyUser normalUser;
    private EasyUser archivistUser;
    private EasyUser adminUser;
    private EasySession easySessionMock;
    private StatisticsLogger statisticsLoggerMock;
    private SearchService searchServiceMock;

    @Test
    public void testRenderLoggedOff()
    {
        userIsLoggedOff();
        replayAll();

        renderHomePage();
        assertLinkVisibilityConformsToLoggedOffStatus();
        tester.assertInvisible(SystemReadOnlyLink.WICKET_ID_LINK);
        assertHomeBrowseAdvSearchVisible();
        assertNavDepositVisible();
        assertPersonalBarItemsNotRendered();
        assertManagementPanelNotRendered();
    }

    private void userIsLoggedOff()
    {
        expectUser(EasyUserAnonymous.getInstance());
    }

    private void expectUser(EasyUser user)
    {
        expect(easySessionMock.getUser()).andReturn(user).anyTimes();
        expect(EasySession.getSessionUser()).andReturn(user).anyTimes();
        expect(easySessionMock.getContextParameters()).andReturn(new ContextParameters(user)).anyTimes();
    }

    private void renderHomePage()
    {
        tester.startPage(HomePage.class);
        tester.assertRenderedPage(HomePage.class);
    }

    private void assertLinkVisibilityConformsToLoggedOffStatus()
    {
        tester.assertVisible("login");
        tester.assertVisible("register");
        tester.assertInvisible("logoff");
    }

    private void assertHomeBrowseAdvSearchVisible()
    {
        tester.assertVisible("homePage");
        tester.assertVisible("browsePage");
        tester.assertVisible("advancedSearchPage");
    }

    private void assertNavDepositVisible()
    {
        tester.assertVisible("navDeposit");
    }

    private void assertPersonalBarItemsNotRendered()
    {
        assertNull("MyDatasets rendered but should not be", tester.getTagByWicketId("myDatasets"));
        assertNull("MyRequests rendered but should not be", tester.getTagByWicketId("myRequests"));
    }

    private void assertManagementPanelNotRendered()
    {
        assertNull("Management bar panel rendered but should not be", tester.getTagByWicketId("managementBarPanel"));
    }

    private void assertNormalUserNameInDisplayName()
    {
        tester.assertLabel("displayName", "Norman Normal");
    }

    @Test
    public void testRenderLoggedInAsUser()
    {
        normalUserIsLoggedIn();
        replayAll();

        renderHomePage();
        assertLinkVisibilityConformsToLoggedInStatus();
        assertHomeBrowseAdvSearchVisible();
        tester.assertInvisible(SystemReadOnlyLink.WICKET_ID_LINK);
        assertNavDepositVisible();
        assertPersonalBarItemsVisible();
        assertManagementPanelNotRendered();
        assertNormalUserNameInDisplayName();
    }

    private void normalUserIsLoggedIn()
    {
        expectUser(normalUser);
    }

    private void assertLinkVisibilityConformsToLoggedInStatus()
    {
        tester.assertInvisible("login");
        tester.assertInvisible("register");
        tester.assertVisible("logoff");
    }

    @Test
    public void testRenderLoggedInAsArchivist()
    {
        archivistIsLoggedIn();
        replayAll();

        renderHomePage();
        assertLinkVisibilityConformsToLoggedInStatus();
        assertHomeBrowseAdvSearchVisible();
        tester.assertInvisible(SystemReadOnlyLink.WICKET_ID_LINK);
        assertNavDepositVisible();
        assertPersonalBarItemsVisible();
        assertArchivistManagementPanelVisible();
        assertArchivstUserNameAndRolesInDisplayName();
    }

    @Test
    public void testRenderLoggedInAsAdmin()
    {
        adminIsLoggedIn();
        replayAll();

        renderHomePage();
        assertLinkVisibilityConformsToLoggedInStatus();
        assertHomeBrowseAdvSearchVisible();
        tester.assertVisible(SystemReadOnlyLink.WICKET_ID_LINK);
        assertNavDepositVisible();
        assertPersonalBarItemsVisible();
        assertAdminManagementPanelVisible();
        assertAdminUserNameAndRolesInDisplayName();
    }

    @Before
    public void setUp() throws Exception
    {
        ApplicationContextMock ctx = new ApplicationContextMock();
        ctx.putBean("editableContentHome", new FileSystemHomeDirectory(new File("src/main/assembly/dist/res/example/editable")));
        ctx.putBean("systemReadonlyStatus", createSystemReadonlyBean());
        EasyWicketApplication app = new EasyWicketApplication();
        app.setApplicationContext(ctx);
        tester = new WicketTester(app);
        setUpAuthz();
        setUpUsers();
        setUpEasySessionMock();
        setUpStatisticsLoggerMock();

        setupSearchServiceMock();
    }

    private void setUpAuthz()
    {
        /*
         * Attention! We are not mocking Authz, but using the CodedAuthz class. This class contains the
         * authorization rules used in production as well.
         */
        mockStatic(Security.class);
        expect(Security.getAuthz()).andReturn(createCodedAuthz()).anyTimes();
    }

    private CodedAuthz createCodedAuthz()
    {
        CodedAuthz codedAuthz = new CodedAuthz();
        codedAuthz.setSystemReadonlyStatus(createSystemReadonlyBean());
        return codedAuthz;
    }

    private SystemReadonlyStatusCamelCaseChangePreparation createSystemReadonlyBean()
    {
        SystemReadonlyStatusCamelCaseChangePreparation systemReadonlyStatus = new SystemReadonlyStatusCamelCaseChangePreparation();
        systemReadonlyStatus.setFile(new File("target/SystemReadOnlyStatus.properties"));
        return systemReadonlyStatus;
    }

    private void setUpUsers()
    {
        normalUser = new EasyUserImpl("normal");
        normalUser.setFirstname("Norman");
        normalUser.setSurname("Normal");
        normalUser.addRole(Role.USER);
        normalUser.setState(State.ACTIVE);

        archivistUser = new EasyUserImpl("archie");
        archivistUser.setFirstname("Archie");
        archivistUser.setSurname("Archiver");
        archivistUser.addRole(Role.USER);
        archivistUser.addRole(Role.ARCHIVIST);
        archivistUser.setState(State.ACTIVE);

        adminUser = new EasyUserImpl("ad");
        adminUser.setFirstname("Ad");
        adminUser.setSurname("Administrator");
        adminUser.addRole(Role.USER);
        adminUser.addRole(Role.ADMIN);
        adminUser.setState(State.ACTIVE);
    }

    private void setUpEasySessionMock()
    {
        mockStatic(EasySession.class);
        easySessionMock = PowerMock.createMock(EasySession.class);
        expect(EasySession.get()).andReturn(easySessionMock).anyTimes();
    }

    private void setUpStatisticsLoggerMock()
    {
        mockStatic(StatisticsLogger.class);
        statisticsLoggerMock = PowerMock.createMock(StatisticsLogger.class);
        expect(StatisticsLogger.getInstance()).andReturn(statisticsLoggerMock).anyTimes();
        statisticsLoggerMock.logEvent(isA(StatisticsEvent.class));
        PowerMock.expectLastCall().anyTimes();
    }

    private void setupSearchServiceMock() throws Exception
    {
        searchServiceMock = PowerMock.createMock(SearchService.class);
        // we want those items to be visible on the Personal bar
        expect(searchServiceMock.getNumberOfDatasets(isA(EasyUser.class))).andReturn(1).anyTimes();
        expect(searchServiceMock.getNumberOfRequests(isA(EasyUser.class))).andReturn(1).anyTimes();

        // show a number for the management items (management bar)
        expect(searchServiceMock.getNumberOfItemsInTrashcan(isA(EasyUser.class))).andReturn(1).anyTimes();
        expect(searchServiceMock.getNumberOfItemsInAllWork(isA(EasyUser.class))).andReturn(1).anyTimes();
        expect(searchServiceMock.getNumberOfItemsInOurWork(isA(EasyUser.class))).andReturn(1).anyTimes();
        expect(searchServiceMock.getNumberOfItemsInMyWork(isA(EasyUser.class))).andReturn(1).anyTimes();

        mockStatic(Services.class);
        expect(Services.getSearchService()).andReturn(searchServiceMock).anyTimes();
    }

    @After
    public void tearDown()
    {
        resetAll();
    }

    private void assertArchivstUserNameAndRolesInDisplayName()
    {
        tester.assertLabel("displayName", "Archie Archiver (Archivist)");
    }

    private void archivistIsLoggedIn()
    {
        expectUser(archivistUser);
    }

    private void assertAdminUserNameAndRolesInDisplayName()
    {
        tester.assertLabel("displayName", "Ad Administrator (Administrator)");
    }

    private void adminIsLoggedIn()
    {
        expectUser(adminUser);
    }

    private void assertPersonalBarItemsVisible()
    {
        tester.assertVisible("myDatasets");
        tester.assertVisible("myRequests");
    }

    private void assertArchivistManagementPanelVisible()
    {
        tester.assertVisible("managementBarPanel:myWork");
        tester.assertVisible("managementBarPanel:ourWork");
        tester.assertVisible("managementBarPanel:allWork");
        tester.assertVisible("managementBarPanel:trashCan");
        tester.assertVisible("managementBarPanel:userInfo");
        tester.assertVisible("managementBarPanel:editableContent");
    }

    private void assertAdminManagementPanelVisible()
    {
        tester.assertVisible("managementBarPanel:trashCan");
        tester.assertVisible("managementBarPanel:userInfo");
        tester.assertVisible("managementBarPanel:editableContent");
    }
}
