package nl.knaw.dans.easy.sword;

import static org.junit.Assert.assertThat;
import static org.junit.internal.matchers.StringContains.containsString;

import java.io.File;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletResponse;

import nl.knaw.dans.easy.sword.util.SubmitFixture;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.junit.Ignore;
import org.junit.Test;
import org.purl.sword.base.ErrorCodes;
import org.purl.sword.base.SWORDAuthenticationException;
import org.purl.sword.base.SWORDException;

public class IntegrationTester extends IntegrationFixture
{
    private static final File VALID_FILE = SubmitFixture.getFile("data-plus-meta.zip");
    private static final UsernamePasswordCredentials ANONYMOUS = new UsernamePasswordCredentials("anonymous", "password");
    static
    {
        // mash up of SWORDErrorException for documentational reasons
        @SuppressWarnings("unused")
        int i;
        @SuppressWarnings("unused")
        String s;

        s = ErrorCodes.ERROR_CHECKSUM_MISMATCH;
        s = ErrorCodes.ERROR_CONTENT;// )) { status = 415; }
        i = HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE;// 415

        s = ErrorCodes.MAX_UPLOAD_SIZE_EXCEEDED;// )) { status = 413; }
        i = HttpStatus.SC_REQUEST_TOO_LONG;// 413

        s = ErrorCodes.MEDIATION_NOT_ALLOWED;// )) { status = 412; }
        i = HttpStatus.SC_PRECONDITION_FAILED;// 412

        new SWORDAuthenticationException("");
        s = ErrorCodes.TARGET_OWNER_UKNOWN;// )) { status = 401; }
        i = HttpStatus.SC_UNAUTHORIZED;// 401

        s = ErrorCodes.ERROR_BAD_REQUEST;
        // anything else
        i = HttpStatus.SC_BAD_REQUEST;// 400

        new SWORDException("");
        new NoSuchAlgorithmException();// security
        i = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }

    @Test
    public void anonymousServiceDocument() throws Exception
    {
        final GetMethod method = new GetMethod(URL + "servicedocument");
        getResponse(method, createClient(ANONYMOUS, null));
        assertResponseCode(method, HttpStatus.SC_OK);
    }

    @Test
    public void athourisedServiceDocument() throws Exception
    {
        final GetMethod method = new GetMethod(URL + "servicedocument");
        getResponse(method, createClient(DEPOSITOR, null));
        assertResponseCode(method, HttpStatus.SC_OK);
    }

    @Test
    public void invalidServicedocumentPath() throws Exception
    {
        final GetMethod method = new GetMethod(URL + "xxx");
        getResponse(method, createClient(DEPOSITOR, null));
        // TODO Move to JMEter, might be different on EOF12/EOF13
        assertResponseCode(method, HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void invalidDepositPath() throws Exception
    {
        final RequestEntity request = createRequest(VALID_FILE);
        final PostMethod method = new PostMethod(URL + "xxx");
        method.setRequestEntity(request);
        getResponse(method, createClient(DEPOSITOR, (15 * SECOND)));
        assertResponseCode(method, HttpStatus.SC_METHOD_NOT_ALLOWED);
    }

    @Test
    public void delete() throws Exception
    {
        final DeleteMethod method = new DeleteMethod(URL + "deposit");
        // TODO Move to JMEter, might be different on EOF12/EOF13
        getResponse(method, createClient(DEPOSITOR, null));
        assertResponseCode(method, HttpStatus.SC_METHOD_NOT_ALLOWED);
    }

    @Test
    public void mediatedServiceDocument() throws Exception
    {
        final GetMethod method = new GetMethod(URL + "servicedocument");
        method.addRequestHeader("X-On-Behalf-Of", DEPOSITOR.getUserName());
        String response = getResponse(method, createClient(DEPOSITOR, null));
        assertResponseCode(method, HttpStatus.SC_PRECONDITION_FAILED);
        assertThat(response, containsString("Error 412"));
        assertThat(response, containsString("Mediated deposits not allowed"));
    }

    @Test
    public void depositWithInvalidDDM() throws Exception
    {
        final RequestEntity request = createRequest(SubmitFixture.getFile("data-plus-invalid-ddm.zip"));
        final PostMethod method = createPostMethod(request, false, false);
        String response = getResponse(method, createClient(DEPOSITOR, (15 * SECOND)));
        assertResponseCode(method, HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE);
        assertThat(response, containsString("Cannot find"));
        assertThat(response, containsString("dans-dataset-md"));
    }

    @Test
    public void depositWithMinimalDDM() throws Exception
    {
        final RequestEntity request = createRequest(SubmitFixture.getFile("minimal-ddm.zip"));
        final PostMethod method = createPostMethod(request, false, false);
        String r = getResponse(method, createClient(DEPOSITOR, (15 * SECOND)));
        assertResponseCode(method, HttpStatus.SC_BAD_REQUEST);
        assertThat(r, containsString("Missing required field dc.title, Missing required field dc.description"));
    }

    @Test
    public void depositWithDDM() throws Exception
    {
        final RequestEntity request = createRequest(SubmitFixture.getFile("data-plus-ddm.zip"));
        final PostMethod method = createPostMethod(request, false, false);
        getResponse(method, createClient(DEPOSITOR, (15 * SECOND)));
        assertResponseCode(method, HttpStatus.SC_ACCEPTED);
    }

    @Test
    public void depositWithEMD() throws Exception
    {
        final RequestEntity request = createRequest(VALID_FILE);
        final PostMethod method = createPostMethod(request, false, false);
        getResponse(method, createClient(DEPOSITOR, (15 * SECOND)));
        assertResponseCode(method, HttpStatus.SC_ACCEPTED);
    }

    @Ignore
    // TODO shouldn't this one fail? check specs.
    @Test
    public void depositInconsistentContentType() throws Exception
    {
        final RequestEntity request = createRequest(VALID_FILE);
        final PostMethod method = createPostMethod(request, null, null);
        method.addRequestHeader("Content-Type", "rqabarbera");
        getResponse(method, createClient(DEPOSITOR, (15 * SECOND)));
        assertResponseCode(method, HttpStatus.SC_ACCEPTED);
    }

    @Test
    public void noOpDeposit() throws Exception
    {
        final RequestEntity request = createRequest(VALID_FILE);
        final PostMethod method = createPostMethod(request, true, false);
        getResponse(method, createClient(DEPOSITOR, (15 * SECOND)));
        assertResponseCode(method, HttpStatus.SC_OK);
    }

    @Test
    public void md5() throws Exception
    {
        final RequestEntity request = createRequest(VALID_FILE);
        final PostMethod method = createPostMethod(request, true, false);
        method.addRequestHeader("Content-MD5", "nonsense checksum");
        getResponse(method, createClient(DEPOSITOR, (15 * SECOND)));
        assertResponseCode(method, HttpStatus.SC_PRECONDITION_FAILED);
    }

    @Test
    public void maxPathLength() throws Throwable
    {
        final RequestEntity request = createRequest(SubmitFixture.getFile("max-path.zip"));
        final PostMethod method = createPostMethod(request, false, false);
        getResponse(method, createClient(DEPOSITOR, (150 * SECOND)));
        assertResponseCode(method, HttpStatus.SC_ACCEPTED);
    }

    @Test
    public void nonBoolean() throws Throwable
    {
        final RequestEntity request = createRequest(SubmitFixture.getFile("max-path.zip"));
        final PostMethod method = createPostMethod(request, null, null);
        method.addRequestHeader("X-No-Op", "fout");
        getResponse(method, createClient(DEPOSITOR, (15 * SECOND)));
        assertResponseCode(method, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void mediatedDeposit() throws Exception
    {
        final RequestEntity request = createRequest(VALID_FILE);
        final PostMethod method = createPostMethod(request, false, false);
        method.addRequestHeader("X-On-Behalf-Of", DEPOSITOR.getUserName());
        String response = getResponse(method, createClient(DEPOSITOR, (15 * SECOND)));
        assertResponseCode(method, HttpStatus.SC_PRECONDITION_FAILED);
        assertThat(response, containsString("href=\"http://purl.org/net/sword/error/MediationNotAllowed\""));
    }

    @Test
    public void invalidDisciplineDeposit() throws Exception
    {
        final RequestEntity request = createRequest(SubmitFixture.getFile("invalidDisciplineId.zip"));
        final PostMethod method = createPostMethod(request, false, false);
        String response = getResponse(method, createClient(DEPOSITOR, (15 * SECOND)));
        assertResponseCode(method, HttpStatus.SC_BAD_REQUEST);
        assertThat(response, containsString("href=\"http://purl.org/net/sword/error/ErrorBadRequest\""));
        assertThat(response, containsString("999 not found"));
    }

    @Test
    public void depositInvalidZip() throws Exception
    {
        final RequestEntity request = createRequest(SubmitFixture.getFile("metadata.xml"));
        final PostMethod method = createPostMethod(request, false, false);
        getResponse(method, createClient(DEPOSITOR, (15 * SECOND)));
        assertResponseCode(method, HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE);
    }
}
