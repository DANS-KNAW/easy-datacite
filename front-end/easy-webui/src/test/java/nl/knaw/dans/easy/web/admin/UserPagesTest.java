package nl.knaw.dans.easy.web.admin;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.powermock.api.easymock.PowerMock.createMock;

import java.util.ArrayList;

import nl.knaw.dans.common.lang.ldap.OperationalAttributes;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.user.User;
import nl.knaw.dans.easy.EasyApplicationContextMock;
import nl.knaw.dans.easy.EasyUserTestImpl;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.servicelayer.services.UserService;
import nl.knaw.dans.easy.web.ErrorPage;

import org.apache.wicket.PageParameters;
import org.apache.wicket.util.tester.FormTester;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

public class UserPagesTest {
    private static final PageParameters PAGE_PARAMETERS = new PageParameters(UserDetailsPage.PM_USER_ID + "=depositor1");
    private static final String SHOW_USER_PATH = "userOverviewPanel:users:1:user:showUser";
    private static final String FORM_PATH = "userDetailsPanel:switchPanel:userInfoForm";
    private static final String EDIT_LINK_PATH = "userDetailsPanel:switchPanel:editLink";
    private EasyApplicationContextMock applicationContext;
    private EasyUserImpl sessionUser;

    @Before
    public void mockApplicationContext() throws Exception {
        sessionUser = new EasyUserTestImpl("mocked-user:archivist");
        sessionUser.setInitials("Archi");
        sessionUser.setSurname("Vist");
        sessionUser.addRole(Role.ARCHIVIST);
        sessionUser.setState(User.State.ACTIVE);

        applicationContext = new EasyApplicationContextMock();
        applicationContext.expectStandardSecurity();
        applicationContext.expectDefaultResources();
        applicationContext.expectNoDatasets();
        applicationContext.expectAuthenticatedAs(sessionUser);
    }

    @After
    public void reset() {
        PowerMock.resetAll();
    }

    @Test
    public void smokeTestNoUsers() throws Exception {
        prepareOveriew(new ArrayList<EasyUser>());
        EasyWicketTester tester = EasyWicketTester.startPage(applicationContext, UsersOverviewPage.class);
        tester.assertRenderedPage(UsersOverviewPage.class);
        tester.dumpPage();
    }

    @Test
    public void smokeTestOneUser() throws Exception {
        final EasyUserImpl user = new EasyUserTestImpl("mocked-user:visitor1");
        user.setInitials("Visi");
        user.setSurname("Tor");
        user.addRole(Role.USER);
        user.setState(User.State.ACTIVE);

        final ArrayList<EasyUser> users = new ArrayList<EasyUser>();
        users.add(user);
        prepareOveriew(users);
        EasyWicketTester tester = EasyWicketTester.startPage(applicationContext, UsersOverviewPage.class);
        tester.assertRenderedPage(UsersOverviewPage.class);
        tester.dumpPage();
    }

    @Test
    public void smokeTestMultipleUsers() throws Exception {
        prepareOveriew(prepareDetails());
        final EasyWicketTester tester = EasyWicketTester.startPage(applicationContext, UsersOverviewPage.class);
        tester.dumpPage();
        tester.assertRenderedPage(UsersOverviewPage.class);
        tester.assertInvisible("addLink");// is it ever made visible?
        tester.clickLink(SHOW_USER_PATH);
        tester.assertRenderedPage(UserDetailsPage.class);
        tester.clickLink(EDIT_LINK_PATH);
        tester.assertRenderedPage(UserDetailsPage.class);
        tester.newFormTester(FORM_PATH).submit();
    }

    @Test
    public void detailsPageWithoutParameters() throws Exception {
        prepareDetails();

        final EasyWicketTester tester = EasyWicketTester.startPage(applicationContext, UserDetailsPage.class);
        tester.assertRenderedPage(ErrorPage.class);
        tester.dumpPage();
    }

    @Test
    public void editUserDetails() throws Exception {
        prepareDetails();

        final EasyWicketTester tester = EasyWicketTester.startPage(applicationContext, UserDetailsPage.class, PAGE_PARAMETERS);
        tester.assertRenderedPage(UserDetailsPage.class);
        tester.clickLink(EDIT_LINK_PATH);
        tester.dumpPage();
        tester.debugComponentTrees();
        final FormTester formTester = tester.newFormTester(FORM_PATH);
        formTester.select("optsForNewsletter", 1);
        formTester.select("logMyActions", 1);
        formTester.setValue("city", "Den Haag");
        formTester.setValue("postalCode", "1234");
        formTester.setValue("email", "noresponse@dans.knaw.nl");
        formTester.setValue("address", "rabarbera");
        formTester.submit("update");
        tester.dumpPage("updated");
        tester.assertVisible(EDIT_LINK_PATH);
        tester.assertRenderedPage(UserDetailsPage.class);
        tester.clickLink("userDetailsPanel:switchPanel:doneLink");
        tester.dumpPage("done");
        tester.assertRenderedPage(UsersOverviewPage.class);
    }

    @Test
    public void cancelEditUserDetails() throws Exception {
        prepareDetails();

        final EasyWicketTester tester = EasyWicketTester.startPage(applicationContext, UserDetailsPage.class, PAGE_PARAMETERS);
        tester.assertRenderedPage(UserDetailsPage.class);
        tester.clickLink(EDIT_LINK_PATH);
        tester.debugComponentTrees();
        final FormTester formTester = tester.newFormTester(FORM_PATH);
        tester.debugComponentTrees();
        formTester.submitLink("cancel", false);
        tester.assertVisible(EDIT_LINK_PATH);
        tester.assertRenderedPage(UserDetailsPage.class);
    }

    private ArrayList<EasyUser> prepareDetails() throws ServiceException, ObjectNotAvailableException {
        final EasyUserImpl user = new EasyUserTestImpl("mocked-user:visitor1");
        user.setInitials("Visi");
        user.setSurname("Tor");
        user.addRole(Role.USER);
        user.setState(User.State.ACTIVE);

        final EasyUserImpl depositor = new EasyUserTestImpl("mocked-user:depositor1");
        depositor.setInitials("De");
        depositor.setSurname("Positor");
        depositor.addRole(Role.USER);
        depositor.setState(User.State.ACTIVE);
        final OperationalAttributes attributes = createMock(OperationalAttributes.class);
        expect(attributes.getCreateTimestamp()).andStubReturn(new DateTime("2014-06-1"));
        expect(attributes.getModifyTimestamp()).andStubReturn(new DateTime("2014-06-2"));

        final ArrayList<EasyUser> users = new ArrayList<EasyUser>();
        users.add(user);
        users.add(depositor);

        applicationContext.expectDisciplineChoices(new KeyValuePair("Disc.key1", "Dics.value1"));

        final UserService userService = applicationContext.getUserService();
        expect(userService.getUserById(isA(EasyUser.class), isA(String.class))).andStubReturn(depositor);
        expect(userService.getOperationalAttributes(isA(EasyUser.class))).andStubReturn(attributes);
        expect(userService.getAllGroupIds()).andStubReturn(new ArrayList<String>());
        expect(userService.getAllUsers()).andStubReturn(users);
        expect(userService.update(isA(EasyUser.class), isA(EasyUser.class))).andStubReturn(depositor);
        return users;
    }

    private void prepareOveriew(final ArrayList<EasyUser> users) throws ServiceException {
        EasyMock.expect(applicationContext.getUserService().getAllUsers()).andStubReturn(users);
    }
}
