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
import nl.knaw.dans.easy.domain.dataset.FileItemMetadataImpl;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.dataset.item.ItemOrder;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.dataset.item.filter.ItemFilters;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.servicelayer.services.ItemService;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class ThumbnailsResourceTest extends RestTest {
    private ItemService itemServiceMock;
    private DatasetService datasetServiceMock;

    @Before
    public void setUp() throws ServiceException {
        itemServiceMock = mock(ItemService.class);
        datasetServiceMock = mock(DatasetService.class);

        Services services = new Services();
        services.setItemService(itemServiceMock);
        services.setDatasetService(datasetServiceMock);

        setUpMocks();
    }

    private void setUpMocks() throws ServiceException {
        when(datasetServiceMock.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).thenReturn(new DatasetImpl("easy-dataset:1"));

        when(
                itemServiceMock.getFilesAndFolders(isA(EasyUser.class), isA(Dataset.class), isA(DmoStoreId.class), isA(Integer.class), isA(Integer.class),
                        (ItemOrder) isNull(), (ItemFilters) isNull())).thenReturn(new ArrayList<ItemVO>());
    }

    @Test
    public void getThumbnailIds() {
        WebResource resource = resource().path("dataset/easy-dataset:1/thumbnails");
        ClientResponse response = resource.get(ClientResponse.class);
        String entity = response.getEntity(String.class);

        assertEquals(200, response.getStatus());
        assertEquals("<thumbnails></thumbnails>", entity);
    }

    @Test
    public void getThumbsnailIdsNotFound() throws ServiceException {
        setUpException(ObjectNotAvailableException.class);

        WebResource resource = resource().path("dataset/easy-dataset:1/thumbnails");
        ClientResponse response = resource.get(ClientResponse.class);

        assertEquals(404, response.getStatus());
    }

    @SuppressWarnings("unchecked")
    private void setUpException(Class<? extends Throwable> t) throws ObjectNotAvailableException, CommonSecurityException, ServiceException {
        when(datasetServiceMock.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).thenThrow(t);
    }

    @Test
    public void getThumbsnailIdsNotAuthorized() throws ServiceException {
        setUpException(CommonSecurityException.class);

        WebResource resource = resource().path("dataset/easy-dataset:1/thumbnails");
        ClientResponse response = resource.get(ClientResponse.class);

        assertEquals(401, response.getStatus());
    }

    @Test
    public void getThumbsnailIdsInternalServerError() throws ServiceException {
        setUpException(ServiceException.class);

        WebResource resource = resource().path("dataset/easy-dataset:1/thumbnails");
        ClientResponse response = resource.get(ClientResponse.class);

        assertEquals(500, response.getStatus());
    }

    // TODO: un-ignore this after mocking the right methods!
    @Ignore
    @Test
    public void getThumbnail() throws ServiceException, MalformedURLException {
        setUpGetFileItem();
        setUpGetFilesAndFolders();

        WebResource resource = resource().path("dataset/easy-dataset:1/thumbnails/easy-file:1");
        ClientResponse response = resource.get(ClientResponse.class);

        assertEquals(200, response.getStatus());
    }

    private void setUpGetFileItem() throws ServiceException, MalformedURLException {
        FileItem file = mock(FileItem.class);
        when(file.getMimeType()).thenReturn(MediaType.TEXT_PLAIN);
        when(file.getSize()).thenReturn(4l);
        FileItemMetadataImpl fimi = new FileItemMetadataImpl(new DmoStoreId("easy-file:1")) {
            private static final long serialVersionUID = 1L;

            @Override
            public long getSize() {
                return 4l;
            }
        };
        when(file.getFileItemMetadata()).thenReturn(fimi);

        when(itemServiceMock.getFileItem(isA(EasyUser.class), isA(Dataset.class), isA(DmoStoreId.class))).thenReturn(file);

        URL url = setUpUrlMock();

        when(itemServiceMock.getFileContentURL(isA(EasyUser.class), isA(Dataset.class), isA(FileItem.class))).thenReturn(url);
    }

    @SuppressWarnings("unchecked")
    private void setUpGetFilesAndFolders() throws ServiceException {
        ArrayList<ItemVO> list = new ArrayList<ItemVO>();
        FolderItemVO folder = new FolderItemVO();
        folder.setName("thumbnails");
        list.add(folder);
        when(itemServiceMock.getFilesAndFolders(isA(EasyUser.class), isA(Dataset.class), isA(Collection.class))).thenReturn(list);
    }

    private URL setUpUrlMock() throws MalformedURLException {
        return new URL(new URL("http://www.gnu.org"), "spec", new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL u) throws IOException {
                return new URLConnection(u) {

                    @Override
                    public void connect() throws IOException {
                        // do nothing
                    }

                    @Override
                    public InputStream getInputStream() {
                        return new ByteArrayInputStream("test".getBytes());
                    }
                };
            }
        });
    }

    // TODO: un-ignore this after mocking the right methods!
    @Ignore
    @Test
    public void getThumbnailThatsNotAThumbnail() throws ServiceException, IOException {
        setUpGetFileItem();

        WebResource resource = resource().path("dataset/easy-dataset:1/thumbnails/easy-file:1");
        ClientResponse response = resource.get(ClientResponse.class);

        assertEquals(401, response.getStatus());
    }

    @Test
    public void getThumbnailNotFound() throws ServiceException {
        setUpException(ObjectNotAvailableException.class);

        WebResource resource = resource().path("dataset/easy-dataset:1/thumbnails/easy-file:1");
        ClientResponse response = resource.get(ClientResponse.class);

        assertEquals(404, response.getStatus());
    }

    @Test
    public void getThumbnailNotAuthorized() throws ServiceException {
        setUpException(CommonSecurityException.class);

        WebResource resource = resource().path("dataset/easy-dataset:1/thumbnails/easy-file:1");
        ClientResponse response = resource.get(ClientResponse.class);

        assertEquals(401, response.getStatus());
    }

    @Test
    public void getThumbnailInternalServerError() throws ServiceException {
        setUpException(ServiceException.class);

        WebResource resource = resource().path("dataset/easy-dataset:1/thumbnails/easy-file:1");
        ClientResponse response = resource.get(ClientResponse.class);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void getThumbnailIOException() throws ServiceException {
        setUpException(IOException.class);

        WebResource resource = resource().path("dataset/easy-dataset:1/thumbnails/easy-file:1");
        ClientResponse response = resource.get(ClientResponse.class);

        assertEquals(500, response.getStatus());
    }

}
