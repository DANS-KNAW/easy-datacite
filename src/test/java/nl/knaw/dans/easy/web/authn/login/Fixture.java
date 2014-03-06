package nl.knaw.dans.easy.web.authn.login;

import java.io.File;
import java.io.IOException;

import nl.knaw.dans.common.lang.FileSystemHomeDirectory;
import nl.knaw.dans.common.lang.HomeDirectory;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.domain.authn.UsernamePasswordAuthentication;
import nl.knaw.dans.easy.security.CodedAuthz;
import nl.knaw.dans.easy.security.Security;
import nl.knaw.dans.easy.servicelayer.SystemReadOnlyStatus;
import nl.knaw.dans.easy.servicelayer.services.UserService;

import org.apache.commons.io.FileUtils;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.powermock.api.easymock.PowerMock;

public class Fixture
{
    protected static final String FORGOTTEN_LINK = "loginPanelRegular:forgottenPassword";
    protected static final String TOKEN_FIELD = "loginPanelRegular:loginForm:token";
    protected static final String PASSWORD_FIELD = "loginPanelRegular:loginForm:credentials";
    protected static final String USER_ID_FIELD = "loginPanelRegular:loginForm:userId";
    protected static final String LOGIN_FORM = "loginPanelRegular:loginForm";
    protected static final String REG_LOGIN_SUBMIT = "loginPanelRegular:loginForm:login";
    protected static final String COMMON_FEEDBACK = "loginPanelRegular:loginForm:commonFeedbackPanel:feedbackul:messages";
    protected static final String USER_FEEDBACK = "loginPanelRegular:loginForm:userId-componentFeedback:feedbackul:messages";
    protected static final String CREDENTIALS_FEEDBACK = "loginPanelRegular:loginForm:credentials-componentFeedback:feedbackul:messages";

    protected ApplicationContextMock applicationContext;
    protected UserService userService;
    protected EasyWicketTester tester;
    protected UsernamePasswordAuthentication authentication;

    @Before
    public void mockApplicationContext() throws Exception
    {
        userService = PowerMock.createMock(UserService.class);
        authentication = new UsernamePasswordAuthentication();
        EasyMock.expect(userService.newUsernamePasswordAuthentication()).andStubReturn(authentication);

        final HomeDirectory homeDir = new FileSystemHomeDirectory(new File("src/main/assembly/dist/res/example/editable/"));

        final SystemReadOnlyStatus systemReadOnlyStatus = PowerMock.createMock(SystemReadOnlyStatus.class);
        EasyMock.expect(systemReadOnlyStatus.getReadOnly()).andStubReturn(false);

        final CodedAuthz codedAuthz = new CodedAuthz();
        codedAuthz.setSystemReadOnlyStatus(systemReadOnlyStatus);

        applicationContext = new ApplicationContextMock();
        applicationContext.putBean("systemReadOnlyStatus", systemReadOnlyStatus);
        applicationContext.putBean("authz", codedAuthz);
        applicationContext.putBean("security", new Security(codedAuthz));
        applicationContext.putBean("editableContentHome", homeDir);
        applicationContext.putBean("userService", userService);
        applicationContext.putBean("federationLoginDebugEnabled", false);
        applicationContext.putBean("federationLoginDebugUserFile", "");
        applicationContext.putBean("staticContentBaseUrl", "http://develop01.dans.knaw.nl/statics");
    }

    @After
    public void verify()
    {
        PowerMock.verifyAll();
        tester.verify();
        PowerMock.resetAll();
    }

    @AfterClass
    public static void cleanup()
    {
        // until someone sees any purpose to preserve these wicket files:
        final File folder = new File("target/work");
        if (folder.exists())
            try
            {
                FileUtils.forceDelete(folder);
            }
            catch (IOException e)
            {
                // occasional bad luck; might succeed next time
                // but don't make it fails the tests
            }
    }
}
