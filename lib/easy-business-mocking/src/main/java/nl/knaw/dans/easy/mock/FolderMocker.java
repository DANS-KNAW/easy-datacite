package nl.knaw.dans.easy.mock;

import nl.knaw.dans.easy.data.store.EasyStore;
import nl.knaw.dans.easy.domain.dataset.item.FolderItemVO;
import nl.knaw.dans.easy.domain.model.FolderItem;
import nl.knaw.dans.easy.servicelayer.services.ItemService;

import org.joda.time.DateTime;
import org.powermock.api.easymock.PowerMock;

/** Wraps mocked instances of a {@link FolderItem} and a {@link FolderItemVO} */
public class FolderMocker extends AbstractItemMocker<FolderItemVO, FolderItem>
{
    /**
     * Creates mocked instances of a {@link FolderItem} a {@link FolderItemVO}. A fluent interface allows
     * further configuration of possible/expected behavior of the objects, and how {@link EasyStore} and
     * {@link ItemService} treat them.
     * 
     * @param path
     * @throws Exception
     */
    FolderMocker(final String path, final String storeId) throws Exception
    {
        super(path, storeId, PowerMock.createMock(FolderItemVO.class), PowerMock.createMock(FolderItem.class));
    }

    public FolderMocker expectPurgeAt(final DateTime dateTime) throws Exception
    {
        return (FolderMocker) super.expectPurgeAt(dateTime);
    }
}
