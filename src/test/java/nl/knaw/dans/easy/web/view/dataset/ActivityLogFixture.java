package nl.knaw.dans.easy.web.view.dataset;

import static org.easymock.EasyMock.isA;

import java.io.File;

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

import org.apache.wicket.spring.test.ApplicationContextMock;
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
    private static UserService userService;
    protected DownloadList downloadList;

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

    protected Dataset mockDataset() throws ServiceException
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
