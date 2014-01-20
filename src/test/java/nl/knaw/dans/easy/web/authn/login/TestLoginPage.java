package nl.knaw.dans.easy.web.authn.login;


import nl.knaw.dans.easy.EasyWicketTester;

import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

public class TestLoginPage extends Fixture
{
    // HINTS: get the "path" values
    // -Dwicket.configuration=development
    // tester.debugComponentTrees();
    private static final String REGISTRATION_LINK = "registration";
    private static final String FORGOTTEN_LINK = "loginPanelRegular:forgottenPassword";
    private static final String TOKEN_FIELD = "loginPanelRegular:loginForm:token";
    private static final String PASSWORD_FIELD = "loginPanelRegular:loginForm:credentials";
    private static final String USER_ID_FIELD = "loginPanelRegular:loginForm:userId";
    private static final String FED_LOGIN_SUBMIT = "loginPanelFederation:federationLink";
    private static final String REG_LOGIN_SUBMIT = "loginPanelRegular:loginForm:login";
    private static final String COMMON_FEEDBACK = "loginPanelRegular:loginForm:commonFeedbackPanel:feedbackul:messages";
    private static final String USER_FEEDBACK = "loginPanelRegular:loginForm:userId-componentFeedback:feedbackul:messages";
    private static final String CREDENTIALS_FEEDBACK = "loginPanelRegular:loginForm:credentials-componentFeedback:feedbackul:messages";

    @Test
    public void smokeTest() throws Exception
    {
        final EasyWicketTester tester = init();
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
        tester.verify();
    }

    @Test
    public void emptyLogin() throws Exception
    {
        final EasyWicketTester tester = init();
        tester.clickLink(REG_LOGIN_SUBMIT);

        tester.assertLabelContains(CREDENTIALS_FEEDBACK, "required");
        tester.assertLabelContains(USER_FEEDBACK, "required");
        tester.assertLabelContains(COMMON_FEEDBACK, "Please check the fields indicated below.");
        tester.verify();
    }

    protected EasyWicketTester init()
    {
        PowerMock.replayAll();
        final EasyWicketTester tester = EasyWicketTester.create(applicationContext);
        tester.startPage(LoginPage.class);
        return tester;
    }
}
