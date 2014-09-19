package nl.knaw.dans.c.store.adapter;

import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.AbstractUnitOfWork;
import nl.knaw.dans.common.lang.repo.DataModelObject;
import nl.knaw.dans.common.lang.repo.DmoStore;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.common.lang.repo.exception.UnitOfWorkInterruptException;

public class UnitOfWorkAdapter extends AbstractUnitOfWork {

    /**
     * 
     */
    private static final long serialVersionUID = 1888942403902490046L;

    public UnitOfWorkAdapter(String updateOwner) {
        super(updateOwner);
    }

    public DmoStore getStore() {
        return Repository.getDmoStore();
    }

    @Override
    public void attach(DataModelObject dmo) throws RepositoryException {
        // super accepts dmo's with storeId == null and puts them in a map with storeId as key!
        if (dmo.getDmoStoreId() == null) {
            dmo.setStoreId(getStore().nextSid(dmo.getDmoNamespace()));
        }
        super.attach(dmo);
    }

    public DataModelObject getDataModelObject(DmoStoreId dmoStoreId) throws RepositoryException {
        return super.retrieveObject(dmoStoreId);
    }

    public DataModelObject saveAndDetach(DataModelObject dmo) throws RepositoryException {
        try {
            return super.saveAndDetach(dmo);
        }
        catch (UnitOfWorkInterruptException e) {
            // never happens. just in case:
            throw new RepositoryException(e);
        }
    }

    public void commit() throws RepositoryException {
        try {
            super.commit();
        }
        catch (UnitOfWorkInterruptException e) {
            // never happens. just in case:
            throw new RepositoryException(e);
        }
    }

    protected String getUpdateLogMessage(DataModelObject dmo) {
        return "Updated by " + getUpdateOwner();
    }

    protected String getIngestLogMessage(DataModelObject dmo) {
        return "Ingested by " + getUpdateOwner();
    }

    protected String getPurgeLogMessage(DataModelObject dmo) {
        return "Purged by " + getUpdateOwner();
    }

}
