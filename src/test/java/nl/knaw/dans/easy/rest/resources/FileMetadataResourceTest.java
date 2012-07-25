package nl.knaw.dans.easy.rest.resources;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import nl.knaw.dans.common.jibx.bean.JiBXDublinCoreMetadata;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.MetadataUnit;
import nl.knaw.dans.common.lang.repo.bean.DublinCoreMetadata;
import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.common.lang.xml.XMLSerializationException;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.dataset.FileItemMetadataImpl;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.servicelayer.services.ItemService;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class FileMetadataResourceTest extends RestTest {
	private DatasetService datasetServiceMock;
	private ItemService itemServiceMock;
	private FileItem fileItemMock;

	@Before
	public void setUp() {
		Services services = new Services();
		itemServiceMock = mock(ItemService.class);
		services.setItemService(itemServiceMock);
		datasetServiceMock = mock(DatasetService.class);
		services.setDatasetService(datasetServiceMock);
	}

	private void setUpServiceMethods() throws ObjectNotAvailableException,
			CommonSecurityException, ServiceException {
		when(
				datasetServiceMock.getDataset(isA(EasyUser.class),
						isA(DmoStoreId.class))).thenReturn(
				new DatasetImpl("easy-dataset:1"));

		when(
				itemServiceMock.getFileItemByPath(isA(EasyUser.class),
						isA(Dataset.class), isA(String.class))).thenReturn(
				fileItemMock);
	}

	private void setUpFileItem() {
		fileItemMock = mock(FileItem.class);
		ArrayList<MetadataUnit> metadata = new ArrayList<MetadataUnit>();
		metadata.add(new FileItemMetadataImpl(new DmoStoreId("easy-file:1")));
		metadata.add(new JiBXDublinCoreMetadata(DublinCoreMetadata.UNIT_ID));
		when(fileItemMock.getMetadataUnits()).thenReturn(metadata);
	}

	@Test
	public void getFileItemMetadataByPath() throws ObjectNotAvailableException,
			CommonSecurityException, ServiceException {
		setUpFileItem();
		setUpServiceMethods();

		WebResource webResource = resource().path(
				"dataset/easy-dataset:1/file-metadata/folder/file.txt");
		ClientResponse response = webResource.get(ClientResponse.class);

		assertEquals(200, response.getStatus());
	}

	@Test
	public void getFileItemMetadataNotAvailable()
			throws ObjectNotAvailableException, CommonSecurityException,
			ServiceException {
		setUpFileItemWithNoMetadata();
		setUpServiceMethods();

		WebResource webResource = resource().path(
				"dataset/easy-dataset:1/file-metadata/folder/file.txt");
		ClientResponse response = webResource.get(ClientResponse.class);

		assertEquals(404, response.getStatus());
	}
	
	@Test
	public void getDcFileItemMetadataByPath() throws ObjectNotAvailableException,
			CommonSecurityException, ServiceException {
		setUpFileItem();
		setUpServiceMethods();

		WebResource webResource = resource().path(
				"dataset/easy-dataset:1/dc-file-metadata/folder/file.txt");
		ClientResponse response = webResource.get(ClientResponse.class);

		assertEquals(200, response.getStatus());
	}
	
	private void setUpFileItemWithNoMetadata() {
		fileItemMock = mock(FileItem.class);
		when(fileItemMock.getMetadataUnits()).thenReturn(new ArrayList<MetadataUnit>());
	}
	
	@Test
	public void getFileItemMetadataNotAvailableWithException()
			throws ObjectNotAvailableException, CommonSecurityException,
			ServiceException {
		setUpException(ObjectNotAvailableException.class);

		WebResource webResource = resource().path(
				"dataset/easy-dataset:1/file-metadata/folder/file.txt");
		ClientResponse response = webResource.get(ClientResponse.class);

		assertEquals(404, response.getStatus());
	}

	@SuppressWarnings("unchecked")
	private void setUpException(Class<? extends Throwable> t)
			throws ObjectNotAvailableException, CommonSecurityException,
			ServiceException {
		when(
				datasetServiceMock.getDataset(isA(EasyUser.class),
						isA(DmoStoreId.class))).thenThrow(t);
	}

	@Test
	public void getFileItemMetadataNotFound()
			throws ObjectNotAvailableException, CommonSecurityException,
			ServiceException {
		setUpException(ObjectNotAvailableException.class);

		WebResource webResource = resource().path(
				"dataset/easy-dataset:1/file-metadata/folder/file.txt");
		ClientResponse response = webResource.get(ClientResponse.class);

		assertEquals(404, response.getStatus());
	}
	
	@Test
	public void getFileItemMetadataNotAuthorized()
			throws ObjectNotAvailableException, CommonSecurityException,
			ServiceException {
		setUpException(CommonSecurityException.class);

		WebResource webResource = resource().path(
				"dataset/easy-dataset:1/file-metadata/folder/file.txt");
		ClientResponse response = webResource.get(ClientResponse.class);

		assertEquals(401, response.getStatus());
	}
	
	@Test
	public void getFileItemMetadataXmlSerializationException()
			throws ObjectNotAvailableException, CommonSecurityException,
			ServiceException {
		setUpException(XMLSerializationException.class);

		WebResource webResource = resource().path(
				"dataset/easy-dataset:1/file-metadata/folder/file.txt");
		ClientResponse response = webResource.get(ClientResponse.class);

		assertEquals(500, response.getStatus());
	}
	
	@Test
	public void getFileItemMetadataInternalServerError()
			throws ObjectNotAvailableException, CommonSecurityException,
			ServiceException {
		setUpException(ServiceException.class);

		WebResource webResource = resource().path(
				"dataset/easy-dataset:1/file-metadata/folder/file.txt");
		ClientResponse response = webResource.get(ClientResponse.class);

		assertEquals(500, response.getStatus());
	}
	
}
