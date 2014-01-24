package nl.knaw.dans.easy.web.authn.login;

import static org.easymock.EasyMock.isA;

import java.io.Serializable;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

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

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

public class TestFederativeAuthenticationResultPage extends Fixture implements Serializable
{
    private static final long serialVersionUID = 1L;
    private EasyUserImpl easyUser;

    @Before
    public void mockFederationUser() throws Exception
    {
        easyUser = createUser();

        final FederativeUserService federativeUserService = PowerMock.createMock(FederativeUserService.class);
        new Services().setFederativeUserService(federativeUserService);
        EasyMock.expect(federativeUserService.getUserById(isA(EasyUser.class), isA(String.class))).andStubReturn(easyUser);
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
        easyUser.setState(State.ACTIVE);
        tester = init();
        tester.dumpPage();
        tester.assertRenderedPage(HomePage.class);
    }

    @Test
    public void blockedUser() throws Exception
    {
        easyUser.setState(State.BLOCKED);
        tester = init();
        tester.assertRenderedPage(LoginPage.class);
        tester.assertLabelContains(COMMON_FEEDBACK, "The account is blocked");
        tester.assertInvisible(CREDENTIALS_FEEDBACK);
        tester.assertInvisible(USER_FEEDBACK);
    }

    @Test
    public void registeredUser() throws Exception
    {
        easyUser.setState(State.REGISTERED);
        tester = init();
        tester.assertRenderedPage(LoginPage.class);
        tester.assertLabelContains(COMMON_FEEDBACK, "This account is not active.");
        tester.assertInvisible(CREDENTIALS_FEEDBACK);
        tester.assertInvisible(USER_FEEDBACK);
    }

    @Test
    public void UserConfirmedRegistration() throws Exception
    {
        easyUser.setState(State.CONFIRMED_REGISTRATION);
        tester = init();
        tester.assertRenderedPage(LoginPage.class);
        tester.assertLabelContains(COMMON_FEEDBACK, "This account is not active.");
        tester.assertInvisible(CREDENTIALS_FEEDBACK);
        tester.assertInvisible(USER_FEEDBACK);
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
