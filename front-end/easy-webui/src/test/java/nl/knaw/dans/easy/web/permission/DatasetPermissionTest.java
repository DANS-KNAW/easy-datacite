package nl.knaw.dans.easy.web.permission;

import static nl.knaw.dans.easy.security.authz.AbstractDatasetAutzStrategy.MSG_NO_FILES;
import static nl.knaw.dans.easy.security.authz.AbstractDatasetAutzStrategy.MSG_PERMISSION;
import static nl.knaw.dans.easy.security.authz.AbstractDatasetAutzStrategy.MSG_PERMISSION_BUTTON;
import static nl.knaw.dans.easy.security.authz.AbstractDatasetAutzStrategy.MSG_PERMISSION_DENIED;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.dataset.AccessCategory;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.security.authz.AuthzMessage;
import nl.knaw.dans.easy.AuthzStrategyTestImpl;
import nl.knaw.dans.easy.EasyApplicationContextMock;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.model.PermissionReplyModel;
import nl.knaw.dans.easy.domain.model.PermissionRequestModel;
import nl.knaw.dans.easy.domain.model.PermissionSequence;
import nl.knaw.dans.easy.domain.model.PermissionSequence.State;
import nl.knaw.dans.easy.domain.model.PermissionSequenceList;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.web.view.dataset.DatasetViewPage;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

public class DatasetPermissionTest
{
    private EasyApplicationContextMock applicationContext;
    private static DatasetImpl dataset;
    private boolean isDepositor;
    private PermissionSequence permissionSequence;
    private PermissionSequenceList permissionSequenceList;
    private PermissionRequestModel permissionRequestModel;

    static public class PageWrapper extends DatasetViewPage
    {
        public PageWrapper()
        {
            super(dataset, DatasetViewPage.Mode.VIEW);
        }
    }

    @Before
    public void mockDataset() throws Exception
    {
        permissionRequestModel = new PermissionRequestModel();
        permissionSequence = PowerMock.createMock(PermissionSequence.class);
        permissionSequenceList = PowerMock.createMock(PermissionSequenceList.class);
        expect(permissionSequenceList.getSequenceFor(isA(EasyUser.class))).andStubReturn(permissionSequence);
        expect(permissionSequenceList.getPermissionRequest(isA(EasyUser.class))).andStubReturn(permissionRequestModel);
        expect(permissionSequenceList.isGrantedTo(isA(EasyUser.class))).andStubReturn(true);

        dataset = new DatasetImpl("mocked-dataset:1")
        {
            private static final long serialVersionUID = 1L;

            public boolean hasDepositor(final EasyUser user)
            {
                return isDepositor;
            }

            public boolean hasDepositor(final String userId)
            {
                return isDepositor;
            }

            public boolean hasPermissionRestrictedItems()
            {
                return true;
            }

            public AccessCategory getAccessCategory()
            {
                return AccessCategory.REQUEST_PERMISSION;
            }
        };
        dataset.setPermissionSequenceList(permissionSequenceList);
        dataset.setState(State.Submitted.toString());
        dataset.setAuthzStrategy(new AuthzStrategyTestImpl());

        final DatasetService datasetService = PowerMock.createMock(DatasetService.class);
        expect(datasetService.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).andStubReturn(dataset);
        expect(datasetService.getAdditionalLicense(dataset)).andStubReturn(null);
        applicationContext.setDatasetService(datasetService);
    }

    @Before
    public void mockApplicationContext() throws Exception
    {
        applicationContext = new EasyApplicationContextMock();
        applicationContext.expectStandardSecurity(false);
        applicationContext.expectDefaultResources();
        applicationContext.expectDisciplines();
        applicationContext.expectNoJumpoff();
        applicationContext.expectNoItems();
        applicationContext.expectAuthenticatedAsVisitor();
    }

    @After
    public void reset()
    {
        PowerMock.resetAll();
    }

    @Test
    public void hasNoSequence()
    {
        isDepositor = false;
        expectAuthzMessages(MSG_NO_FILES, MSG_PERMISSION, MSG_PERMISSION_BUTTON);
        expectHasSequence(false);
        expectNoPermissionSequenceList();

        showTab(2, "Data files (0)");
    }

    @Test
    public void issuefirstRequest()
    {
        isDepositor = false;
        expectAuthzMessages(MSG_NO_FILES, MSG_PERMISSION, MSG_PERMISSION_BUTTON);
        expectHasSequence(false);
        expectNoPermissionSequenceList();

        final EasyWicketTester tester = showTab(2, "Data files (0)");
        tester.clickLink("tabs:panel:explorer.message.permission.button");
        tester.dumpPage();
        tester.assertRenderedPage(PermissionRequestPage.class);
    }

    @Test
    public void permissionsTabVisible()
    {
        mockPermissionSequenceList();
        isDepositor = true;

        final EasyWicketTester tester = EasyWicketTester.create(applicationContext);
        PowerMock.replayAll();
        tester.startPage(PageWrapper.class);
        final String path = "tabs:tabs-container:tabs:" + 4 + ":link";
        tester.assertVisible(path);
        tester.assertEnabled(path);
        tester.dumpPage();
    }

    @Test
    public void permissionsTab()
    {
        preparePermissionTab();
        final EasyWicketTester tester = showTab(4, "Permissions (1 new / 1)");
        tester.dumpPage();
        tester.debugComponentTrees();
    }

    @Test
    public void replyPermission()
    {
        preparePermissionTab();
        expect(permissionSequenceList.getPermissionReply(isA(String.class))).andStubReturn(new PermissionReplyModel("id"));
        final EasyWicketTester tester = showTab(4, "Permissions (1 new / 1)");
        tester.clickLink("tabs:panel:requestTable:body:rows:1:cells:1:cell:link");
        tester.dumpPage();
        tester.assertRenderedPage(PermissionReplyPage.class);
    }

    @Test
    public void isDenied()
    {
        isDepositor = false;
        expectAuthzMessages(MSG_NO_FILES, MSG_PERMISSION, MSG_PERMISSION_DENIED);
        expectLastStateChange("2014-04-14");
        expectNoPermissionSequenceList();

        showTab(2, "Data files (0)");
    }

    @Test
    public void viewDenied()
    {
        isDepositor = false;
        expectAuthzMessages(MSG_NO_FILES, MSG_PERMISSION, MSG_PERMISSION_DENIED);
        expectLastStateChange("2014-04-14");
        permissionRequestModel.setRequestTitle("some title");
        permissionRequestModel.setRequestTheme("some theme");
        expect(permissionSequence.getState()).andStubReturn(State.Denied);
        expect(permissionSequence.getReplyText()).andStubReturn("simply denied");
        expectNoPermissionSequenceList();

        final EasyWicketTester tester = showTab(2, "Data files (0)");
        tester.clickLink("tabs:panel:explorer.message.permission.denied.button");
        tester.dumpPage();
        tester.assertRenderedPage(PermissionRequestPage.class);
    }

    private void preparePermissionTab()
    {
        final String date = "2014-04-14";
        final State state = State.Submitted;
        final EasyUserImpl requestor = createRequestor("R.E.", "Questor", "orga", "depa");
        mockPermissionSequenceList().add(mockPermissionSequence(date, state, "title", "theme", requestor));
        expectHasSequence(true);
        expectLastStateChange(date);
        isDepositor = true;
    }

    private List<PermissionSequence> mockPermissionSequenceList()
    {
        final List<PermissionSequence> list = new ArrayList<PermissionSequence>();
        expect(permissionSequenceList.getPermissionSequences(isA(State.class))).andStubReturn(list);
        expect(permissionSequenceList.getPermissionSequences()).andStubReturn(list);
        return list;
    }

    private PermissionSequence mockPermissionSequence(final String dateTime, final State state, final String title, final String theme, final EasyUser requestor)
    {
        final PermissionSequence ps = PowerMock.createMock(PermissionSequence.class);
        expect(ps.getRequester()).andStubReturn(requestor);
        expect(ps.getLastStateChange()).andStubReturn(new DateTime(dateTime));
        expect(ps.getLastRequestDate()).andStubReturn(new DateTime(dateTime));
        expect(ps.getState()).andStubReturn(state);
        expect(ps.getRequesterId()).andStubReturn(requestor.getId());
        expect(ps.getRequestTitle()).andStubReturn(title);
        expect(ps.getRequestTheme()).andStubReturn(theme);
        return ps;
    }

    private EasyUserImpl createRequestor(final String initials, final String surName, final String organisation, final String department)
    {
        final EasyUserImpl user = new EasyUserImpl("id");
        user.setInitials(initials);
        user.setSurname(surName);
        user.setOrganization(organisation);
        user.setDepartment(department);
        return user;
    }

    private void expectNoPermissionSequenceList()
    {
        expect(permissionSequenceList.getPermissionSequences(isA(State.class))).andStubReturn(null);
    }

    private void expectLastStateChange(final String string)
    {
        expectHasSequence(true);
        expect(permissionSequence.getLastStateChange()).andStubReturn(new DateTime(string));
    }

    private void expectHasSequence(final boolean stubReturnValue)
    {
        expect(permissionSequenceList.hasSequenceFor(isA(EasyUser.class))).andStubReturn(stubReturnValue);
    }

    private void expectAuthzMessages(final String... authzMessages)
    {
        for (final String authzMessage : authzMessages)
            dataset.getAuthzStrategy().getReadMessages().add(new AuthzMessage(authzMessage));
    }

    private EasyWicketTester showTab(final int number, final String title)
    {
        final EasyWicketTester tester = EasyWicketTester.create(applicationContext);
        PowerMock.replayAll();
        tester.startPage(PageWrapper.class);
        final String path = "tabs:tabs-container:tabs:" + number + ":link";
        tester.assertLabel(path + ":title", title);
        tester.clickLink(path);
        tester.debugComponentTrees();
        tester.assertRenderedPage(PageWrapper.class);
        return tester;
    }
}
