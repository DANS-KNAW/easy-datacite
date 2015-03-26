package nl.knaw.dans.easy.util;

import static org.apache.http.impl.client.HttpClients.createDefault;

import java.io.IOException;
import java.net.URL;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * Implementation of {@link HttpClientFacade} that uses Apache HTTP Client.
 */
public class ApacheHttpClientFacade implements HttpClientFacade {

    @Override
    public byte[] post(URL url) throws ClientException, IOException {
        CloseableHttpClient httpClient = createDefault();
        try {
            CloseableHttpResponse response = httpClient.execute(new HttpPost(url.toString()));
            try {
                return getContent(response);
            }
            finally {
                response.close();
            }
        }
        finally {
            httpClient.close();
        }
    }

    private byte[] getContent(CloseableHttpResponse response) throws IOException, ClientException {
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        if (statusCode != HttpStatus.SC_OK)
            throw new ClientException(statusLine.getReasonPhrase(), statusCode);
        else
            return IOUtils.toByteArray(response.getEntity().getContent());
    }

    @Override
    public int post(String url, String content) throws ServiceException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);
        httppost.setEntity(createHttpEntity(content));
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
        }
        catch (ClientProtocolException e) {
            throw new ServiceException("Client Protocol Exception during HTTP exchange", e);
        }
        catch (IOException e) {
            throw new ServiceException("I/O Exception during HTTP exchange", e);
        }
        return response.getStatusLine().getStatusCode();
    }

    private HttpEntity createHttpEntity(String content) {
        return new StringEntity(content, "UTF-8");
    }

    @Override
    public int delete(String url) throws ServiceException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpDelete delete = new HttpDelete(url);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(delete);
        }
        catch (ClientProtocolException e) {
            throw new ServiceException("Client Protocol Exception during HTTP exchange", e);
        }
        catch (IOException e) {
            throw new ServiceException("I/O Exception during HTTP exchange", e);
        }
        return response.getStatusLine().getStatusCode();
    }
}
