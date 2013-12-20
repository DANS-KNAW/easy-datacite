package nl.knaw.dans.easy.web.view.dataset;

import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
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
import nl.knaw.dans.easy.security.CodedAuthz;
import nl.knaw.dans.easy.security.Security;
import nl.knaw.dans.easy.servicelayer.SystemReadOnlyStatus;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.servicelayer.services.UserService;
import nl.knaw.dans.easy.web.EasyWicketApplication;

import org.apache.commons.io.FileUtils;
import org.apache.wicket.Component;
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
    protected static final FolderItemVO FOLDER_ITEM_VO = new FolderItemVO("file:sid", "foler:sid", "dataset:sid", "name", 256);
    protected static final FileItemVO FILE_ITEM_VO = new FileItemVO("file:sid", "foler:sid", "dataset:sid", "name", 256, "mimeType", //
            CreatorRole.DEPOSITOR, VisibleTo.ANONYMOUS, AccessibleTo.ANONYMOUS);
    protected static DatasetService datasetService;
    protected static UserService userService;
    private static ApplicationContextMock applicationContext;
    private StringBuffer labelErrors;

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

    protected Dataset mockDataset(final DownloadList downloadList, final EasyUser user, final boolean isDepositor) throws ServiceException
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
        EasyMock.expect(dataset.hasDepositor(user)).andStubReturn(isDepositor);
        EasyMock.expect(dataset.hasPermissionRestrictedItems()).andStubReturn(false);
        return dataset;
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

        final WicketTester tester = new WicketTester(application)
        {
            @Override
            public void dumpPage()
            {
               try
                {
                    final StackTraceElement caller = new Exception().getStackTrace()[1];
                    final String testClass = caller.getClassName().replaceAll(".*[.]", "");
                    final String testMethod = caller.getMethodName();
                    final File file = new File("target/" + testClass + "/" + testMethod + ".html");
                    FileUtils.write(file, getServletResponse().getDocument());
                }
                catch (final IOException e)
                {
                    super.dumpPage();
                }
            }

            @Override
            public void assertLabel(final String path, final String expected)
            {
                // failure messages become clearer with a path; collect all label assertions
                final Component component = getComponentFromLastRenderedPage(path);
                final String label = component.getDefaultModelObjectAsString();
                if (label==null && expected !=null)
                    labelErrors.append("\nexpected [" + expected + "] but did not find " + path);
                if (!expected.equals(label))
                    labelErrors.append("\nexpected [" + expected + "] got [" + label + "] " + path);
            }
        };
        return tester;
    }
}
