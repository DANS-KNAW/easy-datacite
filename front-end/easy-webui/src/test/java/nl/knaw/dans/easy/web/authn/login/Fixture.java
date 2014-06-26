package nl.knaw.dans.easy.web.authn.login;

import nl.knaw.dans.easy.EasyApplicationContextMock;
import nl.knaw.dans.easy.domain.authn.UsernamePasswordAuthentication;
import nl.knaw.dans.easy.servicelayer.services.UserService;

import org.easymock.EasyMock;
import org.junit.After;
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

    protected EasyApplicationContextMock applicationContext;
    protected UserService userService;
    protected UsernamePasswordAuthentication authentication;

    @Before
    public void mockApplicationContext() throws Exception
    {
        userService = PowerMock.createMock(UserService.class);
        authentication = new UsernamePasswordAuthentication();
        EasyMock.expect(userService.newUsernamePasswordAuthentication()).andStubReturn(authentication);

        applicationContext = new EasyApplicationContextMock();
        applicationContext.expectStandardSecurity(false);
        applicationContext.expectDefaultResources();
        applicationContext.setUserService(userService);
        applicationContext.putBean("federationLoginDebugEnabled", false);
        applicationContext.putBean("federationLoginDebugUserFile", "");
    }

    @After
    public void verify()
    {
        PowerMock.verifyAll();
        PowerMock.resetAll();
    }
}
