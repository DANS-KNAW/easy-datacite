package nl.knaw.dans.easy.web.view.dataset;

import static org.easymock.EasyMock.isA;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.download.DownloadHistory;
import nl.knaw.dans.easy.domain.download.DownloadList;
import nl.knaw.dans.easy.domain.download.DownloadList.Level;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.user.EasyUserAnonymous;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.security.CodedAuthz;
import nl.knaw.dans.easy.security.Security;
import nl.knaw.dans.easy.servicelayer.SystemReadOnlyStatus;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.servicelayer.services.UserService;
import nl.knaw.dans.easy.web.EasyWicketApplication;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.apache.wicket.util.tester.ITestPanelSource;
import org.apache.wicket.util.tester.WicketTester;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

public class DownloadActivityLogPanelTest
{
    private static final String ANONYMOUS_DOWNLOAD_LINE = "2013-12-13T00:00:00.000+01:00;anonymous; ; ; ;null;\n";
    private static final String PANEL = "panel";
    private static final String PANEL_DOWNLOAD_CSV = PANEL+":"+DownloadActivityLogPanel.DOWNLOAD_CSV;
    private static final DateTime DOWNLOAD_DATE_TIME = new DateTime("2013-12-13");
    private static final FileItemVO FILE_ITEM_VO = new FileItemVO("file:sid", "foler:sid", "dataset:sid", "name", 256, "mimeType", //
            CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.ANONYMOUS);

    private static ApplicationContextMock applicationContext;
    private static DatasetService datasetService;
    private static UserService userService;
    private DownloadList downloadList;

    @BeforeClass
    public static void mockApplicationContext() throws Exception
    {
        final SystemReadOnlyStatus systemReadOnlyStatus = new SystemReadOnlyStatus(new File("target/systemReadonlyStatus.propeties"));
        final CodedAuthz codedAuthz = new CodedAuthz();
        codedAuthz.setSystemReadOnlyStatus(systemReadOnlyStatus);

        datasetService = PowerMock.createMock(DatasetService.class);
        userService = PowerMock.createMock(UserService.class);

        applicationContext = new ApplicationContextMock();
        applicationContext.putBean("systemReadOnlyStatus", systemReadOnlyStatus);
        applicationContext.putBean("authz", codedAuthz);
        applicationContext.putBean("security", new Security(codedAuthz));
        applicationContext.putBean("datasetService", datasetService);
        applicationContext.putBean("userService", userService);
    }

    @Before
    public void resetAll()
    {
        PowerMock.resetAll();
        downloadList = new DownloadList(DownloadList.TYPE_MONTH, Level.FILE_ITEM, DOWNLOAD_DATE_TIME);
    }

    @After
    public void verifyAll()
    {
        PowerMock.verifyAll();
    }

    @Test
    public void noDLH() throws Exception
    {
        downloadList = null;
        expectInvisible(new EasyUserImpl(Role.ARCHIVIST));
    }

    @Test
    public void emptyDLH() throws Exception
    {
        expectInvisible(new EasyUserImpl(Role.ARCHIVIST));
    }

    @Test
    public void byUser() throws Exception
    {
        downloadList.addDownload(FILE_ITEM_VO, null, DOWNLOAD_DATE_TIME);
        expectInvisible(new EasyUserImpl(Role.USER));
    }

    @Test
    public void byAdmin() throws Exception
    {
        downloadList.addDownload(FILE_ITEM_VO, null, DOWNLOAD_DATE_TIME);
        expectInvisible(new EasyUserImpl(Role.ADMIN));
    }

    @Test
    public void withoutUser() throws Exception
    {
        downloadList.addDownload(FILE_ITEM_VO, null, DOWNLOAD_DATE_TIME);
        expect(ANONYMOUS_DOWNLOAD_LINE);
    }

    @Test
    public void withAnonymous() throws Exception
    {
        downloadList.addDownload(FILE_ITEM_VO, EasyUserAnonymous.getInstance(), DOWNLOAD_DATE_TIME);
        expect(ANONYMOUS_DOWNLOAD_LINE);
    }

    @Test
    public void userWantsNoActionLog() throws Exception
    {
        downloadList.addDownload(FILE_ITEM_VO, mockUser(false), DOWNLOAD_DATE_TIME);
        expect(ANONYMOUS_DOWNLOAD_LINE);
    }

    @Test
    public void withKnownUser() throws Exception
    {
        downloadList.addDownload(FILE_ITEM_VO, mockUser(true), DOWNLOAD_DATE_TIME);
        expect("2013-12-13T00:00:00.000+01:00;userid;email;organization;function;null;\n");
    }

    @Test
    public void withNotFoundUser() throws Exception
    {
        downloadList.addDownload(FILE_ITEM_VO, mockNotFoundUser(), DOWNLOAD_DATE_TIME);
        expect(ANONYMOUS_DOWNLOAD_LINE);
    }

    @Test
    public void withEmptyUserValues() throws Exception
    {
        downloadList.addDownload(FILE_ITEM_VO, mockUserWithEmptyValues(), DOWNLOAD_DATE_TIME);
        expect("2013-12-13T00:00:00.000+01:00;userid;null;null;null;null;\n");
    }

    @Test
    public void withEmptyDownloaderID() throws Exception
    {
        downloadList.addDownload(FILE_ITEM_VO, new EasyUserImpl(""), DOWNLOAD_DATE_TIME);
        expect(ANONYMOUS_DOWNLOAD_LINE);
    }

    @Test
    public void withNotFoundUserService() throws Exception
    {
        downloadList.addDownload(FILE_ITEM_VO, mockNotFoundUserService(), DOWNLOAD_DATE_TIME);
        expect(ANONYMOUS_DOWNLOAD_LINE);
    }

    @Test
    public void withNotFoundDatasetService() throws Exception
    {
        EasyMock.expect(datasetService.getDownloadHistoryFor(isA(EasyUser.class), isA(Dataset.class), isA(DateTime.class))).andStubThrow(new ServiceException(""));
        expectInvisible(new EasyUserImpl(Role.USER));
    }

    private void expectInvisible(final EasyUserImpl easyUser) throws Exception
    {
        final WicketTester tester = run(easyUser);
        tester.assertInvisible(PANEL);
        tester.assertInvisible(PANEL_DOWNLOAD_CSV);
    }

    private void expect(final String lines) throws Exception
    {
        final WicketTester tester = run(new EasyUserImpl(Role.ARCHIVIST));
        tester.assertVisible(PANEL);
        tester.assertVisible(PANEL_DOWNLOAD_CSV);
        tester.assertEnabled(PANEL_DOWNLOAD_CSV);
        tester.clickLink(PANEL_DOWNLOAD_CSV);
        assertThat(tester.getServletResponse().getDocument(), is(lines));
    }

    private WicketTester run(final EasyUser easyUser) throws Exception
    {
        final Dataset dataset = mockDataset();

        PowerMock.replayAll();
        final EasyWicketApplication application = new EasyWicketApplication();
        application.setApplicationContext(applicationContext);

        final WicketTester tester = new WicketTester(application);
        tester.startPanel(new ITestPanelSource()
        {
            private static final long serialVersionUID = 1L;

            public Panel getTestPanel(final String panelId)
            {
                return new DownloadActivityLogPanel(panelId, dataset, easyUser);
            }
        });
        return tester;
    }

    private EasyUserImpl mockUser(final boolean logMyActions) throws Exception
    {
        final EasyUserImpl user = new EasyUserImpl("userid");
        user.setFunction("function");
        user.setEmail("email");
        user.setOrganization("organization");
        user.setLogMyActions(logMyActions);
        EasyMock.expect(userService.getUserById(isA(EasyUser.class), isA(String.class))).andStubReturn(user);
        return user;
    }

    private EasyUserImpl mockUserWithEmptyValues() throws Exception
    {
        final EasyUserImpl user = new EasyUserImpl("userid");
        user.setFunction(null);
        user.setEmail(null);
        user.setOrganization(null);
        EasyMock.expect(userService.getUserById(isA(EasyUser.class), isA(String.class))).andStubReturn(user);
        return user;
    }

    private EasyUserImpl mockNotFoundUser() throws Exception 
    {
        EasyMock.expect(userService.getUserById(isA(EasyUser.class), isA(String.class))).andStubThrow(new ObjectNotAvailableException(""));
        return new EasyUserImpl("notFoundUser");
    }

    private EasyUserImpl mockNotFoundUserService() throws Exception 
    {
        EasyMock.expect(userService.getUserById(isA(EasyUser.class), isA(String.class))).andStubThrow(new ServiceException(""));
        return new EasyUserImpl("notFoundUser");
    }

    private Dataset mockDataset() throws ServiceException
    {
        final DownloadHistory dlh;
        if (downloadList == null)
            dlh = null;
        else
        {
            dlh = PowerMock.createMock(DownloadHistory.class);
            EasyMock.expect(dlh.getDownloadList()).andStubReturn(downloadList);
        }
        EasyMock.expect(datasetService.getDownloadHistoryFor(isA(EasyUser.class), isA(Dataset.class), isA(DateTime.class))).andStubReturn(dlh);

        final Dataset dataset = PowerMock.createMock(Dataset.class);
        EasyMock.expect(dataset.getDmoStoreId()).andStubReturn(new DmoStoreId("dataset:sid"));
        return dataset;
    }
}
