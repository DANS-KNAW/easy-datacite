package nl.knaw.dans.easy.web.authn.login;

import static org.easymock.EasyMock.isA;

import java.io.Serializable;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.user.User.State;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.model.user.Group;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.servicelayer.services.FederativeUserService;
import nl.knaw.dans.easy.servicelayer.services.SearchService;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.HomePage;
import nl.knaw.dans.easy.web.InfoPage;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

public class TestFederativeAuthenticationResultPage extends Fixture implements Serializable
{
    private static final String MISSION_ACCOMPLISHED_MESSAGES = "missionAccomplishedFeedback:feedbackul:messages:0:message";
    private static final String SHIBOLET_ERROR_MESSAGE = "An error occurred while trying to log in with your federation account.  Please contact DANS (info@dans.knaw.nl) ";
    private static final long serialVersionUID = 1L;
    private EasyUserImpl easyUser;
    private FederativeUserService federativeUserService;

    @Before
    public void mockFederationUser() throws Exception
    {
        easyUser = createUser();
        PartiallyMockedResultPage.hasShibolethSession = true;

        federativeUserService = PowerMock.createMock(FederativeUserService.class);
        new Services().setFederativeUserService(federativeUserService);
        EasyMock.expect(federativeUserService.getPropertyNameShibSessionId()).andStubReturn("shibSessionId");
        EasyMock.expect(federativeUserService.getPropertyNameEmail()).andStubReturn("email");
        EasyMock.expect(federativeUserService.getPropertyNameFirstName()).andStubReturn("firstName");
        EasyMock.expect(federativeUserService.getPropertyNameSurname()).andStubReturn("surname");
        EasyMock.expect(federativeUserService.getPropertyNameRemoteUser()).andStubReturn("remoteUser");
        EasyMock.expect(federativeUserService.getPopertyNameOrganization()).andStubReturn("organization");
        EasyMock.expect(federativeUserService.getFederationUrl()).andStubReturn(new URL("https://mock.dans.knaw.nl/Shibboleth.sso/Login"));
        EasyMock.expect(federativeUserService.isFederationLoginEnabled()).andStubReturn(true);
        applicationContext.putBean("federativeUserService", federativeUserService);

        final SearchService searchService = PowerMock.createMock(SearchService.class);
        EasyMock.expect(searchService.getNumberOfDatasets(isA(EasyUser.class))).andStubReturn(0);
        EasyMock.expect(searchService.getNumberOfRequests(isA(EasyUser.class))).andStubReturn(0);
        applicationContext.putBean("searchService", searchService);
    }

    @Test
    public void activeUser() throws Exception
    {
        EasyMock.expect(federativeUserService.getUserById(isA(EasyUser.class), isA(String.class)))//
                .andStubReturn(easyUser);
        easyUser.setState(State.ACTIVE);
        tester = init();
        tester.debugComponentTrees();
        tester.dumpPage();
        tester.assertRenderedPage(HomePage.class);
    }

    @Test
    public void blockedUser() throws Exception
    {
        EasyMock.expect(federativeUserService.getUserById(isA(EasyUser.class), isA(String.class)))//
                .andStubReturn(easyUser);
        easyUser.setState(State.BLOCKED);
        tester = init();
        tester.dumpPage();
        assertNotActive();
    }

    @Test
    public void registeredUser() throws Exception
    {
        EasyMock.expect(federativeUserService.getUserById(isA(EasyUser.class), isA(String.class)))//
                .andStubReturn(easyUser);
        easyUser.setState(State.REGISTERED);
        tester = init();
        tester.dumpPage();
        assertNotActive();
    }

    @Test
    public void UserConfirmedRegistration() throws Exception
    {
        EasyMock.expect(federativeUserService.getUserById(isA(EasyUser.class), isA(String.class)))//
                .andStubReturn(easyUser);
        easyUser.setState(State.CONFIRMED_REGISTRATION);
        tester = init();
        tester.dumpPage();
        assertNotActive();
    }

    private void assertNotActive()
    {
        tester.debugComponentTrees();
        tester.assertRenderedPage(LoginPage.class);
        tester.assertLabelContains(COMMON_FEEDBACK, "This account is not active.");
        tester.assertInvisible(CREDENTIALS_FEEDBACK);
        tester.assertInvisible(USER_FEEDBACK);
    }

    @Test
    public void userNotLinked() throws Exception
    {
        EasyMock.expect(federativeUserService.getUserById(isA(EasyUser.class), isA(String.class)))//
                .andStubThrow(new ObjectNotAvailableException("mockedException"));
        easyUser.setState(State.REGISTERED);
        tester = init();
        tester.assertRenderedPage(FederationToEasyAccountLinkingPage.class);
        tester.debugComponentTrees();
        tester.dumpPage();
        tester.assertLabel("federationUserInfoPanel:institutiondescription", "mockedOrganization");
        tester.assertLabel("federationUserInfoPanel:userdescription", "mockedSurname, mockedFirstName (mockeEmail)");
        tester.assertInvisible(COMMON_FEEDBACK);
        tester.assertInvisible(CREDENTIALS_FEEDBACK);
        tester.assertInvisible(USER_FEEDBACK);
    }

    @Test
    public void federativeServiceException() throws Exception
    {
        EasyMock.expect(federativeUserService.getUserById(isA(EasyUser.class), isA(String.class)))//
                .andStubThrow(new ServiceException("mockedException"));
        easyUser.setState(State.REGISTERED);
        tester = init();
        tester.assertRenderedPage(InfoPage.class);
        tester.debugComponentTrees();
        tester.dumpPage();
        tester.assertLabel("heading", SHIBOLET_ERROR_MESSAGE);
        // TODO: inconsistent with noShibolethSession?
        // asserted with contains because the message is prefixed with a date-time
        tester.assertLabelContains(MISSION_ACCOMPLISHED_MESSAGES, SHIBOLET_ERROR_MESSAGE);
    }

    @Test
    public void noShibolethSession() throws Exception
    {
        PartiallyMockedResultPage.hasShibolethSession = false;
        easyUser.setState(State.REGISTERED);
        tester = init();
        tester.assertRenderedPage(InfoPage.class);
        tester.debugComponentTrees();
        tester.dumpPage();
        tester.assertLabelContains("heading", "Error during federation login");
        tester.assertLabel(MISSION_ACCOMPLISHED_MESSAGES, SHIBOLET_ERROR_MESSAGE);
    }

    private EasyUserImpl createUser()
    {
        final EasyUserImpl easyUser = new EasyUserImpl(Role.USER)
        {
            private static final long serialVersionUID = 1L;

            public Set<Group> getGroups()
            {
                return new HashSet<Group>();
            }

        };
        return easyUser;
    }

    protected EasyWicketTester init()
    {
        PowerMock.replayAll();
        final EasyWicketTester tester = EasyWicketTester.create(applicationContext);
        tester.startPage(PartiallyMockedResultPage.class);
        return tester;
    }
}
