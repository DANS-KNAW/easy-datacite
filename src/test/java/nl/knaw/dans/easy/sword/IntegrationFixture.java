package nl.knaw.dans.easy.sword;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import nl.knaw.dans.easy.servicelayer.SystemReadOnlyStatus;
import nl.knaw.dans.easy.sword.jetty.Start;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.mortbay.jetty.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntegrationFixture
{

    protected static String URL;
    protected static final UsernamePasswordCredentials DEPOSITOR = new UsernamePasswordCredentials("user", "dev");
    protected static final int SECOND = 1000;
    private static Server server;
    private static Logger log = LoggerFactory.getLogger(EasySwordServer.class);

    @After
    public void reset() throws Exception
    {
        Context.getSystemReadOnlyStatus().setReadOnly(false);
    }

    @BeforeClass
    public static void start() throws Exception
    {
        new Context().setSystemReadOnlyStatus(createSystemReadOnlyBean());
        // zero implies a random port and allows the test to run along with an active server on port 8083
        server = Start.createServer(0, 0);
        server.start();
        URL = "http://localhost:" + server.getConnectors()[0].getLocalPort() + "/";
        log.debug("started " + URL.toString());

    }

    private static SystemReadOnlyStatus createSystemReadOnlyBean()
    {
        SystemReadOnlyStatus systemReadOnlyStatus = new SystemReadOnlyStatus();
        systemReadOnlyStatus.setFile(new File("target/SystemReadOnlyStatus.properties"));
        return systemReadOnlyStatus;
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

    protected static void assertResponseCode(final HttpMethod method, final int expectedResponseCode)
    {
        if (method.getStatusCode() == expectedResponseCode)
            return;
        if (method.getStatusCode() == HttpStatus.SC_UNAUTHORIZED)
            fail("please register EASY user to make the tests work. ID:" + DEPOSITOR.getUserName() + " with password: " + DEPOSITOR.getPassword());
        fail("Unexpected response code: " + method.getStatusLine().toString());
    }

    protected String getResponse(final HttpMethod method, final HttpClient client) throws IOException, HttpException
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

    protected static PostMethod createPostMethod(final RequestEntity request, final Boolean noOp, final Boolean verbose)
    {
        final PostMethod method = new PostMethod(URL + "deposit");
        method.setRequestEntity(request);
        if (noOp != null)
            method.addRequestHeader("X-No-Op", noOp.toString());
        if (noOp != null)
            method.addRequestHeader("X-Verbose", verbose.toString());
        return method;
    }

    protected static HttpClient createClient(final UsernamePasswordCredentials credentials, final Integer timeout)
    {
        final HttpClient client = new HttpClient();
        client.getState().setCredentials(AuthScope.ANY, credentials);
        if (timeout != null)
            client.getParams().setSoTimeout(timeout);
        return client;
    }

    protected static RequestEntity createRequest(final File file) throws FileNotFoundException
    {
        log.info("======== creating request with " + file);
        return new InputStreamRequestEntity(new FileInputStream(file));
    }

}
