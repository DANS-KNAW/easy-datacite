package nl.knaw.dans.easy.sword;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.internal.matchers.StringContains.containsString;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;

import nl.knaw.dans.easy.sword.jetty.Start;
import nl.knaw.dans.easy.sword.util.SubmitFixture;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.purl.sword.base.ErrorCodes;
import org.purl.sword.base.SWORDAuthenticationException;
import org.purl.sword.base.SWORDException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntegrationTester
{
    private static final File VALID_FILE = SubmitFixture.getFile("data-plus-meta.zip");
    private static String URL;
    private static final UsernamePasswordCredentials DEPOSITOR = new UsernamePasswordCredentials("user", "dev");
    private static final UsernamePasswordCredentials ANONYMOUS = new UsernamePasswordCredentials("anonymous", "password");
    private static final int SECOND = 1000;

    private static Server server;

    private static Logger log = LoggerFactory.getLogger(EasySwordServer.class);

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

    @BeforeClass
    public static void start() throws Exception
    {
        // zero implies a random port and allows the test to run along with an active server on port 8083
        server = Start.createServer(0, 0);
        server.start();
        URL = "http://localhost:" + server.getConnectors()[0].getLocalPort() + "/";
        log.debug("started " + URL.toString());
    }

    @AfterClass
    public static void stop() throws Exception
    {
        if (server == null)
            return;
        server.stop();
        server.join();
        log.debug("stopped " + URL.toString());
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
        getResponse(method, createClient(DEPOSITOR, null));
        assertResponseCode(method, HttpStatus.SC_PRECONDITION_FAILED);
    }

    @Test
    public void depositWithInvalidDDM() throws Exception
    {
        final RequestEntity request = createRequest(SubmitFixture.getFile("data-plus-invalid-ddm.zip"));
        final PostMethod method = createPostMethod(request, false, false);
        String r = getResponse(method, createClient(DEPOSITOR, (15 * SECOND)));
        assertResponseCode(method, HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE);
        assertThat(r, containsString("Cannot find"));
        assertThat(r, containsString("dans-dataset-md"));
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
        assertResponseCode(method, HttpStatus.SC_ACCEPTED);
    }

    //@Ignore
    // TODO reduce path length, fits nog longer
    @Test
    public void maxPathLength() throws Throwable
    {
        final RequestEntity request = createRequest(SubmitFixture.getFile("max-path.zip"));
        final PostMethod method = createPostMethod(request, false, false);
        getResponse(method, createClient(DEPOSITOR, (150 * SECOND)));
        assertResponseCode(method, HttpStatus.SC_ACCEPTED);
        /*
original/datafolder/abcdefghijklmnopqr/abcdefghijklmnopqrstuvwxyz/abcdefghijklmnopqrstuvwxyz/abcdefghijklmnopqrstuvwxyz/abcdefghijklmnopqrstuvwxyz/abcdefghijklmnopqrstuvwxyz/abcdefghijklmnopqrstuvwxyz/abcdefghijklmnopqrstuvwxyz/abcdefghijklmn/abc/test.txt
         */
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
        getResponse(method, createClient(DEPOSITOR, (15 * SECOND)));
        assertResponseCode(method, HttpStatus.SC_PRECONDITION_FAILED);
    }

    @Test
    public void invalidDisciplineDeposit() throws Exception
    {
        final RequestEntity request = createRequest(SubmitFixture.getFile("invalidDisciplineId.zip"));
        final PostMethod method = createPostMethod(request, false, false);
        getResponse(method, createClient(DEPOSITOR, (15 * SECOND)));
        assertResponseCode(method, HttpStatus.SC_BAD_REQUEST);
        // assertTrue(response.contains("easy-dataset:"));
    }

    @Test
    public void depositInvalidZip() throws Exception
    {
        final RequestEntity request = createRequest(SubmitFixture.getFile("metadata.xml"));
        final PostMethod method = createPostMethod(request, false, false);
        getResponse(method, createClient(DEPOSITOR, (15 * SECOND)));
        assertResponseCode(method, HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE);
    }

    private static void assertResponseCode(final HttpMethod method, final int expectedResponseCode)
    {
        if (method.getStatusCode() == expectedResponseCode)
            return;
        if (method.getStatusCode() == HttpStatus.SC_UNAUTHORIZED)
            fail("please register EASY user to make the tests work. ID:" + DEPOSITOR.getUserName() + " with password: " + DEPOSITOR.getPassword());
        fail("Unexpected response code: " + method.getStatusLine().toString());
    }

    private String getResponse(final HttpMethod method, final HttpClient client) throws IOException, HttpException
    {
        String responseBody = null;
        try
        {
            client.executeMethod(method);
            switch (method.getStatusCode())
            {
            case HttpStatus.SC_ACCEPTED:
                ;// already logged by org.purl.sword.base.DepositResponse
            case HttpStatus.SC_OK:
                ;// already logged
                break;
            default:
                responseBody = method.getResponseBodyAsString();
                log.info("\n" + responseBody + "\n\t");
            }
        }
        finally
        {
            method.releaseConnection();
        }
        final StringBuffer message = new StringBuffer();
        message.append("======== " + Thread.currentThread().getStackTrace()[2].getMethodName());
        message.append("\n");
        message.append(method.getName() + method.getPath());
        message.append("\n");
        message.append("client timeout: " + client.getParams().getSoTimeout());
        message.append("\n");
        message.append("client state: " + client.getState());
        message.append("\n");
        message.append("host auth state: " + method.getHostAuthState());
        message.append("\n");
        message.append(Arrays.deepToString(method.getRequestHeaders()));
        message.append("\n");
        message.append(method.getStatusLine());
        message.append("\n\t");
        log.info(message.toString());
        return responseBody;
    }

    private static PostMethod createPostMethod(final RequestEntity request, final Boolean noOp, final Boolean verbose)
    {
        final PostMethod method = new PostMethod(URL + "deposit");
        method.setRequestEntity(request);
        if (noOp != null)
            method.addRequestHeader("X-No-Op", noOp.toString());
        if (noOp != null)
            method.addRequestHeader("X-Verbose", verbose.toString());
        return method;
    }

    private static HttpClient createClient(final UsernamePasswordCredentials credentials, final Integer timeout)
    {
        final HttpClient client = new HttpClient();
        client.getState().setCredentials(AuthScope.ANY, credentials);
        if (timeout != null)
            client.getParams().setSoTimeout(timeout);
        return client;
    }

    private static RequestEntity createRequest(final File file) throws FileNotFoundException
    {
        log.info("======== creating request with " + file);
        return new InputStreamRequestEntity(new FileInputStream(file));
    }
}
