package nl.knaw.dans.c.store;

import static org.junit.Assert.assertEquals;
import nl.knaw.dans.c.store.adapter.Repository;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStore;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.UnitOfWork;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.i.store.StoreSession;

import org.easymock.EasyMock;
import org.junit.BeforeClass;
import org.junit.Test;

// some methods cannot be tested with a mock DataModelObject,
// because underlying implementation uses AbstractUnitOfWork,
// and that class does casts of DataModelObject to AbstractDataModelObject.
public class StoreSessionImplTest extends EasyMock {

    private static DataModelObject dmo;
    private static DmoStore dmoStore;

    @BeforeClass
    public static void beforeClass() {
        dmo = createMock(DataModelObject.class);
        dmoStore = createMock(DmoStore.class);
        Repository.register(dmoStore);
    }

    @Test
    public void attach() throws Exception {
        StoreSession session = new StoreSessionImpl("foo1");
        DmoNamespace namespace = new DmoNamespace("bar-test");
        DmoStoreId dmoStoreId = new DmoStoreId("bar-test:1");

        reset(dmo, dmoStore);
        expect(dmo.getDmoStoreId()).andReturn(null).times(1);
        expect(dmo.getDmoNamespace()).andReturn(namespace).times(1);
        expect(dmoStore.nextSid(namespace)).andReturn("bar-test:1");
        dmo.setStoreId("bar-test:1");
        expect(dmo.getDmoStoreId()).andReturn(dmoStoreId).times(1);
        dmo.setUnitOfWork(EasyMock.isA(UnitOfWork.class));

        replay(dmo, dmoStore);
        session.attach(dmo);
        DataModelObject dmoA = session.getDataModelObject(new DmoStoreId("bar-test:1"));
        verify(dmo, dmoStore);

        assertEquals(dmo, dmoA);
    }

    @Test
    public void getDataModelObject() throws Exception {
        StoreSession session = new StoreSessionImpl("foo2");
        DmoStoreId dmoStoreId = new DmoStoreId("bar-test:2");

        reset(dmo, dmoStore);
        expect(dmoStore.retrieve(dmoStoreId)).andReturn(dmo);
        dmo.setUnitOfWork(EasyMock.isA(UnitOfWork.class));

        replay(dmo, dmoStore);
        DataModelObject dmoR = session.getDataModelObject(dmoStoreId);
        verify(dmo, dmoStore);

        assertEquals(dmo, dmoR);
    }

    @Test(expected = ObjectNotInStoreException.class)
    public void getDataModelObjectAndNotFound() throws Exception {
        StoreSession session = new StoreSessionImpl("foo");
        DmoStoreId dmoStoreId = new DmoStoreId("bar-test:2");

        reset(dmo, dmoStore);
        expect(dmoStore.retrieve(dmoStoreId)).andThrow(new ObjectNotInStoreException());

        replay(dmo, dmoStore);
        session.getDataModelObject(dmoStoreId);
    }

}
