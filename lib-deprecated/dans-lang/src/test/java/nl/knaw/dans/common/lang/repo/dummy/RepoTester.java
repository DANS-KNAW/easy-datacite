package nl.knaw.dans.common.lang.repo.dummy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.exception.ConcurrentUpdateException;
import nl.knaw.dans.common.lang.repo.exception.DmoStoreEventListenerException;
import nl.knaw.dans.common.lang.repo.exception.ObjectDeserializationException;
import nl.knaw.dans.common.lang.repo.exception.ObjectIsNotDeletableException;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;

/**
 * Used for mock testing the repository functionality.
 * 
 * @author lobo
 */
public class RepoTester {
    private DummyDmoStore dmoStore;

    public RepoTester(DummyDmoStore store) {
        this.setDmoStore(store);
    }

    public RepoTester() {
        this.setDmoStore(new DummyDmoStore());
    }

    public DataModelObject retrieve(DmoStoreId dmoStoreID) throws ObjectNotInStoreException, ObjectDeserializationException, RepositoryException {
        return retrieve(dmoStoreID, DummyDmo.class);
    }

    @SuppressWarnings("unchecked")
    public DataModelObject retrieve(Class dmoClass) throws ObjectNotInStoreException, ObjectDeserializationException, RepositoryException {
        return retrieve(null, dmoClass);
    }

    @SuppressWarnings("unchecked")
    public DataModelObject retrieve(DmoStoreId dmoStoreId, Class dmoClass) throws ObjectNotInStoreException, ObjectDeserializationException,
            RepositoryException
    {
        DataModelObject dmo = new DummyDmo(null); // getDmoStore().createDmo(dmoClass);
        if (dmoStoreId != null)
            dmo.setStoreId(dmoStoreId.getStoreId());

        getDmoStore().retrieveReturns(dmo);
        DataModelObject result = getDmoStore().retrieve(dmo.getDmoStoreId());

        assertTrue(dmo == result);
        assertTrue(result.isLoaded());
        assertEquals(getDmoStore(), result.getStore());
        // implementation has changed
        // assertFalse(result.isInvalidated());

        return result;
    }

    public void update(DummyDmo dmo) throws DmoStoreEventListenerException, RepositoryException, ConcurrentUpdateException {
        String logMessage = "updating " + dmo.getStoreId();

        getDmoStore().update(dmo, logMessage);

        assertTrue(dmo.isLoaded());
        assertEquals(getDmoStore(), dmo.getStore());
        assertFalse(dmo.isInvalidated());
    }

    public void purge(DummyDmo dmo) throws ObjectNotInStoreException, ObjectIsNotDeletableException, RepositoryException {
        String logMessage = "purging " + dmo.getStoreId();

        dmo.registerDeleted();
        getDmoStore().purge(dmo, false, logMessage);

        assertEquals(getDmoStore(), dmo.getStore());
    }

    public ByteArrayOutputStream serializeDmo(DummyDmo dmo5) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream objstream = new ObjectOutputStream(out);
        objstream.writeObject(dmo5);

        objstream.close();
        return out;
    }

    public DummyDmo deserializeDmo(ByteArrayOutputStream sdmo) throws IOException, ClassNotFoundException {
        ObjectInputStream objstream = new ObjectInputStream(new ByteArrayInputStream(sdmo.toByteArray()));
        Object object = objstream.readObject();
        objstream.close();
        return (DummyDmo) object;
    }

    public void setDmoStore(DummyDmoStore dmoStore) {
        this.dmoStore = dmoStore;
    }

    public DummyDmoStore getDmoStore() {
        return dmoStore;
    }

}
