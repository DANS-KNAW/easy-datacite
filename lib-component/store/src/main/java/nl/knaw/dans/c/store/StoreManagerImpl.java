package nl.knaw.dans.c.store;

import nl.knaw.dans.c.store.adapter.Repository;
import nl.knaw.dans.common.lang.RepositoryException;
import nl.knaw.dans.common.lang.repo.DmoNamespace;
import nl.knaw.dans.common.lang.repo.DmoStoreId;
import nl.knaw.dans.i.store.StoreManager;
import nl.knaw.dans.i.store.StoreSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoreManagerImpl implements StoreManager {

    private static final Logger logger = LoggerFactory.getLogger(StoreManagerImpl.class);

    public StoreManagerImpl() {
        logger.info("Instantiated " + this.getClass().getName());
    }

    public StoreSession newStoreSession(String ownerId) {
        return new StoreSessionImpl(ownerId);
    }

    @Override
    public String nextStoreId(DmoNamespace dmoNamespace) throws RepositoryException {
        return Repository.getDmoStore().nextSid(dmoNamespace);
    }

    @Override
    public DmoStoreId nextDmoStoreId(DmoNamespace dmoNamespace) throws RepositoryException {
        return new DmoStoreId(nextStoreId(dmoNamespace));
    }

}
