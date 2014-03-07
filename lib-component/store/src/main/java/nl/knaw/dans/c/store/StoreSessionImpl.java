package nl.knaw.dans.c.store;

import nl.knaw.dans.c.store.adapter.UnitOfWorkAdapter;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.exception.ObjectNotInStoreException;
import nl.knaw.dans.i.store.StoreSession;

public class StoreSessionImpl implements StoreSession
{

    private final UnitOfWorkAdapter uowa;

    public StoreSessionImpl(String ownerId)
    {
        uowa = new UnitOfWorkAdapter(ownerId);
    }

    public void attach(DataModelObject dmo) throws RepositoryException
    {
        uowa.attach(dmo);
    }

    public DataModelObject getDataModelObject(DmoStoreId dmoStoreId) throws ObjectNotInStoreException, RepositoryException
    {
        return uowa.getDataModelObject(dmoStoreId);
    }

    public DataModelObject detach(DataModelObject dmo)
    {
        return uowa.detach(dmo);
    }

    public DataModelObject saveAndDetach(DataModelObject dmo) throws RepositoryException
    {
        return uowa.saveAndDetach(dmo);
    }

    public void commit() throws RepositoryException
    {
        uowa.commit();
    }

    public void close()
    {
        uowa.close();
    }

}
