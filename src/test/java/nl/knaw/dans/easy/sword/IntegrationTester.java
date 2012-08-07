package nl.knaw.dans.easy.sword;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.BindException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.purl.sword.base.ErrorCodes;
import org.purl.sword.base.SWORDAuthenticationException;
import org.purl.sword.base.SWORDException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntegrationTester
{
    private static final String                      DIR       = new File(SubmitFixture.PROPER_ZIP).getParent();
    private static final String                      URL       = "http://localhost:" + Start.PORT + "/";
    private static final UsernamePasswordCredentials DEPOSITOR = new UsernamePasswordCredentials("depositor", "123456");
    private static final UsernamePasswordCredentials ANONYMOUS = new UsernamePasswordCredentials("anonymous", "password");
    private static final int                         SECOND    = 1000;

    private static Server                            server;

    private static Logger                            log       = LoggerFactory.getLogger(EasySwordServer.class);

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
        server = Start.createServer(Start.PORT, Start.SSL_PORT);
        try
        {
            server.start();
        }
        catch (final BindException e)
        {
            throw new Exception(URL + " already in use", e);
        }
        log.debug("starting server");
        while (server.isStarting())
            Thread.sleep(50);
        log.debug("started server");
    }

    @AfterClass
    public static void stop() throws Exception
    {
        if (server != null)
        {
            server.stop();
            server.join();
            log.debug("stopped server");
        }
    }

    @Test
    public void serviceDocument() throws Exception
    {
        final GetMethod method = new GetMethod(URL + "servicedocument");
        execute(method, createClient(ANONYMOUS, null));
        int scOk = HttpStatus.SC_OK;
        assertResponseCode(method, scOk);
    }

    @Test
    public void mediatedServiceDocument() throws Exception
    {
        final GetMethod method = new GetMethod(URL + "servicedocument");
        method.addRequestHeader("X-On-Behalf-Of", DEPOSITOR.getUserName());
        execute(method, createClient(DEPOSITOR, null));
        int scOk = HttpStatus.SC_PRECONDITION_FAILED;
        assertResponseCode(method, scOk);
    }

    @Test
    public void depositProperZip() throws Exception
    {
        final RequestEntity request = createRequest(new File(SubmitFixture.PROPER_ZIP));
        final PostMethod method = createPostMethod(request, false, false);
        doDeposit(method, 15 * SECOND, HttpStatus.SC_ACCEPTED);
    }

    @Test
    public void noOpDeposit() throws Exception
    {
        final RequestEntity request = createRequest(new File(SubmitFixture.PROPER_ZIP));
        final PostMethod method = createPostMethod(request, true, false);
        doDeposit(method, 15 * SECOND, HttpStatus.SC_ACCEPTED);
    }

    @Test
    public void maxPathLength() throws Throwable
    {
        final RequestEntity request = createRequest(new File(SubmitFixture.getZip("max-path")));
        final PostMethod method = createPostMethod(request, false, false);
        doDeposit(method, 15 * SECOND, HttpStatus.SC_ACCEPTED);
    }

    @Test
    public void mediatedDeposit() throws Exception
    {
        final RequestEntity request = createRequest(new File(SubmitFixture.PROPER_ZIP));
        final PostMethod method = createPostMethod(request, false, false);
        method.addRequestHeader("X-On-Behalf-Of", DEPOSITOR.getUserName());
        doDeposit(method, 15 * SECOND, HttpStatus.SC_PRECONDITION_FAILED);
    }

    @Test
    public void draftDeposit() throws Exception
    {
        final RequestEntity request = createRequest(new File(DIR + "/invalidDisciplineId.zip"));
        final PostMethod method = createPostMethod(request, false, false);
        doDeposit(method, 15 * SECOND, HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void depositInvalidZip() throws Exception
    {
        final RequestEntity request = createRequest(SubmitFixture.META_DATA_FILE);
        final PostMethod method = createPostMethod(request, false, false);
        doDeposit(method, 15 * SECOND, HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE);
    }

    private static void doDeposit(final PostMethod method, final int timeout, final int expectedStatus) throws Exception
    {
        execute(method, createClient(DEPOSITOR, timeout));
        if (method.getStatusCode() == expectedStatus)
            ;
        else if (method.getStatusCode() == HttpStatus.SC_UNAUTHORIZED)
            fail("please register EASY userID: " + DEPOSITOR.getUserName() + " with password: " + DEPOSITOR.getUserName());
        else
            fail("Unexpected response code: " + method.getStatusLine().toString());
    }

    private static void assertResponseCode(final GetMethod method, int expectedResponseCode)
    {
        final String message = "unexpected response code: " + method.getStatusLine().toString();
        assertTrue(message, method.getStatusCode() == expectedResponseCode);
    }

    private static void execute(final HttpMethod method, final HttpClient client) throws IOException, HttpException
    {
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
                // TODO unmarshall responsbody for the href of the root element <sword:error>
                log.info("\n" + method.getResponseBodyAsString());
            }
        }
        finally
        {
            method.releaseConnection();
        }
        log.info(Thread.currentThread().getStackTrace()[3].getMethodName() + ": " + method.getStatusLine());
    }

    private static PostMethod createPostMethod(final RequestEntity request, final Boolean noOp, final Boolean verbose)
    {
        final PostMethod method = new PostMethod(URL + "deposit");
        method.setRequestEntity(request);
        method.addRequestHeader("X-No-Op", noOp.toString());
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
        return new InputStreamRequestEntity(new FileInputStream(file));
    }
}
