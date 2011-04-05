package nl.knaw.dans.easy.sword;

import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.userrepo.EasyUserRepo;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;

import org.easymock.EasyMock;
import org.junit.BeforeClass;
import org.junit.Test;
import org.purl.sword.base.SWORDAuthenticationException;
import org.purl.sword.base.ServiceDocumentRequest;

public class EasySwordServerTest extends Tester
{
    private static final String LOCATION = "http://localhost:8080/easy-sword-0.0.1-SNAPSHOT/serviceDocument";
    private static final String    INVALID_USER  = "wrongUser";
    private static final String    VALID_USER_ID = "someUser";
    private static final String    PASSWORD      = "secret";
    private static EasySwordServer easySwordServer;
    private static EasyUserRepo    userRepo      = EasyMock.createMock(EasyUserRepo.class);

    @BeforeClass
    public static void setup() throws Exception
    {
        easySwordServer = new EasySwordServer();
    }

    @BeforeClass
    public static void mockUser() throws Exception
    {
        final EasyUser user = new EasyUserImpl();
        user.setId(VALID_USER_ID);
        user.setPassword(PASSWORD);
        user.setInitials("S.");
        user.setFirstname("Some");
        user.setSurname("Body");
        user.setEmail("some@body.com");
        user.setState(EasyUser.State.ACTIVE);

        EasyMock.expect(userRepo.authenticate(VALID_USER_ID, PASSWORD)).andReturn(true).anyTimes();
        EasyMock.expect(userRepo.authenticate(INVALID_USER, PASSWORD)).andReturn(false).anyTimes();
        EasyMock.expect(userRepo.authenticate(null, null)).andReturn(false).anyTimes();
        EasyMock.expect(userRepo.authenticate("", "")).andReturn(false).anyTimes();
        EasyMock.expect(userRepo.findById(VALID_USER_ID)).andReturn(user).anyTimes();
        new Data().setUserRepo(userRepo);
        EasyMock.replay(userRepo);
    }

    @Test
    public void serviceDocumentWithUser() throws Exception
    {
        final ServiceDocumentRequest request = new ServiceDocumentRequest();
        request.setUsername(VALID_USER_ID);
        request.setPassword(PASSWORD);
        request.setLocation(LOCATION);
        assertAsExpected(easySwordServer.doServiceDocument(request).toString(), "serviceDocumentWithUser.xml");
    }

    @Test(expected=SWORDAuthenticationException.class)
    public void serviceDocumentWrongUser() throws Exception
    {
        final ServiceDocumentRequest request = new ServiceDocumentRequest();
        request.setUsername(INVALID_USER);
        request.setPassword(PASSWORD);
        request.setLocation(LOCATION);
        assertAsExpected(easySwordServer.doServiceDocument(request).toString(), "serviceDocumentWrongUser.xml");
    }

    @Test
    public void serviceDocumentWithoutUser() throws Exception
    {
        final ServiceDocumentRequest request = new ServiceDocumentRequest();
        request.setLocation(LOCATION);
        assertAsExpected(easySwordServer.doServiceDocument(request).toString(), "serviceDocumentWithoutUser.xml");
    }
}
