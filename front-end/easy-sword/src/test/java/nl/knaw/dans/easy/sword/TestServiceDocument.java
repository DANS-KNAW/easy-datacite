package nl.knaw.dans.easy.sword;

import nl.knaw.dans.easy.sword.util.MockUtil;
import org.junit.Test;
import org.purl.sword.base.SWORDErrorException;
import org.purl.sword.base.ServiceDocumentRequest;

import static nl.knaw.dans.easy.sword.util.MockUtil.INVALID_USER_ID;
import static nl.knaw.dans.easy.sword.util.MockUtil.PASSWORD;
import static nl.knaw.dans.easy.sword.util.MockUtil.VALID_USER_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class TestServiceDocument {

    @Test
    public void athourisedServiceDocument() throws Exception {
        ServiceDocumentRequest request = mockRequest(VALID_USER_ID);
        String xml = new EasySwordServer().doServiceDocument(request).marshall();
        assertThat(xml, containsString("accepts deposits by users registered on http://mockedhost:8080"));
    }

    @Test
    public void anonymousServiceDocument() throws Exception {
        ServiceDocumentRequest request = mockRequest(INVALID_USER_ID);
        String xml = new EasySwordServer().doServiceDocument(request).marshall();
        assertThat(xml, containsString("This is a test server"));
    }

    @Test
    public void mediatedServiceDocument() throws Exception {
        ServiceDocumentRequest request = mockRequest(VALID_USER_ID);
        request.setOnBehalfOf("anyone");
        try {
            new EasySwordServer().doServiceDocument(request).marshall();
        } catch (SWORDErrorException e) {
            assertThat(e.getMessage(), containsString("Mediated deposits not allowed"));
        }
    }

    private ServiceDocumentRequest mockRequest(String userId) throws Exception {
        MockUtil.mockUser();
        MockUtil.mockContext();
        ServiceDocumentRequest sdr = new ServiceDocumentRequest();
        sdr.setUsername(userId);
        sdr.setPassword(PASSWORD);
        sdr.setLocation("");
        return sdr;
    }
}
