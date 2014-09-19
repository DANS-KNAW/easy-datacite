package nl.knaw.dans.easy.rest.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.domain.user.EasyUserImpl;
import nl.knaw.dans.easy.servicelayer.services.DatasetService;
import nl.knaw.dans.easy.servicelayer.services.ItemService;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class ThumbnailUtilTest {
    private ItemService itemServiceMock;
    private DatasetService datasetServiceMock;

    @Before
    public void setUp() {
        itemServiceMock = mock(ItemService.class);
        datasetServiceMock = mock(DatasetService.class);

        Services services = new Services();
        services.setItemService(itemServiceMock);
        services.setDatasetService(datasetServiceMock);
    }

    @Test(expected = AssertionError.class)
    public void notInstantiable() {
        new ThumbnailUtil();
    }

    @Test
    public void isThumbnailFalse() throws ServiceException {
        when(itemServiceMock.getFilesAndFolders(isA(EasyUser.class), isA(Dataset.class), isA(Collection.class))).thenReturn(new ArrayList<ItemVO>());

        FileItemVO f = new FileItemVO();
        f.setParentSid("easy-folder:13");
        boolean result = ThumbnailUtil.isThumbnail(new EasyUserImpl(), new DatasetImpl("easy-dataset:1"), f);

        assertFalse(result);
    }

    @Test
    public void isThumbnailTrue() throws ServiceException {
        ArrayList<ItemVO> items = new ArrayList<ItemVO>();
        FolderItemVO folder = new FolderItemVO();
        folder.setName(ThumbnailUtil.THUMBNAILS);
        items.add(folder);

        when(itemServiceMock.getFilesAndFolders(isA(EasyUser.class), isA(Dataset.class), isA(Collection.class))).thenReturn(items);

        FileItemVO f = new FileItemVO();
        f.setParentSid("easy-folder:13");
        boolean result = ThumbnailUtil.isThumbnail(new EasyUserImpl(), new DatasetImpl("easy-dataset:1"), f);

        assertTrue(result);
    }

    @Test
    public void getThumbnailsEmpty() throws ObjectNotAvailableException, CommonSecurityException, ServiceException {
        setUpMocks(new ArrayList<ItemVO>());

        String xml = ThumbnailUtil.getThumbnailIdsXml(new EasyUserImpl(), "easy-dataset:1");
        assertEquals("<thumbnails></thumbnails>", xml);
    }

    private void setUpMocks(List<ItemVO> returnList) throws ObjectNotAvailableException, CommonSecurityException, ServiceException {
        when(datasetServiceMock.getDataset(isA(EasyUser.class), isA(DmoStoreId.class))).thenReturn(new DatasetImpl("easy-dataset:1"));

        when(
                itemServiceMock.getFilesAndFolders(isA(EasyUser.class), isA(Dataset.class), isA(DmoStoreId.class), isA(Integer.class), isA(Integer.class),
                        (ItemOrder) isNull(), (ItemFilters) isNull())).thenReturn(returnList);
    }

    @Test
    public void getThumbnails() throws ObjectNotAvailableException, CommonSecurityException, ServiceException {
        setUpMocks(setUpReturnList());

        String xml = ThumbnailUtil.getThumbnailIdsXml(new EasyUserImpl(), "easy-dataset:1");

        assertEquals("<thumbnails><sid>easy-file:1</sid></thumbnails>", xml);
    }

    private ArrayList<ItemVO> setUpReturnList() {
        ArrayList<ItemVO> returnList = new ArrayList<ItemVO>();
        FolderItemVO folder = new FolderItemVO();
        folder.setName("thumbnails");
        folder.setSid("easy-folder:1");
        returnList.add(folder);
        FileItemVO file = new FileItemVO();
        file.setName("file");
        file.setSid("easy-file:1");
        returnList.add(file);
        return returnList;
    }

}
