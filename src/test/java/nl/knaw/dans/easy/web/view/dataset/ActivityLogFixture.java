package nl.knaw.dans.easy.web.view.dataset;

import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.EasyApplicationContextMock;
import nl.knaw.dans.easy.EasyWicketTester;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.download.DownloadHistory;
import nl.knaw.dans.easy.domain.download.DownloadList;
import nl.knaw.dans.easy.domain.download.DownloadList.Level;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.CreatorRole;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.servicelayer.services.UserService;
import nl.knaw.dans.easy.web.EasyWicketApplication;

import org.apache.wicket.Session;
import org.apache.wicket.util.tester.WicketTester;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.powermock.api.easymock.PowerMock;

public class ActivityLogFixture
{

    protected static final DateTime DOWNLOAD_DATE_TIME = new DateTime("2013-12-13");
    protected static final FolderItemVO FOLDER_ITEM_VO = new FolderItemVO("file:sid", "foler:sid", "dataset:sid", "name", 256);
    protected static final FileItemVO FILE_ITEM_VO = new FileItemVO("file:sid", "foler:sid", "dataset:sid", "name", 256, "mimeType", //
            CreatorRole.DEPOSITOR, null, VisibleTo.ANONYMOUS, AccessibleTo.ANONYMOUS);
    protected static DatasetService datasetService;
    protected static UserService userService;
    private static EasyApplicationContextMock applicationContext;
    private StringBuffer labelErrors;

    @BeforeClass
    public static void mockApplicationContext() throws Exception
    {
        datasetService = PowerMock.createMock(DatasetService.class);
        userService = PowerMock.createMock(UserService.class);

        applicationContext = new EasyApplicationContextMock(false);
        applicationContext.putBean("datasetService", datasetService);
        applicationContext.putBean("userService", userService);
    }

    @BeforeClass
    public static void mockNow()
    {
        DateTimeUtils.setCurrentMillisFixed(new DateTime("2013-12-11").getMillis());
    }

    @Before
    public void resetAll()
    {
        labelErrors = new StringBuffer();
        PowerMock.resetAll();
    }

    @After
    public void verifyAll()
    {
        assertTrue(labelErrors.toString(), labelErrors.length() == 0);
        PowerMock.verifyAll();
    }

    protected DownloadList createDownloadList()
    {
        return new DownloadList(DownloadList.TYPE_MONTH, Level.FILE_ITEM, DOWNLOAD_DATE_TIME);
    }

    protected EasyUserImpl mockUser(final boolean logMyActions) throws Exception
    {
        final EasyUserImpl user = new EasyUserImpl("userid");
        user.setFunction("function");
        user.setSurname("surname");
        user.setEmail("email");
        user.setOrganization("organization");
        user.setLogMyActions(logMyActions);
        EasyMock.expect(userService.getUserById(isA(EasyUser.class), isA(String.class))).andStubReturn(user);
        return user;
    }

    protected EasyUserImpl mockUserWithEmptyValues() throws Exception
    {
        final EasyUserImpl user = new EasyUserImpl("userid");
        user.setFunction(null);
        user.setEmail(null);
        user.setOrganization(null);
        EasyMock.expect(userService.getUserById(isA(EasyUser.class), isA(String.class))).andStubReturn(user);
        return user;
    }

    protected EasyUserImpl mockNotFoundUser() throws Exception
    {
        EasyMock.expect(userService.getUserById(isA(EasyUser.class), isA(String.class))).andStubThrow(new ObjectNotAvailableException(""));
        return new EasyUserImpl("notFoundUser");
    }

    protected EasyUserImpl mockNotFoundUserService() throws Exception
    {
        EasyMock.expect(userService.getUserById(isA(EasyUser.class), isA(String.class))).andStubThrow(new ServiceException(""));
        return new EasyUserImpl("notFoundUser");
    }

    protected Dataset mockDataset(final DownloadList downloadList, final EasyUser user, final boolean isDepositor, final boolean hasPermissionRestrictedItems,
            boolean isPermissionGranted) throws ServiceException
    {
        EasyMock.expect(datasetService.getDownloadHistoryFor(isA(EasyUser.class), isA(Dataset.class), isA(DateTime.class)))//
                .andStubReturn(mockDownloadHistory(downloadList));

        final Dataset dataset = PowerMock.createMock(Dataset.class);
        EasyMock.expect(dataset.getDmoStoreId()).andStubReturn(new DmoStoreId("dataset:sid"));
        EasyMock.expect(dataset.hasDepositor(user)).andStubReturn(isDepositor);
        EasyMock.expect(dataset.hasPermissionRestrictedItems()).andStubReturn(hasPermissionRestrictedItems);
        EasyMock.expect(dataset.isPermissionGrantedTo(isA(EasyUser.class))).andStubReturn(isPermissionGranted);
        return dataset;
    }

    private DownloadHistory mockDownloadHistory(final DownloadList downloadList)
    {
        final DownloadHistory dlh;
        if (downloadList == null)
            dlh = null;
        else
        {
            dlh = PowerMock.createMock(DownloadHistory.class);
            EasyMock.expect(dlh.getDownloadList()).andStubReturn(downloadList);
        }
        return dlh;
    }

    protected Session mockSessionFor_Component_isActionAuthourized()
    {
        final Session session = PowerMock.createMock(Session.class);
        EasyMock.expect(session.getAuthorizationStrategy()).andStubReturn(null);

        // in case of exceptions catched by wicket
        EasyMock.expect(session.getLocale()).andStubReturn(Locale.ENGLISH);
        return session;
    }

    protected WicketTester createWicketTester()
    {
        final EasyWicketApplication application = new EasyWicketApplication();
        application.setApplicationContext(applicationContext);
        final EasyWicketTester tester = EasyWicketTester.create(applicationContext);

        // tell resource locator were to find HTML of TestHomePage
        tester.getApplication().getResourceSettings().addResourceFolder("src/test/java/");
        return tester;
    }
}
