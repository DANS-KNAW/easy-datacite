package nl.knaw.dans.c.store.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.knaw.dans.common.lang.repo.DmoStore;

public abstract class Repository {

    private static final Logger logger = LoggerFactory.getLogger(Repository.class);

    private static DmoStore dmoStore;

    public static DmoStore getDmoStore() {
        if (dmoStore == null) {
            throw new IllegalStateException("No DmoStore set. Make sure your binding is properly configured.");
        }
        return dmoStore;
    }

    public static void register(DmoStore dmoStore) {
        Repository.dmoStore = dmoStore;
        logger.info("Registered " + dmoStore.getClass().getName());
    }

    public static class Registrator {

        public void setDmoStore(DmoStore dmoStore) {
            Repository.register(dmoStore);
        }

    }

}
