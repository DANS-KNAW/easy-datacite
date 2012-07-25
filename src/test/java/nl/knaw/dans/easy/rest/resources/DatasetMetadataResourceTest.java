package nl.knaw.dans.easy.rest.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;
import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.emd.EasyMetadata;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

@SuppressWarnings("unchecked")
public class DatasetMetadataResourceTest extends RestTest {
	private DatasetService datasetServiceMock;
	private Dataset datasetMock;
	private EasyMetadata metadataMock;
	private DublinCoreMetadata dcMetadataMock;
	
	@Before
	public void setUp() throws ServiceException {
		setUpServices();
		
        datasetMock = Mockito.mock(Dataset.class);
        metadataMock = Mockito.mock(EasyMetadata.class);
        dcMetadataMock = Mockito.mock(DublinCoreMetadata.class);
        
        when(datasetMock.getEasyMetadata()).thenReturn(metadataMock);
	}
	
	private void setUpServices() {
		Services services = new Services();
		
		datasetServiceMock = Mockito.mock(DatasetService.class);
		services.setDatasetService(datasetServiceMock);
	}
	
	@Test
    public void getMetadata() throws ServiceException, XMLSerializationException {
    	when(datasetServiceMock.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).thenReturn(datasetMock);
    	String metadataXml = "<metadata><title>TEST</title></metadata>";
    	when(metadataMock.asXMLString()).thenReturn(metadataXml);
    	
        WebResource webResource = resource().path("dataset/easy-dataset:1/metadata");
        
        String responseBody = webResource.get(String.class);
        
        verify(datasetMock, times(1)).getEasyMetadata();
        assertEquals(200, webResource.head().getStatus());
        assertEquals(metadataXml, responseBody);
    }
    
	@Test
    public void getNonExistentMetadata() throws ServiceException {
    	when(datasetServiceMock.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).thenThrow(ObjectNotAvailableException.class);
    	
    	ClientResponse response = resource().path("dataset/easy-dataset:1/metadata").head();
    	
    	assertEquals(404, response.getStatus());
    }
    
	@Test
    public void getMetadataUnauthorized() throws ServiceException {
    	when(datasetServiceMock.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).thenThrow(CommonSecurityException.class);
    	
    	ClientResponse response = resource().path("dataset/easy-dataset:1/metadata").head();
    	
    	assertEquals(401, response.getStatus());
    }
    
	@Test
    public void getMetadataInternalError() throws ServiceException {
    	when(datasetServiceMock.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).thenThrow(ServiceException.class);
    	
    	ClientResponse response = resource().path("dataset/easy-dataset:1/metadata").head();
    	
    	assertEquals(500, response.getStatus());
    }
	
	@Test
    public void getMetadataIllegalArgument() throws ServiceException {
    	when(datasetServiceMock.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).thenThrow(IllegalArgumentException.class);
    	
    	ClientResponse response = resource().path("dataset/easy-dataset:1/metadata").head();
    	
    	assertEquals(404, response.getStatus());
    }
	
	@Test
    public void getMetadataXmlSerializationProblem() throws ServiceException {
    	when(datasetServiceMock.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).thenThrow(XMLSerializationException.class);
    	
    	ClientResponse response = resource().path("dataset/easy-dataset:1/metadata").head();
    	
    	assertEquals(500, response.getStatus());
    }
	
	@Test
	public void headMetadata() throws ServiceException {
		when(datasetServiceMock.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).thenReturn(datasetMock);
		
		WebResource webResource = resource().path("dataset/easy-dataset:1/metadata");
	    ClientResponse response = webResource.head();
	    MultivaluedMap<String, String> headers = response.getHeaders();
	    
	    assertTrue(headers.containsKey("content-type"));
	    assertEquals(MediaType.APPLICATION_XML, headers.get("content-type").get(0));
	}
	
	@Test
	public void optionsMetadata() throws ServiceException {
		when(datasetServiceMock.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).thenReturn(datasetMock);
		
		WebResource webResource = resource().path("dataset/easy-dataset:1/metadata");
		ClientResponse response = webResource.options(ClientResponse.class);
		Set<String> allowHeader = response.getAllow();
		
	    assertTrue(allowHeader.contains("GET"));
	    assertTrue(allowHeader.contains("OPTIONS"));
	    assertTrue(allowHeader.contains("HEAD"));
	}
	
	@Test
    public void optionsMetadataNotAvailable() throws ServiceException {
    	when(datasetServiceMock.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).thenThrow(ObjectNotAvailableException.class);
    	
    	ClientResponse response = resource().path("dataset/easy-dataset:1/metadata").options(ClientResponse.class);
    	
    	assertEquals(404, response.getStatus());
    }
	
	@Test
    public void optionsMetadataNotAuthorized() throws ServiceException {
    	when(datasetServiceMock.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).thenThrow(CommonSecurityException.class);
    	
    	ClientResponse response = resource().path("dataset/easy-dataset:1/metadata").options(ClientResponse.class);
    	
    	assertEquals(401, response.getStatus());
    }
	
	@Test
    public void optionsMetadataInternalServerError() throws ServiceException {
    	when(datasetServiceMock.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).thenThrow(ServiceException.class);
    	
    	ClientResponse response = resource().path("dataset/easy-dataset:1/metadata").options(ClientResponse.class);
    	
    	assertEquals(500, response.getStatus());
    }
	
	@Test
    public void getDcMetadata() throws ServiceException, XMLSerializationException {
    	when(datasetServiceMock.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).thenReturn(datasetMock);
    	String metadataXml = "<dc:metadata><dc:title>TEST</dc:title></dc:metadata>";
    	when(metadataMock.getDublinCoreMetadata()).thenReturn(dcMetadataMock);
    	when(dcMetadataMock.asXMLString()).thenReturn(metadataXml);
    	
        WebResource webResource = resource().path("dataset/easy-dataset:1/dc-metadata");
        String responseBody = webResource.get(String.class);
        
        verify(datasetMock, times(1)).getEasyMetadata();
        assertEquals(200, webResource.head().getStatus());
        assertEquals(metadataXml, responseBody);
    }
    
	@Test
    public void getNonExistentDcMetadata() throws ServiceException {
    	when(datasetServiceMock.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).thenThrow(ObjectNotAvailableException.class);
    	
    	ClientResponse response = resource().path("dataset/easy-dataset:1/dc-metadata").head();
    	
    	assertEquals(404, response.getStatus());
    }
    
	@Test
    public void getDcMetadataUnauthorized() throws ServiceException {
    	when(datasetServiceMock.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).thenThrow(CommonSecurityException.class);
    	
    	ClientResponse response = resource().path("dataset/easy-dataset:1/dc-metadata").head();
    	
    	assertEquals(401, response.getStatus());
    }
    
	@Test
    public void getDcMetadataInternalError() throws ServiceException {
    	when(datasetServiceMock.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).thenThrow(ServiceException.class);
    	
    	ClientResponse response = resource().path("dataset/easy-dataset:1/dc-metadata").head();
    	
    	assertEquals(500, response.getStatus());
    }
	
	@Test
    public void getDcMetadataIllegalArgument() throws ServiceException {
    	when(datasetServiceMock.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).thenThrow(IllegalArgumentException.class);
    	
    	ClientResponse response = resource().path("dataset/easy-dataset:1/dc-metadata").head();
    	
    	assertEquals(404, response.getStatus());
    }
	
	@Test
    public void getDcMetadataXmlSerializationProblem() throws ServiceException {
    	when(datasetServiceMock.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).thenThrow(XMLSerializationException.class);
    	
    	ClientResponse response = resource().path("dataset/easy-dataset:1/dc-metadata").head();
    	
    	assertEquals(500, response.getStatus());
    }
	
	@Test
	public void headDcMetadata() throws ServiceException, XMLSerializationException {
		when(datasetServiceMock.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).thenReturn(datasetMock);
		String metadataXml = "<dc:metadata><dc:title>TEST</dc:title></dc:metadata>";
    	when(metadataMock.getDublinCoreMetadata()).thenReturn(dcMetadataMock);
    	when(dcMetadataMock.asXMLString()).thenReturn(metadataXml);
    	
		WebResource webResource = resource().path("dataset/easy-dataset:1/dc-metadata");
	    ClientResponse response = webResource.head();
	    MultivaluedMap<String, String> headers = response.getHeaders();
	    
	    assertTrue(headers.containsKey("content-type"));
	    assertEquals(MediaType.APPLICATION_XML, headers.get("content-type").get(0));
	}
	
	@Test
	public void optionsDcMetadata() throws ServiceException {
		when(datasetServiceMock.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).thenReturn(datasetMock);
		
		WebResource webResource = resource().path("dataset/easy-dataset:1/dc-metadata");
		ClientResponse response = webResource.options(ClientResponse.class);
		Set<String> allowHeader = response.getAllow();
		
	    assertTrue(allowHeader.contains("GET"));
	    assertTrue(allowHeader.contains("OPTIONS"));
	    assertTrue(allowHeader.contains("HEAD"));
	}
	
	@Test
    public void optionsDcMetadataNotAvailable() throws ServiceException {
    	when(datasetServiceMock.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).thenThrow(ObjectNotAvailableException.class);
    	
    	ClientResponse response = resource().path("dataset/easy-dataset:1/dc-metadata").options(ClientResponse.class);
    	
    	assertEquals(404, response.getStatus());
    }
	
	@Test
    public void optionsDcMetadataNotAuthorized() throws ServiceException {
    	when(datasetServiceMock.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).thenThrow(CommonSecurityException.class);
    	
    	ClientResponse response = resource().path("dataset/easy-dataset:1/dc-metadata").options(ClientResponse.class);
    	
    	assertEquals(401, response.getStatus());
    }
	
	@Test
    public void optionsDcMetadataInternalServerError() throws ServiceException {
    	when(datasetServiceMock.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).thenThrow(ServiceException.class);
    	
    	ClientResponse response = resource().path("dataset/easy-dataset:1/dc-metadata").options(ClientResponse.class);
    	
    	assertEquals(500, response.getStatus());
    }
	
}
