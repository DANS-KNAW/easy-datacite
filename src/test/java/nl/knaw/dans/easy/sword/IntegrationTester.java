package nl.knaw.dans.easy.sword;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.BindException;

import nl.knaw.dans.easy.util.EasyHome;

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
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntegrationTester
{
    private static final String                      URL       = "http://localhost:" + Start.PORT + "/";
    private static final UsernamePasswordCredentials DEPOSITOR = new UsernamePasswordCredentials("depositor", "123456");
    private static final UsernamePasswordCredentials ANONYMOUS = new UsernamePasswordCredentials("anonymous", "password");
    private static final int                         MILLIS    = 1000;

    private static Server                            server;

    private static Logger                            log       = LoggerFactory.getLogger(EasySwordServer.class);

    @BeforeClass
    public static void start() throws Exception
    {
        if (EasyHome.getValue() == null)
            throw new Exception("Please specify the system property '" + EasyHome.EASY_HOME_KEY + "'");

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
        if (server !=null) {
            server.stop();
            server.join();
            log.debug("stopped server");
        }
    }

    @Test
    public void serviceDocument() throws Exception
    {
        final HttpMethod method = new GetMethod(URL + "servicedocument");
        execute(method, createClient(ANONYMOUS, null));

        if (method.getStatusCode() == HttpStatus.SC_OK)
            log.info("\n" + method.getResponseBodyAsString());
        else
            fail("Unexpected failure: " + method.getStatusLine().toString());
    }

    @Test
    public void depositProperZip() throws Exception
    {
        doAcceptedDeposit(createRequest(SubmitFixture.PROPER_ZIP), 15 * MILLIS);
    }

    private static void doAcceptedDeposit(final RequestEntity request, final int timeout) throws Exception
    {
        final PostMethod method = new PostMethod(URL + "deposit");
        method.setRequestEntity(request);
        execute(method, createClient(DEPOSITOR, timeout));

        if (method.getStatusCode() == HttpStatus.SC_ACCEPTED)
            log.info("\n" + method.getResponseBodyAsString());
        else if (method.getStatusCode() == HttpStatus.SC_UNAUTHORIZED)
            fail("please register EASY userID: " + DEPOSITOR.getUserName() + " with password: " + DEPOSITOR.getUserName());
        else
            fail("Unexpected failure: " + method.getStatusLine().toString());
    }

    private static HttpClient createClient(final UsernamePasswordCredentials credentials, final Integer timeout)
    {
        final HttpClient client = new HttpClient();
        client.getState().setCredentials(AuthScope.ANY, credentials);
        if (timeout != null)
            client.getParams().setSoTimeout(timeout);
        return client;
    }

    private static void execute(final HttpMethod method, final HttpClient client) throws IOException, HttpException
    {
        try
        {
            client.executeMethod(method);
        }
        finally
        {
            method.releaseConnection();
        }
    }

    private static RequestEntity createRequest(final String file) throws FileNotFoundException
    {
        return new InputStreamRequestEntity(new FileInputStream(new File(file)));
    }

    @SuppressWarnings("unused"/* TODO fix error "415 unsupported Media Type" */)
    private static RequestEntity createRequest(final String file, final boolean verbose, final boolean noOp) throws FileNotFoundException
    {
        final Part[] parts = {new FilePart(file, new File(file))};
        final HttpClientParams params = new HttpClientParams();
        params.setBooleanParameter("X-No-Op", noOp);
        params.setBooleanParameter("X-Verbose", verbose);
        // http://svn.apache.org/viewvc/httpcomponents/oac.hc3x/trunk/src/examples/
        // http://hc.apache.org/httpclient-3.x/
        assertFalse("not yet implemented", noOp);
        assertFalse("not yet implemented", verbose);
        return new MultipartRequestEntity(parts, params);
    }
}
