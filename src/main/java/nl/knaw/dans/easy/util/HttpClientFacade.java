package nl.knaw.dans.easy.util;

/**
 * Interface to hide a (more complex, probably third-party) HTTP implementation, when all you need to do
 * is some simple interactions like the ones below.
 */
public interface HttpClientFacade
{
    /**
     * Posts <code>content</code> to a resource and returns the resulting status code.
     * 
     * @param url
     *        the URL of the resource to post to
     * @param content
     *        the content to post as a {@link String}
     * @return status code received from server
     */
    int post(String url, String content);

    /**
     * Deletes a resource and resturns the resulting status code.
     * 
     * @param url
     *        the URL of the resource to delete
     * @return status code received from server
     */
    int delete(String url);
}
