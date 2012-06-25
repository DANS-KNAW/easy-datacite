package nl.knaw.dans.easy.sword;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntegrationTest
{
    private static Logger log = LoggerFactory.getLogger(EasySwordServer.class);

    @BeforeClass
    public static void start() throws Exception
    {
        // TODO what if already launched and how to stop it?
        // Start.main(new String[] {});
        // Thread.sleep(9000);
        // http://httpunit.sourceforge.net/doc/servletunit-intro.html
    }

    @Test
    public void serviceDocument() throws Exception
    {

        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("anonymous", "password");
        final HttpMethod post = new GetMethod("http://localhost:8083/servicedocument");
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
        doAcceptedDeposit(SubmitFixture.PROPER_ZIP, false, false);
    }

    private void doAcceptedDeposit(final String file, final boolean verbose, final boolean noOp) throws FileNotFoundException, IOException, HttpException
    {
        @SuppressWarnings("unused")
        final NameValuePair[] parametersBody = new NameValuePair[] {new NameValuePair("noOp", "" + noOp), new NameValuePair("verbose", "" + verbose)};
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("depositor", "123456");
        final FileInputStream input = new FileInputStream(new File(file));
        final InputStreamRequestEntity entity = new InputStreamRequestEntity(input);
        final PostMethod post = new PostMethod("http://localhost:8083/deposit");
        final HttpClient client = new HttpClient();
        client.getState().setCredentials(AuthScope.ANY, credentials);
        post.setRequestEntity(entity);

        // TODO post.setRequestBody(parametersBody);
        // http://svn.apache.org/viewvc/httpcomponents/oac.hc3x/trunk/src/examples/
        // http://hc.apache.org/httpclient-3.x/
        assertFalse("not yet implemented", noOp);
        assertFalse("not yet implemented", verbose);

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
