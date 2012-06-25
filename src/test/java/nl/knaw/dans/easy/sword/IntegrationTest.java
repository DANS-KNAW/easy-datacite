package nl.knaw.dans.easy.sword;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;

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
        // TODO rewrite to replace HttpUnit by HttpClient
        final WebConversation webConversation = new WebConversation();
        webConversation.setAuthentication("SWORD", "", "");
        try
        {
            final WebResponse response = webConversation.getResponse("http://u:p@localhost:8083/servicedocument");
            log.debug(response.toString());
            log.debug(response.getResponseMessage());
            log.debug("\n" + response.getText());
            // log.debug(response.getContentType());
        }
        catch (final ConnectException e)
        {
            fail("please first launch Start");
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
                fail("Unexpected failure: " + post.getStatusLine().toString());
        }
        finally
        {
            post.releaseConnection();
        }
    }
}
