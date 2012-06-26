package nl.knaw.dans.easy.sword;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.BindException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
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

public class IntegrationTest
{
    private static final String URL    = "http://localhost:" + Start.PORT + "/";
    private static final int    MILLIS = 1000;
    private static Server       server;

    private static Logger       log    = LoggerFactory.getLogger(EasySwordServer.class);

    @BeforeClass
    public static void start() throws Exception
    {
        server = Start.createServer(Start.PORT, Start.SSL_PORT);
        try
        {
            server.start();
        }
        catch (BindException e)
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
        server.stop();
        server.join();
        log.debug("stopped server");
    }

    @Test
    public void serviceDocument() throws Exception
    {

        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("anonymous", "password");
        final HttpMethod post = new GetMethod(URL + "servicedocument");
        final HttpClient client = new HttpClient();
        client.getState().setCredentials(AuthScope.ANY, credentials);

        try
        {
            client.executeMethod(post);

            if (post.getStatusCode() == HttpStatus.SC_OK)
                log.info("\n" + post.getResponseBodyAsString());
            else
                fail("Start was launched? Unexpected failure: " + post.getStatusLine().toString());
        }
        finally
        {
            post.releaseConnection();
        }
    }

    @Test
    public void depositProperZip() throws Exception
    {
        doAcceptedDeposit(SubmitFixture.PROPER_ZIP, false, false, 15 * MILLIS);
    }

    private void doAcceptedDeposit(final String file, final boolean verbose, final boolean noOp, int timeout) throws FileNotFoundException, IOException,
            HttpException
    {
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("depositor", "123456");

        final FileInputStream input = new FileInputStream(new File(file));
        final InputStreamRequestEntity entity1 = new InputStreamRequestEntity(input);

        final Part[] parts = {new FilePart(file, new File(file))};
        HttpClientParams params = new HttpClientParams();
        params.setBooleanParameter("X-No-Op", noOp);
        params.setBooleanParameter("X-Verbose", verbose);
        @SuppressWarnings("unused"/* TODO fix error "415 unsupported Media Type" to use entity2 */)
        final MultipartRequestEntity entity2 = new MultipartRequestEntity(parts, params);
        // http://svn.apache.org/viewvc/httpcomponents/oac.hc3x/trunk/src/examples/
        // http://hc.apache.org/httpclient-3.x/
        assertFalse("not yet implemented", noOp);
        assertFalse("not yet implemented", verbose);

        final PostMethod post = new PostMethod(URL + "deposit");
        final HttpClient client = new HttpClient();
        client.getState().setCredentials(AuthScope.ANY, credentials);
        client.getParams().setSoTimeout(timeout);
        post.setRequestEntity(entity1);

        try
        {
            client.executeMethod(post);

            if (post.getStatusCode() == HttpStatus.SC_ACCEPTED)
                log.info(file + "\n" + post.getResponseBodyAsString());
            else if (post.getStatusCode() == HttpStatus.SC_UNAUTHORIZED)
                fail("please register EASY userID: " + credentials.getUserName() + " with password: " + credentials.getUserName());
            else
                fail("Start was launched? Unexpected failure: " + post.getStatusLine().toString());
        }
        finally
        {
            post.releaseConnection();
        }
    }
}
