package nl.knaw.dans.easy.web.view.dataset;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertTrue;

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
import org.apache.wicket.markup.html.basic.Label;
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

    protected Dataset mockDataset(final DownloadList downloadList) throws ServiceException
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

    protected Integer[] expectedNrOfFilesPerRowFor36028()
    {
        final Integer[] files = {1, 1, 1, 1, 1, 1, 1, 4, 1, 3, 1, 1};
        return files;
    }

    protected String expectedDownloadFor36028()
    {
        String string = "2013-02-05T14:40:06.700+01:00;null;null;null;null;original/CV 2008 data en documentatiefiles/SCP_CV2008_juli.pdf;\n"
                + "2013-02-05T14:47:22.715+01:00;null;null;null;null;original/CV 2008 data en documentatiefiles/SCP_CV2008.pdf;\n"
                + "2013-02-05T18:44:51.846+01:00;anonymous; ; ; ;original/CV 2008 data en documentatiefiles/SCP_Culturele_Veranderingen_2008_DANS.sav;\n"
                + "2013-02-11T10:55:19.434+01:00;null;null;null;null;original/CV 2008 data en documentatiefiles/SCP_CV2008.pdf;\n"
                + "2013-02-11T10:55:31.976+01:00;null;null;null;null;original/CV 2008 data en documentatiefiles/SCP_CV2008.pdf;\n"
                + "2013-02-11T11:01:16.151+01:00;null;null;null;null;original/CV 2008 data en documentatiefiles/SCP_CV20089.pdf;\n"
                + "2013-02-12T13:51:29.008+01:00;anonymous; ; ; ;original/CV 2008 data en documentatiefiles/SCP_CV20089_augustus.pdf;\n"
                + "2013-02-21T14:31:01.962+01:00;null;null;null;null;original/CV 2008 data en documentatiefiles/SCP_CV20089_augustus.pdf;\n"
                + "2013-02-21T14:31:01.962+01:00;null;null;null;null;original/CV 2008 data en documentatiefiles/SCP_CV2008_juli.pdf;\n"
                + "2013-02-21T14:31:01.962+01:00;null;null;null;null;original/CV 2008 data en documentatiefiles/SCP_CV2008.pdf;\n"
                + "2013-02-21T14:31:01.962+01:00;null;null;null;null;original/CV 2008 data en documentatiefiles/SCP_CV20089.pdf;\n"
                + "2013-02-21T14:34:03.198+01:00;null;null;null;null;original/CV 2008 data en documentatiefiles/SCP_CV_2008.por;\n"
                + "2013-02-22T11:36:28.861+01:00;anonymous; ; ; ;original/CV 2008 data en documentatiefiles/SCP_Culturele_Veranderingen_2008_DANS.sav;\n"
                + "2013-02-22T11:36:28.861+01:00;anonymous; ; ; ;original/CV 2008 data en documentatiefiles/SCP_CV_2008.por;\n"
                + "2013-02-22T11:36:28.861+01:00;anonymous; ; ; ;original/CV 2008 data en documentatiefiles/SCP_CV2008.pdf;\n"
                + "2013-02-23T11:29:40.653+01:00;anonymous; ; ; ;original/CV 2008 data en documentatiefiles/SCP_CV2008.pdf;\n"
                + "2013-02-23T11:33:00.059+01:00;anonymous; ; ; ;original/CV 2008 data en documentatiefiles/SCP_Culturele_Veranderingen_2008_DANS.sav;\n";
        return string;
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

        final WicketTester tester = new WicketTester(application)
        {
            @Override
            public void assertLabel(final String path, final String expected)
            {
                // clearer failure messages 
                final Label component = (Label) getComponentFromLastRenderedPage(path);
                final String label = component.getDefaultModelObjectAsString();
                assertTrue(path+"\n"+"expected ["+expected+"]\ngot ["+label+"]", label.equals(expected));
            }
        };
        return tester;
    }
}
