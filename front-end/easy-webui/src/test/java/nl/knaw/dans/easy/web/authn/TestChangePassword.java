package nl.knaw.dans.easy.web.authn;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;

import java.util.HashSet;
import java.util.Set;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.business.services.EasyUserService;
import nl.knaw.dans.easy.domain.authn.ChangePasswordMessenger;
import nl.knaw.dans.easy.domain.authn.ForgottenPasswordMailAuthentication;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.Group;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.servicelayer.services.UserService;
import nl.knaw.dans.easy.web.ErrorPage;
import nl.knaw.dans.easy.web.HomePage;
import nl.knaw.dans.easy.web.InfoPage;

import org.apache.wicket.PageParameters;
import org.apache.wicket.util.tester.FormTester;
import org.joda.time.DateTime;
import org.junit.Test;

public class TestChangePassword extends UserInfoFixture {

    private static final String CHANGE_PASSWORD_FORM = "changePasswordPanel:changePasswordForm";
    private static final String NEW_PASSWORD_MESSAGE_PATH = CHANGE_PASSWORD_FORM + ":newPassword-componentFeedback:feedbackul:messages:0:message";

    @Test
    public void cancelChangePassword() throws Exception {
        addMocks(createSessionUser());
        final EasyWicketTester tester = startFromHomePage();
        tester.dumpPage();
        tester.clickLink(CHANGE_PASSWORD_FORM + ":cancel");
        tester.assertRenderedPage(UserInfoPage.class);
    }

    @Test
    public void anonymousChangesPassword() throws Exception {
        // this covers a security check, but how to get here manually?
        final EasyWicketTester tester = EasyWicketTester.startPage(applicationContext, ChangePasswordPage.class);
        tester.dumpPage();
        tester.assertRenderedPage(HomePage.class);
    }

    @Test
    public void submitChangePassword() throws Exception {
        final String messagePath = "missionAccomplishedFeedback:feedbackul:messages:0:message";
        addMocks(createSessionUser());
        final EasyWicketTester tester = startFromHomePage();
        final FormTester formTester = tester.newFormTester(CHANGE_PASSWORD_FORM);
        formTester.setValue("oldPassword", "pqstuvwx");
        formTester.setValue("newPassword", "12345678");
        formTester.setValue("confirmPassword", "12345678");
        formTester.submit();
        tester.dumpPage();
        tester.debugComponentTrees();
        tester.assertLabelContains(messagePath, "with your new password");
        tester.assertRenderedPage(InfoPage.class);
    }

    @Test
    public void submitEmptyChangePassword() throws Exception {
        addMocks(createSessionUser());
        final EasyWicketTester tester = startFromHomePage();
        tester.newFormTester(CHANGE_PASSWORD_FORM).submit();
        tester.dumpPage();
        tester.assertLabelContains(NEW_PASSWORD_MESSAGE_PATH, "required");
        tester.assertRenderedPage(ChangePasswordPage.class);
    }

    @Test
    public void submitIdenticalChangePassword() throws Exception {
        addMocks(createSessionUser());
        final EasyWicketTester tester = startFromHomePage();
        final FormTester formTester = tester.newFormTester(CHANGE_PASSWORD_FORM);
        formTester.setValue("oldPassword", "12345678");
        formTester.setValue("newPassword", "12345678");
        formTester.setValue("confirmPassword", "12345678");
        formTester.submit();
        tester.dumpPage();
        tester.assertLabelContains(NEW_PASSWORD_MESSAGE_PATH, "that differs from");
        tester.assertRenderedPage(ChangePasswordPage.class);
    }

    @Test
    public void changeForgottenPassword() throws Exception {
        mockAuthentication(true);
        final EasyWicketTester tester = startForgottenPasswordPage(true, true, true);
        // with a manual test the new password field has a value
        // with this emulated test there is no value
        tester.dumpPage();
        final FormTester formTester = tester.newFormTester(CHANGE_PASSWORD_FORM);
        formTester.submit();
        tester.assertLabelContains(NEW_PASSWORD_MESSAGE_PATH, "required");
        tester.assertRenderedPage(ChangePasswordPage.class);
    }

    @Test
    public void changeInvalidForgottenPassword1() throws Exception {
        mockAuthentication(true);
        final EasyWicketTester tester = startForgottenPasswordPage(true, true, false);
        tester.dumpPage();
        assertFeedbackMessage(tester, "Invalid url");
    }

    @Test
    public void changeInvalidForgottenPassword2() throws Exception {
        mockAuthentication(true);
        final EasyWicketTester tester = startForgottenPasswordPage(true, false, false);
        assertFeedbackMessage(tester, "Invalid url");
    }

    @Test
    public void changeInvalidForgottenPassword3() throws Exception {
        mockAuthentication(true);
        final EasyWicketTester tester = startForgottenPasswordPage(false, false, false);
        assertFeedbackMessage(tester, "Invalid url");
    }

    @Test
    public void changeInvalidForgottenPassword4() throws Exception {
        mockAuthentication(false);
        final EasyWicketTester tester = startForgottenPasswordPage(true, true, true);
        assertFeedbackMessage(tester, "Invalid url");
    }

    @Test
    public void changeInvalidForgottenPassword5() throws Exception {
        mockAuthenticationException();
        final EasyWicketTester tester = startForgottenPasswordPage(true, true, true);
        assertFeedbackMessage(tester, "Internal error");
    }

    private void assertFeedbackMessage(final EasyWicketTester tester, final String value) {
        tester.assertLabelContains("commonFeedbackPanel:feedbackul:messages", value);
        tester.assertRenderedPage(ErrorPage.class);
    }

    private EasyWicketTester startForgottenPasswordPage(final boolean userId, final boolean requestTime, final boolean requestToken) {
        final PageParameters parameters = new PageParameters();
        parameters.add("requestTime", requestTime ? new DateTime().minusMinutes(5).getMillis() + "" : "");
        parameters.add("requestToken", requestToken ? "tokenValue" : "");
        parameters.add("userId", userId ? "mockedUserId" : "");
        return EasyWicketTester.startPage(applicationContext, ChangePasswordPage.class, parameters);
    }

    private EasyWicketTester startFromHomePage() {
        final EasyWicketTester tester = EasyWicketTester.startPage(applicationContext, HomePage.class);
        tester.assertRenderedPage(HomePage.class);
        tester.clickLink("myPersonalInfoLink");
        tester.assertRenderedPage(UserInfoPage.class);
        tester.clickLink("userInfoPanel:switchPanel:changePasswordLink");
        tester.assertRenderedPage(ChangePasswordPage.class);
        return tester;
    }

    private void mockAuthenticationException() throws ServiceException {
        applicationContext.expectNoDatasetsInToolBar();
        final UserService mock = applicationContext.getUserService();
        expect(mock.newForgottenPasswordMailAuthentication(isA(String.class), isA(String.class), isA(String.class))).andThrow(new ServiceException(""));
    }

    private void mockAuthentication(final boolean isCompleted) throws ServiceException {
        applicationContext.expectNoDatasetsInToolBar();
        final UserService mock = applicationContext.getUserService();
        final ForgottenPasswordMailAuthentication authentication = new ForgottenPasswordMailAuthentication(null, null, null) {
            private static final long serialVersionUID = 1L;

            public boolean isCompleted() {
                return isCompleted;
            }

            public EasyUser getUser() {
                return createSessionUser();
            }
        };
        expect(mock.newForgottenPasswordMailAuthentication(isA(String.class), isA(String.class), isA(String.class))).andStubReturn(authentication);
        mock.authenticate(authentication);
        expectLastCall().anyTimes();
    }

    private void addMocks(final EasyUser sessionUser) throws Exception {
        applicationContext.expectAuthenticatedAs(sessionUser);
        applicationContext.expectNoDatasetsInToolBar();
        applicationContext.getUserService().changePassword(isA(ChangePasswordMessenger.class));
        expectLastCall().andStubDelegateTo(new EasyUserService() {
            public void changePassword(final ChangePasswordMessenger messenger) {
                messenger.setState(ChangePasswordMessenger.State.PasswordChanged);
            }
        });
    }

    private EasyUserImpl createSessionUser() {
        return new EasyUserImpl("sessionUserId") {
            private static final long serialVersionUID = 1L;

            public Set<Group> getGroups() {
                return new HashSet<Group>();
            }
        };
    }
}
