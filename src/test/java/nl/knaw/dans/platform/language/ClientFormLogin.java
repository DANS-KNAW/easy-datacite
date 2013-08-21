package nl.knaw.dans.platform.language;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

/**
 * A example that demonstrates how HttpClient APIs can be used to perform
 * form-based logon.
 */
public class ClientFormLogin {

    public static void main(String[] args) throws Exception {

        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
         // Create a local instance of cookie store
            CookieStore cookieStore = new BasicCookieStore();
            
         // Create local HTTP context
            HttpContext localContext = new BasicHttpContext();
            // Bind custom cookie store to the local context
            localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
            
            // get the login form
            HttpGet httpget = new HttpGet("https://tools.sikb.nl/sikb0102");

            HttpResponse response = httpclient.execute(httpget, localContext);
            HttpEntity entity = response.getEntity();

            System.out.println("------------------response 1-------------");
            System.out.println("Login form get: " + response.getStatusLine());
            
            //System.out.println(IOUtils.toString(entity.getContent()));
            EntityUtils.consume(entity);

            System.out.println("Initial set of cookies:");
            List<Cookie> cookies = httpclient.getCookieStore().getCookies();
            if (cookies.isEmpty()) {
                System.out.println("None");
            } else {
                for (int i = 0; i < cookies.size(); i++) {
                    System.out.println("- " + cookies.get(i).toString());
                }
            }
            
            HttpPost httpost = new HttpPost(
                    "https://tools.sikb.nl/sikb0102/Account/LogOn?ReturnUrl=%2fsikb0102%2fValidation%2fValidate");


            List <NameValuePair> nvps = new ArrayList <NameValuePair>();
            nvps.add(new BasicNameValuePair("UserName", "bergh"));
            nvps.add(new BasicNameValuePair("Password", "cC!XzlKK"));

            httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

            response = httpclient.execute(httpost, localContext);
            int status = response.getStatusLine().getStatusCode();
            
            entity = response.getEntity();

            System.out.println("------------------response 2-------------");
            System.out.println("Login form post: " + response.getStatusLine());
            
            //System.out.println(IOUtils.toString(entity.getContent()));
            EntityUtils.consume(entity);

            System.out.println("Post logon cookies:");
            cookies = httpclient.getCookieStore().getCookies();
            if (cookies.isEmpty()) {
                System.out.println("None");
            } else {
                for (int i = 0; i < cookies.size(); i++) {
                    System.out.println("- " + cookies.get(i).toString());
                }
            }
            
            if (status == 302)
            {
//                httpget = new HttpGet("https://tools.sikb.nl/sikb0102/Validation/Validate");
//                response = httpclient.execute(httpget, localContext);
//                entity = response.getEntity();
//
//                System.out.println("------------------response 3-------------");
//                System.out.println("validation form get: " + response.getStatusLine());
//                System.out.println(IOUtils.toString(entity.getContent()));
//                EntityUtils.consume(entity);
                
                //
                httpost = new HttpPost("https://tools.sikb.nl/sikb0102/Validation/Validate");
                FileBody bin = new FileBody(new File("src/test/resources/test-files/pakbon_valid.xml"));
                StringBody comment = new StringBody("A binary file of some kind");

                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("File", bin);
                reqEntity.addPart("comment", comment);

                httpost.setEntity(reqEntity);
                
                response = httpclient.execute(httpost, localContext);
                entity = response.getEntity();

                System.out.println("------------------response 4-------------");
                System.out.println("validation form post: " + response.getStatusLine());
                
                System.out.println(IOUtils.toString(entity.getContent()));
                EntityUtils.consume(entity);
                
            }
            else
            {
                System.err.println("status = " + status);
            }
            

        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
    }

}
