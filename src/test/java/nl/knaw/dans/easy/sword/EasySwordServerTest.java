package nl.knaw.dans.easy.sword;


import org.junit.BeforeClass;
import org.junit.Test;
import org.purl.sword.base.SWORDAuthenticationException;
import org.purl.sword.base.ServiceDocumentRequest;

public class EasySwordServerTest extends Tester
{
    private static final String LOCATION = "http://localhost:8080/easy-sword-0.0.1-SNAPSHOT/serviceDocument";
    private static EasySwordServer easySwordServer;
    
    @BeforeClass
    public static void setup() throws Exception
    {
        easySwordServer = new EasySwordServer();
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
        request.setUsername(INVALID_USER_ID);
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
