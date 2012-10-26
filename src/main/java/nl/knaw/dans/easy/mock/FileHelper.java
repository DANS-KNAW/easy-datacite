package nl.knaw.dans.easy.mock;

import static org.easymock.EasyMock.anyBoolean;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;

import java.io.File;
import java.net.URL;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.EasyStore;
import nl.knaw.dans.easy.domain.dataset.item.FileItemVO;
import nl.knaw.dans.easy.domain.model.AccessibleTo;
import nl.knaw.dans.easy.domain.model.Dataset;
import nl.knaw.dans.easy.domain.model.FileItem;
import nl.knaw.dans.easy.domain.model.VisibleTo;
import nl.knaw.dans.easy.domain.model.user.EasyUser;
import nl.knaw.dans.easy.servicelayer.services.ItemService;
import nl.knaw.dans.easy.servicelayer.services.Services;

import org.aspectj.lang.annotation.Before;
import org.joda.time.DateTime;
import org.powermock.api.easymock.PowerMock;

public class FileHelper
{
    private final String path;
    private final String storeId;
    private final FileItemVO fileItemVO;
    private final FileItem fileItem;
    private static EasyStore easyStoreMock;
    private static ItemService itemServiceMock;

    private static int fileCounter = 0;

    /**
     * Creates mocked instances of a {@link FileItem} a {@link FileItemVO}. A fluent interface allows
     * further configuration of possible/expected behavior of the objects, and how {@link EasyStore} and
     * {@link ItemService} treat them.
     * 
     * @param path
     * @throws Exception
     */
    FileHelper(final String path) throws Exception
    {
        this.path = path;
        storeId = "easy-file:" + ++fileCounter;
        fileItem = PowerMock.createMock(FileItem.class);
        fileItemVO = PowerMock.createMock(FileItemVO.class);
        final DmoStoreId dmoStoreId = new DmoStoreId(storeId);
        expect(fileItem.getDmoStoreId()).andReturn(dmoStoreId).anyTimes();
        expect(fileItem.getStoreId()).andReturn(storeId).anyTimes();
        expect(fileItem.getPath()).andReturn(path).anyTimes();
        expect(fileItem.getFile()).andReturn(new File(path)).anyTimes();
        expect(fileItemVO.getSid()).andReturn(storeId).anyTimes();
        expect(fileItemVO.getPath()).andReturn(path).anyTimes();
        expect(fileItemVO.getName()).andReturn(new File(path).getName()).anyTimes();
        expect(easyStoreMock.retrieve(dmoStoreId)).andReturn(fileItem).anyTimes();
    }

    /**
     * Prepares the mocks for a new configuration. To be called by a {@link Before}.
     */
    static void reset()
    {
        fileCounter = 0;
        easyStoreMock = PowerMock.createMock(EasyStore.class);
        itemServiceMock = PowerMock.createMock(ItemService.class);
        expect(Data.getEasyStore()).andStubReturn(easyStoreMock);
        expect(Services.getItemService()).andStubReturn(itemServiceMock);
    }

    /**
     * Configures get methods of the mocked instances to return {@link AccessibleTo.NONE} respectively
     * {@link VisibleTo.NONE}.
     * 
     * @return this object to allow a fluent interface.
     */
    public FileHelper forNone()
    {
        expect(fileItem.getAccessibleTo()).andReturn(AccessibleTo.NONE).anyTimes();
        expect(fileItem.getVisibleTo()).andReturn(VisibleTo.NONE).anyTimes();
        expect(fileItemVO.getAccessibleTo()).andReturn(AccessibleTo.NONE).anyTimes();
        expect(fileItemVO.getVisibleTo()).andReturn(VisibleTo.NONE).anyTimes();
        return this;
    }

    /**
     * Configures the expectation that
     * {@link EasyStore#purge(nl.knaw.dans.common.lang.repo.DataModelObject, boolean, String)} is called
     * exactly once for the mocked {@link FileItem} with any value for the other arguments.
     * 
     * @return this object to allow a fluent interface.
     */
    public FileHelper expectPurgeAt(final DateTime dateTime) throws Exception
    {
        expect(easyStoreMock.purge(eq(fileItem), anyBoolean(), isA(String.class))).andReturn(dateTime).once();
        return this;
    }

    /**
     * Configures the {@link URL} returned by
     * {@link ItemService#getFileContentURL(EasyUser, Dataset, FileItem)}. The arguments {@link Dataset}
     * and {@link User} are ignored for the expectations. The URL should locate test data that mocks
     * content of the repository.
     * 
     * @return this object to allow a fluent interface.
     */
    public FileHelper with(final URL itemServiceContentUrl) throws Exception
    {
        expect(itemServiceMock.getFileContentURL(isA(EasyUser.class), isA(Dataset.class), eq(fileItem))).andReturn(itemServiceContentUrl).anyTimes();
        return this;
    }

    /**
     * Configures get methods of the mocked instances to return the expected {@link AccessibleTo}.
     * 
     * @return this object to allow a fluent interface.
     */
    public FileHelper with(final AccessibleTo expected)
    {
        expect(fileItem.getAccessibleTo()).andReturn(expected).anyTimes();
        expect(fileItemVO.getAccessibleTo()).andReturn(expected).anyTimes();
        return this;
    }

    /**
     * Configures get methods of the mocked instances to return the expected {@link VisibleTo}.
     * 
     * @return this object to allow a fluent interface.
     */
    public FileHelper with(final VisibleTo expected)
    {
        expect(fileItem.getVisibleTo()).andReturn(expected).anyTimes();
        expect(fileItemVO.getVisibleTo()).andReturn(expected).anyTimes();
        return this;
    }

    public String getPath()
    {
        return path;
    }

    public String getStoreId()
    {
        return storeId;
    }

    public FileItemVO getFileItemVO()
    {
        return fileItemVO;
    }
}
