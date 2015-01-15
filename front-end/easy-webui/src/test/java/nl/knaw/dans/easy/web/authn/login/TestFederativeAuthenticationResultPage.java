package nl.knaw.dans.easy.web.authn.login;

import static org.easymock.EasyMock.isA;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import nl.knaw.dans.common.lang.dataset.DatasetSB;
import nl.knaw.dans.common.lang.search.simple.EmptySearchResult;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.user.User.State;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.TestUtil;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.model.user.Group;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.servicelayer.services.FederativeUserService;
import nl.knaw.dans.easy.web.HomePage;
import nl.knaw.dans.easy.web.InfoPage;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

public class TestFederativeAuthenticationResultPage extends Fixture {
    private static final String MISSION_ACCOMPLISHED_MESSAGES = "missionAccomplishedFeedback:feedbackul:messages:0:message";
    private static final String SHIBOLET_ERROR_MESSAGE = "An error occurred while trying to log in with your federation account.  Please contact DANS (info@dans.knaw.nl) ";

    private FederationUserFactory.Factory federationUserFactory;
    private FederativeUserService federativeUserService;
    private FederationUser fedUser;
    private EasyUserImpl easyUser;

    @Before
    public void mockFederationUser() throws Exception {
        easyUser = createUser();

        federativeUserService = PowerMock.createMock(FederativeUserService.class);
        EasyMock.expect(federativeUserService.getPropertyNameShibSessionId()).andStubReturn("shibSessionId");
        EasyMock.expect(federativeUserService.getPropertyNameEmail()).andStubReturn("email");
        EasyMock.expect(federativeUserService.getPropertyNameFirstName()).andStubReturn("firstName");
        EasyMock.expect(federativeUserService.getPropertyNameSurname()).andStubReturn("surname");
        EasyMock.expect(federativeUserService.getPropertyNameRemoteUser()).andStubReturn("remoteUser");
        EasyMock.expect(federativeUserService.getPopertyNameOrganization()).andStubReturn("organization");
        EasyMock.expect(federativeUserService.getFederationUrl()).andStubReturn(new URL("https://mock.dans.knaw.nl/Shibboleth.sso/Login"));
        EasyMock.expect(federativeUserService.isFederationLoginEnabled()).andStubReturn(true);
        applicationContext.putBean("federativeUserService", federativeUserService);
        applicationContext.expectNoDatasetsInToolBar(new EmptySearchResult<DatasetSB>());

        fedUser = new FederationUser();
        fedUser.setUserId("mockFederationID");
        fedUser.setEmail("mockFederationEmail");
        fedUser.setGivenName("mockFederationGivenName");
        fedUser.setHomeOrg("mockFederationHomeOrg");
        fedUser.setSurName("mockFederationSurname");
        federationUserFactory = PowerMock.createMock(FederationUserFactory.Factory.class);
        FederationUserFactory.setFactory(federationUserFactory);

    }

    @After
    public void reset() {
        TestUtil.cleanup();
    }

    @Test
    public void activeUser() throws Exception {
        EasyMock.expect(federationUserFactory.create(EasyMock.isA(HttpServletRequest.class))).andStubReturn(fedUser);
        EasyMock.expect(federativeUserService.getUserById(isA(EasyUser.class), isA(String.class)))//
                .andStubReturn(easyUser);
        easyUser.setState(State.ACTIVE);
        final EasyWicketTester tester = init();
        tester.debugComponentTrees();
        tester.dumpPage();
        tester.assertRenderedPage(HomePage.class);
    }

    @Test
    public void blockedUser() throws Exception {
        EasyMock.expect(federationUserFactory.create(EasyMock.isA(HttpServletRequest.class))).andStubReturn(fedUser);
        EasyMock.expect(federativeUserService.getUserById(isA(EasyUser.class), isA(String.class)))//
                .andStubReturn(easyUser);
        easyUser.setState(State.BLOCKED);
        final EasyWicketTester tester = init();
        tester.dumpPage();
        assertNotActive(tester);
    }

    @Test
    public void registeredUser() throws Exception {
        EasyMock.expect(federationUserFactory.create(EasyMock.isA(HttpServletRequest.class))).andStubReturn(fedUser);
        EasyMock.expect(federativeUserService.getUserById(isA(EasyUser.class), isA(String.class)))//
                .andStubReturn(easyUser);
        easyUser.setState(State.REGISTERED);
        final EasyWicketTester tester = init();
        tester.dumpPage();
        assertNotActive(tester);
    }

    @Test
    public void UserConfirmedRegistration() throws Exception {
        EasyMock.expect(federationUserFactory.create(EasyMock.isA(HttpServletRequest.class))).andStubReturn(fedUser);
        EasyMock.expect(federativeUserService.getUserById(isA(EasyUser.class), isA(String.class)))//
                .andStubReturn(easyUser);
        easyUser.setState(State.CONFIRMED_REGISTRATION);
        final EasyWicketTester tester = init();
        tester.dumpPage();
        assertNotActive(tester);
    }

    private void assertNotActive(final EasyWicketTester tester) {
        tester.debugComponentTrees();
        tester.assertRenderedPage(LoginPage.class);
        tester.assertLabelContains(COMMON_FEEDBACK, "This account is not active.");
        tester.assertInvisible(CREDENTIALS_FEEDBACK);
        tester.assertInvisible(USER_FEEDBACK);
    }

    @Test
    public void userNotLinked() throws Exception {
        EasyMock.expect(federationUserFactory.create(EasyMock.isA(HttpServletRequest.class))).andStubReturn(fedUser);
        EasyMock.expect(federativeUserService.getUserById(isA(EasyUser.class), isA(String.class)))//
                .andStubThrow(new ObjectNotAvailableException("mockedException"));
        easyUser.setState(State.REGISTERED);
        final EasyWicketTester tester = init();
        tester.assertRenderedPage(FederationToEasyAccountLinkingPage.class);
        tester.debugComponentTrees();
        tester.dumpPage();
        tester.assertLabel("federationUserInfoPanel:institutiondescription", "mockFederationHomeOrg");
        tester.assertLabel("federationUserInfoPanel:userdescription", "mockFederationSurname, mockFederationGivenName (mockFederationEmail)");
        tester.assertInvisible(COMMON_FEEDBACK);
        tester.assertInvisible(CREDENTIALS_FEEDBACK);
        tester.assertInvisible(USER_FEEDBACK);
    }

    @Test
    public void federativeServiceException() throws Exception {
        EasyMock.expect(federationUserFactory.create(EasyMock.isA(HttpServletRequest.class))).andStubReturn(fedUser);
        EasyMock.expect(federativeUserService.getUserById(isA(EasyUser.class), isA(String.class)))//
                .andStubThrow(new ServiceException("mockedException"));
        easyUser.setState(State.REGISTERED);
        final EasyWicketTester tester = init();
        tester.debugComponentTrees();
        tester.dumpPage();
        tester.assertRenderedPage(InfoPage.class);
        tester.assertLabel("heading", "Error during federation login");
        tester.assertLabelContains(MISSION_ACCOMPLISHED_MESSAGES, SHIBOLET_ERROR_MESSAGE);
    }

    @Test
    public void noShibolethSession() throws Exception {
        EasyMock.expect(federationUserFactory.create(EasyMock.isA(HttpServletRequest.class))).andThrow(new IllegalArgumentException("mockedException"));
        easyUser.setState(State.REGISTERED);
        final EasyWicketTester tester = init();
        tester.debugComponentTrees();
        tester.dumpPage();
        tester.assertRenderedPage(InfoPage.class);
        tester.assertLabelContains("heading", "Error during federation login");
        tester.assertLabel(MISSION_ACCOMPLISHED_MESSAGES, SHIBOLET_ERROR_MESSAGE);
    }

    private EasyUserImpl createUser() {
        final EasyUserImpl easyUser = new EasyUserImpl(Role.USER) {
            private static final long serialVersionUID = 1L;

            public Set<Group> getGroups() {
                return new HashSet<Group>();
            }

        };
        return easyUser;
    }

    protected EasyWicketTester init() {
        PowerMock.replayAll();
        final EasyWicketTester tester = EasyWicketTester.create(applicationContext);
        tester.startPage(PartiallyMockedResultPage.class);
        return tester;
    }
}
