package nl.knaw.dans.platform.language;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class Spar
{

    public static void main(String[] args) throws Exception
    {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        try
        {
            httpclient.getCredentialsProvider().setCredentials(new AuthScope("tools.sikb.nl", 80), new UsernamePasswordCredentials("bergh", "cC!XzlKK"));

            HttpGet httpget = new HttpGet("https://tools.sikb.nl/sikb0102/Validation/Validate");

            System.out.println("executing request" + httpget.getRequestLine());
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();

            System.out.println("----------------------------------------");
            System.out.println(response.getStatusLine());
            if (entity != null)
            {
                System.out.println("Response content length: " + entity.getContentLength());
                System.out.println(IOUtils.toString(entity.getContent()));
            }
            EntityUtils.consume(entity);
        }
        finally
        {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
    }

}
