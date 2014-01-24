package nl.knaw.dans.easy.web.authn.login;

import java.net.URL;

import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.servicelayer.services.FederativeUserService;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

public class TestLoginPage extends Fixture
{
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

        tester.assertLabelContains(CREDENTIALS_FEEDBACK, "required");
        tester.assertLabelContains(USER_FEEDBACK, "required");
        tester.assertLabelContains(COMMON_FEEDBACK, "Please check the fields indicated below.");
    }

    protected EasyWicketTester init()
    {
        PowerMock.replayAll();
        final EasyWicketTester tester = EasyWicketTester.create(applicationContext);
        tester.startPage(LoginPage.class);
        return tester;
    }
}
