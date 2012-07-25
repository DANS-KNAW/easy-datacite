package nl.knaw.dans.easy.rest.resources;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.core.MediaType;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.dataset.FileItemImpl;
import nl.knaw.dans.easy.domain.dataset.FolderItemImpl;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.dataset.item.ItemOrder;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.dataset.item.filter.ItemFilters;
import nl.knaw.dans.easy.domain.download.ZipFileContentWrapper;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.servicelayer.services.ItemService;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;

@SuppressWarnings("unchecked")
public class DatasetSpecificDataTest extends RestTest {
	private DatasetService datasetServiceMock;
	private ItemService itemServiceMock;

	@Before
	public void setUp() {
		Services services = new Services();

		datasetServiceMock = mock(DatasetService.class);
		itemServiceMock = mock(ItemService.class);

		services.setDatasetService(datasetServiceMock);
		services.setItemService(itemServiceMock);
	}

	private void setUpServiceMethods() throws ServiceException {
		ArrayList<ItemVO> items = new ArrayList<ItemVO>();
		FileItemVO file = new FileItemVO();
		file.setSid("easy-file:1");
		items.add(file);
		FolderItemVO folder = new FolderItemVO();
		folder.setSid("easy-folder:1");
		items.add(folder);

		when(
				datasetServiceMock.getDataset(isA(EasyUser.class),
						isA(DmoStoreId.class))).thenReturn(
				new DatasetImpl("easy-dataset:1"));

		when(
				itemServiceMock.getFilesAndFolders(isA(EasyUser.class),
						isA(Dataset.class), isA(DmoStoreId.class),
						isA(Integer.class), isA(Integer.class),
						(ItemOrder) isNull(), (ItemFilters) isNull()))
				.thenReturn(items);

		when(
				itemServiceMock.getZippedContent(isA(EasyUser.class),
						isA(Dataset.class), isA(Collection.class))).thenReturn(
				new ZipFileContentWrapper());

	}

	@Test
	public void getSpecificDataWithPathToFolder() throws ServiceException {
		setUpServiceMethods();
		setUpGetFolderItemByPath();

		ClientResponse response = resource().path(
				"dataset/easy-dataset:1/data/root/folder").get(
				ClientResponse.class);

		assertEquals(200, response.getStatus());
	}

	private void setUpGetFolderItemByPath() throws ServiceException {
		FolderItemImpl folder = new FolderItemImpl("easy-folder:2");
		when(
				itemServiceMock.getFolderItemByPath(isA(EasyUser.class),
						isA(Dataset.class), isA(String.class))).thenReturn(
				folder);
	}

	@Test
	public void getSpecificDataWithPathToFile() throws ServiceException,
			MalformedURLException {
		setUpServiceMethods();
		setUpGetFolderItemByPathException();
		setUpGetFileItemByPath();

		ClientResponse response = resource().path(
				"dataset/easy-dataset:1/data/root/file.txt").get(
				ClientResponse.class);

		assertEquals(200, response.getStatus());
	}

	private void setUpGetFolderItemByPathException() throws ServiceException {
		when(
				itemServiceMock.getFolderItemByPath(isA(EasyUser.class),
						isA(Dataset.class), isA(String.class))).thenThrow(
				ServiceException.class);
	}

	private void setUpGetFileItemByPath() throws ServiceException,
			MalformedURLException {
		FileItemImpl file = new FileItemImpl("easy-file:1");
		file.setMimeType(MediaType.TEXT_PLAIN);
		file.setSize(4);
		when(
				itemServiceMock.getFileItemByPath(isA(EasyUser.class),
						isA(Dataset.class), isA(String.class)))
				.thenReturn(file);

		URL url = setUpUrlMock();

		when(
				itemServiceMock.getFileContentURL(isA(EasyUser.class),
						isA(Dataset.class), isA(FileItem.class))).thenReturn(
				url);
	}

	private URL setUpUrlMock() throws MalformedURLException {
		return new URL(new URL("http://www.gnu.org"), "spec",
				new URLStreamHandler() {
					@Override
					protected URLConnection openConnection(URL u)
							throws IOException {
						return new URLConnection(u) {

							@Override
							public void connect() throws IOException {
								// do nothing
							}
							
							@Override
							public InputStream getInputStream() {
								return new ByteArrayInputStream(
										"test".getBytes());
							}
						};
					}
				});
	}

	@Test
	public void getSpecificDataWithPathNotAuthorized() throws ServiceException {
		setException(CommonSecurityException.class);

		ClientResponse response = resource().path(
				"dataset/easy-dataset:1/data/root/folder").get(
				ClientResponse.class);

		assertEquals(401, response.getStatus());
	}

	private void setException(Class<? extends Throwable> t)
			throws ServiceException {
		when(
				datasetServiceMock.getDataset(isA(EasyUser.class),
						isA(DmoStoreId.class))).thenThrow(t);

	}

	@Test
	public void getSpecificDataWithPathInternalServerError()
			throws ServiceException {
		setException(ServiceException.class);

		ClientResponse response = resource().path(
				"dataset/easy-dataset:1/data/root/folder").get(
				ClientResponse.class);

		assertEquals(500, response.getStatus());
	}

	@Test
	public void getSpecificDataWithPathNotAvailable() throws ServiceException {
		setUpServiceMethods();
		setUpGetFolderItemByPathException();
		when(
				itemServiceMock.getFileItemByPath(isA(EasyUser.class),
						isA(Dataset.class), isA(String.class))).thenThrow(
				ObjectNotAvailableException.class);

		ClientResponse response = resource().path(
				"dataset/easy-dataset:1/data/root/folder").get(
				ClientResponse.class);

		assertEquals(404, response.getStatus());
	}

	@Test
	public void getSpecificDataWithPathIOProblem() throws ServiceException {
		setUpServiceMethods();
		setUpGetFolderItemByPathException();
		when(
				itemServiceMock.getFileItemByPath(isA(EasyUser.class),
						isA(Dataset.class), isA(String.class))).thenThrow(
				IOException.class);

		ClientResponse response = resource().path(
				"dataset/easy-dataset:1/data/root/folder").get(
				ClientResponse.class);

		assertEquals(500, response.getStatus());
	}

	@Test
	public void getSpecificFileWithId() throws ServiceException,
			MalformedURLException {
		setUpServiceMethods();
		setUpGetFileItem();

		ClientResponse response = resource().path(
				"dataset/easy-dataset:1/data/easy-file:1").get(
				ClientResponse.class);

		assertEquals(200, response.getStatus());
	}

	private void setUpGetFileItem() throws ServiceException,
			MalformedURLException {
		FileItemImpl file = new FileItemImpl("easy-file:1");
		file.setMimeType(MediaType.TEXT_PLAIN);
		file.setSize(4);

		when(
				itemServiceMock.getFileItem(isA(EasyUser.class),
						isA(Dataset.class), isA(DmoStoreId.class))).thenReturn(
				file);

		when(
				itemServiceMock.getFileContentURL(isA(EasyUser.class),
						isA(Dataset.class), isA(FileItem.class))).thenReturn(
				setUpUrlMock());
	}

	@Test
	public void getSpecificFileWithIdNotAuthorized() throws ServiceException {
		setException(CommonSecurityException.class);

		ClientResponse response = resource().path(
				"dataset/easy-dataset:1/data/easy-file:1").get(
				ClientResponse.class);

		assertEquals(401, response.getStatus());
	}

	@Test
	public void getSpecificFileWithIdInternalServerError()
			throws ServiceException {
		setException(ServiceException.class);

		ClientResponse response = resource().path(
				"dataset/easy-dataset:1/data/easy-file:1").get(
				ClientResponse.class);

		assertEquals(500, response.getStatus());
	}

	@Test
	public void getSpecificFileWithIdIOProblem() throws ServiceException {
		setException(IOException.class);

		ClientResponse response = resource().path(
				"dataset/easy-dataset:1/data/easy-file:1").get(
				ClientResponse.class);

		assertEquals(500, response.getStatus());
	}

	@Test
	public void getSpecificFolderWithId() throws ServiceException {
		setUpServiceMethods();

		ClientResponse response = resource().path(
				"dataset/easy-dataset:1/data/easy-folder:1").get(
				ClientResponse.class);

		assertEquals(200, response.getStatus());
	}

	@Test
	public void getSpecificFolderWithIdNotAuthorized() throws ServiceException {
		setException(CommonSecurityException.class);

		ClientResponse response = resource().path(
				"dataset/easy-dataset:1/data/easy-folder:1").get(
				ClientResponse.class);

		assertEquals(401, response.getStatus());
	}

	@Test
	public void getSpecificFolderWithIdInternalServerError()
			throws ServiceException {
		setException(ServiceException.class);

		ClientResponse response = resource().path(
				"dataset/easy-dataset:1/data/easy-folder:1").get(
				ClientResponse.class);

		assertEquals(500, response.getStatus());
	}

}
