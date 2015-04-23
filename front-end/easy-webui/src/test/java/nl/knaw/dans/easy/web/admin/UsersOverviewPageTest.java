package nl.knaw.dans.easy.web.admin;

import static nl.knaw.dans.easy.web.admin.UsersOverviewPage.STATE_KEY;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;

import java.util.Arrays;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.ldap.OperationalAttributes;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.user.User;
import nl.knaw.dans.common.lang.user.User.State;
import nl.knaw.dans.easy.EasyApplicationContextMock;
import nl.knaw.dans.easy.EasyUserTestImpl;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.TestUtil;
import nl.knaw.dans.easy.business.services.EasyUserService;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.userrepo.EasyUserRepo;
import nl.knaw.dans.easy.domain.authn.UsernamePasswordAuthentication;
import nl.knaw.dans.easy.domain.deposit.discipline.KeyValuePair;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.web.ErrorPage;

import org.apache.wicket.PageParameters;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

public class UsersOverviewPageTest {

    private static final EasyUser ACTIVE_USER = createUser("visitor1", "Visi", "Tor", Role.USER, State.ACTIVE);
    private static final EasyUser BLOCKED_USER = createUser("visitor2", "V", "Isit", Role.USER, State.BLOCKED);
    private static final EasyUser SESSION_USER = createUser("archivist", "Archi", "Vist", Role.ARCHIVIST, User.State.ACTIVE);
    private static final EasyUser[] USERS = {ACTIVE_USER, BLOCKED_USER};

    private static final String MESSAGE_VALUE = "Could not retrieve users";
    private static final String MESSAGE_PATH = "commonFeedbackPanel:feedbackul:messages:0:message";

    @After
    public void cleanup() {
        TestUtil.cleanup();
    }

    @Test
    public void repositoryException() throws Exception {
        mockUserRepoRepositoryException();
        final EasyWicketTester tester = startPage(new PageParameters());
        tester.dumpPage();
        assertError(tester);
    }

    @Test
    public void nullpointerException() throws Exception {
        mockUserRepoNullPointerException();
        final EasyWicketTester tester = startPage(new PageParameters());
        tester.dumpPage();
        assertError(tester);
    }

    @Test
    public void noUsers() throws Exception {
        mockUserRepo();
        final EasyWicketTester tester = startPage(new PageParameters());
        tester.dumpPage();
        assertBasics(tester, 0);
    }

    @Test
    public void blockedUsers() throws Exception {
        mockUserRepo(USERS);
        final EasyWicketTester tester = startPage(stateParameter(State.BLOCKED.toString()));
        tester.dumpPage();
        assertBasics(tester, 1);
        tester.debugComponentTrees();

        tester.clickLink("userOverviewPanel:users:0:user:showUser");
        tester.assertRenderedPage(UserDetailsPage.class);
        // further clicking around requires mocking a group repo and belongs to testing UserDetailsPage
    }

    @Test
    public void invalidState() throws Exception {
        mockUserRepo(USERS);
        final EasyWicketTester tester = startPage(stateParameter("rabarbera"));
        tester.dumpPage();
        assertBasics(tester, 0);
    }

    private PageParameters stateParameter(String value) {
        PageParameters parameters = new PageParameters();
        parameters.add(STATE_KEY, value);
        return parameters;
    }

    @Test
    public void oneUser() throws Exception {
        mockUserRepo(ACTIVE_USER);
        final EasyWicketTester tester = startPage(new PageParameters());
        tester.dumpPage();
        assertBasics(tester, 1);
    }

    @Test
    public void multipleUsers() throws Exception {
        mockUserRepo(USERS);
        final EasyWicketTester tester = startPage(new PageParameters());
        tester.dumpPage();
        assertBasics(tester, 2);
        tester.assertLabel("userOverviewPanel:users:0:user:showUser:commonName", "Tor, Visi");
        tester.assertLabel("userOverviewPanel:users:0:user:userId", "visitor1");
        tester.assertLabel("userOverviewPanel:users:1:user:displayName", "V Isit");
        tester.assertLabel("userOverviewPanel:users:1:user:state", "BLOCKED");
    }

    private EasyWicketTester startPage(PageParameters parameters) throws ServiceException {
        EasyApplicationContextMock applicationContext = new EasyApplicationContextMock();
        applicationContext.expectStandardSecurity();
        applicationContext.expectDefaultResources();
        applicationContext.expectNoDatasetsInToolBar();

        // required to select a UserDetailsPage
        applicationContext.expectDisciplineChoices(new KeyValuePair("Disc.key1", "Dics.value1"));

        // in this case we want to mock the repository, not the service using the repository
        applicationContext.putBean("userService", new EasyUserService());
        Whitebox.setInternalState(applicationContext, "authentication", createAuthentication());

        replayAll();
        return EasyWicketTester.startPage(applicationContext, UsersOverviewPage.class, parameters);
    }

    private UsernamePasswordAuthentication createAuthentication() {
        final UsernamePasswordAuthentication authentication = new UsernamePasswordAuthentication();
        authentication.setUserId(SESSION_USER.getId());
        authentication.setUser(SESSION_USER);
        return authentication;
    }

    private void mockUserRepo(EasyUser... users) throws RepositoryException {
        EasyUserRepo userRepo = createMock(EasyUserRepo.class);
        expect(userRepo.findAll()).andStubReturn(Arrays.asList(users));
        for (EasyUser user : users) {
            // required to select a UserDetailsPage
            OperationalAttributes attribs = createMock(OperationalAttributes.class);
            expect(userRepo.findById(EasyMock.eq(user.getId()))).andStubReturn(user);
            expect(userRepo.getOperationalAttributes(EasyMock.eq(user.getId()))).andStubReturn(attribs);
            expect(attribs.getCreateTimestamp()).andStubReturn(new DateTime(2015, 4, 12, 8, 30));
        }
        new Data().setUserRepo(userRepo);
    }

    private void mockUserRepoRepositoryException() throws RepositoryException {
        EasyUserRepo userRepo = createMock(EasyUserRepo.class);
        expect(userRepo.findAll()).andThrow(new RepositoryException(""));
        new Data().setUserRepo(userRepo);
    }

    private void mockUserRepoNullPointerException() throws RepositoryException {
        EasyUserRepo userRepo = createMock(EasyUserRepo.class);
        expect(userRepo.findAll()).andThrow(new NullPointerException());
        new Data().setUserRepo(userRepo);
    }

    private static EasyUser createUser(String userId, String initials, String surname, Role role, State active) {

        final EasyUser user = new EasyUserTestImpl(userId);
        user.setInitials(initials);
        user.setSurname(surname);
        user.addRole(role);
        user.setState(active);
        return user;
    }

    private void assertBasics(final EasyWicketTester tester, int tableSize) {
        tester.assertRenderedPage(UsersOverviewPage.class);
        tester.assertVisible("userOverviewPanel:users");
        assertThat(tester.getTagByWicketId("userOverviewPanel").getValue(), containsString("Display name"));
        assertThat(tester.getLastRenderedPage().get("userOverviewPanel:users:" + tableSize), nullValue());
        if (tableSize > 0)
            tester.assertVisible("userOverviewPanel:users:" + (tableSize - 1));
    }

    private void assertError(final EasyWicketTester tester) {
        tester.assertRenderedPage(ErrorPage.class);
        tester.assertLabelContains(MESSAGE_PATH, MESSAGE_VALUE);
    }
}
