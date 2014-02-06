package nl.knaw.dans.easy.web.authn;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import nl.knaw.dans.common.lang.FileSystemHomeDirectory;
import nl.knaw.dans.common.lang.HomeDirectory;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.domain.authn.UsernamePasswordAuthentication;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.security.CodedAuthz;
import nl.knaw.dans.easy.security.Security;
import nl.knaw.dans.easy.servicelayer.SystemReadOnlyStatus;
import nl.knaw.dans.easy.servicelayer.services.DepositService;
import nl.knaw.dans.easy.servicelayer.services.SearchService;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.servicelayer.services.UserService;
import nl.knaw.dans.easy.web.HomePage;

import org.apache.wicket.spring.test.ApplicationContextMock;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

public class TestUserInfoPage
{
    protected ApplicationContextMock applicationContext;
    protected EasyWicketTester tester;
    private EasyUserImpl shownUser;
    private UserService userService;

    @Before
    public void mockApplicationContext() throws Exception
    {
        shownUser = new EasyUserImpl("shownUserId");
        shownUser.setInitials("s.");
        shownUser.setSurname("Hown");

        userService = PowerMock.createMock(UserService.class);
        expect(userService.isUserWithStoredPassword(EasyMock.eq(shownUser))).andReturn(true).anyTimes();
        expect(userService.getUserById(EasyMock.isA(EasyUser.class), EasyMock.isA(String.class))).andReturn(shownUser).anyTimes();

        applicationContext = new ApplicationContextMock();
        applicationContext.putBean("security", new Security(createCodedAuthz()));
        applicationContext.putBean("editableContentHome", getHomeDir());
        applicationContext.putBean("depositService", mockDespositChoices());
        applicationContext.putBean("searchService", mockSearchService());
        applicationContext.putBean("userService", userService);
    }

    /**
     * Were are not changing the editable files but need it for the banner, so we can use the source
     * versions.
     */
    private HomeDirectory getHomeDir()
    {
        return new FileSystemHomeDirectory(new File("src/main/assembly/dist/res/example/editable/"));
    }

    /** Standard security rules for an update mode of the system */
    private CodedAuthz createCodedAuthz()
    {
        final SystemReadOnlyStatus systemReadOnlyStatus = PowerMock.createMock(SystemReadOnlyStatus.class);
        EasyMock.expect(systemReadOnlyStatus.getReadOnly()).andStubReturn(false);

        final CodedAuthz codedAuthz = new CodedAuthz();
        codedAuthz.setSystemReadOnlyStatus(systemReadOnlyStatus);
        applicationContext.putBean("systemReadOnlyStatus", systemReadOnlyStatus);
        applicationContext.putBean("authz", codedAuthz);
        return codedAuthz;
    }

    /** We are not going to deposit anything, so no choices needed. */
    private DepositService mockDespositChoices() throws ServiceException
    {
        final ArrayList<KeyValuePair> choices = new ArrayList<KeyValuePair>();
        choices.add(new KeyValuePair("custom.Disciplines", "mockedDisciplines"));

        final DepositService depositService = PowerMock.createMock(DepositService.class);
        EasyMock.expect(depositService.getChoices(EasyMock.isA(String.class), (Locale) EasyMock.isNull())).andStubReturn(new ChoiceList(choices));

        // can't use SpringBean in the static DisciplineUtils
        new Services().setDepositService(depositService);
        return depositService;
    }

    /** The user has no data sets and no search requests */
    private SearchService mockSearchService() throws ServiceException
    {
        final SearchService searchService = PowerMock.createMock(SearchService.class);
        EasyMock.expect(searchService.getNumberOfDatasets(isA(EasyUser.class))).andStubReturn(0);
        EasyMock.expect(searchService.getNumberOfRequests(isA(EasyUser.class))).andStubReturn(0);
        return searchService;
    }

    @Test
    public void viewSmokeTest() throws Exception
    {
        UserInfoPageWrapper.enableModeSwith = true;
        UserInfoPageWrapper.inEditMode = false;
        UserInfoPageWrapper.userId = shownUser.getId();
        init(createSessionUser());
        tester.dumpPage();
        tester.debugComponentTrees();
        tester.assertRenderedPage(UserInfoPageWrapper.class);

        final String sessionUserDisplayName = "s. Ession";
        tester.assertLabel("displayName", sessionUserDisplayName);
        tester.assertLabel("userInfoPanel:switchPanel:displayName", "s. Hown");

        // used to fail when LogoffLink got an anonymous session user
        assertTrue(tester.getServletResponse().getDocument().contains(sessionUserDisplayName));
    }

    @Test
    public void clickEdit() throws Exception
    {
        final EasyUserImpl sessionUser = createSessionUser();
        UserInfoPageWrapper.enableModeSwith = true;
        UserInfoPageWrapper.inEditMode = false;
        UserInfoPageWrapper.userId = sessionUser.getId();
        init(sessionUser);
        tester.clickLink("userInfoPanel:switchPanel:editLink");
        tester.dumpPage();
    }

    @Test
    public void notLoggedIn() throws Exception
    {
        UserInfoPageWrapper.enableModeSwith = true;
        UserInfoPageWrapper.inEditMode = false;
        UserInfoPageWrapper.userId = shownUser.getId();
        initAnonymous();
        tester.assertRenderedPage(HomePage.class);
    }

    private EasyUserImpl createSessionUser()
    {
        final EasyUserImpl user = new EasyUserImpl("sessionUserId");
        user.setInitials("s.");
        user.setSurname("Ession");
        return user;
    }

    @After
    public void verify()
    {
        PowerMock.verifyAll();
        tester.verify();
        PowerMock.resetAll();
    }

    protected void initAnonymous() throws Exception
    {
        PowerMock.replayAll();
        tester = EasyWicketTester.create(applicationContext);
        tester.startPage(UserInfoPageWrapper.class);
    }

    protected void init(final EasyUser sessionUser) throws Exception
    {
        final UsernamePasswordAuthentication authentication = new UsernamePasswordAuthentication();
        authentication.setUser(sessionUser);
        if (!sessionUser.isAnonymous())
            authentication.setUserId(sessionUser.getId());

        EasyMock.expect(userService.newUsernamePasswordAuthentication()).andStubReturn(authentication);
        PowerMock.replayAll();

        tester = EasyWicketTester.create(applicationContext, authentication);
        tester.startPage(UserInfoPageWrapper.class);
    }
}
