package nl.knaw.dans.easy.mock;

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

/** Wraps mocked instances of a {@link FileItem} a {@link FileItemVO} */
public class FileMocker extends AbstractItemMocker<FileItemVO, FileItem> {
    /**
     * Creates mocked instances of a {@link FileItem} a {@link FileItemVO}. A fluent interface allows further configuration of possible/expected behavior of the
     * objects, and how {@link EasyStore} and {@link ItemService} treat them.
     * 
     * @param path
     * @throws Exception
     */
    FileMocker(final String path, final String storeId) throws Exception {
        super(path, storeId, PowerMock.createMock(FileItemVO.class), PowerMock.createMock(FileItem.class));
        expect(getItem().getFile()).andStubReturn(new File(path));
    }

    public FileMocker expectPurgeAt(final DateTime dateTime) throws Exception {
        super.expectPurgeAt(dateTime);
        return this;
    }

    /**
     * Configures the {@link URL} returned by {@link ItemService#getFileContentURL(EasyUser, Dataset, FileItem)}. The arguments {@link Dataset} and {@link User}
     * are ignored for the expectations. The URL should locate test data that mocks content of the repository.
     * 
     * @return this object to allow a fluent interface.
     */
    public FileMocker with(final URL contentUrl) throws Exception {
        expect(Services.getItemService().getFileContentURL(isA(EasyUser.class), isA(Dataset.class), eq(getItem()))).andStubReturn(contentUrl);
        expect(Data.getEasyStore().getFileURL(eq(new DmoStoreId(getStoreId())))).andStubReturn(contentUrl);
        return this;
    }

    /**
     * Configures get methods of the mocked instances to return the expected values.
     * 
     * @return this object to allow a fluent interface.
     */
    public FileMocker with(final AccessibleTo accessibleTo, VisibleTo visibleTo) {
        expect(getItem().getVisibleTo()).andStubReturn(visibleTo);
        expect(getItem().getAccessibleTo()).andStubReturn(accessibleTo);
        expect(getItemVO().getVisibleTo()).andStubReturn(visibleTo);
        expect(getItemVO().getAccessibleTo()).andStubReturn(accessibleTo);
        return this;
    }
}
