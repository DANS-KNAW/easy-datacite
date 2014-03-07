package nl.knaw.dans.easy.rest.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Set;

import nl.knaw.dans.common.jibx.bean.JiBXJumpoffDmoMetadata;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.jumpoff.JumpoffDmo;
import nl.knaw.dans.common.lang.repo.jumpoff.MarkupUnit;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.JumpoffService;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

@SuppressWarnings("unchecked")
public class JumpoffResourceTest extends RestTest
{
    private JumpoffService jumpoffServiceMock;
    private JumpoffDmo jumpoffMock;

    @Before
    public void setUp() throws ServiceException
    {
        setUpServices();

        jumpoffMock = Mockito.mock(JumpoffDmo.class);
    }

    private void setUpServices()
    {
        Services services = new Services();

        jumpoffServiceMock = Mockito.mock(JumpoffService.class);
        services.setJumpoffService(jumpoffServiceMock);
    }

    @Test
    public void getJumpoff() throws ServiceException
    {
        when(jumpoffServiceMock.getJumpoffDmoFor(isA(EasyUser.class), isA(DmoStoreId.class))).thenReturn(jumpoffMock);
        MarkupUnit markup = new MarkupUnit("", "");
        markup.setHtml("<html></html>");
        when(jumpoffMock.getHtmlMarkup()).thenReturn(markup);

        WebResource resource = resource().path("dataset/easy-dataset:1/jumpoff");
        ClientResponse response = resource.head();

        assertEquals(200, response.getStatus());
        assertEquals(markup.getHtml(), resource.get(String.class));
    }

    @Test
    public void getNonExistentJumpoff() throws ServiceException
    {
        when(jumpoffServiceMock.getJumpoffDmoFor(isA(EasyUser.class), isA(DmoStoreId.class))).thenReturn(null);

        ClientResponse response = resource().path("dataset/easy-dataset:1/jumpoff").head();

        assertEquals(404, response.getStatus());
    }

    @Test
    public void getJumpoffInternalError() throws ServiceException
    {
        when(jumpoffServiceMock.getJumpoffDmoFor(isA(EasyUser.class), isA(DmoStoreId.class))).thenThrow(ServiceException.class);

        ClientResponse response = resource().path("dataset/easy-dataset:1/jumpoff").head();

        assertEquals(500, response.getStatus());
    }

    @Test
    public void optionsJumpoff() throws ServiceException
    {
        when(jumpoffServiceMock.getJumpoffDmoFor(isA(EasyUser.class), isA(DmoStoreId.class))).thenReturn(jumpoffMock);

        ClientResponse response = resource().path("dataset/easy-dataset:1/jumpoff").options(ClientResponse.class);
        Set<String> allowHeader = response.getAllow();

        assertTrue(allowHeader.contains("GET"));
        assertTrue(allowHeader.contains("OPTIONS"));
        assertTrue(allowHeader.contains("HEAD"));
    }

    @Test
    public void optionsNonExistentJumpoff() throws ServiceException
    {
        when(jumpoffServiceMock.getJumpoffDmoFor(isA(EasyUser.class), isA(DmoStoreId.class))).thenReturn(null);

        ClientResponse response = resource().path("dataset/easy-dataset:1/jumpoff").options(ClientResponse.class);

        assertEquals(404, response.getStatus());
    }

    @Test
    public void optionsJumpoffInternalServerError() throws ServiceException
    {
        when(jumpoffServiceMock.getJumpoffDmoFor(isA(EasyUser.class), isA(DmoStoreId.class))).thenThrow(ServiceException.class);

        ClientResponse response = resource().path("dataset/easy-dataset:1/jumpoff").options(ClientResponse.class);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void getJumpoffMetadata() throws ServiceException
    {
        when(jumpoffServiceMock.getJumpoffDmoFor(isA(EasyUser.class), isA(DmoStoreId.class))).thenReturn(jumpoffMock);
        when(jumpoffMock.getJumpoffDmoMetadata()).thenReturn(new JiBXJumpoffDmoMetadata());

        ClientResponse response = resource().path("dataset/easy-dataset:1/jumpoff/metadata").head();

        assertEquals(200, response.getStatus());
        verify(jumpoffMock, times(1)).getJumpoffDmoMetadata();
    }

    @Test
    public void getNonExistentJumpoffMetadata() throws ServiceException
    {
        when(jumpoffServiceMock.getJumpoffDmoFor(isA(EasyUser.class), isA(DmoStoreId.class))).thenReturn(null);

        ClientResponse response = resource().path("dataset/easy-dataset:1/jumpoff/metadata").head();

        assertEquals(404, response.getStatus());
    }

    @Test
    public void getJumpoffMetadataInternalServerError() throws ServiceException
    {
        when(jumpoffServiceMock.getJumpoffDmoFor(isA(EasyUser.class), isA(DmoStoreId.class))).thenThrow(ServiceException.class);

        ClientResponse response = resource().path("dataset/easy-dataset:1/jumpoff/metadata").head();

        assertEquals(500, response.getStatus());
    }

    @Test
    public void getJumpoffMetadataXmlSerializationError() throws ServiceException
    {
        when(jumpoffServiceMock.getJumpoffDmoFor(isA(EasyUser.class), isA(DmoStoreId.class))).thenThrow(XMLSerializationException.class);

        ClientResponse response = resource().path("dataset/easy-dataset:1/jumpoff/metadata").head();

        assertEquals(500, response.getStatus());
    }

    @Test
    public void optionsJumpoffMetadata() throws ServiceException
    {
        when(jumpoffServiceMock.getJumpoffDmoFor(isA(EasyUser.class), isA(DmoStoreId.class))).thenReturn(jumpoffMock);

        ClientResponse response = resource().path("dataset/easy-dataset:1/jumpoff/metadata").options(ClientResponse.class);
        Set<String> allowHeader = response.getAllow();

        assertTrue(allowHeader.contains("GET"));
        assertTrue(allowHeader.contains("OPTIONS"));
        assertTrue(allowHeader.contains("HEAD"));
    }

    @Test
    public void optionsNonExistentJumpoffMetadata() throws ServiceException
    {
        when(jumpoffServiceMock.getJumpoffDmoFor(isA(EasyUser.class), isA(DmoStoreId.class))).thenReturn(null);

        ClientResponse response = resource().path("dataset/easy-dataset:1/jumpoff/metadata").options(ClientResponse.class);

        assertEquals(404, response.getStatus());
    }

    @Test
    public void optionsJumpoffMetadataInternalServerError() throws ServiceException
    {
        when(jumpoffServiceMock.getJumpoffDmoFor(isA(EasyUser.class), isA(DmoStoreId.class))).thenThrow(ServiceException.class);

        ClientResponse response = resource().path("dataset/easy-dataset:1/jumpoff/metadata").options(ClientResponse.class);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void optionsJumpoffMetadataXmlSerializationError() throws ServiceException
    {
        when(jumpoffServiceMock.getJumpoffDmoFor(isA(EasyUser.class), isA(DmoStoreId.class))).thenThrow(XMLSerializationException.class);

        ClientResponse response = resource().path("dataset/easy-dataset:1/jumpoff/metadata").options(ClientResponse.class);

        assertEquals(500, response.getStatus());
    }

}
