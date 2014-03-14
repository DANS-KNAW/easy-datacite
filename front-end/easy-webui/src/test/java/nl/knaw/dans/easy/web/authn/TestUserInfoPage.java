package nl.knaw.dans.easy.web.authn;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.EasyApplicationContextMock;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.data.federation.FederativeUserRepo;
import nl.knaw.dans.easy.domain.deposit.discipline.ChoiceList;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.federation.FederativeUserIdMap;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserAnonymous;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.servicelayer.services.DepositService;
import nl.knaw.dans.easy.servicelayer.services.FederativeUserService;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.servicelayer.services.UserService;
import nl.knaw.dans.easy.web.HomePage;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

public class TestUserInfoPage
{
    protected EasyApplicationContextMock applicationContext;
    private EasyUserImpl shownUser;

    static public class UserInfoPageWrapper extends UserInfoPage
    {
        static boolean inEditMode;
        static boolean enableModeSwith;
        static String userId;

        public UserInfoPageWrapper()
        {
            super(userId, inEditMode, enableModeSwith);
        }
    }

    @Before
    public void mockApplicationContext() throws Exception
    {
        shownUser = new EasyUserImpl("shownUserId");
        shownUser.setInitials("s.");
        shownUser.setSurname("Hown");

        final UserService userService = PowerMock.createMock(UserService.class);
        expect(userService.isUserWithStoredPassword(EasyMock.eq(shownUser))).andReturn(true).anyTimes();
        expect(userService.getUserById(EasyMock.isA(EasyUser.class), EasyMock.isA(String.class))).andReturn(shownUser).anyTimes();

        applicationContext = new EasyApplicationContextMock();
        applicationContext.expectStandardSecurity(false);
        applicationContext.expectDefaultResources();
        applicationContext.putBean("depositService", mockDespositChoices());
        applicationContext.putBean("userService", userService);
    }

    /** Mock drop-down list for a discipline. */
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

    @Test
    public void viewSmokeTest() throws Exception
    {
        createSessionUser();
        UserInfoPageWrapper.enableModeSwith = true;
        UserInfoPageWrapper.inEditMode = false;
        UserInfoPageWrapper.userId = shownUser.getId();
        final EasyWicketTester tester = init();
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
        UserInfoPageWrapper.enableModeSwith = true;
        UserInfoPageWrapper.inEditMode = false;
        UserInfoPageWrapper.userId = createSessionUser().getId();
        final EasyWicketTester tester = init();
        tester.clickLink("userInfoPanel:switchPanel:editLink");
        tester.dumpPage();
    }

    @Test
    public void notLoggedIn() throws Exception
    {
        applicationContext.expectNoDepositsBy(EasyUserAnonymous.getInstance());
        UserInfoPageWrapper.enableModeSwith = true;
        UserInfoPageWrapper.inEditMode = false;
        UserInfoPageWrapper.userId = shownUser.getId();
        final EasyWicketTester tester = init();
        tester.assertRenderedPage(HomePage.class);
    }

    @Test
    public void hasFederationUsers() throws Exception
    {
        final FederativeUserRepo federativeUserRepoMock = PowerMock.createMock(FederativeUserRepo.class);
        final FederativeUserService federativeUserServiceMock = PowerMock.createMock(FederativeUserService.class);
        applicationContext.putBean("federativeUserRepo", federativeUserRepoMock);
        applicationContext.putBean("federativeUserServiceMock", federativeUserServiceMock);

        final EasyUser sessionUser = createSessionUser();
        final List<FederativeUserIdMap> list = new ArrayList<FederativeUserIdMap>();
        list.add(new FederativeUserIdMap(UserInfoPageWrapper.userId, "mockeFedUserId1"));
        list.add(new FederativeUserIdMap(UserInfoPageWrapper.userId, "mockeFedUserId2"));
        EasyMock.expect(federativeUserServiceMock.getUserById(shownUser, null)).andStubReturn(sessionUser);
        // TODO
        // EasyMock.expect(federativeUserRepoMock.findByDansUserId(UserInfoPageWrapper.userId)).andStubReturn(list);

        UserInfoPageWrapper.enableModeSwith = true;
        UserInfoPageWrapper.inEditMode = false;
        UserInfoPageWrapper.userId = sessionUser.getId();
        final EasyWicketTester tester = init();
        tester.assertRenderedPage(UserInfoPageWrapper.class);
        tester.dumpPage();
    }

    private EasyUser createSessionUser() throws ServiceException
    {
        final EasyUser user = new EasyUserImpl("sessionUserId");
        user.setInitials("s.");
        user.setSurname("Ession");
        applicationContext.expectNoDepositsBy(user);
        applicationContext.expectAuthenticatedAs(user, user.getId());
        return user;
    }

    @After
    public void verify()
    {
        PowerMock.verifyAll();
        PowerMock.resetAll();
    }

    protected EasyWicketTester init() throws Exception
    {
        PowerMock.replayAll();
        final EasyWicketTester tester = EasyWicketTester.create(applicationContext);
        tester.startPage(UserInfoPageWrapper.class);
        return tester;
    }
}
