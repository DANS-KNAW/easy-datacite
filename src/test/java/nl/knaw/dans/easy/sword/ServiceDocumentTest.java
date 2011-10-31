package nl.knaw.dans.easy.sword;


import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.purl.sword.base.SWORDErrorException;
import org.purl.sword.base.ServiceDocumentRequest;

public class ServiceDocumentTest extends EasySwordServerTester
{
    @BeforeClass
    public static void setupMocking() throws Exception {
        MockUtil.mockAll();
    }

    @Test
    public void serviceDocumentWithUser() throws Exception
    {
        final ServiceDocumentRequest request = new ServiceDocumentRequest();
        request.setUsername(MockUtil.VALID_USER_ID);
        request.setPassword(MockUtil.PASSWORD);
        request.setLocation(LOCATION);
        EasySwordServer.setPolicy("No guarantee of service, or that deposits will be retained for any length of time.");
        EasySwordServer.setTreatment("This is a test server");
        assertAsExpected(easySwordServer.doServiceDocument(request).toString(), "serviceDocumentWithUser.xml");
    }

    @Test(expected=SWORDErrorException.class)
    public void serviceDocumentWrongUser() throws Exception
    {
        final ServiceDocumentRequest request = new ServiceDocumentRequest();
        request.setUsername(MockUtil.INVALID_USER_ID);
        request.setPassword(MockUtil.PASSWORD);
        request.setLocation(LOCATION);
        assertAsExpected(easySwordServer.doServiceDocument(request).toString(), "serviceDocumentWrongUser.xml");
    }

    @Test//(expected=SWORDErrorException.class)
    public void serviceDocumentWithoutUser() throws Exception
    {
        final ServiceDocumentRequest request = new ServiceDocumentRequest();
        request.setLocation(LOCATION);
        assertAsExpected(easySwordServer.doServiceDocument(request).toString(), "serviceDocumentWithoutUser.xml");
    }
}
