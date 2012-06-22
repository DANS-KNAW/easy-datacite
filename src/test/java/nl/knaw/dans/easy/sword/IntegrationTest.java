package nl.knaw.dans.easy.sword;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.net.ConnectException;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.purl.sword.base.Deposit;
import org.purl.sword.base.DepositResponse;
import org.purl.sword.server.DepositServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.javascript.JavaScript.Form;
import com.meterware.httpunit.protocol.UploadFileSpec;

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
        final WebConversation webConversation = new WebConversation();
        webConversation.setAuthentication("SWORD", "", "");
        try
        {
            final WebResponse response = webConversation.getResponse("http://localhost:8083/servicedocument");
            log.debug(response.toString());
            log.debug(response.getResponseMessage());
            log.debug("\n" + response.getText());
//            log.debug(response.getContentType());
        }
        catch (final ConnectException e)
        {
            fail("please first launch Start");
        }
    }

    @Ignore("")
    @Test
    public void deposit() throws Exception
    {
        final FileInputStream inputStream = new FileInputStream(new File(SubmitFixture.PROPER_ZIP));
        log.debug("available: "+inputStream.available());
        
        final PostMethodWebRequest request = new PostMethodWebRequest("http://localhost:8083/deposit",inputStream,"application/binary");
        
        /*
         * FIXME httpunit might not support "Expect: 100-Continue", SWORD seems to use it. This results in an IOException "Bad file descriptor"
         * http://git.661346.n2.nabble.com/PATCH-smart-http-Don-t-use-Expect-100-Continue-td6028355.html
         */
        
        final WebConversation webConversation = new WebConversation();
        webConversation.setAuthentication("SWORD", "", "");
        try
        {
            final WebResponse response = webConversation.getResponse(request);
            log.debug(response.toString());
            log.debug(response.getResponseMessage());
            log.debug("\n" + response.getText());
        }
        catch (final ConnectException e)
        {
            fail("please first launch Start");
        }
    }
}
