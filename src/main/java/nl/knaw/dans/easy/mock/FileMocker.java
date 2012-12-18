package nl.knaw.dans.easy.mock;

import static org.easymock.EasyMock.anyBoolean;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;

import java.io.File;
import java.net.URL;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.user.User;
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

import org.joda.time.DateTime;
import org.powermock.api.easymock.PowerMock;

public class FileMocker
{
    private final String path;
    private final String storeId;
    private final FileItemVO fileItemVO;
    private final FileItem fileItem;

    /**
     * Creates mocked instances of a {@link FileItem} a {@link FileItemVO}. A fluent interface allows
     * further configuration of possible/expected behavior of the objects, and how {@link EasyStore} and
     * {@link ItemService} treat them.
     * 
     * @param path
     * @throws Exception
     */
    FileMocker(final String path, final String storeId) throws Exception
    {
        final DmoStoreId dmoStoreId = new DmoStoreId(storeId);
        this.path = path;
        this.storeId = storeId;
        fileItem = PowerMock.createMock(FileItem.class);
        fileItemVO = PowerMock.createMock(FileItemVO.class);
        expect(fileItem.getDmoStoreId()).andStubReturn(dmoStoreId);
        expect(fileItem.getStoreId()).andStubReturn(storeId);
        expect(fileItem.getPath()).andStubReturn(path);
        expect(fileItem.getFile()).andStubReturn(new File(path));
        expect(fileItemVO.getSid()).andStubReturn(storeId);
        expect(fileItemVO.getPath()).andStubReturn(path);
        expect(fileItemVO.getName()).andStubReturn(new File(path).getName());
        expect(Data.getEasyStore().retrieve(eq(dmoStoreId))).andStubReturn(fileItem);
        expect(Data.getEasyStore().exists(eq(dmoStoreId))).andStubReturn(true);
    }

    /**
     * Configures the expectation that
     * {@link EasyStore#purge(nl.knaw.dans.common.lang.repo.DataModelObject, boolean, String)} is called
     * exactly once for the mocked {@link FileItem} with any value for the other arguments.<br/>
     * Note that the mocked purge does not change anything to the mocked datasets or files. The mocked
     * objects are already in replay mode and therefore their behavior can't be changed any more. After
     * calling the mocked purge the file will keep showing up when calling some method from the mocked
     * {@link Data#getFileStoreAccess()}
     * 
     * @return this object to allow a fluent interface.
     */
    public FileMocker expectPurgeAt(final DateTime dateTime) throws Exception
    {
        expect(Data.getEasyStore().purge(eq(fileItem), anyBoolean(), isA(String.class))).andReturn(dateTime).once();
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
    public FileMocker with(final URL itemServiceContentUrl) throws Exception
    {
        expect(Services.getItemService().getFileContentURL(isA(EasyUser.class), isA(Dataset.class), eq(fileItem))).andStubReturn(itemServiceContentUrl);
        return this;
    }

    /**
     * Configures get methods of the mocked instances to return the expected {@link AccessibleTo}.
     * 
     * @return this object to allow a fluent interface.
     */
    public FileMocker with(final AccessibleTo accessibleTo, VisibleTo visibleTo)
    {
        expect(fileItem.getVisibleTo()).andStubReturn(visibleTo);
        expect(fileItem.getAccessibleTo()).andStubReturn(accessibleTo);
        expect(fileItemVO.getVisibleTo()).andStubReturn(visibleTo);
        expect(fileItemVO.getAccessibleTo()).andStubReturn(accessibleTo);
        return this;
    }

    /** @return the id generated for the mocked object */
    String getStoreId()
    {
        return storeId;
    }

    /**
     * Meant for {@link DatasetMocker}
     * 
     * @return the path as set by the constructor
     */
    String getPath()
    {
        return path;
    }

    /**
     * Meant for {@link DatasetMocker}. 
     * 
     * @return a mocked object. Please keep it in sync with the object returned by {@link #getFileItem()}.
     */
    FileItemVO getFileItemVO()
    {
        return fileItemVO;
    }

    /**
     * Meant for {@link DatasetMocker}. 
     * 
     * @return a mocked object. Please keep it in sync with the object returned by {@link #getFileItemVO()}.
     */
    FileItem getFileItem()
    {
        return fileItem;
    }
}
