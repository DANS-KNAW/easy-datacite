package nl.knaw.dans.common.lang.repo;

import nl.knaw.dans.common.lang.repo.exception.DmoStoreEventListenerException;

/**
 * Does nothing. Subclasses can override methods.
 * 
 * @author ecco
 */
public class DefaultDmoStoreEventListener implements DmoStoreEventListener {

    @Override
    public void beforeIngest(DmoStore store, DataModelObject dmo) throws DmoStoreEventListenerException {

    }

    @Override
    public void afterIngest(DmoStore store, DataModelObject dmo) throws DmoStoreEventListenerException {

    }

    @Override
    public void beforeUpdate(DmoStore store, DataModelObject dmo) throws DmoStoreEventListenerException {

    }

    @Override
    public void afterUpdate(DmoStore store, DataModelObject dmo) throws DmoStoreEventListenerException {

    }

    @Override
    public void afterPartialUpdate(DmoStore store, DataModelObject dmo) throws DmoStoreEventListenerException {

    }

    @Override
    public void beforePurge(DmoStore store, DataModelObject dmo) throws DmoStoreEventListenerException {

    }

    @Override
    public void afterPurge(DmoStore store, DataModelObject dmo) throws DmoStoreEventListenerException {

    }

}
