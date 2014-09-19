package nl.knaw.dans.easy.rest.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.stream.FactoryConfigurationError;

import org.junit.Before;
import org.junit.Test;

public class ResourceResponseTest {
    private Resource resource;
    private HttpHeaders requestHeadersMock;
    private static final String bodyXML = "<xml>foobar</xml>";

    @Before
    public void setUp() {
        resource = new Resource();
        requestHeadersMock = mock(HttpHeaders.class);
        resource.setRequestHeaders(requestHeadersMock);
    }

    @Test
    public void testResponseNoAcceptHeader() {
        when(requestHeadersMock.getRequestHeader(isA(String.class))).thenReturn(new ArrayList<String>());
        Response response = resource.responseXmlOrJson(bodyXML.getBytes());
        assertEquals(200, response.getStatus());
        assertEquals(bodyXML, response.getEntity());
        assertContentType(MediaType.APPLICATION_XML_TYPE, response);
    }

    private void assertContentType(MediaType expectedType, Response response) {
        assertTrue(response.getMetadata().get("Content-Type").contains(expectedType));
    }

    @Test
    public void testResponseWrongAcceptHeader() {
        ArrayList<MediaType> acceptHeaders = new ArrayList<MediaType>();
        acceptHeaders.add(MediaType.APPLICATION_OCTET_STREAM_TYPE);
        when(requestHeadersMock.getAcceptableMediaTypes()).thenReturn(acceptHeaders);
        Response response = resource.responseXmlOrJson(bodyXML);
        assertEquals(406, response.getStatus());
    }

    @Test
    public void testWantsTextXml() {
        ArrayList<MediaType> acceptHeaders = new ArrayList<MediaType>();
        acceptHeaders.add(MediaType.TEXT_XML_TYPE);
        when(requestHeadersMock.getAcceptableMediaTypes()).thenReturn(acceptHeaders);
        Response response = resource.responseXmlOrJson(bodyXML);
        assertEquals(200, response.getStatus());
        assertContentType(MediaType.APPLICATION_XML_TYPE, response);
    }

    @Test
    public void testWantsApplicationXml() {
        ArrayList<MediaType> acceptHeaders = new ArrayList<MediaType>();
        acceptHeaders.add(MediaType.APPLICATION_XML_TYPE);
        when(requestHeadersMock.getAcceptableMediaTypes()).thenReturn(acceptHeaders);
        Response response = resource.responseXmlOrJson(bodyXML);
        assertEquals(200, response.getStatus());
        assertContentType(MediaType.APPLICATION_XML_TYPE, response);
    }

    @Test
    public void testWantsJson() {
        ArrayList<MediaType> acceptHeaders = new ArrayList<MediaType>();
        acceptHeaders.add(MediaType.APPLICATION_JSON_TYPE);
        when(requestHeadersMock.getAcceptableMediaTypes()).thenReturn(acceptHeaders);
        Response response = resource.responseXmlOrJson(bodyXML);
        assertEquals(200, response.getStatus());
        assertContentType(MediaType.APPLICATION_JSON_TYPE, response);
    }

    @Test
    public void testWantsJsonWrongXML() {
        ArrayList<MediaType> acceptHeaders = new ArrayList<MediaType>();
        acceptHeaders.add(MediaType.APPLICATION_JSON_TYPE);
        when(requestHeadersMock.getAcceptableMediaTypes()).thenReturn(acceptHeaders);
        Response response = resource.responseXmlOrJson("foobar");
        assertEquals(500, response.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void responseXmlOrJsonIOException() {
        when(requestHeadersMock.getAcceptableMediaTypes()).thenThrow(IOException.class);
        Response response = resource.responseXmlOrJson(bodyXML);
        assertEquals(500, response.getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void responseXmlOrJsonFactoryConfigurationException() {
        when(requestHeadersMock.getAcceptableMediaTypes()).thenThrow(FactoryConfigurationError.class);
        Response response = resource.responseXmlOrJson(bodyXML);
        assertEquals(500, response.getStatus());
    }

    @Test
    public void optionsResponseWantsXml() {
        ArrayList<MediaType> acceptHeaders = new ArrayList<MediaType>();
        acceptHeaders.add(MediaType.APPLICATION_XHTML_XML_TYPE);
        when(requestHeadersMock.getAcceptableMediaTypes()).thenReturn(acceptHeaders);
        Response response = resource.optionsResponse();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void optionsResponseWantsJson() {
        ArrayList<MediaType> acceptHeaders = new ArrayList<MediaType>();
        acceptHeaders.add(MediaType.APPLICATION_JSON_TYPE);
        when(requestHeadersMock.getAcceptableMediaTypes()).thenReturn(acceptHeaders);
        Response response = resource.optionsResponse();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void optionsResponseNotAcceptable() {
        ArrayList<MediaType> acceptHeaders = new ArrayList<MediaType>();
        acceptHeaders.add(MediaType.APPLICATION_OCTET_STREAM_TYPE);
        when(requestHeadersMock.getAcceptableMediaTypes()).thenReturn(acceptHeaders);
        Response response = resource.optionsResponse();
        assertEquals(406, response.getStatus());
    }

    @Test
    public void simpleResponseOnlyCode() {
        Response response = resource.simpleResponse(200);
        assertEquals(200, response.getStatus());
    }

}
