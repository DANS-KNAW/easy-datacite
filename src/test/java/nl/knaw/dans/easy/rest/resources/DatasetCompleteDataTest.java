package nl.knaw.dans.easy.rest.resources;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.service.exceptions.CommonSecurityException;
import nl.knaw.dans.common.lang.service.exceptions.ObjectNotAvailableException;
import nl.knaw.dans.common.lang.service.exceptions.ServiceException;
import nl.knaw.dans.easy.domain.dataset.DatasetImpl;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.dataset.item.ItemOrder;
import nl.knaw.dans.easy.domain.dataset.item.ItemVO;
import nl.knaw.dans.easy.domain.dataset.item.filter.ItemFilters;
import nl.knaw.dans.easy.domain.download.ZipFileContentWrapper;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.servicelayer.services.ItemService;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

@SuppressWarnings("unchecked")
public class DatasetCompleteDataTest extends RestTest
{
    private DatasetService datasetServiceMock;
    private ItemService itemServiceMock;

    @Before
    public void setUp()
    {
        Services services = new Services();

        datasetServiceMock = mock(DatasetService.class);
        itemServiceMock = mock(ItemService.class);

        services.setDatasetService(datasetServiceMock);
        services.setItemService(itemServiceMock);
    }

    private void setUpServiceMethods() throws ServiceException
    {
        ArrayList<ItemVO> items = new ArrayList<ItemVO>();
        FileItemVO file = new FileItemVO();
        file.setSid("easy-file:1");
        items.add(file);
        FolderItemVO folder = new FolderItemVO();
        folder.setSid("easy-folder:1");
        items.add(folder);

        when(datasetServiceMock.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).thenReturn(new DatasetImpl("easy-dataset:1"));

        when(
                itemServiceMock.getFilesAndFolders(isA(EasyUser.class), isA(Dataset.class), isA(DmoStoreId.class), isA(Integer.class), isA(Integer.class),
                        (ItemOrder) isNull(), (ItemFilters) isNull())).thenReturn(items);

        when(itemServiceMock.getZippedContent(isA(EasyUser.class), isA(Dataset.class), isA(Collection.class))).thenReturn(new ZipFileContentWrapper());

    }

    @Test
    public void getCompleteData() throws ServiceException
    {
        setUpServiceMethods();

        WebResource webResource = resource().path("dataset/easy-dataset:1/data");

        webResource.get(File.class);

        verify(datasetServiceMock, times(1)).getDataset(isA(EasyUser.class), isA(DmoStoreId.class));

        verify(itemServiceMock, times(1)).getFilesAndFolders(isA(EasyUser.class), isA(Dataset.class), isA(DmoStoreId.class), isA(Integer.class),
                isA(Integer.class), (ItemOrder) isNull(), (ItemFilters) isNull());

        verify(itemServiceMock, times(1)).getZippedContent(isA(EasyUser.class), isA(Dataset.class), isA(Collection.class));
    }

    @Test
    public void getCompleteDataObjectNotAvailable() throws ServiceException
    {
        setException(ObjectNotAvailableException.class);

        ClientResponse response = resource().path("dataset/easy-dataset:1/data").get(ClientResponse.class);

        assertEquals(404, response.getStatus());
    }

    private void setException(Class<? extends Throwable> t) throws ServiceException
    {
        when(datasetServiceMock.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).thenThrow(t);

    }

    @Test
    public void getCompleteDataNotAuthorized() throws ServiceException
    {
        setException(CommonSecurityException.class);

        ClientResponse response = resource().path("dataset/easy-dataset:1/data").get(ClientResponse.class);

        assertEquals(401, response.getStatus());
    }

    @Test
    public void getCompleteDataIllegalArgument() throws ServiceException
    {
        setException(IllegalArgumentException.class);

        ClientResponse response = resource().path("dataset/easy-dataset:1/data").get(ClientResponse.class);

        assertEquals(404, response.getStatus());
    }

    @Test
    public void getCompleteDataInternalServerError() throws ServiceException
    {
        setException(ServiceException.class);

        ClientResponse response = resource().path("dataset/easy-dataset:1/data").get(ClientResponse.class);

        assertEquals(500, response.getStatus());
    }

}
