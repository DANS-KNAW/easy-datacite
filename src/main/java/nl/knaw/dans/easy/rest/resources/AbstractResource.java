package nl.knaw.dans.easy.rest.resources;

import java.util.List;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

/**
 * Abstract class for resources. This class wraps HTTP request information and
 * methods that are injected by JAX-RS.
 * 
 * @author Georgi Khomeriki
 * @author Roshan Timal
 */
public abstract class AbstractResource
{
    /**
     * With each request the headers of that request are injected into the
     * requestHeaders parameter.
     */
    @Context
    private HttpHeaders requestHeaders;

    /**
     * With each request URI info is injected into the uriInfo parameter.
     */
    @Context
    private UriInfo uriInfo;

    /**
     * Checks whether the requested Media Type is acceptable.
     * 
     * @return True iff the requested Media Type is acceptable.
     */
    protected boolean isAcceptable()
    {
        return wantsXml() || wantsJson();
    }

    /**
     * Checks whether the requested Media Type indicates XML.
     * 
     * @return True iff the requested Media Type is XML.
     */
    protected boolean wantsXml()
    {
        List<MediaType> mediaTypes = getRequestHeaders().getAcceptableMediaTypes();
        return mediaTypes.isEmpty() || mediaTypes.contains(MediaType.TEXT_XML_TYPE) || mediaTypes.contains(MediaType.APPLICATION_XML_TYPE)
                || mediaTypes.contains(MediaType.APPLICATION_XHTML_XML_TYPE) || mediaTypes.contains(MediaType.TEXT_HTML_TYPE);
    }

    /**
     * Checks whether the requested Media Type indicates JSON.
     * 
     * @return True iff the requested Media Type is JSON.
     */
    protected boolean wantsJson()
    {
        List<MediaType> mediaTypes = getRequestHeaders().getAcceptableMediaTypes();
        return mediaTypes.contains(MediaType.APPLICATION_JSON_TYPE);
    }

    /**
     * Getter for the request headers.
     * 
     * @return The request headers.
     */
    protected HttpHeaders getRequestHeaders()
    {
        return requestHeaders;
    }

    /**
     * Getter for the request query parameters.
     * 
     * @return The query parameters.
     */
    protected MultivaluedMap<String, String> getQueryParameters()
    {
        return uriInfo.getQueryParameters();
    }

    /**
     * Setter for the request headers. This is practical for testing in
     * particular.
     * 
     * @param requestHeaders
     *            The new request headers.
     */
    protected void setRequestHeaders(HttpHeaders requestHeaders)
    {
        this.requestHeaders = requestHeaders;
    }

}
