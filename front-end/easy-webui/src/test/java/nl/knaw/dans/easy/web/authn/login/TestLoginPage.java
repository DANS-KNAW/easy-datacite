package nl.knaw.dans.easy.web.authn.login;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;

import java.net.URL;

import nl.knaw.dans.common.lang.dataset.DatasetSB;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.common.lang.search.simple.EmptySearchResult;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.user.User;
import nl.knaw.dans.easy.EasyApplicationContextMock;
import nl.knaw.dans.easy.EasyUserTestImpl;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.TestUtil;
import nl.knaw.dans.easy.business.authn.PasswordService;
import nl.knaw.dans.easy.business.services.EasyUserService;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.ext.ExternalServices;
import nl.knaw.dans.easy.data.userrepo.EasyUserRepo;
import nl.knaw.dans.easy.domain.authn.Authentication;
import nl.knaw.dans.easy.domain.authn.Authentication.State;
import nl.knaw.dans.easy.domain.authn.ForgottenPasswordMessenger;
import nl.knaw.dans.easy.domain.user.EasyUserAnonymous;
import nl.knaw.dans.easy.mail.EasyMailer;
import nl.knaw.dans.easy.servicelayer.services.FederativeUserService;
import nl.knaw.dans.easy.web.HomePage;
import nl.knaw.dans.easy.web.InfoPage;
import nl.knaw.dans.easy.web.authn.ForgottenPasswordPage;
import nl.knaw.dans.easy.web.deposit.DepositIntroPage;

import org.apache.wicket.util.tester.FormTester;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestLoginPage extends Fixture {
    private static final String PASSWORD_FORM_PATH = "forgottenPasswordPanel:forgottenPasswordForm";
    private static final String FIRST_COMMON_FEEDBACK_MESSAGE = ":commonFeedbackPanel:feedbackul:messages:0:message";
    private static final String INVALID_USER_ID = "invalidUserID";
    private static final String PASSWORD = "password";
    private static final String VALID_USER_ID = "validUserID";
    private static final String REGISTRATION_LINK = "registration";
    private static final String FED_LOGIN_SUBMIT = "loginPanelFederation:federationLink";
    private static FederativeUserService federativeUserService;

    @Before
    public void mockFederativeUserService() throws Exception {
        federativeUserService = createMock(FederativeUserService.class);
        applicationContext.putBean("federativeUserService", federativeUserService);
        applicationContext.expectNoDatasetsInToolBar(new EmptySearchResult<DatasetSB>());

        expect(federativeUserService.getFederationUrl()).andStubReturn(new URL("http://mocked.federative.url"));
        expect(federativeUserService.isFederationLoginEnabled()).andStubReturn(true);
    }

    @After
    public void reset() {
        TestUtil.cleanup();
    }

    @Test
    public void smokeTest() throws Exception {
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
    public void emptyLogin() throws Exception {
        final EasyWicketTester tester = init();
        tester.clickLink(REG_LOGIN_SUBMIT);
        tester.assertRenderedPage(LoginPage.class);

        tester.assertLabelContains(CREDENTIALS_FEEDBACK, "required");
        tester.assertLabelContains(USER_FEEDBACK, "required");
        tester.assertLabelContains(COMMON_FEEDBACK, "Please check the fields indicated below.");
    }

    @Test
    public void loginWhenLoggedIn() throws Exception {
        applicationContext.expectAuthenticatedAsVisitor();
        final EasyWicketTester tester = init();

        tester.dumpPage();
        assertEmptyLoginPage(tester);
    }

    private void assertEmptyLoginPage(final EasyWicketTester tester) {
        tester.assertRenderedPage(LoginPage.class);
        tester.assertLabel("displayName", "S.U.R. Name");
        tester.assertInvisible("register");
    }

    @Test
    public void cancelForgottenPassword() throws Exception {
        final EasyWicketTester tester = init();
        tester.clickLink(FORGOTTEN_LINK);
        tester.assertRenderedPage(ForgottenPasswordPage.class);
        tester.dumpPage();
        tester.clickLink("forgottenPasswordPanel:forgottenPasswordForm:cancel");
        tester.assertRenderedPage(HomePage.class);
    }

    @Test
    public void submitEmptyForgottenPassword() throws Exception {
        final EasyWicketTester tester = init();
        tester.clickLink(FORGOTTEN_LINK);
        tester.assertRenderedPage(ForgottenPasswordPage.class);
        tester.newFormTester(PASSWORD_FORM_PATH).submit();
        tester.dumpPage();
        tester.assertLabelContains(PASSWORD_FORM_PATH + FIRST_COMMON_FEEDBACK_MESSAGE, "contains errors");
        tester.assertRenderedPage(ForgottenPasswordPage.class);
    }

    @Test
    public void submitIdForforgottenPasswordFailed() throws Exception {
        mockUserNotFound();
        mockHandleForgottenPasswordRequest();
        final EasyWicketTester tester = init();
        final FormTester formTester = clickForgottenPassword(tester);
        formTester.setValue("userId", "someUserId");
        formTester.submit();
        tester.dumpPage();
        // message for a manual test: "no user found"
        tester.assertLabelContains(PASSWORD_FORM_PATH + FIRST_COMMON_FEEDBACK_MESSAGE, "No user found");
        tester.assertRenderedPage(ForgottenPasswordPage.class);
    }

    @Test
    public void submitIdForforgottenPassword() throws Exception {
        mockQualifiedUser();
        mockHandleForgottenPasswordRequest();
        final EasyWicketTester tester1 = init();
        final FormTester formTester = clickForgottenPassword(tester1);
        formTester.setValue("userId", "someUserId");
        formTester.submit();
        final EasyWicketTester tester = tester1;
        tester.dumpPage();
        tester.assertRenderedPage(InfoPage.class);
        tester.dumpPage("mailSent");
    }

    @Test
    public void submitInvalidEmailForforgottenPassword() throws Exception {
        mockQualifiedUser();
        mockHandleForgottenPasswordRequest();

        final EasyWicketTester tester = init();
        final FormTester formTester = clickForgottenPassword(tester);
        final String messagePath = PASSWORD_FORM_PATH + ":email-componentFeedback:feedbackul:messages:0:message";
        final String emailValue = "rabarbera";
        formTester.setValue("email", emailValue);
        formTester.submit();
        tester.dumpPage();
        tester.assertLabelContains(messagePath, "not a valid email address");
        tester.assertLabelContains(messagePath, emailValue);
        tester.assertRenderedPage(ForgottenPasswordPage.class);
    }

    private FormTester clickForgottenPassword(final EasyWicketTester tester) {
        tester.clickLink(FORGOTTEN_LINK);
        tester.assertRenderedPage(ForgottenPasswordPage.class);
        final FormTester formTester = tester.newFormTester(PASSWORD_FORM_PATH);
        return formTester;
    }

    private void mockQualifiedUser() throws Exception {
        final EasyUserRepo userRepo = createMock(EasyUserRepo.class);

        // required by business layer so no SpringBean injection possible
        new Data().setUserRepo(userRepo);

        EasyUserTestImpl mockedUser = new EasyUserTestImpl("mockedUser") {
            private static final long serialVersionUID = 1L;

            public boolean isQualified() {
                return true;
            }
        };
        mockedUser.setSurname("Test");
        mockedUser.setInitials("A.B.");

        expect(userRepo.findById(isA(String.class))).andStubReturn(mockedUser);
        applicationContext.putBean("userRepo", userRepo);
    }

    private void mockUserNotFound() throws Exception {
        final EasyUserRepo userRepo = createMock(EasyUserRepo.class);

        // required by business layer so no SpringBean injection possible
        new Data().setUserRepo(userRepo);

        expect(userRepo.findById(isA(String.class))).andStubThrow(new ObjectNotInStoreException());
        applicationContext.putBean("userRepo", userRepo);
    }

    private void mockHandleForgottenPasswordRequest() throws Exception {
        final String[] s = {};
        final EasyMailer mailer = createMock(EasyMailer.class);
        final EasyUserService easyUserService = new EasyUserService();
        final PasswordService passwordService = new PasswordService() {
            // hack because ClassPathHacker does not help to find resources in AbstractNotification and EasyMailComposer
            protected void handleSendUpdatePasswordLink(final ForgottenPasswordMessenger messenger) throws ServiceException {}
        };
        easyUserService.setPasswordService(passwordService);
        new ExternalServices().setMailOffice(mailer);

        mailer.sendMail(isA(String.class), isA(s.getClass()), isA(String.class), isA(String.class));
        expectLastCall().anyTimes();

        applicationContext.getUserService().handleForgottenPasswordRequest(isA(ForgottenPasswordMessenger.class));
        expectLastCall().andStubDelegateTo(easyUserService);
    }

    @Test
    public void validLogin() throws Exception {
        authentication.setState(State.Authenticated);
        authentication.setUser(EasyUserAnonymous.getInstance());

        expect(userService.newUsernamePasswordAuthentication()).andStubReturn(authentication);
        userService.authenticate(authentication);
        expectLastCall();

        final EasyWicketTester tester = init();

        final FormTester formTester = tester.newFormTester(LOGIN_FORM);
        formTester.setValue("userId", VALID_USER_ID);
        formTester.setValue("credentials", PASSWORD);
        tester.clickLink(REG_LOGIN_SUBMIT);

        tester.assertRenderedPage(HomePage.class);
    }

    @Test
    public void invalidLogin1() throws Exception {
        final EasyWicketTester tester = submitInvalidUser();
        tester.assertInvisible(COMMON_FEEDBACK);
    }

    @Test
    public void invalidLogin2() throws Exception {
        authentication.setState(State.NotAuthenticated);
        authentication.setUser(null);
        final EasyWicketTester tester = submitInvalidUser();
        tester.assertLabelContains(COMMON_FEEDBACK, "Not authenticated");
    }

    private EasyWicketTester submitInvalidUser() throws ServiceException {
        userService.authenticate(isA(Authentication.class));
        expectLastCall().anyTimes();
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

    protected EasyWicketTester init() {
        replayAll();
        final EasyWicketTester tester = EasyWicketTester.create(applicationContext);
        tester.startPage(LoginPage.class);
        return tester;
    }
}
