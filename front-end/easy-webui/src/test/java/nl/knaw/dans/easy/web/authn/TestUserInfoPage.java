package nl.knaw.dans.easy.web.authn;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.createMock;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.domain.federation.FederativeUserIdMap;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.FederativeUserService;
import nl.knaw.dans.easy.web.HomePage;

import org.junit.Test;

public class TestUserInfoPage extends UserInfoFixture
{
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

    @Test
    public void viewSmokeTest() throws Exception
    {
        UserInfoPageWrapper.enableModeSwith = true;
        UserInfoPageWrapper.inEditMode = false;
        UserInfoPageWrapper.userId = applicationContext.expectAuthenticatedAsVisitor().getId();
        final EasyWicketTester tester = EasyWicketTester.startPage(applicationContext, UserInfoPageWrapper.class);
        tester.dumpPage();
        tester.debugComponentTrees();
        tester.assertRenderedPage(UserInfoPageWrapper.class);

        final String sessionUserDisplayName = "S.U.R. Name";
        tester.assertLabel("displayName", sessionUserDisplayName);
        tester.assertLabel("userInfoPanel:switchPanel:displayName", "s. Hown");

        // used to fail when LogoffLink got an anonymous session user
        assertTrue(tester.getServletResponse().getDocument().contains(sessionUserDisplayName));
    }

    @Test
    public void clickEdit() throws Exception
    {
        applicationContext.expectAuthenticatedAsVisitor();
        UserInfoPageWrapper.enableModeSwith = true;
        UserInfoPageWrapper.inEditMode = false;
        UserInfoPageWrapper.userId = shownUser.getId();
        final EasyWicketTester tester = EasyWicketTester.startPage(applicationContext, UserInfoPageWrapper.class);
        tester.clickLink("userInfoPanel:switchPanel:editLink");
        tester.dumpPage();
    }

    @Test
    public void notLoggedIn() throws Exception
    {
        UserInfoPageWrapper.enableModeSwith = true;
        UserInfoPageWrapper.inEditMode = false;
        UserInfoPageWrapper.userId = shownUser.getId();
        final EasyWicketTester tester = EasyWicketTester.startPage(applicationContext, UserInfoPageWrapper.class);
        tester.assertRenderedPage(HomePage.class);
    }

    @Test
    public void deleteFederationUsers() throws Exception
    {
        UserInfoPageWrapper.enableModeSwith = true;
        UserInfoPageWrapper.inEditMode = false;
        UserInfoPageWrapper.userId = mockUserWithFederationLinks().getId();

        mockdFederativeUserRepo.delete(isA(FederativeUserIdMap.class));
        expectLastCall().times(2);

        final String switchPath = "userInfoPanel:switchPanel:";
        final String unlinkPath = switchPath + "unlinkInstitutionAccountsLink";
        final String labelPath = switchPath + "institutionAccounts";
        final String areYouSurePath = switchPath + "popup:content:confirm";
        final String yesPath = switchPath + "popup:content:yes";

        final EasyWicketTester tester = EasyWicketTester.startPage(applicationContext, UserInfoPageWrapper.class);
        tester.assertVisible(unlinkPath);
        tester.assertLabel(labelPath, "This EASY account is linked with 2 institution account(s)");
        tester.dumpPage();
        tester.clickLink(unlinkPath);
        tester.debugComponentTrees();
        tester.assertLabel(areYouSurePath, "Are you sure you want to remove the link(s) with 2 institution account(s)?");
        tester.clickLink(yesPath);
        tester.assertInvisible(unlinkPath);
    }

    @Test
    public void popupStyle() throws Exception
    {
        UserInfoPageWrapper.enableModeSwith = true;
        UserInfoPageWrapper.inEditMode = false;
        UserInfoPageWrapper.userId = mockUserWithFederationLinks().getId();

        final String switchPath = "userInfoPanel:switchPanel:";
        final String unlinkPath = switchPath + "unlinkInstitutionAccountsLink";

        final EasyWicketTester tester = EasyWicketTester.startPage(applicationContext, UserInfoPageWrapper.class);
        tester.clickLink(unlinkPath);
        tester.dumpPage();
    }

    private EasyUser mockUserWithFederationLinks() throws ServiceException, ObjectNotAvailableException
    {
        final FederativeUserService federativeUserServiceMock = createMock(FederativeUserService.class);
        applicationContext.putBean("federativeUserServiceMock", federativeUserServiceMock);

        final EasyUser sessionUser = applicationContext.expectAuthenticatedAsVisitor();
        federationUsers.add(new FederativeUserIdMap(UserInfoPageWrapper.userId, "mockeFedUserId1"));
        federationUsers.add(new FederativeUserIdMap(UserInfoPageWrapper.userId, "mockeFedUserId2"));
        expect(federativeUserServiceMock.getUserById(shownUser, null)).andStubReturn(sessionUser);
        return sessionUser;
    }
}
