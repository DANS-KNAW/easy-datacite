package nl.knaw.dans.easy.web.authn.login;

import java.net.URL;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.domain.authn.Authentication;
import nl.knaw.dans.easy.domain.authn.Authentication.State;
import nl.knaw.dans.easy.domain.user.EasyUserAnonymous;
import nl.knaw.dans.easy.servicelayer.services.FederativeUserService;
import nl.knaw.dans.easy.web.HomePage;
import nl.knaw.dans.easy.web.authn.ForgottenPasswordPage;

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
        tester = init();
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
        tester = init();
        tester.clickLink(REG_LOGIN_SUBMIT);
        tester.assertRenderedPage(LoginPage.class);

        tester.assertLabelContains(CREDENTIALS_FEEDBACK, "required");
        tester.assertLabelContains(USER_FEEDBACK, "required");
        tester.assertLabelContains(COMMON_FEEDBACK, "Please check the fields indicated below.");
    }

    @Test
    public void forgottenPassword() throws Exception
    {
        tester = init();
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

        tester = init();

        final FormTester formTester = tester.newFormTester(LOGIN_FORM);
        formTester.setValue("userId", VALID_USER_ID);
        formTester.setValue("credentials", PASSWORD);
        tester.clickLink(REG_LOGIN_SUBMIT);

        tester.assertRenderedPage(HomePage.class);
    }

    @Test
    public void invalidLogin1() throws Exception
    {
        submitInvalidUser();
        tester.assertInvisible(COMMON_FEEDBACK);
    }

    @Test
    public void invalidLogin2() throws Exception
    {
        authentication.setState(State.NotAuthenticated);
        authentication.setUser(null);
        submitInvalidUser();
        tester.assertLabelContains(COMMON_FEEDBACK, "Not authenticated");
    }

    private void submitInvalidUser() throws ServiceException
    {
        userService.authenticate(EasyMock.isA(Authentication.class));
        EasyMock.expectLastCall().anyTimes();
        tester = init();

        final FormTester formTester = tester.newFormTester(LOGIN_FORM);
        formTester.setValue("userId", INVALID_USER_ID);
        formTester.setValue("credentials", PASSWORD);
        tester.clickLink(REG_LOGIN_SUBMIT);

        tester.assertRenderedPage(LoginPage.class);
        tester.assertLabel(USER_ID_FIELD, INVALID_USER_ID);
        tester.assertLabel(PASSWORD_FIELD, PASSWORD);
        tester.assertInvisible(CREDENTIALS_FEEDBACK);
        tester.assertInvisible(USER_FEEDBACK);
    }

    protected EasyWicketTester init()
    {
        PowerMock.replayAll();
        final EasyWicketTester tester = EasyWicketTester.create(applicationContext);
        tester.startPage(LoginPage.class);
        return tester;
    }
}
