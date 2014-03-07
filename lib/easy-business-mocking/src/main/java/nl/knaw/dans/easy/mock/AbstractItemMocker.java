package nl.knaw.dans.easy.mock;

import static org.easymock.EasyMock.anyBoolean;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;

import java.io.File;

import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.easy.data.Data;
import nl.knaw.dans.easy.data.store.EasyStore;
import nl.knaw.dans.easy.data.store.FileStoreAccess;
import nl.knaw.dans.easy.domain.dataset.item.AbstractItemVO;
import nl.knaw.dans.easy.domain.model.DatasetItem;
import nl.knaw.dans.easy.servicelayer.services.ItemService;

import org.joda.time.DateTime;

/** Wraps mocked instances of a {@link AbstractItem} and a {@link AbstractItemVO} */
class AbstractItemMocker<VO extends AbstractItemVO, I extends DatasetItem>
{
    private final String path;
    private final String storeId;
    private final VO mockedItemVO;
    private final I mockedItem;

    private String parentStoreId;

    /**
     * Creates mocked instances of a file or folder. A fluent interface allows further configuration of
     * possible/expected behavior of the objects, and how {@link EasyStore} and {@link ItemService} treat
     * them.
     */
    AbstractItemMocker(final String path, final String storeId, final VO mockedItemVO, final I mockedItem) throws Exception
    {
        this.mockedItem = mockedItem;
        final DmoStoreId dmoStoreId = new DmoStoreId(storeId);
        this.path = path;
        this.storeId = storeId;
        this.mockedItemVO = mockedItemVO;
        expect(mockedItemVO.getSid()).andStubReturn(storeId);
        expect(mockedItemVO.getPath()).andStubReturn(path);
        expect(mockedItemVO.getName()).andStubReturn(new File(path).getName());
        expect(getItem().getDmoStoreId()).andStubReturn(dmoStoreId);
        expect(getItem().getStoreId()).andStubReturn(storeId);
        expect(getItem().getPath()).andStubReturn(path);
        expect(Data.getEasyStore().exists(eq(dmoStoreId))).andStubReturn(true);
        expect(Data.getEasyStore().retrieve(eq(dmoStoreId))).andStubReturn(getItem());
    }

    /**
     * Configures the expectation that
     * {@link EasyStore#purge(nl.knaw.dans.common.lang.repo.DataModelObject, boolean, String)} is called
     * exactly once for the mocked {@link DatasetItem} with any value for the other arguments.<br/>
     * Remember to override corresponding method calls of {@link FileStoreAccess} set by
     * {@link DatasetMocker}. Both the calls expected before as after the purge. Otherwise the default
     * stubs give the items an eternal life.
     * 
     * @return this object to allow a fluent interface.
     */
    public AbstractItemMocker<VO, I> expectPurgeAt(final DateTime dateTime) throws Exception
    {
        // TODO cast problem currently fixed with override in subclasses, other suggestions:
        // http://stackoverflow.com/questions/450807/java-generics-how-do-i-make-the-method-return-type-generic
        // http://stackoverflow.com/questions/1069528/method-chaining-inheritance-dont-play-well-together-java
        expect(Data.getEasyStore().purge(eq(getItem()), anyBoolean(), isA(String.class))).andReturn(dateTime).once();
        return this;
    }

    /** @return the id as set by the constructor */
    String getStoreId()
    {
        return storeId;
    }

    /** @return the path as set by the constructor */
    String getPath()
    {
        return path;
    }

    /** @return a mocked object. Please keep it in sync with the object returned by {@link #getItem()}. */
    VO getItemVO()
    {
        return mockedItemVO;
    }

    /** @return a mocked object. Please keep it in sync with the object returned by {@link #getItemVO()}. */
    I getItem()
    {
        return mockedItem;
    }

    String getParentStoreId()
    {
        return parentStoreId;
    }

    void setParentStoreId(String parentStoreId)
    {
        this.parentStoreId = parentStoreId;
    }
}
