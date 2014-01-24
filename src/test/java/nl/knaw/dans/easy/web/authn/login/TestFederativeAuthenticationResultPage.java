package nl.knaw.dans.easy.web.authn.login;

import static org.easymock.EasyMock.isA;

import java.io.Serializable;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import nl.knaw.dans.common.lang.user.User.State;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.model.user.Group;
import nl.knaw.dans.easy.domain.user.EasyUserAnonymous;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.servicelayer.services.FederativeUserService;
import nl.knaw.dans.easy.servicelayer.services.SearchService;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.HomePage;

import org.apache.wicket.Page;
import org.apache.wicket.util.tester.ITestPageSource;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

public class TestFederativeAuthenticationResultPage extends Fixture implements Serializable
{
    private final class MockedFederativeAuthenticationResultPage extends FederativeAuthenticationResultPage
    {
        public boolean hasShibbolethSession(HttpServletRequest request)
        {
            request.setAttribute("shibSessionId", "mockedSessionID");
            request.setAttribute("email", "mockeEmail");
            request.setAttribute("firstName", "mockedFirstName");
            request.setAttribute("surname", "mockedSurname");
            request.setAttribute("remoteUser", "mockedRemoteUser");
            request.setAttribute("organization", "mockedOrganization");
            return true;
        }

        @Override
        public boolean isBookmarkable()
        {
            return true;
        }
    }

    private static final long serialVersionUID = 1L;
    private static FederativeUserService federativeUserService;

    @Before
    public void mockFederationUser() throws Exception
    {
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
        EasyMock.expect(federativeUserService.getUserById(null, null)).andStubReturn(EasyUserAnonymous.getInstance());
        applicationContext.putBean("federativeUserService", federativeUserService);

        SearchService searchService = PowerMock.createMock(SearchService.class);
        new Services().setSearchService(searchService);
        EasyMock.expect(searchService.getNumberOfDatasets(isA(EasyUser.class))).andStubReturn(0);
        EasyMock.expect(searchService.getNumberOfRequests(isA(EasyUser.class))).andStubReturn(0);
    }

    @Test
    public void activeUser() throws Exception
    {
        EasyUserImpl easyUser = createUser();
        easyUser.setState(State.ACTIVE);
        EasyMock.expect(federativeUserService.getUserById(isA(EasyUser.class), isA(String.class))).andStubReturn(easyUser);

        tester = init();
        tester.dumpPage();
        tester.assertRenderedPage(HomePage.class);
    }

    @Test
    public void blockedUser() throws Exception
    {
        EasyUserImpl easyUser = createUser();
        easyUser.setState(State.BLOCKED);
        EasyMock.expect(federativeUserService.getUserById(isA(EasyUser.class), isA(String.class))).andStubReturn(easyUser);

        tester = init();
        tester.dumpPage();
        tester.assertRenderedPage(MockedFederativeAuthenticationResultPage.class);
    }

    private EasyUserImpl createUser()
    {
        EasyUserImpl easyUser = new EasyUserImpl(Role.USER)
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
        tester.startPage(new ITestPageSource()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public Page getTestPage()
            {
                return new MockedFederativeAuthenticationResultPage();
            }
        });
        return tester;
    }

}
