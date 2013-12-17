package nl.knaw.dans.easy.web.view.dataset;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.isA;

import java.io.File;

import nl.knaw.dans.common.jibx.JiBXObjectFactory;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.util.FileUtil;
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
import nl.knaw.dans.easy.domain.model.user.EasyUser.Role;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.security.CodedAuthz;
import nl.knaw.dans.easy.security.Security;
import nl.knaw.dans.easy.servicelayer.SystemReadOnlyStatus;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.servicelayer.services.UserService;
import nl.knaw.dans.easy.web.EasyWicketApplication;

import org.apache.wicket.Session;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.apache.wicket.util.tester.WicketTester;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.powermock.api.easymock.PowerMock;

public class ActivityLogFixture
{

    protected static final DateTime DOWNLOAD_DATE_TIME = new DateTime("2013-12-13");
    protected static final FileItemVO FILE_ITEM_VO = new FileItemVO("file:sid", "foler:sid", "dataset:sid", "name", 256, "mimeType", //
                CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.ANONYMOUS);
    protected static final FolderItemVO FOLDER_ITEM_VO = new FolderItemVO("file:sid", "foler:sid", "dataset:sid", "name", 256);
    protected static ApplicationContextMock applicationContext;
    protected static DatasetService datasetService;
    protected static UserService userService;

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
    }

    @After
    public void verifyAll()
    {
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

    protected Dataset mockDataset(DownloadList downloadList) throws ServiceException
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

    protected DownloadList mockDownloadList36028() throws Exception
    {
        // user IDs are altered, but the LogMyActions flags match with 
        // http://easy.dans.knaw.nl:8080/fedora/objects/easy-dlh:36028/datastreams/DLHL/content
        // files are visible for anonymous so no further need for scrambling the data
        final byte[] data = FileUtil.readFile(new File("src/test/resources/mock-xml/issue560-dlh36028.xml"));
        final EasyUserImpl user = new EasyUserImpl(Role.USER);
        final EasyUserImpl userNoLog = new EasyUserImpl(Role.USER);
        userNoLog.setLogMyActions(false);
        EasyMock.expect(userService.getUserById(isA(EasyUser.class), eq("s1234567"))).andStubReturn(user);
        EasyMock.expect(userService.getUserById(isA(EasyUser.class), eq("laalbers"))).andStubReturn(userNoLog);
        EasyMock.expect(userService.getUserById(isA(EasyUser.class), eq("pschilder"))).andStubReturn(user);
        EasyMock.expect(userService.getUserById(isA(EasyUser.class), eq("juulesengel"))).andStubReturn(userNoLog);
        EasyMock.expect(userService.getUserById(isA(EasyUser.class), eq("warejacob"))).andStubReturn(user);
        EasyMock.expect(userService.getUserById(isA(EasyUser.class), eq("hvanleeuwen"))).andStubReturn(userNoLog);
        EasyMock.expect(userService.getUserById(isA(EasyUser.class), eq("theodorus"))).andStubReturn(userNoLog);
        return (DownloadList) JiBXObjectFactory.unmarshal(DownloadList.class, data);
    }

    protected Session mockSessionFor_Component_isActionAuthourized()
    {
        final Session session = PowerMock.createMock(Session.class);
        EasyMock.expect(session.getAuthorizationStrategy()).andStubReturn(null);
        return session;
    }

    protected WicketTester createWicketTester()
    {
        final EasyWicketApplication application = new EasyWicketApplication();
        application.setApplicationContext(applicationContext);
    
        final WicketTester tester = new WicketTester(application);
        return tester;
    }
}
