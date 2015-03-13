package nl.knaw.dans.easy.util;

import java.io.IOException;
import java.net.URL;

import nl.knaw.dans.common.lang.service.exceptions.ServiceException;

/**
 * Interface to hide a (more complex, probably third-party) HTTP implementation, when all you need to do is some simple interactions like the ones below.
 */
public interface HttpClientFacade {

    public static class ClientException extends IOException {

        private static final long serialVersionUID = 1L;
        private final int statusCode;

        public ClientException(String reason, int statusCode) {
            super(reason);
            this.statusCode = statusCode;
        }

        public int getStatusCode() {
            return statusCode;
        }
    }

    /**
     * Posts a request.
     * 
     * @param url
     *        the URL of the resource to post to
     * @return the content of the response
     * @throws ClientException
     *         if the status code of the response was not 200 (OK)
     * @throws IOException
     */
    byte[] post(URL url) throws ClientException, IOException;

    /**
     * Posts <code>content</code> to a resource and returns the resulting status code.
     * 
     * @param url
     *        the URL of the resource to post to
     * @param content
     *        the content to post as a {@link String}
     * @return status code received from server
     * @throws ServiceException
     */
    int post(String url, String content) throws ServiceException;

    /**
     * Deletes a resource and resturns the resulting status code.
     * 
     * @param url
     *        the URL of the resource to delete
     * @return status code received from server
     */
    int delete(String url) throws ServiceException;
}
