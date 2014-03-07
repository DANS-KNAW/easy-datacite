package nl.knaw.dans.common.lang.repo;

import java.io.Serializable;
import java.util.HashMap;

import nl.knaw.dans.common.lang.repo.exception.CouldNotGetStoreException;
import nl.knaw.dans.common.lang.repo.exception.StoreNameNotUniqueException;

/**
 * A singleton object that holds a list of uniquely named dmo stores. These dmo
 * currently register themselves during their creation process.
 * 
 * Note: This class does not store the stores with weak references, therefore once
 *  a store is created and registered it exists forever, because the singleton
 *  will keep the references forever.   
 * 
 * @author lobo
 */
public class DmoStores implements Serializable
{
    private static final long serialVersionUID = 1468152880397856857L;

    private static DmoStores INSTANCE = new DmoStores();

    private HashMap<String, DmoStore> stores = new HashMap<String, DmoStore>();

    private DmoStores()
    {
    }

    /**
     * @return the DmoStores singleton that holds the list of dmo stores.
     */
    public static DmoStores get()
    {
        return INSTANCE;
    }

    /**
     * Returns a store by its name.
     * @throws CouldNotGetStoreException if the store does not exist
     */
    public DmoStore getStoreByName(String name) throws CouldNotGetStoreException
    {
        DmoStore store = stores.get(name);
        if (store == null)
            throw new CouldNotGetStoreException(name);
        return store;
    }

    /**
     * Package level method that gets called by the constructor of a data model 
     * object store.
     * @param store the store
     */
    public synchronized void register(DmoStore store)
    {
        String storeName = store.getName();
        stores.put(storeName, store);
    }
}
