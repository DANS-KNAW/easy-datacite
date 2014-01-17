package nl.knaw.dans.easy.web.authn.login;

import java.io.File;
import java.net.URL;

import nl.knaw.dans.common.lang.FileSystemHomeDirectory;
import nl.knaw.dans.common.lang.HomeDirectory;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.domain.authn.UsernamePasswordAuthentication;
import nl.knaw.dans.easy.security.CodedAuthz;
import nl.knaw.dans.easy.security.Security;
import nl.knaw.dans.easy.servicelayer.SystemReadOnlyStatus;
import nl.knaw.dans.easy.servicelayer.services.FederativeUserService;
import nl.knaw.dans.easy.servicelayer.services.UserService;

import org.apache.wicket.spring.test.ApplicationContextMock;
import org.easymock.EasyMock;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

public class TestLoginPage
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

    private static ApplicationContextMock applicationContext;

    private static FederativeUserService federativeUserService;
    private static UserService userService;

    @BeforeClass
    public static void mockApplicationContext() throws Exception
    {
        PowerMock.resetAll();

        federativeUserService = PowerMock.createMock(FederativeUserService.class);
        EasyMock.expect(federativeUserService.getFederationUrl()).andStubReturn(new URL("http://mocked.federative.url"));
        EasyMock.expect(federativeUserService.isFederationLoginEnabled()).andStubReturn(true);

        userService = PowerMock.createMock(UserService.class);
        EasyMock.expect(userService.newUsernamePasswordAuthentication()).andStubReturn(new UsernamePasswordAuthentication());

        final HomeDirectory homeDir = new FileSystemHomeDirectory(new File("src/main/assembly/dist/res/example/editable/"));
        final SystemReadOnlyStatus systemReadOnlyStatus = PowerMock.createMock(SystemReadOnlyStatus.class);
        final CodedAuthz codedAuthz = new CodedAuthz();
        codedAuthz.setSystemReadOnlyStatus(systemReadOnlyStatus);

        applicationContext = new ApplicationContextMock();
        applicationContext.putBean("systemReadOnlyStatus", systemReadOnlyStatus);
        applicationContext.putBean("authz", codedAuthz);
        applicationContext.putBean("security", new Security(codedAuthz));
        applicationContext.putBean("editableContentHome", homeDir);
        applicationContext.putBean("federativeUserService", federativeUserService);
        applicationContext.putBean("userService", userService);
    }

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
        tester.assertLabelContains(CREDENTIALS_FEEDBACK, "required");
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

    private EasyWicketTester init()
    {
        PowerMock.replayAll();
        final EasyWicketTester tester = EasyWicketTester.create(applicationContext);
        tester.startPage(LoginPage.class);
        return tester;
    }
}
