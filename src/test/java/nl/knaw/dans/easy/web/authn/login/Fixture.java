package nl.knaw.dans.easy.web.authn.login;

import java.io.File;

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
import org.junit.BeforeClass;
import org.powermock.api.easymock.PowerMock;

public class Fixture
{
    protected static final String FORGOTTEN_LINK = "loginPanelRegular:forgottenPassword";
    protected static final String TOKEN_FIELD = "loginPanelRegular:loginForm:token";
    protected static final String PASSWORD_FIELD = "loginPanelRegular:loginForm:credentials";
    protected static final String USER_ID_FIELD = "loginPanelRegular:loginForm:userId";
    protected static final String REG_LOGIN_SUBMIT = "loginPanelRegular:loginForm:login";
    protected static final String COMMON_FEEDBACK = "loginPanelRegular:loginForm:commonFeedbackPanel:feedbackul:messages";
    protected static final String USER_FEEDBACK = "loginPanelRegular:loginForm:userId-componentFeedback:feedbackul:messages";
    protected static final String CREDENTIALS_FEEDBACK = "loginPanelRegular:loginForm:credentials-componentFeedback:feedbackul:messages";

    protected static ApplicationContextMock applicationContext;
    private static UserService userService;
    protected EasyWicketTester tester;

    @BeforeClass
    public static void mockApplicationContext() throws Exception
    {
        PowerMock.resetAll();

        userService = PowerMock.createMock(UserService.class);
        EasyMock.expect(userService.newUsernamePasswordAuthentication()).andStubReturn(new UsernamePasswordAuthentication());

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
        applicationContext.putBean("federationLoginDebugEnabled",false);
        applicationContext.putBean("federationLoginDebugUserFile","");
    }

    @After
    public void verify()
    {
        PowerMock.verifyAll();
        tester.verify();
    }

    @AfterClass
    public static void cleanup() throws Exception
    {
        // until someone sees any purpose to preserve these wicket files:
        final File folder = new File("target/work");
        if (folder.exists())
            FileUtils.forceDelete(folder);
    }
}
