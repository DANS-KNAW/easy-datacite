package nl.knaw.dans.easy.web.authn.login;

import java.net.URL;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.user.User;
import nl.knaw.dans.easy.EasyApplicationContextMock;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.domain.authn.Authentication;
import nl.knaw.dans.easy.domain.authn.Authentication.State;
import nl.knaw.dans.easy.domain.user.EasyUserAnonymous;
import nl.knaw.dans.easy.servicelayer.services.FederativeUserService;
import nl.knaw.dans.easy.web.HomePage;
import nl.knaw.dans.easy.web.authn.ForgottenPasswordPage;
import nl.knaw.dans.easy.web.deposit.DepositIntroPage;

import org.apache.wicket.util.tester.FormTester;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

public class TestLoginPage extends Fixture
{
    private static final String INVALID_USER_ID = "invalidUserID";
    private static final String PASSWORD = "password";
    private static final String VALID_USER_ID = "validUserID";
    private static final String REGISTRATION_LINK = "registration";
    private static final String FED_LOGIN_SUBMIT = "loginPanelFederation:federationLink";
    private static FederativeUserService federativeUserService;

    @Before
    public void mockFederativeUserService() throws Exception
    {
        federativeUserService = PowerMock.createMock(FederativeUserService.class);
        applicationContext.putBean("federativeUserService", federativeUserService);

        EasyMock.expect(federativeUserService.getFederationUrl()).andStubReturn(new URL("http://mocked.federative.url"));
        EasyMock.expect(federativeUserService.isFederationLoginEnabled()).andStubReturn(true);
    }

    @Test
    public void smokeTest() throws Exception
    {
        final EasyWicketTester tester = init();
        tester.assertRenderedPage(LoginPage.class);
        tester.assertInvisible(COMMON_FEEDBACK);
        tester.assertInvisible(USER_FEEDBACK);
        tester.assertInvisible(CREDENTIALS_FEEDBACK);
        tester.assertVisible(TOKEN_FIELD);
        tester.assertVisible(USER_ID_FIELD);
        tester.assertVisible(PASSWORD_FIELD);
        tester.assertVisible(FED_LOGIN_SUBMIT);
        tester.assertVisible(REG_LOGIN_SUBMIT);
        tester.assertVisible(FORGOTTEN_LINK);
        tester.assertVisible(REGISTRATION_LINK);
        tester.dumpPage();
    }

    @Test
    public void emptyLogin() throws Exception
    {
        final EasyWicketTester tester = init();
        tester.clickLink(REG_LOGIN_SUBMIT);
        tester.assertRenderedPage(LoginPage.class);

        tester.assertLabelContains(CREDENTIALS_FEEDBACK, "required");
        tester.assertLabelContains(USER_FEEDBACK, "required");
        tester.assertLabelContains(COMMON_FEEDBACK, "Please check the fields indicated below.");
    }

    @Test
    public void loginWhenLoggedIn() throws Exception
    {
        applicationContext.expectAuthenticatedAsVisitor();
        final EasyWicketTester tester = init();

        tester.dumpPage();
        assertEmptyLoginPage(tester);
    }

    @Test
    public void depositInReadOnlyMode() throws Exception
    {
        final EasyApplicationContextMock applicationContext = new EasyApplicationContextMock();
        applicationContext.expectStandardSecurity(false);
        applicationContext.expectDefaultResources();
        // an active state requires more to mock
        applicationContext.expectAuthenticatedAsVisitor().setState(User.State.REGISTERED);

        PowerMock.replayAll();
        final EasyWicketTester tester = EasyWicketTester.create(applicationContext);
        tester.startPage(DepositIntroPage.class);

        // in real life the banner says the system is going to shut down
        tester.dumpPage();
        assertEmptyLoginPage(tester);
    }

    private void assertEmptyLoginPage(final EasyWicketTester tester)
    {
        tester.assertRenderedPage(LoginPage.class);
        tester.debugComponentTrees();
        tester.assertLabel("displayName", "S.U.R. Name");
        tester.assertInvisible("register");
    }

    @Test
    public void forgottenPassword() throws Exception
    {
        final EasyWicketTester tester = init();
        tester.clickLink(FORGOTTEN_LINK);
        tester.assertRenderedPage(ForgottenPasswordPage.class);
    }

    @Test
    public void validLogin() throws Exception
    {
        authentication.setState(State.Authenticated);
        authentication.setUser(EasyUserAnonymous.getInstance());

        EasyMock.expect(userService.newUsernamePasswordAuthentication()).andStubReturn(authentication);
        userService.authenticate(authentication);
        EasyMock.expectLastCall();

        final EasyWicketTester tester = init();

        final FormTester formTester = tester.newFormTester(LOGIN_FORM);
        formTester.setValue("userId", VALID_USER_ID);
        formTester.setValue("credentials", PASSWORD);
        tester.clickLink(REG_LOGIN_SUBMIT);

        tester.assertRenderedPage(HomePage.class);
    }

    @Test
    public void invalidLogin1() throws Exception
    {
        final EasyWicketTester tester = submitInvalidUser();
        tester.assertInvisible(COMMON_FEEDBACK);
    }

    @Test
    public void invalidLogin2() throws Exception
    {
        authentication.setState(State.NotAuthenticated);
        authentication.setUser(null);
        final EasyWicketTester tester = submitInvalidUser();
        tester.assertLabelContains(COMMON_FEEDBACK, "Not authenticated");
    }

    private EasyWicketTester submitInvalidUser() throws ServiceException
    {
        userService.authenticate(EasyMock.isA(Authentication.class));
        EasyMock.expectLastCall().anyTimes();
        final EasyWicketTester tester = init();

        final FormTester formTester = tester.newFormTester(LOGIN_FORM);
        formTester.setValue("userId", INVALID_USER_ID);
        formTester.setValue("credentials", PASSWORD);
        tester.clickLink(REG_LOGIN_SUBMIT);

        tester.assertRenderedPage(LoginPage.class);
        tester.assertLabel(USER_ID_FIELD, INVALID_USER_ID);
        tester.assertLabel(PASSWORD_FIELD, PASSWORD);
        tester.assertInvisible(CREDENTIALS_FEEDBACK);
        tester.assertInvisible(USER_FEEDBACK);
        return tester;
    }

    protected EasyWicketTester init()
    {
        PowerMock.replayAll();
        final EasyWicketTester tester = EasyWicketTester.create(applicationContext);
        tester.startPage(LoginPage.class);
        return tester;
    }
}
