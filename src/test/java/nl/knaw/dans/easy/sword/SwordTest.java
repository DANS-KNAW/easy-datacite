package nl.knaw.dans.easy.sword;



import org.junit.BeforeClass;
import org.junit.Test;
import org.purl.sword.base.ServiceDocumentRequest;

public class SwordTest extends Tester
{
    private static EasySwordServer server;

    @BeforeClass
    public static void setup()
    {
        server = new EasySwordServer();
    }

    @Test
    public void serviceDocument() throws Exception
    {
        final ServiceDocumentRequest request = new ServiceDocumentRequest();
        request.setUsername("someUser");
        request.setPassword("someUser");
        request.setLocation("http://localhost:8080/easy-sword-0.0.1-SNAPSHOT/serviceDocument");
        assertAsExpected(server.doServiceDocument(request).toString(),"serviceDocument.xml");
    }
}
