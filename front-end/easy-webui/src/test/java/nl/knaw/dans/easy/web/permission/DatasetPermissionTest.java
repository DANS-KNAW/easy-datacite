package nl.knaw.dans.easy.web.permission;

import static nl.knaw.dans.easy.security.authz.AbstractDatasetAutzStrategy.*;

import java.util.ArrayList;
import java.util.List;

import nl.knaw.dans.common.lang.dataset.DatasetState;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.security.authz.AuthzMessage;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.EasyApplicationContextMock;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.domain.dataset.item.ItemOrder;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.dataset.item.filter.ItemFilters;
import nl.knaw.dans.easy.domain.model.AdministrativeMetadata;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.PermissionReplyModel;
import nl.knaw.dans.easy.domain.model.PermissionRequestModel;
import nl.knaw.dans.easy.domain.model.PermissionSequence;
import nl.knaw.dans.easy.domain.model.PermissionSequence.State;
import nl.knaw.dans.easy.domain.model.PermissionSequenceList;
import nl.knaw.dans.easy.domain.model.StateChangeDate;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.disciplinecollection.DisciplineContainer;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.servicelayer.services.ItemService;
import nl.knaw.dans.easy.servicelayer.services.Services;
import nl.knaw.dans.easy.web.fileexplorer.AuthzStrategyTestImpl;
import nl.knaw.dans.easy.web.view.dataset.DatasetViewPage;
import nl.knaw.dans.pf.language.emd.EasyMetadata;
import nl.knaw.dans.pf.language.emd.EmdDescription;
import nl.knaw.dans.pf.language.emd.EmdIdentifier;
import nl.knaw.dans.pf.language.emd.EmdRelation;
import nl.knaw.dans.pf.language.emd.Term;

import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

public class DatasetPermissionTest
{
    private EasyApplicationContextMock applicationContext;
    private static Dataset dataset;
    private ArrayList<AuthzMessage> authzMessageList;
    private PermissionSequence permissionSequence;
    private PermissionSequenceList permissionSequenceList;
    private PermissionRequestModel permissionRequestModel;
    private List<StateChangeDate> stateChangeDates;

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
        stateChangeDates = new ArrayList<StateChangeDate>();
        // listPermissionSequenses = new ArrayList<PermissionSequence>();
        permissionRequestModel = new PermissionRequestModel();
        permissionSequence = PowerMock.createMock(PermissionSequence.class);
        permissionSequenceList = PowerMock.createMock(PermissionSequenceList.class);
        EasyMock.expect(permissionSequenceList.getSequenceFor(EasyMock.isA(EasyUser.class))).andStubReturn(permissionSequence);
        EasyMock.expect(permissionSequenceList.getPermissionRequest(EasyMock.isA(EasyUser.class))).andStubReturn(permissionRequestModel);

        final EasyMetadata emd = PowerMock.createMock(EasyMetadata.class);
        EasyMock.expect(emd.toString(EasyMock.isA(String.class), EasyMock.isA(Term.Name.class))).andStubReturn("mocked easy metadata");
        EasyMock.expect(emd.getEmdIdentifier()).andStubReturn(new EmdIdentifier());
        EasyMock.expect(emd.getEmdDescription()).andStubReturn(new EmdDescription());
        EasyMock.expect(emd.getEmdRelation()).andStubReturn(new EmdRelation());

        final AdministrativeMetadata amd = PowerMock.createMock(AdministrativeMetadata.class);
        EasyMock.expect(amd.getDepositor()).andStubReturn(new EasyUserImpl("mocke-user:depositor"));
        EasyMock.expect(amd.getStateChangeDates()).andStubReturn(stateChangeDates);

        final DmoStoreId datasetStoreId = new DmoStoreId("mocked-dataset:1");
        dataset = PowerMock.createMock(Dataset.class);
        EasyMock.expect(dataset.getStoreId()).andStubReturn(datasetStoreId.getStoreId());
        EasyMock.expect(dataset.getDmoStoreId()).andStubReturn(datasetStoreId);
        EasyMock.expect(dataset.isInvalidated()).andStubReturn(false);
        EasyMock.expect(dataset.getPreferredTitle()).andStubReturn("mocked title");
        EasyMock.expect(dataset.getEasyMetadata()).andStubReturn(emd);
        EasyMock.expect(dataset.getAdministrativeMetadata()).andStubReturn(amd);
        EasyMock.expect(dataset.getAdministrativeState()).andStubReturn(DatasetState.PUBLISHED);
        EasyMock.expect(dataset.getParentDisciplines()).andStubReturn(new ArrayList<DisciplineContainer>());
        EasyMock.expect(dataset.getPermissionSequenceList()).andStubReturn(permissionSequenceList);
        EasyMock.expect(dataset.getLastModified()).andStubReturn(new DateTime());
        EasyMock.expect(dataset.getTotalFileCount()).andStubReturn(1);

        // we are not interested in files but in permission request messages
        EasyMock.expect(dataset.getVisibleToFileCount(EasyMock.isA(VisibleTo.class))).andStubReturn(0);
        EasyMock.expect(dataset.hasVisibleItems(EasyMock.isA(EasyUser.class))).andStubReturn(false);
        EasyMock.expect(dataset.hasGroupRestrictedItems()).andStubReturn(false);
        EasyMock.expect(dataset.hasPermissionRestrictedItems()).andStubReturn(true);
        EasyMock.expect(dataset.isPermissionGrantedTo(EasyMock.isA(EasyUser.class))).andStubReturn(false);

        // annotated SpringBean injection does not work for constructor of DatasetModel
        final DatasetService datasetService = PowerMock.createMock(DatasetService.class);
        EasyMock.expect(datasetService.getDataset(EasyMock.isA(EasyUser.class), EasyMock.isA(DmoStoreId.class))).andStubReturn(dataset);
        EasyMock.expect(datasetService.getAdditionalLicense(dataset)).andStubReturn(null);
        new Services().setDatasetService(datasetService);
    }

    @Before
    public void mockAuthzMessages()
    {
        authzMessageList = new ArrayList<AuthzMessage>();
        EasyMock.expect(dataset.getAuthzStrategy()).andStubReturn(new AuthzStrategyTestImpl()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public List<AuthzMessage> getReadMessages()
            {
                return authzMessageList;
            }
        });
    }

    @Before
    public void mockApplicationContext() throws Exception
    {
        applicationContext = new EasyApplicationContextMock();
        applicationContext.expectStandardSecurity(false);
        applicationContext.expectDefaultResources();
        applicationContext.expectDisciplines();
        applicationContext.expectNoJumpoff();
        applicationContext.putBean("itemService", mockNoItems());
        applicationContext.expectAuthenticatedAsVisitor();
    }

    private ItemService mockNoItems() throws ServiceException
    {
        final ItemService itemService = PowerMock.createMock(ItemService.class);
        EasyMock.expect(itemService.hasChildItems(EasyMock.isA(DmoStoreId.class))).andStubReturn(false);
        EasyMock.expect(itemService.getFilesAndFolders(EasyMock.isA(EasyUser.class), //
                EasyMock.isA(Dataset.class), EasyMock.isA(DmoStoreId.class), //
                EasyMock.isA(Integer.class), EasyMock.isA(Integer.class), //
                EasyMock.isNull(ItemOrder.class), EasyMock.isNull(ItemFilters.class)))//
                .andStubReturn(new ArrayList<ItemVO>());
        new Services().setItemService(itemService);
        return itemService;
    }

    @After
    public void reset()
    {
        PowerMock.resetAll();
    }

    @Test
    public void hasNoSequence()
    {
        isDepositor(false);
        expectAuthzMessages(MSG_NO_FILES, MSG_PERMISSION, MSG_PERMISSION_BUTTON);
        expectHasSequence(false);
        expectNoPermissionSequenceList();

        showTab(2, "Data files (0)");
    }

    @Test
    public void issuefirstRequest()
    {
        isDepositor(false);
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
        isDepositor(true);

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
        EasyWicketTester tester = showTab(4, "Permissions (1 new / 1)");
        tester.dumpPage();
        tester.debugComponentTrees();
    }

    @Test
    public void replyPermission()
    {
        preparePermissionTab();
        EasyMock.expect(permissionSequenceList.getPermissionReply(EasyMock.isA(String.class))).andStubReturn(new PermissionReplyModel("id"));
        EasyWicketTester tester = showTab(4, "Permissions (1 new / 1)");
        tester.clickLink("tabs:panel:requestTable:body:rows:1:cells:1:cell:link");
        tester.dumpPage();
        tester.assertRenderedPage(PermissionReplyPage.class);
    }

    @Test
    public void isDenied()
    {
        isDepositor(false);
        expectAuthzMessages(MSG_NO_FILES, MSG_PERMISSION, MSG_PERMISSION_DENIED);
        expectLastStateChange("2014-04-14");
        expectNoPermissionSequenceList();

        showTab(2, "Data files (0)");
    }

    @Test
    public void viewDenied()
    {
        isDepositor(false);
        expectAuthzMessages(MSG_NO_FILES, MSG_PERMISSION, MSG_PERMISSION_DENIED);
        expectLastStateChange("2014-04-14");
        permissionRequestModel.setRequestTitle("some title");
        permissionRequestModel.setRequestTheme("some theme");
        EasyMock.expect(permissionSequence.getState()).andStubReturn(State.Denied);
        EasyMock.expect(permissionSequence.getReplyText()).andStubReturn("simply denied");
        expectNoPermissionSequenceList();

        final EasyWicketTester tester = showTab(2, "Data files (0)");
        tester.clickLink("tabs:panel:explorer.message.permission.denied.button");
        tester.dumpPage();
        tester.assertRenderedPage(PermissionRequestPage.class);
    }

    private void preparePermissionTab()
    {
        final String date = "2014-04-14";
        State state = State.Submitted;
        final EasyUserImpl requestor = createRequestor("R.E.", "Questor", "orga", "depa");
        mockPermissionSequenceList().add(mockPermissionSequence(date, state, "title", "theme", requestor));
        expectHasSequence(true);
        expectLastStateChange(date);
        EasyMock.expect(dataset.getState()).andStubReturn(state.toString());
        isDepositor(true);
    }

    private List<PermissionSequence> mockPermissionSequenceList()
    {
        final List<PermissionSequence> list = new ArrayList<PermissionSequence>();
        EasyMock.expect(permissionSequenceList.getPermissionSequences(EasyMock.isA(State.class))).andStubReturn(list);
        EasyMock.expect(permissionSequenceList.getPermissionSequences()).andStubReturn(list);
        return list;
    }

    private PermissionSequence mockPermissionSequence(final String dateTime, final State state, String title, String theme, final EasyUser requestor)
    {
        final PermissionSequence ps = PowerMock.createMock(PermissionSequence.class);
        EasyMock.expect(ps.getRequester()).andStubReturn(requestor);
        EasyMock.expect(ps.getLastStateChange()).andStubReturn(new DateTime(dateTime));
        EasyMock.expect(ps.getLastRequestDate()).andStubReturn(new DateTime(dateTime));
        EasyMock.expect(ps.getState()).andStubReturn(state);
        EasyMock.expect(ps.getRequesterId()).andStubReturn(requestor.getId());
        EasyMock.expect(ps.getRequestTitle()).andStubReturn(title);
        EasyMock.expect(ps.getRequestTheme()).andStubReturn(theme);
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

    private void isDepositor(final Boolean value)
    {
        EasyMock.expect(dataset.hasDepositor(EasyMock.isA(EasyUser.class))).andStubReturn(value);
    }

    private void expectNoPermissionSequenceList()
    {
        EasyMock.expect(permissionSequenceList.getPermissionSequences(EasyMock.isA(State.class))).andStubReturn(null);
    }

    private void expectLastStateChange(final String string)
    {
        expectHasSequence(true);
        EasyMock.expect(permissionSequence.getLastStateChange()).andStubReturn(new DateTime(string));
    }

    private void expectHasSequence(final boolean stubReturnValue)
    {
        EasyMock.expect(permissionSequenceList.hasSequenceFor(EasyMock.isA(EasyUser.class))).andStubReturn(stubReturnValue);
    }

    private void expectAuthzMessages(final String... authzMessages)
    {
        for (final String authzMessage : authzMessages)
            authzMessageList.add(new AuthzMessage(authzMessage));
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
