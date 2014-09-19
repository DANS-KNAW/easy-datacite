package nl.knaw.dans.easy.rest.resources;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.dataset.item.ItemOrder;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.dataset.item.filter.ItemFilters;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.servicelayer.services.ItemService;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class FileTreeResourceTest extends RestTest {
    private ItemService itemServiceMock;
    private DatasetService datasetServiceMock;

    @Before
    public void setUp() {
        Services services = new Services();
        itemServiceMock = mock(ItemService.class);
        services.setItemService(itemServiceMock);
        datasetServiceMock = mock(DatasetService.class);
        services.setDatasetService(datasetServiceMock);
    }

    private void setUpServiceMethods() throws ObjectNotAvailableException, CommonSecurityException, ServiceException {
        when(datasetServiceMock.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).thenReturn(new DatasetImpl("easy-dataset:1"));

        when(
                itemServiceMock.getFilesAndFolders(isA(EasyUser.class), isA(Dataset.class), isA(DmoStoreId.class), isA(Integer.class), isA(Integer.class),
                        (ItemOrder) isNull(), (ItemFilters) isNull())).thenReturn(new ArrayList<ItemVO>());

    }

    @Test
    public void getTreeRoots() throws ObjectNotAvailableException, CommonSecurityException, ServiceException {
        setUpServiceMethods();

        WebResource resource = resource().path("dataset/easy-dataset:1/filetree");
        ClientResponse response = resource.get(ClientResponse.class);

        assertEquals(200, response.getStatus());
        assertServiceMethods();
    }

    private void assertServiceMethods() throws ServiceException {
        verify(datasetServiceMock, times(1)).getDataset(isA(EasyUser.class), isA(DmoStoreId.class));

        verify(itemServiceMock, times(1)).getFilesAndFolders(isA(EasyUser.class), isA(Dataset.class), isA(DmoStoreId.class), isA(Integer.class),
                isA(Integer.class), (ItemOrder) isNull(), (ItemFilters) isNull());
    }

    @Test
    public void getTreeRootsNotFound() throws ObjectNotAvailableException, CommonSecurityException, ServiceException {
        setUpException(ObjectNotAvailableException.class);

        WebResource resource = resource().path("dataset/easy-dataset:1/filetree");
        ClientResponse response = resource.get(ClientResponse.class);

        assertEquals(404, response.getStatus());
    }

    @SuppressWarnings("unchecked")
    private void setUpException(Class<? extends Throwable> t) throws ObjectNotAvailableException, CommonSecurityException, ServiceException {
        when(datasetServiceMock.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).thenThrow(t);
    }

    @Test
    public void getTreeRootsNotAuthorized() throws ObjectNotAvailableException, CommonSecurityException, ServiceException {
        setUpException(CommonSecurityException.class);

        WebResource resource = resource().path("dataset/easy-dataset:1/filetree");
        ClientResponse response = resource.get(ClientResponse.class);

        assertEquals(401, response.getStatus());
    }

    @Test
    public void getTreeRootsInternalServerError() throws ObjectNotAvailableException, CommonSecurityException, ServiceException {
        setUpException(ServiceException.class);

        WebResource resource = resource().path("dataset/easy-dataset:1/filetree");
        ClientResponse response = resource.get(ClientResponse.class);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void getFolderSubTree() throws ObjectNotAvailableException, CommonSecurityException, ServiceException {
        setUpServiceMethods();

        WebResource resource = resource().path("dataset/easy-dataset:1/filetree/easy-folder:1");
        ClientResponse response = resource.get(ClientResponse.class);

        assertEquals(200, response.getStatus());
        assertServiceMethods();
    }

    @Test
    public void getFolderSubTreeNotFound() throws ObjectNotAvailableException, CommonSecurityException, ServiceException {
        setUpException(ObjectNotAvailableException.class);

        WebResource resource = resource().path("dataset/easy-dataset:1/filetree/easy-folder:1");
        ClientResponse response = resource.get(ClientResponse.class);

        assertEquals(404, response.getStatus());
    }

    @Test
    public void getFolderSubTreeNotAuthorized() throws ObjectNotAvailableException, CommonSecurityException, ServiceException {
        setUpException(CommonSecurityException.class);

        WebResource resource = resource().path("dataset/easy-dataset:1/filetree/easy-folder:1");
        ClientResponse response = resource.get(ClientResponse.class);

        assertEquals(401, response.getStatus());
    }

    @Test
    public void getFolderSubTreeInternalServerError() throws ObjectNotAvailableException, CommonSecurityException, ServiceException {
        setUpException(ServiceException.class);

        WebResource resource = resource().path("dataset/easy-dataset:1/filetree/easy-folder:1");
        ClientResponse response = resource.get(ClientResponse.class);

        assertEquals(500, response.getStatus());
    }

}
